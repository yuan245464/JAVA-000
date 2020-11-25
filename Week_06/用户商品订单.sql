/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
   id                   bigint not null auto_increment comment '主键id',
   name                 varchar(20) not null comment '用户名',
   primary key (id)
);

alter table user comment '用户';

/*==============================================================*/
/* Table: product                                               */
/*==============================================================*/
create table product
(
   id                   bigint not null auto_increment comment '主键id',
   code                 varchar(20) not null comment '商品编号',
   description          varchar(100) comment '商品描述',
   price                decimal not null comment '价格'
);

alter table product comment '商品';

/*==============================================================*/
/* Table: "order"                                               */
/*==============================================================*/
create table "order"
(
   id                   bigint not null comment '主键id',
   user_id              bigint not null comment '用户id',
   create_time          timestamp not null comment '创建时间',
   total_price          decimal not null comment '总价'
);

alter table "order" comment '订单';

/*==============================================================*/
/* Table: order_entry                                           */
/*==============================================================*/
create table order_entry
(
   id                   bigint not null auto_increment comment '主键id',
   product_id           bigint not null comment '商品id',
   order_id             bigint not null comment '订单id',
   count                int not null comment '数量',
   sum_price            decimal not null comment '价格总计'
);

alter table order_entry comment '订单条目';