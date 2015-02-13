# Original version, 2014-03-12: xinyu.wu@csiro.au
#
# A database schema change was introduce: ENABLED_IND column in PV table is moved to ALARM_TREE table
# This script does the following things:
# 1) add column ENABLED_IND to ALARM_TREE table, default value for the column is true
# 2) copy the tree item ENABLED_IND value from PV table to ALARM_TREE table
# 3) remove ENABLED_IND column from PV table
#
#
# Before using this file to create config tables, you must change hostname, 
# username, password to the real name.
# Under the directory containing this file, use this command to create the database:
# mysql -h hostname -u username -p alarm<ALARM_MySQL.sql
#
# Take snapshot, restore from snapshot:
#
#  mysqldump -u username -p -l alarm >alarm_snapshot.sql
#  mysql -u username -p alarm <alarm_snapshot.sql
#


# Add column
ALTER TABLE ALARM_TREE
ADD ENABLED_IND BOOL NOT NULL 
DEFAULT true;

# copy the tree item ENABLED_IND value from PV table to ALARM_TREE table
UPDATE ALARM_TREE a, PV p
SET a.ENABLED_IND = p.ENABLED_IND
where a.COMPONENT_ID = p.COMPONENT_ID;

# drop the ENABLED_IND column from PV table
ALTER TABLE PV
DROP ENABLED_IND;

