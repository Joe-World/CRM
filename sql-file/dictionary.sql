/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2020/3/5 9:12:54                             */
/*==============================================================*/


drop table if exists tbl_dic_type;

drop table if exists tbl_dic_value;

/*==============================================================*/
/* Table: tbl_dic_type                                          */
/*==============================================================*/
create table tbl_dic_type
(
   code                 varchar(255) not null comment '编码是主键，不能为空，不能含有中文。',
   name                 varchar(255),
   description          varchar(255),
   primary key (code)
);

/*==============================================================*/
/* Table: tbl_dic_value                                         */
/*==============================================================*/
create table tbl_dic_value
(
   id                   char(32) not null comment '主键，采用UUID',
   value                varchar(255) comment '不能为空，并且要求同一个字典类型下字典值不能重复，具有唯一性。',
   text                 varchar(255) comment '可以为空',
   order_no             varchar(255) comment '可以为空，但不为空的时候，要求必须是正整数',
   type_code            varchar(255) comment '外键',
   primary key (id)
);

