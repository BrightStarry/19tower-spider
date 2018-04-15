package com.liantong.spider.convertor;

import com.liantong.spider.entity.SpiderTask;
import com.liantong.spider.enums.SpiderTaskStatusEnum;
import com.liantong.spider.util.EnumUtil;
import com.liantong.spider.vo.PageVO;
import com.liantong.spider.vo.SpiderTaskVO;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author:ZhengXing
 * datetime:2018-04-15 10:48
 * {@link com.liantong.spider.entity.SpiderTask} 转 {@link com.liantong.spider.vo.SpiderTaskVO}
 */
public class SpiderTask2SpiderTaskVOConvertor {

    public static PageVO<SpiderTaskVO> convert(PageVO<SpiderTask> pageVO) {
        PageVO<SpiderTaskVO> result = new PageVO<>();
        BeanUtils.copyProperties(pageVO, result);
        return result.setList(convert(pageVO.getList()));
    }

    public static List<SpiderTaskVO> convert(List<SpiderTask> spiderTaskList) {
        if(CollectionUtils.isEmpty(spiderTaskList))
            return new LinkedList<>();
        return spiderTaskList.stream().map(SpiderTask2SpiderTaskVOConvertor::convert).collect(Collectors.toList());
    }

    @SneakyThrows
    public static SpiderTaskVO convert(SpiderTask spiderTask) {
        SpiderTaskVO spiderTaskVO = new SpiderTaskVO();
        BeanUtils.copyProperties(spiderTask,spiderTaskVO);
        return spiderTaskVO.setStatus(EnumUtil.getMessageByCode(spiderTask.getStatus(), SpiderTaskStatusEnum.class));
    }



}
