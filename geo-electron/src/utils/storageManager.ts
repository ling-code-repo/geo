// storageManager.ts - 智能存储管理器
import Cookies from "js-cookie";

export enum StorageType {
  AUTO = 'auto',
  COOKIE = 'cookie',
  LOCAL = 'local',
}

export class SmartStorage {
  private static instance: SmartStorage;
  private storageType: StorageType = StorageType.AUTO;
  private isElectron: boolean;

  private constructor() {
    // 检测环境
    this.isElectron = this.detectElectron();
    this.detectBestStorage();
  }

  static getInstance(): SmartStorage {
    if (!SmartStorage.instance) {
      SmartStorage.instance = new SmartStorage();
    }
    return SmartStorage.instance;
  }

  private detectElectron(): boolean {
    return (
      typeof navigator !== 'undefined' &&
      navigator.userAgent.toLowerCase().indexOf('electron') >= 0
    );
  }

  private detectBestStorage(): void {
    if (this.isElectron) {
      // Electron 环境优先使用 localStorage
      this.storageType = StorageType.LOCAL;
    } else {
      // Web 环境使用 Cookie（保持现有逻辑）
      this.storageType = StorageType.COOKIE;

      // 检查 Cookie 是否可用
      try {
        Cookies.set('__test__', 'test');
        if (Cookies.get('__test__') === 'test') {
          Cookies.remove('__test__');
        } else {
          // Cookie 不可用，降级到 localStorage
          this.storageType = StorageType.LOCAL;
        }
      } catch (e) {
        this.storageType = StorageType.LOCAL;
      }
    }
  }

  // 统一的 set 方法
  set(key: string, value: any, options?: any): void {
    switch (this.storageType) {
      case StorageType.COOKIE:
        Cookies.set(key, JSON.stringify(value), options);
        // 同时在 localStorage 备份（用于 Electron 降级）
        if (typeof localStorage !== 'undefined') {
          localStorage.setItem(`cookie_backup_${key}`, JSON.stringify(value));
        }
        break;

      case StorageType.LOCAL:
        if (typeof localStorage !== 'undefined') {
          localStorage.setItem(key, JSON.stringify(value));
          // 同时在 Cookie 备份（用于 Web 环境）
          if (!this.isElectron) {
            Cookies.set(`local_backup_${key}`, JSON.stringify(value));
          }
        }
        break;

      default:
        // 自动选择
        this.isElectron ? this.set(key, value, options) : this.set(key, value, options);
    }
  }

  // 统一的 get 方法
  get(key: string): any {
    // 优先尝试主存储
    let value: any = null;

    switch (this.storageType) {
      case StorageType.COOKIE:
        const cookieValue = Cookies.get(key);
        if (cookieValue) {
          try {
            value = JSON.parse(cookieValue);
          } catch {
            value = cookieValue;
          }
        }
        break;

      case StorageType.LOCAL:
        if (typeof localStorage !== 'undefined') {
          const localValue = localStorage.getItem(key);
          if (localValue) {
            try {
              value = JSON.parse(localValue);
            } catch {
              value = localValue;
            }
          }
        }
        break;
    }

    // 如果主存储没有，尝试从备份获取
    if (!value) {
      value = this.getFromBackup(key);
    }

    return value;
  }

  private getFromBackup(key: string): any {
    let value = null;

    if (this.storageType === StorageType.COOKIE) {
      // Cookie 主存储 -> 检查 localStorage 备份
      if (typeof localStorage !== 'undefined') {
        const backup = localStorage.getItem(`cookie_backup_${key}`);
        if (backup) {
          try {
            value = JSON.parse(backup);
          } catch {
            value = backup;
          }
        }
      }
    } else if (this.storageType === StorageType.LOCAL) {
      // localStorage 主存储 -> 检查 Cookie 备份
      const backup = Cookies.get(`local_backup_${key}`);
      if (backup) {
        try {
          value = JSON.parse(backup);
        } catch {
          value = backup;
        }
      }
    }

    return value;
  }

  remove(key: string): void {
    // 删除主存储
    switch (this.storageType) {
      case StorageType.COOKIE:
        Cookies.remove(key);
        break;
      case StorageType.LOCAL:
        if (typeof localStorage !== 'undefined') {
          localStorage.removeItem(key);
        }
        break;
    }

    // 删除备份
    this.removeBackup(key);
  }

  private removeBackup(key: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(`cookie_backup_${key}`);
    }
    Cookies.remove(`local_backup_${key}`);
  }
}

// 导出单例
export const smartStorage = SmartStorage.getInstance();
