# Create users for alarm database
#
# chenx1@ornl.gov
# March. 5, 2009

# Before using this file to create config tables, you must change hostname, 
# username, password, database_name to the real name!

# Under the directory containing this file, use this command to execute the commands:
# mysql -h hostname -u root -p'the_mysql_root_password' < MYSQL_USER.sql

# Or copy/paste the sections of interest into a mysql shell.


-- Create user 'alarm' with password '$alarm'
-- who can access the config tables remotely,
-- but only change the table layout locally
GRANT ALL ON alarm.* TO alarm@localhost IDENTIFIED BY '$alarm';
GRANT INSERT, SELECT, UPDATE ON alarm.* TO alarm@'%' IDENTIFIED BY '$alarm';
FLUSH PRIVILEGES;

-- Allow user 'reports' to read from the alarm tables
-- Assuming  user 'report' already exists.
-- Otherwise create it by adding ... IDENTIFIED BY '$report';
GRANT SELECT ON alarm.* TO report@localhost;
GRANT SELECT ON alarm.* TO report@'%';
FLUSH PRIVILEGES;


-- Example for other user who can fully access the config tables remotely
GRANT ALL ON alarm.* TO username2@localhost IDENTIFIED BY 'password';
GRANT INSERT, SELECT, UPDATE, DELETE ON alarm.* TO username2@'%' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;

-- Check
USE mysql;
SELECT User, Host FROM user ORDER BY user;
SELECT User, Host, Db, Select_priv, Insert_priv, Update_priv, Delete_priv, Create_priv, Grant_priv FROM db ORDER BY user;
