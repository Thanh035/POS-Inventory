--DELETE FROM tbl_role;
--DELETE FROM tbl_user;
--DELETE FROM tbl_user_role;

--add data role
INSERT INTO "tbl_role" ( "code", "name") VALUES (  'ROLE_ADMIN', 'quản trị');
INSERT INTO "tbl_role" ( "code", "name") VALUES (  'ROLE_USER', 'người dùng');
INSERT INTO "tbl_role" ( "code", "name") VALUES (  'ROLE_MANAGER', 'quản lý');

--add data user
INSERT INTO "tbl_user" ( "email","fullname", "password_hash","activated","login","created_by","created_date") VALUES (  'admin@localhost','Administrator', '$2a$10$Y7rT4wOn6xnfF/t6nZ8FXe6jJ9AuI8k1xzxFWiAQIXPUeioJdkEZy',TRUE,'admin@localhost','system',NOW());
INSERT INTO "tbl_user" ( "email","fullname", "password_hash","activated","login","created_by","created_date") VALUES (  'user@localhost','User', '$2a$10$Y7rT4wOn6xnfF/t6nZ8FXe6jJ9AuI8k1xzxFWiAQIXPUeioJdkEZy',TRUE,'user@localhost','system',NOW());

--add data user_role
INSERT INTO "tbl_user_role" ( "user_id", "role_id") VALUES ( '1','1' );
INSERT INTO "tbl_user_role" ( "user_id", "role_id") VALUES ( '2','2' );

--add data category
INSERT INTO tbl_category
(created_by, created_date, last_modified_by, last_modified_date, description, code, name)
VALUES ('system', NOW(), '', NOW(), 'Computers Description', 'CT001', 'Computers')


