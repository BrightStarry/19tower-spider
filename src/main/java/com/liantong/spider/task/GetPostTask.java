package com.liantong.spider.task;

import com.liantong.spider.dto.Post;
import com.liantong.spider.queue.SpiderQueue;
import com.liantong.spider.util.HtmlResolver;
import com.liantong.spider.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * author:ZhengXing
 * datetime:2018-04-13 19:24
 * 获取 {@link com.liantong.spider.dto.Post 帖子} 任务
 *
 * 每个圈子的帖子列表页格式: http://taizhou.19lou.com/r/37/{postName}-{pageNo}.html
 * 其中{postName}是圈子名,{pageNo}为页码. 如果为第一页,可以直接输入http://taizhou.19lou.com/r/37/{postName}.html
 *
 * 每个该任务对象,异步解析 同一个圈子的所有帖子列表
 */
@Slf4j
public class GetPostTask implements Runnable{

    private static final String LOG = "[获取帖子任务]";

    /**
     * 当前任务格式化路径
     */
    private String path;

    /**
     * 当前圈子名字
     */
    private String circleName;

    /**
     * http请求工具类
     */
    private final HttpClientUtil httpClientUtil;

    /**
     * 队列
     */
    private final SpiderQueue spiderQueue;

    public GetPostTask(String path, String circleName,HttpClientUtil httpClientUtil, SpiderQueue spiderQueue) {
        this.path = path;
        this.circleName = circleName;
        this.httpClientUtil = httpClientUtil;
        this.spiderQueue = spiderQueue;
    }


    @Override
    public void run() {
        try {
            run1();
        } catch (Exception e) {
            log.error("{}异常:",LOG,e);
        }
    }

    /**
     * 主方法
     */
    private void run1() {
        /**
         * 要先在首页通过最后一页按钮的属性,解析出 该圈子帖子列表的总页数. 因为该网站如果 输入页数 大于 最大页数, 也会返回最后一页内容.
         */
        int maxPage = getMaxPage();


        String html;
        String currentPath = "";
        // 循环解析每一页
        for (int i = 1; i <= maxPage ; i++) {
            try {
                 currentPath  = String.format(path,i);
                 html = httpClientUtil.doGetByChrome(currentPath);
                // 主体元素
                Element body = HtmlResolver.getElement(html, "#J_itemFeedWrap");
                // 帖子窗口元素 集合
                Elements postElements = HtmlResolver.getElements(body, ".J_item");
                // 循环每个帖子
                for (Element postElement : postElements) {
                    // 标题
                    String postTitle = HtmlResolver.getElementText(postElement, "div.item-hd > h3");
                    /**
                     * 该路径有两种格式
                     * 1. http://taizhou.19lou.com/forum-827-thread-52631523575526488-1-1.html 直接到帖子首页1楼
                     * 2. http://taizhou.19lou.com/redirect/post?fid=834&tid=52471515224776506&pid=72841520060886835 会重定向到帖子某页某楼
                     * 此处都先直接加入队列, 在解析任务中处理
                     */
                    String path = postElement.attr("data-url");
                    Post post = new Post(postTitle, path);
                    //路径中不包含"board",才入队
                    if(!path.contains("board")){
                        log.info("{}加入队列:{}",LOG,post);
                        spiderQueue.putToPostQueue(post);
                    }
                }
            } catch (Exception e) {
                log.error("{}当前页:{},当前路径:{},异常:{}",LOG,i,currentPath,e);
            }
        }
    }

    /**
     * 获取 最大页数
     */
    private int getMaxPage() {
        try {
            String html = httpClientUtil.doGetByChrome(String.format(path, 1));
            // 最后一页 按钮元素
            Elements lastPageButton = HtmlResolver.getElements(html, "#J_pageWrap > div > a.page-last");
            // 获取最大页数
            if(lastPageButton.size() == 0)
                return 1;
            return  Integer.parseInt(lastPageButton.attr("data-page"));

            // 此处为了暂不解析第一页内容
        } catch (Exception e) {
            log.error("{}当前圈子:{},帖子列表页首页,解析异常:{}",LOG,circleName,e);
            return 1;
        }
    }

//    public static void main(String[] args) {
//        GetPostTask getPostTask = new GetPostTask("http://taizhou.19lou.com/r/37/hlxcch-%d.html", "aaa", new HttpClientUtil(null), new SpiderQueue(new SpiderConfig()));
//        getPostTask.run1();
//    }
}
