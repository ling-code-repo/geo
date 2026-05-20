package com.qianyu.module.geo.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.time.Duration;

@Slf4j
public class HttpUtils {

    private static final OkHttpClient OK_HTTP_CLIENT = createClient();

    public static final OkHttpClient httpClient() {
        return OK_HTTP_CLIENT;
    }

    public static OkHttpClient createClient() {
        return createClient(null);
    }

    public static OkHttpClient createClient(String proxyUrl) {
        // 2. OkHttpClient.Builder 基础配置
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(15))
                .callTimeout(Duration.ofSeconds(60));
        try {
            if (StringUtils.isNotBlank(proxyUrl)) {
                URI uri = new URI(ProxyParser.fromString(proxyUrl).asUrl());
                String scheme = uri.getScheme().toLowerCase();
                String host = uri.getHost();
                int port = uri.getPort();
                String userInfo = uri.getUserInfo();

                // 1. 根据 scheme 选择代理类型
                Proxy proxy;
                switch (scheme) {
                    case "http":
                    case "https":  // HTTPS 代理，行为上跟 HTTP 代理类似，但要走 TLS
                        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
                        break;
                    case "socks":
                    case "socks5":
                        proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port));
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported proxy protocol: " + scheme);
                }
                builder = builder.proxy(proxy);
                // 3. 处理代理认证（如果 URL 包含 user:pass）
                if (userInfo != null && !userInfo.isEmpty()) {
                    String credential = Credentials.basic(
                            userInfo.split(":")[0],
                            userInfo.split(":", 2)[1]
                    );
                    builder.proxyAuthenticator((route, response) -> {
                        if (response.request().header("Proxy-Authorization") != null) {
                            return null; // avoid infinite loop
                        }
                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credential)
                                .build();
                    });
                }

                // 4. 如果是 HTTPS 代理，给代理连接做 TLS 绕过（等同于 Python 中 ssl=False）
                if ("https".equals(scheme)) {
                    // 信任所有证书
                    TrustManager[] trustAll = new TrustManager[]{
                            new X509TrustManager() {
                                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                                }

                                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                                }

                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return new java.security.cert.X509Certificate[0];
                                }
                            }
                    };
                    SSLContext sslCtx = SSLContext.getInstance("TLS");
                    sslCtx.init(null, trustAll, new SecureRandom());

                    builder.sslSocketFactory(sslCtx.getSocketFactory(), (X509TrustManager) trustAll[0])
                            .hostnameVerifier((hostname, session) -> true);
                }
            }
        } catch (Exception e) {
            log.error("实例化http失败！", e);
        }
        return builder.build();
    }
}
