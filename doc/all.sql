drop table if exists `test`;
create table `test`
(
    `id`   bigint not null comment 'id',
    `name` varchar(50) comment '名称',
    `password` varchar(50) comment '密码',
    primary key (`id`)
)engine = innodb default charset = utf8mb4 comment = '测试';

insert into `test` (`id`,`name`,`password`) values(1,'a','123');
insert into `test` (`id`,`name`,`password`) values(2,'b','456');

#demo
drop table if exists `demo`;
create table `demo`
(
    `id`   bigint not null comment 'id',
    `name` varchar(50) comment '名称',
    primary key (`id`)
)engine = innodb default charset = utf8mb4 comment = 'mybatis-generator test';

insert into `demo` (`id`,`name`) values(1,'a');
insert into `demo` (`id`,`name`) values(2,'b');