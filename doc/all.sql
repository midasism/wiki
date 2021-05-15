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

insert into `ebook` (`id`, `name`,`description`)
values (1, 'SpringBoot 入门教程','企业级开发首选框架');
insert into `ebook` (`id`, `name`,`description`)
values (2, 'Vue 入门教程','前端框架');