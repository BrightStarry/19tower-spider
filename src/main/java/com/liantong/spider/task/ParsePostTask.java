package com.liantong.spider.task;

import com.liantong.spider.config.SpiderConfig;
import com.liantong.spider.entity.MatchPost;
import com.liantong.spider.repository.MatchPostRepository;
import com.liantong.spider.util.HtmlResolver;
import com.liantong.spider.util.HttpClientUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2018-04-14 0:20
 * 解析 {@link com.liantong.spider.dto.Post 帖子} 任务
 *
 * 路径规则为: http://taizhou.19lou.com/forum-832-thread-52131523600673566-{pageNo}-1.html
 * 或: http://taizhou.19lou.com/board-36471450848933557-thread-49981523263456283-1.html
 * 如果为后者,直接抛弃,因为只有一个 招聘什么的圈子是这个格式的, 并且基本没有评论, 也不知道每一层的规则
 * 还有一种是: http://taizhou.19lou.com/forum-834-thread-52471515224776506-4-1.html#72841520060886835
 * 当Post的路径是需要重定向的路径,会重定向为这样的路径表示某页某楼(锚点)(在通过代码获取后, #及后面的内容不会被包含)
 *
 * {pageNo} 表示当前页码
 *
 */
@Slf4j
@Component
@NoArgsConstructor
public class ParsePostTask implements Runnable{
    private static final String LOG = "[解析帖子任务]";
    // httpClientUtil中header的key
    public static final String DEFAULT_HEADER_KEY = "postHeader";

    /**
     * 关键词数组
     */
    private String[] keywordArray;

    private static MatchPostRepository matchPostRepository;

    @Autowired
    public void init(MatchPostRepository matchPostRepository) {
        ParsePostTask.matchPostRepository = matchPostRepository;
    }

    /**
     * 当前帖子路径
     * 1. http://taizhou.19lou.com/forum-832-thread-52131523600673566-{pageNo}-1.html
     * 2. http://taizhou.19lou.com/redirect/post?fid=834&tid=52471515224776506&pid=72841520060886835
     */
    private String path;

    /**
     * 当前帖子标题
     */
    private String title;

    /**
     * http请求工具类
     */
    private  HttpClientUtil httpClientUtil;

    /**
     * 任务id
     */
    private Long taskId;

    public ParsePostTask(String path, String title, HttpClientUtil httpClientUtil,Long taskId,String[] keywordArray) {
        this.path = path;
        this.title = title;
        this.httpClientUtil = httpClientUtil;
        this.taskId = taskId;
        this.keywordArray = keywordArray;
    }

//    public static void main(String[] args) {
////        http://taizhou.19lou.com/redirect/post?fid=834&tid=52471515224776506&pid=72841520060886835
////        http://taizhou.19lou.com/forum-829-thread-163211403098896703-1-1.html
//        ParsePostTask t = new ParsePostTask("http://taizhou.19lou.com/forum-829-thread-163211403098896703-1-1.html",
//                "xxx", new HttpClientUtil(null),1L,new String[]{"xx"});
//        t.run1();
//
//    }

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
        // 获取实际路径
        String realUri = getRealUri();
        // 格式化路径
        String formatUri = convertPath(realUri);
        // 获取总页数
        int maxPage = getMaxPage(formatUri);

        String currentPath = null;
        for (int i = 1; i <= maxPage ; i++) {
            try {
                currentPath = String.format(formatUri, i);
                String html = httpClientUtil.doGet(currentPath, null, null, DEFAULT_HEADER_KEY);
                // 所有楼(回复)集合
                Elements towers = HtmlResolver.getElements(html, "#view-bd > .clearall.floor");
                //循环每一楼
                for (Element tower : towers) {
                    // 回复内容
                    String content = HtmlResolver.getElementText(tower, ".thread-cont");
                    // 匹配每个关键词, 如果包含
                    for (String keyword : keywordArray) {
                        if(content.contains(keyword)){
                            log.info("{}匹配成功");
                            // 入库
                            MatchPost matchPost = new MatchPost(title, currentPath, content,keyword,taskId);
                            matchPostRepository.save(matchPost);
                            // 只退出当前页,继续该帖子下一页的关键词匹配
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                log.error("{}当前路径:{},解析第{}页失败,异常:{}",LOG,currentPath,i,e);
            }
        }


    }

    /**
     * 请求该路径,获取 获取请求的真实路径(防止重定向)
     *
     * 通过HttpClient的 {@link HttpContext}对象
     *
     * 例如 http://taizhou.19lou.com/redirect/post?fid=834&tid=52471515224776506&pid=72841520060886835
     * 会返回 http://taizhou.19lou.com/forum-834-thread-52471515224776506-4-1.html
     * 而浏览器实际访问则是 http://taizhou.19lou.com/forum-834-thread-52471515224776506-4-1.html#72841520060886835
     */
    private String getRealUri() {
        HttpGet httpGet = httpClientUtil.buildHttpGet(path);
        CloseableHttpClient httpClient = httpClientUtil.getHttpClient(null, HttpClientUtil.HeaderKey.CHROME_HEADERS.getCode());
        HttpContext httpContext = httpClientUtil.buildHttpContext();
        try {
            httpClient.execute(httpGet, httpContext);
            HttpUriRequest realRequest = (HttpUriRequest)httpContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
            return realRequest.getURI().getQuery();
        } catch (IOException e) {
            log.error("{}当前帖子名:{},当前路径:{},获取真实路径失败,异常:{}",LOG,title,path,e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * 将路径转换为 格式化路径
     * 例如 http://taizhou.19lou.com/forum-834-thread-52471515224776506-4-1.html 转 http://taizhou.19lou.com/forum-834-thread-52471515224776506-%d-1.html
     */
    private String convertPath(String uri) {
        int i = StringUtils.lastIndexOf(uri, "-");
        return uri.substring(0,i-1).concat("%d-1.html");
    }

    /**
     * 获取总页数
     */
    private int getMaxPage(String formatUri) {
        try {
            String html = httpClientUtil.doGet(String.format(formatUri, 1), null, null, DEFAULT_HEADER_KEY);
            //  获取最后一页按钮中的href属性
            String href = HtmlResolver.getElementAttr(html, "a.page-last", "href");
            String href2 = href.substring(0, href.length() - 7);
            String href3 = href2.substring(href2.lastIndexOf("-")+1);
            return Integer.parseInt(href3);
        } catch (Exception e) {
//            log.error("{}当前路径:{},获取总页数异常:{}",LOG,formatUri,e.getMessage());
            return 1;
        }
    }

}
