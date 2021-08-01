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


# 电子书 ebook
drop table if exists `ebook`;
create table `ebook`
(
    `id`           bigint not null comment 'id',
    `name`         varchar(50) comment '名称',
    `category1_id` bigint comment '分类1',
    `category2_id` bigint comment '分类2',
    `description`  varchar(200) comment '描述',
    `cover`        varchar(200) comment '封面',
    `doc_count`    int default 0 comment '文档数',
    `view_count`   int default 0 comment '阅读数',
    `vote_count`   int default 0 comment '点赞数',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment = '电子书';

# delete
# from `ebook`;

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


# 分类 category
drop table if exists `category`;
create table `category`
(
    `id`     bigint not null comment 'id',
    `parent` bigint not null default 0 comment '父id',
    `name`   varchar(50) comment '名称',
    `sort`   int comment '排序',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment = '分类';

insert into `category`(`id`, `parent`, `name`, `sort`)
values (100, 000, '前端开发', 100);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (101, 100, 'Vue', 101);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (102, 100, 'HTML、CSS', 102);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (200, 000, 'Java', 200);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (201, 200, '基础', 201);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (202, 200, '框架', 202);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (300, 000, 'Python', 300);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (301, 300, '基础', 301);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (302, 300, '进阶', 302);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (400, 000, '数据库', 400);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (401, 400, 'MySQL', 401);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (500, 000, '计算机基础', 500);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (501, 500, '计算机网络', 501);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (502, 500, '操作系统', 502);
insert into `category`(`id`, `parent`, `name`, `sort`)
values (503, 500, '计算机组成原理', 503);


# 文档 doc
drop table if exists `doc`;
create table `doc`
(
    `id`         bigint not null comment 'id',
    `ebook_id`   bigint not null default 0 comment '电子书id',
    `parent`     bigint not null default 0 comment '父id',
    `name`       varchar(50) comment '名称',
    `sort`       int comment '排序',
    `view_count` int             default 0 comment '阅读数',
    `vote_count` int             default 0 comment '点赞数',

    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment = '文档';

insert into `doc`(`id`, `ebook_id`, `parent`, `name`, `sort`, `view_count`, `vote_count`)
values (1, 1, 0, '文档1', 1, 0, 0);
insert into `doc`(`id`, `ebook_id`, `parent`, `name`, `sort`, `view_count`, `vote_count`)
values (2, 1, 1, '文档1.1', 1, 0, 0);
insert into `doc`(`id`, `ebook_id`, `parent`, `name`, `sort`, `view_count`, `vote_count`)
values (3, 1, 0, '文档2', 2, 0, 0);
insert into `doc`(`id`, `ebook_id`, `parent`, `name`, `sort`, `view_count`, `vote_count`)
values (4, 1, 3, '文档2.1', 1, 0, 0);
insert into `doc`(`id`, `ebook_id`, `parent`, `name`, `sort`, `view_count`, `vote_count`)
values (5, 1, 3, '文档2.2', 2, 0, 0);
insert into `doc`(`id`, `ebook_id`, `parent`, `name`, `sort`, `view_count`, `vote_count`)
values (6, 1, 5, '文档2.2.1', 1, 0, 0);


# 文档内容 content
# 纵向分表设计：content属于doc的一部分 id也是相同的
# 横向分表：比如根据日期不同，分成不同的表
# 大字段一般分表 防止查询时效率太低
# 原因：MySQL底层通过数据页存储，如果一个记录过大，会导致跨页，造成额外的开销
drop table if exists `content`;
create table `content`
(
    `id`      bigint     not null comment '文档id',
    `content` mediumtext not null comment '内容',
    primary key (`id`)
) engine = innodb
  default charset = utf8mb4 comment = '文档内容';


# 用户表
drop table if exists `user`;
create table `user`
(
    `id`         bigint      not null comment 'ID',
    `login_name` varchar(50) not null comment '登录名',
    `name`       varchar(50) comment '昵称',
    `password`   char(32)    not null comment '密码',
    primary key (`id`),
    unique key `login_name_unique` (`login_name`)
) engine = innodb
  default charset = utf8mb4 comment = '用户';

insert into `user` (`id`, `login_name`, `name`, `password`)
values (1, 'test', '测试', 'test');
insert into `user` (`id`, `login_name`, `name`, `password`)
values (2, 'aaa', '测试', 'aaa');


# 电子书快照表
drop table if exists `ebook_snapshot`;
create table `ebook_snapshot`
(
    `id`         bigint auto_increment not null comment 'ID',
    `ebook_id`   bigint                not null default 0 comment '电子书ID',
    `date`       date                  not null comment '快照日期',
    `view_count` int                   not null default 0 comment '阅读数',
    `vote_count` int                   not null default 0 comment '点赞数',
    `view_increase` int                   not null default 0 comment '阅读增长',
    `vote_increase` int                   not null default 0 comment '点赞增长',
    primary key (`id`),
    unique key `ebook_id_date_unique` (`ebook_id`, `date`)
) engine = innodb
  default charset = utf8mb4 comment = '电子书快照表';