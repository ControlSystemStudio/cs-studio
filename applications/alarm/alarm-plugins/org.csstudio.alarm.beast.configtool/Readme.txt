Alarm Config Tool
=================
Command-line tool for exporting alarm config as XML,
or to import XML config into RDB.

Can be used to generate snapshots of the configuration.

To import 'foreign' formats like ALH, they can be
converted into XML and then imported.

Note that this this tool only interact with the alarm configuration
in the RDB. After changing the configuration one should either restart
the alarm server and GUI clients, or send the 'CONFIG' update message
via JMS to trigger them to re-load the configuration.


Basic Incantations
==================
Note that alarm configurations are identified by their "Root"
element.

The following examples assume that the RDB URL/user/password are built into
the tool via plugin_customization.ini or the underlying
org.csstudio.alarm/preferences.ini.
To specify another URL, add the option
 
  -rdb_url jdbc:....

= List Available Configurations =
List all configurations, i.e. the names of all alarm tree 'root' elements currently found in the RDB:

  AlarmConfigTool -list


= Import Configuration =
To import an initial configuration, or to replace the current configuration with
root element "Test" with an XML snapshot, run

  AlarmConfigTool -import -file /some/path/Test.xml -root Test

This deletes the configuration, then loads the file.
The "-modify" option will instead try to update the existing configuration,
i.e. load new components or PVs, or update the guidance etc. of existing
entries.

This would import the AnnunciatorDemoConfig.xml:
  AlarmConfigTool -import -file /some/path/AnnunciatorDemoConfig.xml -root Annunciator
  
Note that the name of the root element in the configuration file must
match the name specified in the -root .... command-line argument.
This is a basic consistency check to avoid importing the wrong configuration.

It is best to always specify the full path to the XML configuration file.
When not using the full path, the tool will read or create files relative
to its current working directory, which in turn will be the 'workspace'
directory that depends on the operating system and can thus be somewhat unpredictable.
Using the full, absolute path, there will be no surprises.
  
= Export Configuration =
To take an XML snapshot of the current configuration with
root element "Test", run

  AlarmConfigTool -export -file /some/path/Test.xml -root Test

Again provide the full path to the XML file to assert that it is
indeed created in the desired location. 

= Edit Configuration =
Export, edit the file, import again.


= Remove Configuration =
Export, edit the file to remove all but the top-level "config" element,
import again.
This leaves the "root" element in the RDB. If desired, it needs to be
removed manually via direct RDB SQL access like this:

  -- List all 'root' elements, note its NAME or COMPONENT_ID
  SELECT * FROM ALARM_TREE WHERE PARENT_CMPNT_ID is null;

  DELETE FROM ALARM_TREE WHERE NAME='that_name';

= Copy Configuration =
To create a new configuration "CopyOfTest", first export "Test",
then edit the root "config" element in the configuration file to be "CopyOfTest",
and finally import that edited file as "CopyOfTest".

The editing is required because the tool will only import configuration
files into the root that matches the top level element in the file.
This is a simple consistency check.

