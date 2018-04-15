package com.liantong.spider.task;

import com.liantong.spider.config.SpiderConfig;
import com.liantong.spider.dto.Circle;
import com.liantong.spider.queue.SpiderQueue;
import com.liantong.spider.util.HtmlResolver;
import com.liantong.spider.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import java.util.Collections;

/**
 * author:ZhengXing
 * datetime:2018-04-13 18:17
 * 获取 {@link Circle 圈子} 任务
 * 圈子列表页: http://taizhou.19lou.com/board/list-1.html
 *
 * 该任务因为总页数不多,直接单线程运行
 *
 * 该网站必须模拟谷歌浏览器的请求头， 否则会返回403权限异常的html
 * Host 和 cookies 请求头可以不需要
 */
@Slf4j
public class GetCircleTask implements Runnable{

    private static final String LOG = "[获取圈子任务]";

    /**
     * 圈子列表页 路径
     * %d 在{@link String#format(String, Object...)}用页码替换
     */
    private String listPath =  "http://taizhou.19lou.com/board/list-%d.html";

    /**
     * 最大页数
     * 当前该网站的最大页数限定死为30，如果超过30，返回的还是第30页的内容
     */
    private Integer maxPage = 30;

    /**
     * http请求工具类
     */
    private final HttpClientUtil httpClientUtil;

    /**
     * 队列
     */
    private final SpiderQueue spiderQueue;

    public GetCircleTask(HttpClientUtil httpClientUtil, SpiderQueue spiderQueue) {
        this.httpClientUtil = httpClientUtil;
        this.spiderQueue = spiderQueue;
    }


    @Override
    public void run() {
        try {
            run1();
        } catch (Exception e) {
            log.error("{}异常:{}",LOG,e);
        }
    }

    /**
     * 主方法
     */
    private void run1() {
        // 循环每一页
        for (int i = 1; i <= maxPage; i++) {
            try {
                // 获取html
                String html = getHtml(i);
                // 为空时跳过
                if(StringUtils.isEmpty(html))
                    continue;
                // 从列表页中抽取出 圈子 ,放入队列
                getCircle(html);
            } catch (Exception e) {
                log.error("{}当前页:{},循环页面异常:{}",LOG,i,e);
            }
        }
    }


    /**
     * 从列表页中抽取出 圈子 ,放入队列
     */
    private void getCircle(String html) {
        // 获取其中的列表主体 ，是一个<ul>
        Element body = HtmlResolver.getElement(html, "#board-wrap > ul");
        // 循环每个<li>
        for (Element element : body.children()) {
            try {
                // 圈子标题
                String title = HtmlResolver.getElementAttr(element, "div > div.board-hd > h2 > a", "title");
                // 圈子路径, href中的是 "//taizhou.19lou.com/r/37/rd.html" 这样的路径.
                String path = "http:" + HtmlResolver.getElementAttr(element, "div > div.board-hd > h2 > a", "href");
                // 圈子
                Circle circle = new Circle(title, pathConvert(path));
                log.info("{}加入队列:{}",LOG,circle);
                // 入队
                spiderQueue.putToCircleQueue(circle);
            } catch (Exception e) {
                log.error("{}循环li异常:{}",LOG,e);
            }
        }
    }

    /**
     * 路径转换
     * http://taizhou.19lou.com/r/37/dsjlbdhd.html 转  http://taizhou.19lou.com/r/37/dsjlbdhd-%d.html
     */
    private  String pathConvert(String path) {
        return StringUtils.substringBeforeLast(path, ".") + "-%d.html";
    }

    /**
     * 获取整个页面的html
     */
    private String getHtml(int i) {
        String currentPath = String.format(listPath,i);
        String html = null;
        try {
            html = httpClientUtil.doGetByChrome(currentPath);
        } catch (Exception e) {
            log.error("{}当前页:{},当前路径:{},http请求异常:{}",LOG,i,currentPath,e.getMessage());
        }
        return html;
    }

//    public static void main(String[] args) {
//        HttpClientUtil.DefaultHttpClientConfig config = new HttpClientUtil.DefaultHttpClientConfig();
//        config.setCustomCookieKeys(Collections.singletonList("a"));
//        HttpClientUtil httpClientUtil = new HttpClientUtil(null);
//        GetCircleTask task = new GetCircleTask(httpClientUtil, new SpiderQueue(new SpiderConfig()));
//        task.run1();
//    }
}
