/**
 匹配帖子记录表
 */
CREATE TABLE match_post(
  id  BIGINT   AUTO_INCREMENT COMMENT 'id',
  title VARCHAR(128) NOT NULL COMMENT '帖子标题',
  uri VARCHAR(128) NOT NULL COMMENT '路径',
  content VARCHAR(10240) NOT NULL COMMENT '匹配的内容',
  keyword VARCHAR(32) NOT NULL COMMENT '匹配的关键词',
  spider_task_id BIGINT NOT NULL COMMENT '爬虫任务id',
  update_time  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP
  COMMENT '修改时间',
    create_time  DATETIME NOT NULL
  COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX (keyword)
) AUTO_INCREMENT = 1000, COMMENT = '匹配帖子记录表';

/**
 爬虫任务记录表
 */
CREATE TABLE spider_task(
  id  BIGINT   AUTO_INCREMENT COMMENT 'id',
  status TINYINT NOT NULL COMMENT '任务状态: 0运行中; 1结束; 2失败',
  keyword VARCHAR(1024) NOT NULL COMMENT '关键词,用分号分割',
  end_time DATETIME COMMENT '任务结束时间',
  update_time  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP
  COMMENT '修改时间',
  create_time  DATETIME NOT NULL
  COMMENT '创建时间',

  PRIMARY KEY (id)
) AUTO_INCREMENT = 1000, COMMENT = '爬虫任务记录表';