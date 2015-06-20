insert into ftp_user ( username, admin, enabled, max_idle_time, password, workspace, ftp_server_id) values ( 'jlong', true, true, 0 , 'password' , 'ws' ,  (select id from ftp_server limit 1)) ;
insert into ftp_user ( username, admin, enabled, max_idle_time, password, workspace, ftp_server_id) values ( 'mlee', true, true, 0 , 'password' , 'ws' ,  (select id from ftp_server limit 1)) ;
insert into ftp_user ( username, admin, enabled, max_idle_time, password, workspace, ftp_server_id) values ( 'mchang', true, true, 0 , 'password' , 'ws' ,  (select id from ftp_server limit 1) ) ;
insert into ftp_user ( username, admin, enabled, max_idle_time, password, workspace, ftp_server_id) values ( 'mgray', true, true, 0 , 'password' , 'ws' ,  (select id from ftp_server limit 1)) ;
/*

INSERT INTO FTP_USER(username, admin, enabled, max_idle_time, password, workspace ) VALUES(  'jlong', true, true, 0, 'password', 'spring' );
INSERT INTO FTP_USER(username, admin, enabled, max_idle_time, password, workspace ) VALUES(  'jhoeller', true, true, 0, 'password', 'spring' );
INSERT INTO FTP_USER(username, admin, enabled, max_idle_time, password, workspace ) VALUES(  'mpollack', true, true, 0, 'password', 'data' );
INSERT INTO FTP_USER(username, admin, enabled, max_idle_time, password, workspace ) VALUES(  'kbastani', true, true, 0, 'password', 'data' );
INSERT INTO FTP_USER(username, admin, enabled, max_idle_time, password, workspace ) VALUES(  'mfisher', true, true, 0, 'password', 'integration' );
INSERT INTO FTP_USER(username, admin, enabled, max_idle_time, password, workspace ) VALUES(  'abilan', true, true, 0, 'password', 'integration' );
INSERT INTO FTP_USER(username, admin, enabled, max_idle_time, password, workspace ) VALUES(  'grussell', true, true, 0, 'password', 'integration' );
*/
