package com.liantong.spider.controller;

import com.liantong.spider.entity.MatchPost;
import com.liantong.spider.exception.CustomException;
import com.liantong.spider.form.DeleteSpiderTaskAndAllPostForm;
import com.liantong.spider.form.InterruptSpiderTaskForm;
import com.liantong.spider.form.PageForm;
import com.liantong.spider.form.StartSpiderTaskForm;
import com.liantong.spider.service.MatchPostService;
import com.liantong.spider.service.SpiderTaskService;
import com.liantong.spider.vo.PageVO;
import com.liantong.spider.vo.ResultVO;
import com.liantong.spider.vo.SpiderTaskVO;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * author:ZhengXing
 * datetime:2018-04-15 10:06
 */
@Controller
@RequestMapping("/")
public class MainController implements ControllerPlus{

    private final SpiderTaskService spiderTaskService;
    private final MatchPostService matchPostService;

    public MainController(SpiderTaskService spiderTaskService, MatchPostService matchPostService) {
        this.spiderTaskService = spiderTaskService;
        this.matchPostService = matchPostService;
    }


    /**
     * 进入首页
     */
    @GetMapping("/")
    public String indexView(@Valid PageForm pageForm, BindingResult bindingResult, Model model) {
        isValid(bindingResult);
        // 分页查询所有记录数
        PageVO<SpiderTaskVO> spiderTaskPageVO = spiderTaskService.listSpiderTaskByPage(buildPageable(pageForm.getPageNo(), pageForm.getPageSize()));
        model.addAttribute("spiderTaskPageVO", spiderTaskPageVO);
        return "index";
    }

    /**
     * 开启新任务
     */
    @PostMapping("/start")
    @ResponseBody
    public ResultVO startSpiderTask( StartSpiderTaskForm form, BindingResult bindingResult) {
        isValid(bindingResult);
        spiderTaskService.startTask(form.getKeyword());
        return ResultVO.success();
    }

    /**
     * 中断任务
     */
    @PostMapping("/interrupt")
    @ResponseBody
    public ResultVO interruptSpiderTask(InterruptSpiderTaskForm form,BindingResult bindingResult){
        isValid(bindingResult);
        spiderTaskService.interruptSpiderTask(form.getSpiderTaskId());
        return ResultVO.success();
    }

    /**
     * 某任务的 匹配帖子列表页
     */
    @GetMapping("/{spiderTaskId}/post/list")
    public String matchPostListBySpiderTaskIdView(@PathVariable Long spiderTaskId, @Valid PageForm form, BindingResult bindingResult, Model model) {
        isValid(bindingResult);
        if(spiderTaskId == null)
            throw new CustomException("该任务不存在");

        // 帖子列表
        PageVO<MatchPost> matchPostPageVO = matchPostService.listMatchPostByPage(buildPageable(form.getPageNo(), form.getPageSize()), spiderTaskId);
        matchPostPageVO.setServiceId(spiderTaskId);
        model.addAttribute("matchPostPageVO", matchPostPageVO);

        // 该任务信息
        SpiderTaskVO spiderTaskVO = spiderTaskService.selectSpiderTaskVOById(spiderTaskId);
        model.addAttribute("spiderTask", spiderTaskVO);

        return "matchPostList";
    }

    /**
     * 删除某爬虫任务和其下所有帖子记录
     */
    @PostMapping("/deleteTask")
    @ResponseBody
    public ResultVO deleteSpiderTaskAndAllPost(DeleteSpiderTaskAndAllPostForm form, BindingResult bindingResult) {
        isValid(bindingResult);
        spiderTaskService.deleteOne(form.getSpiderTaskId());
        return ResultVO.success();
    }
}
