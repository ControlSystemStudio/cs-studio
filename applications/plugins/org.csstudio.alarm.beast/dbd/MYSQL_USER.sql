# Create user for alarm database
#
# chenx1@ornl.gov
# March. 5, 2009

# Before using this file to create config tables, you must change hostname, 
# username, password, database_name to the real name!
# Under the directory containing this file, use this command to create the database:
# mysql -h hostname -u username -p alarm < MYSQL_USER.sql


-- Create user who can access the config tables remotely,
-- but only change the table layout locally
GRANT ALL ON alarm.* TO username@localhost IDENTIFIED BY 'password';
GRANT INSERT, SELECT, UPDATE ON alarm.* TO username@'%' IDENTIFIED BY 'password';

-- Create user2 who can fully access the config tables remotely
GRANT ALL ON alarm.* TO username2@localhost IDENTIFIED BY 'password';
GRANT INSERT, SELECT, UPDATE, DELETE ON alarm.* TO username2@'%' IDENTIFIED BY 'password';


-- Check
USE mysql;
SELECT User, Host FROM user ORDER BY user;
SELECT User, Host, Db, Select_priv, Insert_priv, Update_priv, Delete_priv, Create_priv, Grant_priv FROM db ORDER BY user;
