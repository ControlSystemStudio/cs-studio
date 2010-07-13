/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.csstudio.apputil.time.BenchmarkTimer;
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
public class AlarmConfigurationTest
{
    //MySQL4
	// final static String URL = "jdbc:mysql://ics-web.sns.ornl.gov/ALARM";
    
    //MySQL5
	//final static String URL =
	//      "jdbc:mysql://localhost/ALARM";
    
    //Oracle
    final static String URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.138)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.141)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=ics_prod_lba)))";
	
	final static String USER = "sns_reports"; //"root";
	final static String PASSWORD = "sns";
	
    final private static String TEST_ROOT = "DEVL";

    //please change the file path in your computer environment
    final private static File filename = new File("/tmp", TEST_ROOT + ".xml");

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
        final AlarmConfiguration config = new AlarmConfiguration(URL, USER, PASSWORD, TEST_ROOT, false);
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
    	final AlarmConfiguration config = new AlarmConfiguration(URL,USER, PASSWORD, TEST_ROOT, true);
        final AlarmTreeRoot root = config.getAlarmTree();
        //print TEST_ROOT AlarmTree to console
        root.dump();
        //remove TEST_ROOT AlarmTree from RDB
        config.removeAllItems();
        
        //add component and PV in RDB
        final AlarmTreeComponent fac = config.addComponent(root, "TestFac");
        final AlarmTreeComponent sys1 = config.addComponent(fac, "Sys1");
        final AlarmTreeComponent sys2 = config.addComponent(fac, "Sys2");
        AlarmTreePV pv1 = config.addPV(sys1, "XihuiTest.TestFac.Sys1.PV1");
        config.configurePV(pv1, "XihuiTestPV1", true, true, true, 0, 0, "", 
        		new ArrayList<GDCDataStructure>(Arrays.asList(
        				new GDCDataStructure("call xihui", "Xihui's phone is 123456"),
        				new GDCDataStructure("call fred","Fred's Email is fred@ornl.gov \n !@#$%^&*()_+-=~`:\"|\\?/</details>,.;'"))), 
        		null, null);
        config.addPV(sys1, "XihuiTest.TestFac.Sys1.PV2");
        config.addPV(sys2, "XihuiTest.TestFac.Sys2.PV1");
        config.addPV(sys2, "XihuiTest.TestFac.Sys2.PV2");
        //print TEST_ROOT AlarmTree to console
        root.dump();
        //close the connection with RDB.
        config.close();
    }
    
    /** Read config from RDB, dump to XML file */
    @Test
    @Ignore
    public void testXMLWriteToFile() throws Exception
    {
        System.out.println("-------------------testXMLWriteToFile-----------------------");
        final AlarmConfiguration config = new AlarmConfiguration(URL , USER, PASSWORD, TEST_ROOT, false);
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
        final AlarmConfiguration config = new AlarmConfiguration(URL , USER, PASSWORD, TEST_ROOT, true);
        System.out.println("******* Configuration read from RDB: ******");
        config.getAlarmTree().dump();
        config.removeAllItems();
 
        new AlarmConfigurationLoader(config, new FileInputStream(filename));
        System.out.println("******* Configuration read back from file: ******");
        config.getAlarmTree().dump();
    }
}
