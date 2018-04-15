package com.liantong.spider.factory;

import com.liantong.spider.config.SpiderConfig;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;


/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 16:05
 * {@link org.springframework.data.domain.PageRequest} 工厂
 * 主要为了复用通用构建方法
 */
@Component
public class PageRequestFactory {

	private static SpiderConfig spiderConfig;

	public PageRequestFactory(SpiderConfig spiderConfig) {
		PageRequestFactory.spiderConfig = spiderConfig;
	}

	/**
	 * 构建通用的分页
	 */
	public static Pageable buildForCommon(int pageNo, int pageSize) {
		return new PageRequest(--pageNo, pageSize == 0  ? spiderConfig.getService().getDefaultPageSize() : pageSize, buildSort());
	}

	/**
	 * 构建只查询一条记录的分页
	 */
	public static Pageable buildForLimitOne() {
		return new PageRequest(0, 1);
	}


	/**
	 * 构建默认 Sort
	 * @return
	 */
	public static Sort buildSort() {
		return new Sort(Sort.Direction.DESC, "id");
	}

	/**
	 * 构建 某字段 升序的sort
	 */
	public static Sort buildSortASC(String fieldName) {
		return new Sort(Sort.Direction.ASC, fieldName);}


}
