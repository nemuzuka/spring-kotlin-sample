INSERT INTO tasks

(task_id, task_code, status, title, content_value, deadline, attributes,
 create_user_code, create_at, last_update_user_code, last_update_at, version_no)

VALUES

(
 'task_id_002', 'task_code_002', 'DONE', '件名その2', '内容その2', 1246732800002, null,
 'create_user_002', 1646732800002, 'last_update_user_002', 1746732800002, 2
),
(
 'task_id_001', 'task_code_001', 'OPEN', '件名その1', '内容その1', 1246732800001, '{"hige":"hage"}',
 'create_user_001', 1646732800001, 'last_update_user_001', 1746732800001, 1
);
