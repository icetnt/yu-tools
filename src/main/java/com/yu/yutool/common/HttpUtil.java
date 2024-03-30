package com.yu.yutool.common;

import com.yu.yutool.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * HttpUtil
 */
@Slf4j
public class HttpUtil {

    public static CloseableHttpClient commonHttpClient = null;

    private static final Integer TIMEOUT = 3000;
    private static final String CHARSET = "UTF-8";
    private static final String CONTENT_TYPE = "application/json";
    public static PoolingHttpClientConnectionManager cm = null;
    private static ScheduledExecutorService monitorExecutor;

    static {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContexts.custom().loadTrustMaterial((TrustStrategy) (chain, authType) -> true).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("HttpUtil 配置连接池失败", e);
        }
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext,
                new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"}, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslConnectionSocketFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 设置最大连接数
        cm.setMaxTotal(1000);
        // 设置每个连接的路由数
        cm.setDefaultMaxPerRoute(cm.getMaxTotal());
        cm.closeExpiredConnections();
        cm.closeIdleConnections(5, TimeUnit.SECONDS);
//        cleanIdleHttpConnects(cm);
        commonHttpClient = getHttpClient(TIMEOUT);
    }

    public static CloseableHttpClient getHttpClient(Integer timeout) {
        return getHttpClient(timeout, timeout, 600);
    }

    public static CloseableHttpClient getHttpClient(Integer connectTimeout, Integer readTimeout, Integer keepAliveSeconds) {
        // 创建Http请求配置参数
        RequestConfig requestConfig = RequestConfig.custom()
                // 设置从连接池获取连接实例的超时时间
                .setConnectionRequestTimeout(connectTimeout)
                // 设置连接超时
                .setConnectTimeout(connectTimeout)
                // 设置读取超时
                .setSocketTimeout(readTimeout)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return keepAliveSeconds * 1000;
        };
        // 创建httpClient
        return HttpClients.custom()
                // 把请求相关的超时信息设置到连接客户端
                .setDefaultRequestConfig(requestConfig)
                // 配置KeepAlive策略
                .setKeepAliveStrategy(keepAliveStrategy)
                // 配置连接池管理对象
                .setConnectionManager(cm)
                // 定时清理过期连接
                .evictExpiredConnections()
                // 定期清理空闲连接
                .evictIdleConnections(keepAliveSeconds, TimeUnit.SECONDS)
                .build();
    }

    public static void cleanIdleHttpConnects(PoolingHttpClientConnectionManager manager) {
        //开启监控线程,对异常和空闲线程进行关闭
        monitorExecutor = Executors.newScheduledThreadPool(1);
        monitorExecutor.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //关闭异常连接
                manager.closeExpiredConnections();
                //关闭5s空闲的连接
                manager.closeIdleConnections(5000, TimeUnit.MILLISECONDS);
            }
        }, 10000, 5000, TimeUnit.MILLISECONDS);
    }




    /**
     * 发送GET请求
     */
    public static String sendGet(String url, Map<String, String> headers, Map<String, String> parameters, String charset) {
        if (StringUtils.isBlank(charset)) {
            charset = "UTF-8";
        }
        String result = "";

        // 获取客户端连接对象
        String getUrl = getUrl(url, parameters, charset);
        // 创建GET请求对象
        HttpGet httpGet = new HttpGet(getUrl);
        addHeadsToHttpGet(httpGet, headers);

        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = commonHttpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 401 || response.getStatusLine().getStatusCode() == 403 || response.getStatusLine().getStatusCode() == 404) {
                return String.valueOf(response.getStatusLine().getStatusCode());
            }
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            // 获取响应信息
            result = EntityUtils.toString(entity, charset);
        } catch (ClientProtocolException e) {
            log.error("协议错误: {}", e.getMessage());
            throw new BaseException("Http请求失败");
        } catch (ParseException e) {
            log.error("解析错误: {}", e.getMessage());
            throw new BaseException("Http请求失败");
        } catch (IOException e) {
            log.error("IO错误: {}", e.getMessage());
            throw new BaseException("Http请求失败");
        } finally {
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    log.error("释放链接错误: {}", e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * 发送POST请求
     */
    public static String sendPost(String url, Map<String, String> headers, Map<String, String> parameters, String charset) {
        if (StringUtils.isBlank(charset)) {
            charset = "UTF-8";
        }
        String result = "";
        HttpPost httpPost = new HttpPost(url);
        addHeadsToHttpPost(httpPost, headers);

        CloseableHttpResponse response = null;
        int httpCode;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postNameValuePairs(parameters), charset));
            response = commonHttpClient.execute(httpPost);
            httpCode = response.getStatusLine().getStatusCode();
            if (httpCode == 401 || httpCode == 403) {
                return String.valueOf(httpCode);
            }
            if (httpCode < 300) {
                if(httpCode != 204) {
                    HttpEntity entity = response.getEntity();
                    result = EntityUtils.toString(entity, charset);
                }
            }else {
                log.error("sendPost failed, url:{}, httpCode:{}, msg:{}", url, httpCode, response);
                throw new BaseException("http请求失败，code："+ httpCode);
            }
        } catch (ClientProtocolException e) {
            log.error("协议错误: {}", e.getMessage());
            throw new BaseException("Http请求失败");
        } catch (UnsupportedEncodingException e) {
            log.error("解析错误: {}", e.getMessage());
            throw new BaseException("Http请求失败");
        } catch (IOException e) {
            log.error("IO错误: {}", e.getMessage());
            throw new BaseException("Http请求失败");
        } finally {
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    log.error("释放链接错误: {}", e.getMessage());
                }
            }
        }

        return result;
    }

    /**
     * 功能描述：发送获取字节流GET请求
     */
    public static byte[] getRequestBytes(String url, Map<String, String> headers, Map<String, String> parameters, String charset) {
        if (StringUtils.isBlank(charset)) {
            charset = "UTF-8";
        }
        byte[] result = null;
        CloseableHttpResponse response = null;
        try {
            String getUrl = getUrl(url, parameters, charset);
            HttpGet httpGet = new HttpGet(getUrl);
            addHeadsToHttpGet(httpGet, headers);
            response = commonHttpClient.execute(httpGet);

            if (response == null || response.getStatusLine() == null) {
                return null;
            }
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                result = IOUtils.toByteArray(entity.getContent());
            }
        } catch (Exception e) {
            log.error("getRequestBytes failed, url: {}", url, e);
            throw new BaseException("Http请求失败");
        } finally {
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (IOException e) {
                    log.error("释放链接错误: {}", e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * 发送POST请求，传递json
     */
    public static String sendPostWithJson(String url, String json) {
        return sendPostWithJson(url, json, null);
    }

    /**
     * 发送POST请求，传递json
     */
    public static String sendPostWithJson(String url, String json, Map<String, String> headers) {
        HttpPost post = new HttpPost(url);
        String response;
        int httpCode;
        try {
            StringEntity stringEntity = new StringEntity(json, CHARSET);
            stringEntity.setContentEncoding(CHARSET);
            stringEntity.setContentType(CONTENT_TYPE);
            post.setEntity(stringEntity);
            if (ObjectUtils.isNotEmpty(headers)) {
                addHeadsToHttpPost(post, headers);
            }
            HttpResponse httpResponse = commonHttpClient.execute(post);
            if (httpResponse.getStatusLine().getStatusCode() == 401 || httpResponse.getStatusLine().getStatusCode() == 403) {
                return String.valueOf(httpResponse.getStatusLine().getStatusCode());
            }
            HttpEntity entity = httpResponse.getEntity();
            response = EntityUtils.toString(entity, CHARSET);
            httpCode = httpResponse.getStatusLine().getStatusCode();
        } catch (Exception e) {
            log.error("sendPostWithJson failed, url:{}", url, e);
            throw new BaseException(RestResult.FAIL_CODE, "网络不可用，请检查你的网络设置");
        }
        if (httpCode != HttpStatus.SC_OK) {
            log.error("sendPostWithJson failed, url:{}, httpCode:{}, msg:{}", url, httpCode, response);
            throw new BaseException("系统繁忙，请重试, code:" + httpCode + ", resp:" + response);
        }
        return response;
    }

    /**
     * 发送POST请求，传递参数经过url编码
     */
    public static String sendPostWithUrlEncode(String url, String data) {
        HttpPost post = new HttpPost(url);
        String response = null;

        try {
            StringEntity stringEntity = new StringEntity(data, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/x-www-form-urlencoded");
            post.setEntity(stringEntity);
            HttpResponse httpResponse = commonHttpClient.execute(post);
            if (httpResponse.getStatusLine().getStatusCode() == 401 || httpResponse.getStatusLine().getStatusCode() == 403) {
                return String.valueOf(httpResponse.getStatusLine().getStatusCode());
            }
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                response = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (Exception var8) {
            log.error("sendPostWithUrlEncode failed, url:{}", url, var8);
            throw new BaseException("系统繁忙，请重试");
        }

        if (ObjectUtils.isEmpty(response)) {
            throw new BaseException("系统繁忙，请重试");
        } else {
            return response;
        }
    }

    /**
     * HTTP GET 增加 header
     */
    private static void addHeadsToHttpGet(HttpGet httpGet, Map<String, String> headMap) {
        if (headMap == null || headMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            httpGet.addHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * HTTP POST 增加 header
     */
    public static void addHeadsToHttpPost(HttpPost httpPost, Map<String, String> headMap) {
        if (headMap == null || headMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            httpPost.addHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 组织 GET 请求的URL
     */
    private static String getUrl(String url, Map<String, String> parameters, String charset) {
        StringBuffer sb = new StringBuffer();
        if (parameters != null && parameters.size() > 0) {
            for (String name : parameters.keySet()) {
                sb.append(StringUtils.isBlank(sb) ? name : "&" + name);
                if (StringUtils.isNotBlank(parameters.get(name))) {
                    try {
                        sb.append("=").append(URLEncoder.encode(parameters.get(name), charset));
                    } catch (UnsupportedEncodingException e) {
                        log.error("http client unsupportedEncodingException: {}", e.getMessage());
                        throw new BaseException("Http请求失败");
                    }
                }
            }
            url += "?" + sb.toString();
        }
        return url;
    }

    /**
     * HTTP POST 增加键值对参数
     */
    private static List<NameValuePair> postNameValuePairs(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return Collections.emptyList();
        }
        List<NameValuePair> pairs = new ArrayList<>(parameters.size());
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairs.add(pair);
        }
        return pairs;
    }

}
