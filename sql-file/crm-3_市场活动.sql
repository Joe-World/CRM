drop table if exists tbl_activity;

drop table if exists tbl_activity_remark;

/*==============================================================*/
/* Table: tbl_activity                                          */
/*==============================================================*/
create table tbl_activity
(
   id                   char(32) not null,
   owner                char(32),
   name                 varchar(255),
   start_date            char(10),
   end_date              char(10),
   cost                 varchar(255),
   description          varchar(255),
   create_time           char(19),
   create_by             varchar(255),
   edit_time             char(19),
   edit_by               varchar(255),
   primary key (id)
);

/*==============================================================*/
/* Table: tbl_activity_remark                                   */
/*==============================================================*/
create table tbl_activity_remark
(
   id                   char(32) not null,
   note_content          varchar(255),
   create_time           char(19),
   create_by             varchar(255),
   edit_time             char(19),
   edit_by               varchar(255),
   edit_flag             char(1) comment '0表示未修改，1表示已修改',
   activity_id           char(32),
   primary key (id)
);
