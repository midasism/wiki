drop table if exists `test`;
create table `test`
(
    `id`       bigint not null comment 'id',
    `name`     varchar(50) comment '名称',
    `password` varchar(50) comment '密码',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment = '测试';

insert into `test` (`id`, `name`, `password`)
values (1, 'a', '123');
insert into `test` (`id`, `name`, `password`)
values (2, 'b', '456');

#demo
drop table if exists `demo`;
create table `demo`
(
    `id`   bigint not null comment 'id',
    `name` varchar(50) comment '名称',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment = 'mybatis-generator test';

insert into `demo` (`id`, `name`)
values (1, 'a');
insert into `demo` (`id`, `name`)
values (2, 'b');


# ebook
drop table if exists `ebook`;
create table `ebook`
(
    `id`           bigint not null comment 'id',
    `name`         varchar(50) comment '名称',
    `category1_id` bigint comment '分类1',
    `category2_id` bigint comment '分类2',
    `description`  varchar(200) comment '描述',
    `cover`        varchar(200) comment '封面',
    `doc_count`    int comment '文档数',
    `view_count`   int comment '阅读数',
    `vote_count`   int comment '点赞数',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment = '电子书';

delete
from `ebook`;

insert into `ebook` (`id`, `name`, `category1_id`, `category2_id`, `description`, `cover`, `doc_count`, `view_count`,
                     `vote_count`)
values (1, 'SpringBoot 入门教程', 1, 2, '企业级开发首选框架', '/image/cover1.png', 1, 2, 3);

insert into `ebook` (`id`, `name`, `category1_id`, `category2_id`, `description`, `cover`, `doc_count`, `view_count`,
                     `vote_count`)
values (2, 'Vue 入门教程', 3, 4, '前端框架', '/image/cover2.png', 4, 5, 6);

insert into `ebook` (`id`, `name`, `category1_id`, `category2_id`, `description`, `cover`, `doc_count`, `view_count`,
                     `vote_count`)
values (3, 'Spring 入门教程', 1, 2, '轻量级开源框架', '/image/cover1.png', 7, 6, 9);

insert into `ebook` (`id`, `name`, `category1_id`, `category2_id`, `description`, `cover`, `doc_count`, `view_count`,
                     `vote_count`)
values (4, 'Spring Security 入门教程', 2, 2, '权限框架', '/image/cover2.png', 4, 2, 1);

insert into `ebook` (`id`, `name`, `category1_id`, `category2_id`, `description`, `cover`, `doc_count`, `view_count`,
                     `vote_count`)
values (5, 'MySQL 入门教程', 1, 3, '开源数据库', '/image/cover1.png', 10, 1, 1);