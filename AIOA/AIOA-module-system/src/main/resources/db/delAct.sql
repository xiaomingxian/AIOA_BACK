-- 流程数据清除
 set foreign_key_checks = 0;
TRUNCATE act_ru_task ;
TRUNCATE act_ru_identitylink;
TRUNCATE act_ru_variable;
TRUNCATE act_ru_job;
TRUNCATE act_ru_event_subscr;
TRUNCATE act_ru_execution;
TRUNCATE act_hi_varinst;
TRUNCATE act_hi_taskinst;
TRUNCATE act_hi_procinst;
TRUNCATE act_hi_identitylink;
TRUNCATE act_hi_detail;
TRUNCATE act_hi_comment;
TRUNCATE act_hi_attachment;
TRUNCATE act_hi_actinst;
 set foreign_key_checks = 1;

DELETE from oa_task_dept;
DELETE from oa_task_user_record ;
DELETE  FROM oa_bus_task_transfer ;