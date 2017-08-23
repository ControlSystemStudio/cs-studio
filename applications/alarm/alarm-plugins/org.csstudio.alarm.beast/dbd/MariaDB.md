MariaDB vs. MySQL
=================

Some Linux systems changed from including MySQL to MariaDB, for example RedHat EL 7.

Start/Stop:

	sudo systemctl start mariadb.service
	sudo systemctl status mariadb.service
	sudo systemctl stop mariadb.service

When first installed, the root password will be empty, so set it something:

	mysql -u root

	USE mysql;
	UPDATE user SET password=PASSWORD('YourPassword') WHERE User='root' AND Host = 'localhost';
	FLUSH PRIVILEGES;	

From now on the MySQL commands in MYSQL_USER.sql and ALARM_MYSQL.sql should work.
In the end, this should list the configuration roots:


	mysql -u alarm -p'$alarm' ALARM
	select * from ALARM_TREE where PARENT_CMPNT_ID is null;
