package com.liantong.spider.task;

import com.liantong.spider.ApplicationTests;
import com.liantong.spider.util.HttpClientUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * author:ZhengXing
 * datetime:2018-04-14 14:27
 */
public class ParsePostTaskTest extends ApplicationTests{

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Test
    public void testRun1() {
//        ParsePostTask a = new ParsePostTask("http://taizhou.19lou.com/forum-2163-thread-52901523454809112-1-1.html", "帖子标题", httpClientUtil);
//        a.run();
    }

}