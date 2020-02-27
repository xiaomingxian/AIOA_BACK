-- group_concat最大值设置
SET GLOBAL group_concat_max_len = 4294967295;
SET SESSION group_concat_max_len = 4294967295;
show variables like 'group_concat_max_len';

