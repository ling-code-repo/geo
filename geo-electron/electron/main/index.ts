import { release } from "node:os";
import { fileURLToPath } from "node:url";
import { dirname, join } from "node:path";
import {
  app,
  BrowserWindow,
  globalShortcut,
  ipcMain,
  Menu,
  shell,
} from "electron";
import { ChildProcess, spawn } from "child_process";
import { existsSync } from "fs";
import portfinder from "portfinder";

// The built directory structure
//
// ├─┬ dist-electron
// │ ├─┬ main
// │ │ └── index.js    > Electron-Main
// │ └─┬ preload
// │   └── index.mjs    > Preload-Scripts
// ├─┬ dist
// │ └── index.html    > Electron-Renderer
//
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
process.env.DIST_ELECTRON = join(__dirname, "..");
process.env.DIST = join(process.env.DIST_ELECTRON, "../dist");
process.env.PUBLIC = process.env.VITE_DEV_SERVER_URL
  ? join(process.env.DIST_ELECTRON, "../public")
  : process.env.DIST;
// 是否为开发环境
// Disable GPU Acceleration for Windows 7
if (release().startsWith("6.1")) app.disableHardwareAcceleration();

// Set application name for Windows 10+ notifications
if (process.platform === "win32") app.setAppUserModelId(app.getName());

if (!app.requestSingleInstanceLock()) {
  app.quit();
  process.exit(0);
}
// Remove electron security warnings
// This warning only shows in development mode
// Read more on https://www.electronjs.org/docs/latest/tutorial/security
// process.env['ELECTRON_DISABLE_SECURITY_WARNINGS'] = 'true'

let win: BrowserWindow | null = null;
// Here, you can also use other preload
const preload = join(__dirname, "../preload/index.mjs");
const url = process.env.VITE_DEV_SERVER_URL;
const indexHtml = join(process.env.DIST, "index.html");

let pythonProcess: ChildProcess | null = null;
let pythonPort = 0;
const isDev = process.env.NODE_ENV === "development";
const isProd = process.env.NODE_ENV === "production";

// console.log("=== 环境信息 ===");
// console.log("isPackaged:", app.isPackaged);
// console.log("isDev:", isDev);
// console.log("isProd:", isProd);
// console.log("resourcesPath:", process.resourcesPath);
// console.log("appPath:", app.getAppPath());
// console.log("=================");
// geo.exe --enable-logging start "GeoApp" /B geo.exe --enable-logging --log-level=0 --v=1 > "./geo_log.txt" 2>&1
// ==================== Python 进程管理（最简单版本）====================
/**
 * 获取 Python 可执行文件路径
 */
function getPythonPath(): string {
  return join(process.resourcesPath, "internal", "main.exe");
}

/**
 * 启动 Python 进程
 */
async function startPythonProcess(): Promise<boolean> {
  try {
    const pythonPath = getPythonPath();

    if (!existsSync(pythonPath)) {
      console.error(`Python executable not found at: ${pythonPath}`);
      return false;
    }

    // 使用 5000 端口（或其他你想要的端口）
    // pythonPort = 5000;

    pythonPort = await portfinder.getPortPromise({
      port: 49152, // 起始端口
      stopPort: 65535, // 最大端口范围
    });

    console.log(`启动 Python 进程: ${pythonPath}`);
    console.log(`使用端口: ${pythonPort}`);

    // 直接使用 spawn，这是最可靠的方式
    pythonProcess = spawn(pythonPath, ["--port", pythonPort.toString()], {
      cwd: process.resourcesPath, // 重要：设置工作目录
      stdio: ["pipe", "pipe", "pipe"], // 获取所有输出
      windowsHide: true, // Windows 下隐藏控制台窗口
      detached: false,
    });

    console.log(`Python 进程 PID: ${pythonProcess.pid}`);

    // 监听标准输出
    pythonProcess.stdout.on("data", (data: Buffer) => {
      const output = data.toString().trim();
      if (output) {
        console.log(`Python stdout: ${output}`);
      }
    });

    // 监听错误输出
    pythonProcess.stderr.on("data", (data: Buffer) => {
      const output = data.toString().trim();
      if (output) {
        console.error(`Python stderr: ${output}`);

        // 如果是权限错误，可以尝试其他端口
        if (
          output.includes("Errno 13") ||
          output.includes("Permission denied")
        ) {
          console.error("权限错误，可能需要管理员权限或更换端口");
        }
      }
    });

    // 监听进程退出
    pythonProcess.on("close", (code: number, signal: string) => {
      console.log(`Python 进程退出，代码: ${code}, 信号: ${signal}`);
      pythonProcess = null;
    });

    // 监听进程错误
    pythonProcess.on("error", (error: Error) => {
      console.error(`Python 进程错误: ${error.message}`);
      pythonProcess = null;
    });

    // 等待 3 秒，检查进程是否还在运行
    await new Promise((resolve) => setTimeout(resolve, 3000));

    // 检查进程是否还在运行
    if (pythonProcess && !pythonProcess.killed) {
      console.log("Python 进程启动成功，正在运行");
      return new Promise((resolve) => resolve(true));
    }
    console.log("Python 进程可能已退出");
  } catch (error) {
    console.error(`启动 Python 进程时出错: ${error}`);
  }
  return new Promise((resolve) => resolve(false));
}

/**
 * 停止 Python 进程
 */
function stopPythonProcess(): void {
  if (pythonProcess) {
    console.log("停止 Python 进程...");

    if (process.platform === "win32") {
      // Windows: 使用 taskkill 强制终止进程树
      spawn(
        "taskkill",
        ["/pid", pythonProcess.pid?.toString() || "", "/f", "/t"],
        {
          stdio: "ignore",
          windowsHide: true,
        },
      );
    } else {
      // Unix-like 系统
      pythonProcess.kill("SIGTERM");
    }

    pythonProcess = null;
    console.log("Python 进程已停止");
  }
}

async function createWindow() {
  if (app.isPackaged) {
    const pythonStarted = await startPythonProcess();

    if (!pythonStarted) {
      console.warn(
        "Python backend failed to start, but continuing with Electron...",
      );
    }
  }
  Menu.setApplicationMenu(null);
  win = new BrowserWindow({
    width: 1750,
    height: 995,
    minWidth: 1750,
    minHeight: 995,
    title: "Main window",
    icon: join(process.env.PUBLIC, "favicon.ico"),
    webPreferences: {
      preload,
      webviewTag: true,
      // devTools: isDev, // 确保 devTools 是开启的
      devTools: true, // 确保 devTools 是开启的
      // Warning: Enable nodeIntegration and disable contextIsolation is not secure in production
      // nodeIntegration: true,

      // Consider using contextBridge.exposeInMainWorld
      // Read more on https://www.electronjs.org/docs/latest/tutorial/context-isolation
      // contextIsolation: false,
    },
  });
  if (isDev || process.env.VITE_DEVTOOL || import.meta.env.VITE_DEVTOOL) {
    win.maximize();
    // VITE_DEVTOOL
    // 可选：Ctrl+Shift+I 快捷键
    globalShortcut.register("CommandOrControl+Shift+I", () => {
      const focusedWindow = BrowserWindow.getFocusedWindow();
      if (focusedWindow) {
        focusedWindow.webContents.toggleDevTools();
      }
    });
  }

  if (process.env.VITE_DEV_SERVER_URL) {
    // electron-vite-vue#298
    win.loadURL(url);
    // Open devTool if the app is not packaged
    // win.webContents.openDevTools();
  } else {
    win.loadFile(indexHtml);
  }

  // Test actively push message to the Electron-Renderer
  win.webContents.on("did-finish-load", () => {
    win?.webContents.send("main-process-message", new Date().toLocaleString());
    win?.webContents.send("python-port", pythonPort);
  });

  // Make all links open with the browser, not with the application
  win.webContents.setWindowOpenHandler(({ url }) => {
    if (url.startsWith("https:")) shell.openExternal(url);
    return { action: "deny" };
  });
  // win.webContents.on('will-navigate', (event, url) => { }) #344

  // 窗口进入全屏状态时触发
  // win.on("enter-full-screen", () => {
  //   createMenu("退出全屏幕");
  // });

  // 窗口离开全屏状态时触发
  // win.on("leave-full-screen", () => {
  //   createMenu();
  // });
}

// console.log('用户数据路径:', app.getPath('userData'))
// console.log('缓存路径:', app.getPath('cache'))

ipcMain.handle("getDataPath", async (event) => {
  // 模拟异步操作
  return {
    userDataPath: app.getPath("userData"),
    cachePath: app.getPath("cache"),
  };
});

// 获取 Python 端口
ipcMain.handle("getLocalServerAddress", async () => {
  let url = "http://127.0.0.1:";
  if (isDev) {
    return url + import.meta.env.VITE_LOCAL_SERVER_PORT;
  }
  return url + pythonPort;
});

app.whenReady().then(createWindow);

//
// app.commandLine.appendSwitch('remote-debugging-port', '9222')
//
// // 如果需要允许所有来源访问（开发环境）
// app.commandLine.appendSwitch('remote-allow-origins', '*')
// app.on("window-all-closed", () => {
//   win = null;
//   if (process.platform !== "darwin") app.quit();
// });

app.on("second-instance", () => {
  if (win) {
    // Focus on the main window if the user tried to open another
    if (win.isMinimized()) win.restore();
    win.focus();
  }
});

// 窗口关闭时处理
app.on("window-all-closed", () => {
  globalShortcut.unregisterAll();
  stopPythonProcess();
  app.quit();
});
// 应用退出时注销快捷键
app.on("will-quit", () => {
  globalShortcut.unregisterAll();
  stopPythonProcess(); // 停止 Python 进程
  app.quit();
});

app.on("activate", () => {
  const allWindows = BrowserWindow.getAllWindows();
  if (allWindows.length) {
    allWindows[0].focus();
  } else {
    createWindow();
  }
});

// New window example arg: new windows url
ipcMain.handle("open-win", (_, arg) => {
  const childWindow = new BrowserWindow({
    webPreferences: {
      preload,
      nodeIntegration: true,
      contextIsolation: false,
    },
  });

  if (process.env.VITE_DEV_SERVER_URL) {
    childWindow.loadURL(`${url}#${arg}`);
  } else {
    childWindow.loadFile(indexHtml, { hash: arg });
  }
});
