package com.liantong.spider.util;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * author:ZhengXing
 * datetime:2018/3/21 0021 09:51
 */
@Slf4j
public class HttpClientUtil {
    private static final String LOG = "[HttpClientUtil]";
    private static final String EMPTY = "";

    // 请求头-------------------------------
    // 谷歌浏览器请求头
    public static final List<Header> CHROME_HEADERS = Arrays.asList(
            new BasicHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36"),
            new BasicHeader("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"),
            new BasicHeader("Accept-Encoding",
                    "gzip, deflate"),
            new BasicHeader("Connection",
                    "keep-alive"),
            new BasicHeader("Accept-Language",
                    "zh-CN,zh;q=0.9"),
            new BasicHeader("Upgrade-Insecure-Requests",
                    "1"),
            new BasicHeader("Cache-Control",
                    "max-age=0")
    );


    // 属性-------------------

    /**
     * 连接池
     */
    private PoolingHttpClientConnectionManager pool = null;

    /**
     * 默认请求配置
     */
    private RequestConfig requestConfig;

    /**
     * 默认的cookie管理器
     */
    private Map<String, CookieStore> cookieStoreManager = new HashMap<>();

    /**
     * 默认的header管理器
     */
    private Map<String, List<Header>> headerManager = new HashMap<>();


    // 完整的请求方法----------------

    /**
     * 发起GET请求，并返回String，使用 {@link HeaderKey#CHROME_HEADERS}
     */
    public String doGetByChrome(String uri) throws Exception {
        return doGet(uri, null, null, HeaderKey.CHROME_HEADERS.getCode());
    }

    /**
     * 发起GET请求，并返回String，使用 {@link HeaderKey#CHROME_HEADERS} 和 一个默认的cookie
     */
    public String doGetByChromeAndCookie(String uri) throws Exception {
        return doGet(uri, null, CookieKey.DEFAULT.getCode(), HeaderKey.CHROME_HEADERS.getCode());
    }

    /**
     * 发起GET请求，并返回String
     */
    public <T> String doGet(String uri, T obj, String cookieKey, String headerKey) throws Exception {
        HttpGet httpGet;
        if (obj == null) {
            httpGet = buildHttpGet(uri);
        } else {
            httpGet = buildHttpGet(uri, obj);
        }
        return doRequestForString(getHttpClient(cookieKey, headerKey), httpGet);

    }

    /**
     * 发起GET请求，返回String
     */
    public <T> String doGet(String uri) throws Exception {
        return doRequestForString(getHttpClient(), buildHttpGet(uri));

    }

    /**
     * 发起POST请求，返回String
     */
    public <T> String doPost(String uri, T obj, String cookieKey, String headerKey) throws Exception {
        return doRequestForString(getHttpClient(cookieKey, headerKey), buildHttpPost(uri, obj));

    }

    /**
     * 发起POST请求，返回String,Json
     */
    public String doPost(String uri, String jsonStr, String cookieKey, String headerKey) throws Exception {
        return doRequestForString(getHttpClient(cookieKey, headerKey), buildHttpPost(uri, jsonStr));

    }


    // 构造HttpUriRequest相关----------------------------------------------------

    /**
     * 构造 {@link HttpGet}
     */
    public HttpGet buildHttpGet(String uri, Iterable<? extends NameValuePair> params) throws Exception {
        String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
        return new HttpGet(uri.concat("?").concat(paramsStr));
    }

    /**
     * 构造 {@link HttpGet}
     */
    public <T> HttpGet buildHttpGet(String uri, T obj) throws Exception {
        return buildHttpGet(uri, mapToList(beanToMap(obj)));
    }

    /**
     * 构造 {@link HttpGet}
     */
    public HttpGet buildHttpGet(String uri) {
        return new HttpGet(uri);
    }

    /**
     * 构造 {@link HttpPost}, 使用 {@link Iterable <? extends NameValuePair>}
     */
    public HttpPost buildHttpPost(String uri, Iterable<? extends NameValuePair> params) {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
        return httpPost;
    }

    /**
     * 构造 {@link HttpPost}, 使用 {@link T}Bean
     */
    public <T> HttpPost buildHttpPost(String uri, T obj) throws Exception {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new UrlEncodedFormEntity(mapToList(beanToMap(obj)), Consts.UTF_8));
        return httpPost;
    }

    /**
     * 构造 {@link HttpPost}, JSON
     */
    public HttpPost buildHttpPost(String uri, String jsonStr) {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(jsonStr, ContentType.APPLICATION_JSON));
        return httpPost;
    }

    /**
     * 构造 {@link org.apache.http.protocol.HttpContext},用于记录某个请求的上下文,包括重定向后的真实路径
     */
    public HttpContext buildHttpContext() {
        return new BasicHttpContext();
    }

    // 发起请求相关方法-----------------------------

    /**
     * 发起请求,返回String
     */
    public String doRequestForString(CloseableHttpClient httpClient, HttpUriRequest request) throws IOException {
        // 注意,try-with-resources语法的赋值语句中如果抛出异常,将不会被捕获
        try (CloseableHttpResponse response = doRequestForResponse(httpClient, request)) {
            // 此处不做校验是因为当前 应用为了最大并发，不关注异常
//			if(!isRequestSuccess(response))
//				throw new RuntimeException(LOG  + "请求失败.当前状态码：" + response.getStatusLine().getStatusCode() +  ",当前路径：" + request.getURI());
            return getStringResultByResponse(response);
        }
    }


    /**
     * 发起请求,返回{@link org.apache.http.client.methods.CloseableHttpResponse}
     */
    public CloseableHttpResponse doRequestForResponse(CloseableHttpClient httpClient, HttpUriRequest request) throws IOException {
        return httpClient.execute(request);
    }


    // 返回结果处理相关方法------------------------

    /**
     * {@link CloseableHttpResponse} 提取String返回结果
     */
    public String getStringResultByResponse(CloseableHttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    /**
     * 判断是否请求成功,根据{@link CloseableHttpResponse}
     */
    public boolean isRequestSuccess(CloseableHttpResponse response) {
        return response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    /**
     * 关闭{@link CloseableHttpResponse}
     */
    @SneakyThrows
    public void closeResponse(CloseableHttpResponse response) {
        if (response != null)
            response.close();
    }


    // 获取HttpClient--------------------------

    /**
     * 获取HttpClient
     */
    public CloseableHttpClient getHttpClient(String cookieKey, String headerKey) {

        HttpClientBuilder builder = HttpClients.custom()
                // 设置连接池
                .setConnectionManager(pool)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig);
        // 设置CookieStore
        if (StringUtils.isNotEmpty(cookieKey))
            builder.setDefaultCookieStore(cookieStoreManager.get(cookieKey));
        // 设置Headers
        if (StringUtils.isNotEmpty(headerKey))
            builder.setDefaultHeaders(headerManager.get(headerKey));
        return builder.build();
    }

    /**
     * 获取HttpClient
     */
    public CloseableHttpClient getHttpClientByCookieKey(String cookieKey) {
        return getHttpClient(cookieKey, null);
    }

    /**
     * 获取HttpClient
     */
    public CloseableHttpClient getHttpClientByHeadereKey(String headerKey) {
        return getHttpClient(null, headerKey);
    }

    /**
     * 获取HttpClient
     */
    public CloseableHttpClient getHttpClient() {
        return getHttpClient(null, null);
    }

    // 构造/初始化方法等-----------------------------------------

    /**
     * 构造方法
     */
    public HttpClientUtil(HttpClientConfigurable config) {
        if (config == null)
            config = new DefaultHttpClientConfig();
        try {
            initPool(config);
            initDefaultRequestConfig(config);
            initCookie(config);
            initHeader(config);
        } catch (Exception e) {
            // 失败直接停止进程
            throw new Error(LOG + "初始化异常" + e.getMessage());
        }
    }


    /**
     * 初始化连接池
     */
    private void initPool(HttpClientConfigurable config) throws Exception {
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                sslContextBuilder.build());
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", socketFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        pool.setMaxTotal(config.getMaxConnectionNum());
        pool.setDefaultMaxPerRoute(config.getMaxPerRoute());
    }

    /**
     * 初始化默认请求配置
     */
    private void initDefaultRequestConfig(HttpClientConfigurable config) {
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(config.getSocketTimeout())
                .setConnectionRequestTimeout(config.getConnectionRequestTimeout())
                .setConnectTimeout(config.getConnectionTimeout())
                .setCookieSpec(CookieSpecs.DEFAULT)
                .build();
    }

    /**
     * 初始化cookieStore
     */
    private void initCookie(HttpClientConfigurable config) {
        /**
         * 导入{@link CookieKey}中除了{@link CookieKey#NONE}外的所有属性
         */
        for (CookieKey item : CookieKey.values()) {
            if (!item.equals(CookieKey.NONE))
                cookieStoreManager.put(item.getCode(), new BasicCookieStore());
        }
        /**
         * 导入自定义cookie
         */
        if (!CollectionUtils.isEmpty(config.getCustomCookieKeys()))
            for (String item : config.getCustomCookieKeys()) {
                cookieStoreManager.put(item, new BasicCookieStore());
            }
    }

    /**
     * 初始化header
     */
    private void initHeader(HttpClientConfigurable config) {
        if (!CollectionUtils.isEmpty(config.getCustomHeaders()))
            headerManager.putAll(config.getCustomHeaders());
        /**
         * 加载  {@link HeaderKey#CHROME_HEADERS}
         */
        headerManager.put(HeaderKey.CHROME_HEADERS.getCode(),
                CHROME_HEADERS);

    }


    // 其他方法--------------------------

    /**
     * bean 转 {@link Map<String,String>}
     */
    private <T> Map<String, String> beanToMap(T obj) throws Exception {
        Map<String, String> map = BeanUtils.describe(obj);
        //会产生key为class的元素
        map.remove("class");
        return map;
    }

    /**
     * {@link Map<String,String>} 转 {@link List<NameValuePair>}
     */
    private List<NameValuePair> mapToList(Map<String, String> map) {
        LinkedList<NameValuePair> result = new LinkedList<>();
        if (CollectionUtils.isEmpty(map))
            return result;
        map.forEach((k, v) -> result.add(new BasicNameValuePair(k, v)));
        return result;
    }

    // 该类使用的枚举/接口等--------------------------------

    /**
     * 默认 cookieStore  Key 枚举
     */
    @AllArgsConstructor
    @Getter
    public enum CookieKey {
        NONE("none", "不保存/共享cookie"),
        DEFAULT("default", "默认cookie"),;
        private String code;
        private String message;
    }

    /**
     * 默认 Header key 枚举
     */
    @AllArgsConstructor
    @Getter
    public enum HeaderKey {
        NONE("none", "不添加header"),
        // 可传入key为default的自定义header.来使用
        DEFAULT("default", "默认header"),
        CHROME_HEADERS("chromeHeaders", "模拟谷歌浏览器的请求头");
        private String code;
        private String message;
    }

    /**
     * httpClient可配置接口
     */
    public interface HttpClientConfigurable {
        /**
         * 最大连接数
         */
        Integer getMaxConnectionNum();

        /**
         * 最大连接路由
         */
        Integer getMaxPerRoute();

        /**
         * socket超时时间
         */
        Integer getSocketTimeout();

        /**
         * 从连接池中获取连接超时时间
         */
        Integer getConnectionRequestTimeout();

        /**
         * 连接超时时间(总?)
         */
        Integer getConnectionTimeout();

        /**
         * 自定义cookieKeys
         */
        List<String> getCustomCookieKeys();

        /**
         * 自定义Header
         */
        Map<String, List<Header>> getCustomHeaders();
    }


    /**
     * 默认的 {@link HttpClientConfigurable}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class DefaultHttpClientConfig implements HttpClientConfigurable {
        private Integer maxConnectionNum = 300;
        private Integer maxPerRoute = Integer.MAX_VALUE;
        private Integer socketTimeout = 2000;
        private Integer connectionRequestTimeout = 2000;
        private Integer connectionTimeout = 2000;
        private List<String> customCookieKeys = new LinkedList<>();
        private Map<String, List<Header>> customHeaders = new HashMap<>();
    }
}
