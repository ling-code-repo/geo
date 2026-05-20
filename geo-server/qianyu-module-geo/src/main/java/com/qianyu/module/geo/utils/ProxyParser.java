package com.qianyu.module.geo.utils;

import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyParser {
    public enum Protocol {HTTP, HTTPS, SOCKS5}

    private static final List<Pattern> PROXY_PATTERNS = Arrays.asList(
            // 支持 protocol://login:password@host:port 或 login:password@host:port
            Pattern.compile(
                    "^(?:(?<protocol>.+)://)?"
                            + "(?<login>[^@:]+):(?<password>[^@]+)[@:]"
                            + "(?<host>[^@:\\s]+):(?<port>\\d{1,5})"
                            + "(?:\\[(?<refreshUrl>https?://[^\\s\\]]+)\\])?$"
            ),
            // 支持 protocol://host:port@login:password 或 host:port@login:password
            Pattern.compile(
                    "^(?:(?<protocol>.+)://)?"
                            + "(?<host>[^@:\\s]+):(?<port>\\d{1,5})[@:]"
                            + "(?<login>[^@:]+):(?<password>[^@]+)"
                            + "(?:\\[(?<refreshUrl>https?://[^\\s\\]]+)\\])?$"
            ),
            // 支持 protocol://host:port 或 host:port
            Pattern.compile(
                    "^(?:(?<protocol>.+)://)?"
                            + "(?<host>[^@:\\s]+):(?<port>\\d{1,5})"
                            + "(?:\\[(?<refreshUrl>https?://[^\\s\\]]+)\\])?$"
            )
    );

    private final String host;
    private final int port;
    private final Protocol protocol;
    private final String login;
    private final String password;
    private final String refreshUrl;

    private ProxyParser(String host,
                        int port,
                        Protocol protocol,
                        String login,
                        String password,
                        String refreshUrl) {
        validateHost(host);
        validatePort(port);
        this.host = host;
        this.port = port;
        this.protocol = Objects.requireNonNull(protocol, "protocol");
        this.login = login;
        this.password = password;
        this.refreshUrl = refreshUrl;
        if (refreshUrl != null) {
            validateUrl(refreshUrl);
        }
    }

    /**
     * 将像 "http://user:pass@1.2.3.4:1080" 或 "1.2.3.4:1080:user:pass" 这样的字符串解析成 Proxy
     */
    public static ProxyParser fromString(String proxyStr) {
        if (proxyStr == null || ObjectUtils.isEmpty(proxyStr)) {
            throw new IllegalArgumentException("Proxy string cannot be null or empty");
        }
        for (Pattern p : PROXY_PATTERNS) {
            Matcher m = p.matcher(proxyStr.trim());
            if (m.matches()) {
                String proto = m.group("protocol");
                String host = m.group("host");
                String portStr = m.group("port");
                String login = m.group("login");
                String pass = m.group("password");
                // ← 这里改成 refreshUrl
                String refresh = m.group("refreshUrl");

                Protocol protocol = Protocol.HTTP;
                if (proto != null) {
                    switch (proto.toLowerCase()) {
                        case "http":
                            protocol = Protocol.HTTP;
                            break;
                        case "https":
                            protocol = Protocol.HTTPS;
                            break;
                        case "socks5":
                            protocol = Protocol.SOCKS5;
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported protocol: " + proto);
                    }
                }

                int port = Integer.parseInt(portStr);
                return new ProxyParser(host, port, protocol, login, pass, refresh);
            }
        }
        throw new IllegalArgumentException("Unsupported proxy format: " + proxyStr);
    }

    /**
     * 从文本文件中逐行读取代理，每行一个字符串，返回 Proxy 列表
     */
    public static List<ProxyParser> fromFile(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("Proxy file not found: " + filePath);
        }
        List<ProxyParser> result = new ArrayList<>();
        for (String line : Files.readAllLines(filePath)) {
            line = line.strip();
            if (!line.isEmpty()) {
                result.add(fromString(line));
            }
        }
        return result;
    }

    private static void validateHost(String host) {
        try {
            // 如果全是数字和点，就当作 IP 校验
            if (host.matches("[0-9.]+")) {
                InetAddress.getByName(host);
            } else {
                // 当作域名校验，试着用 URL 构造
                new URL("http://" + host);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid host: " + host, e);
        }
    }

    private static void validatePort(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be 1–65535: " + port);
        }
    }

    private static void validateUrl(String url) {
        try {
            new URL(url);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }

    /**
     * 返回类似 "http://user:pass@host:port" 的完整 URL
     */
    public String asUrl() {
        String auth = (login != null && password != null)
                ? (login + ":" + password + "@")
                : "";
        return protocol.name().toLowerCase()
                + "://" + auth + host + ":" + port;
    }

    /**
     * 返回类似 "http://host:port" 的 server 地址
     */
    public String getServer() {
        return protocol.name().toLowerCase() + "://" + host + ":" + port;
    }

    /**
     * 返回一个 Map，key 可包含 "http"、"https"、"socks5"，
     * value 都指向 asUrl()
     */
    public Map<String, String> asProxiesMap() {
        Map<String, String> map = new HashMap<>();
        String url = asUrl();
        switch (protocol) {
            case HTTP:
            case HTTPS:
                map.put("http", url);
                map.put("https", url);
                break;
            case SOCKS5:
                map.put("socks5", url);
                break;
        }
        return map;
    }

    @Override
    public String toString() {
        if (refreshUrl != null) {
            return String.format("Proxy(%s, [%s])", asUrl(), refreshUrl);
        }
        return "Proxy(" + asUrl() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProxyParser)) return false;
        ProxyParser p = (ProxyParser) o;
        return port == p.port
                && protocol == p.protocol
                && Objects.equals(host, p.host)
                && Objects.equals(login, p.login)
                && Objects.equals(password, p.password)
                && Objects.equals(refreshUrl, p.refreshUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, protocol, login, password, refreshUrl);
    }

    // Getter 方法
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRefreshUrl() {
        return refreshUrl;
    }


}
