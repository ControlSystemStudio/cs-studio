/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.csstudio.apputil.test.TestProperties;
import org.csstudio.apputil.time.BenchmarkTimer;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Ignore;
import org.junit.Test;

/** JUnit test of AlarmConfiguration
 *  <p>
 *  testRDBRead(), testXMLWriteToFile run as a plain JUnit test.
 *  Others might require JUnit Plug-in test.
 *
 *  @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
public class AlarmConfigurationUnitTest
{
	final private String url, user, password, root;
    final private File filename;

    /** Initialize from TestProperties */
    public AlarmConfigurationUnitTest() throws Exception
    {
    	final TestProperties settings = new TestProperties();
    	url = settings.getString("alarm_rdb_url");
		user = settings.getString("alarm_rdb_user", "");
		password = settings.getString("alarm_rdb_password", "");
    	root = settings.getString("alarm_root");
    	filename = new File(settings.getString("temp_path", "/tmp"), root + ".xml");
    }

    /** @return AlarmConfiguration or <code>null</code> if lacking settings */
    private AlarmConfiguration getConfiguration(final boolean writable) throws Exception
    {
    	if (url == null  ||  root == null)
    	{
    		System.out.println("Skipping test: no alarm_rdb_* settings found.");
    		return null;
    	}
    	final AlarmConfiguration config = new AlarmConfiguration(url, user, password, "ALARM");
    	config.readConfiguration(root, writable, new NullProgressMonitor());
    	return config;
    }

    @Test
    public void listConfigurations() throws Exception
    {
    	System.out.println("-------------------listConfigurations-----------------------");
        final AlarmConfiguration config = getConfiguration(false);
        if (config == null)
        	return;
    	final String names[] = config.listConfigurations();
    	System.out.println("Configurations:");
    	for (String name : names)
    		System.out.println(" '" + name + "'");
    }


    /** Alarm config readout test/demo
     *  <p>
     *  'CUB' on my Linux PC: 3 .. 3.2 seconds
     *  In the profiler a run that spent about 108 secs in org.csstudio.alarm
     *  needed almost all that time (104 secs) in oracle.jdbc.driver, so
     *  the time is almost completely driven by RDB access.
     */
    @Test
    public void testRDBRead() throws Exception
    {
    	System.out.println("-------------------testRDBRead-----------------------");
    	final BenchmarkTimer timer = new BenchmarkTimer();
        final AlarmConfiguration config = getConfiguration(false);
        if (config == null)
        	return;
        // Quirk: Without 'flush', you don't see anything.
        // With 'close', System.out is actually closed ...

        final PrintWriter out = new PrintWriter(System.out);
        config.getAlarmTree().writeXML(out);
        out.flush();
        config.close();

        timer.stop();
        System.out.println(timer);
    }

    /** This test changes the RDB configuration!
     *  Needs password for write access.
     */
    @Test
    @Ignore
    public void testRDBWrite() throws Exception
    {
    	System.out.println("-------------------testRDBWrite-----------------------");
        //read TEST_ROOT AlarmTree from RDB
    	final AlarmConfiguration config = getConfiguration(true);
    	if (config == null)
    		return;
        final AlarmTreeRoot root = config.getAlarmTree();
        //print TEST_ROOT AlarmTree to console
        root.dump(System.out);
        //remove TEST_ROOT AlarmTree from RDB
        config.removeAllItems();

        //add component and PV in RDB
        final AlarmTreeItem fac = config.addComponent(root, "TestFac");
        final AlarmTreeItem sys1 = config.addComponent(fac, "Sys1");
        final AlarmTreeItem sys2 = config.addComponent(fac, "Sys2");
        AlarmTreePV pv1 = config.addPV(sys1, "XihuiTest.TestFac.Sys1.PV1");
        config.configurePV(pv1, "XihuiTestPV1", true, true, true, 0, 0, "",
        		new GDCDataStructure[]
                {
                    new GDCDataStructure("call xihui", "Xihui's phone is 123456"),
                    new GDCDataStructure("call fred","Fred's Email is fred@ornl.gov \n !@#$%^&*()_+-=~`:\"|\\?/</details>,.;'")
                },
        		null, null, null);
        config.addPV(sys1, "XihuiTest.TestFac.Sys1.PV2");
        config.addPV(sys2, "XihuiTest.TestFac.Sys2.PV1");
        config.addPV(sys2, "XihuiTest.TestFac.Sys2.PV2");
        //print TEST_ROOT AlarmTree to console
        root.dump(System.out);
        //close the connection with RDB.
        config.close();
    }

    /** Read config from RDB, dump to XML file */
    @Test
    @Ignore
    public void testXMLWriteToFile() throws Exception
    {
        System.out.println("-------------------testXMLWriteToFile-----------------------");
        final AlarmConfiguration config = getConfiguration(false);
    	if (config == null)
    		return;
        final FileWriter file = new FileWriter(filename);
        config.getAlarmTree().writeXML(new PrintWriter(file));
        file.close();
        config.close();

        System.out.println("Please check this file: " + filename.getCanonicalPath());
    }

    /** Read config from RDB, dump to stdout, delete, read file back into RDB from file
     *  !!!!!!!!!!!!!!
     *  This changes the RDB, and will DELETE the RDB configuration!
     *  In case the readback from the file fails, you have an empty configuration
     *  in the RDB!
     *  !!!!!!!!!!!!!!
     *  Requires account with write access.
     */
    @Test
    @Ignore
    public void testXMLFileReadback() throws Exception
    {
    	System.out.println("-------------------testXMLFileReadback-----------------------");
        final AlarmConfiguration config = getConfiguration(true);
    	if (config == null)
    		return;
        System.out.println("******* Configuration read from RDB: ******");
        config.getAlarmTree().dump(System.out);
        config.removeAllItems();

        new AlarmConfigurationLoader(config).load(filename.getAbsolutePath());
        System.out.println("******* Configuration read back from file: ******");
        config.getAlarmTree().dump(System.out);
    }
}
