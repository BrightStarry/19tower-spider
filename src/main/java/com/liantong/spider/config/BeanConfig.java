package com.liantong.spider.config;

import com.liantong.spider.task.ParsePostTask;
import com.liantong.spider.util.HttpClientUtil;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;

/**
 * author:ZhengXing
 * datetime:2018-04-13 18:09
 * spring bean 配置
 */
@Configuration
public class BeanConfig {

    /**
     * httpClient 工具类
     */
    @Bean
    public HttpClientUtil httpClientUtil(SpiderConfig config) {

        /**
         * 自定义headers
         * 根据{@link HttpClientUtil#CHROME_HEADERS}扩展
         *
         * 访问 帖子 页面需要cookie 和 host 参数
         */
        HttpClientUtil.DefaultHttpClientConfig httpClientConfig = config.getHttpClient();
        LinkedList<Header> headers = new LinkedList<>();
        headers.addAll(HttpClientUtil.CHROME_HEADERS);
        headers.add(new BasicHeader("Cookie",
                "_DM_SID_=256fa87c5dc3bcab5e9226e8d6cc572a; _Z3nY0d4C_=37XgPK9h; bdshare_firstime=1523609461325; JSESSIONID=2226153368D2681EF6EBCA1B13C522E3; f19big=ip48; _DM_S_=941d8cd734603b557d4cfacfa1c9a454; reg_source=taizhou.19lou.com; reg_first=http%253A//marry.taizhou.19lou.com/; screen=1895; fr_adv=bbs_huatan_ck; reg_step=12; _dm_tagnames=%5B%7B%22k%22%3A%22%E6%B8%B8%E8%AE%B0%E6%94%BB%E7%95%A5%22%2C%22c%22%3A3%7D%2C%7B%22k%22%3A%22%E6%B8%B8%E8%AE%B0%22%2C%22c%22%3A3%7D%2C%7B%22k%22%3A%22%E6%B3%B8%E6%B2%BD%E6%B9%96%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E4%B8%BD%E6%B1%9F%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E5%A4%A7%E7%90%86%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E7%8E%89%E9%BE%99%E9%9B%AA%E5%B1%B1%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E5%8F%8C%E5%BB%8A%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E6%96%B0%E5%A8%98%E6%97%A5%E8%AE%B0%22%2C%22c%22%3A30%7D%2C%7B%22k%22%3A%22%E5%A9%9A%E7%BA%B1%22%2C%22c%22%3A27%7D%2C%7B%22k%22%3A%22%E6%97%A0%E9%94%A1%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E6%A8%B1%E8%8A%B1%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E9%BC%8B%E5%A4%B4%E6%B8%9A%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E6%A8%B1%E8%8A%B1%E8%B0%B7%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E4%B8%8A%E6%B5%B7%E6%B8%B8%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E5%91%A8%E5%BA%84%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E5%A4%96%E6%BB%A9%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E4%B8%80%E5%8F%AA%E9%B1%BC%E7%9A%84%E6%97%85%E8%A1%8C%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E6%88%91%E8%A6%81%E8%AF%B4%E7%9A%84%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E9%9A%8F%E6%89%8B%E6%8B%8D%22%2C%22c%22%3A1%7D%2C%7B%22k%22%3A%22%E5%8F%B0%E5%B7%9E%E7%BB%93%E5%A9%9A%E5%9B%A2%E8%B4%AD%22%2C%22c%22%3A3%7D%5D; pm_count=%7B%22pc_allCity_threadView_button_adv_190x205_1%22%3A65%2C%22pc_allCity_threadlist_streamer_adv_980x60_1%22%3A88%7D; dayCount=%5B%7B%22id%22%3A78784%2C%22count%22%3A5%7D%2C%7B%22id%22%3A106670%2C%22count%22%3A1%7D%5D; fr_adv_last=merry_thread_pc"));
        headers.add(new BasicHeader("Host", "taizhou.19lou.com"));
        httpClientConfig.getCustomHeaders().put(ParsePostTask.DEFAULT_HEADER_KEY, headers);

        return new HttpClientUtil(httpClientConfig);
    }
}
