/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/**
 * <p align="center"><img alt="pvManager" src="http://pvmanager.sourceforge.net/images/PVManagerLogo150.png"/></p>
 * <div style="float: right; margin-top: -170px" id="contents"></div>
 * 
 * 
 * <h1>Contents</h1>
 * 
 * <h3>Configuration</h3>
 * <ol>
 *     <li><a href="#c1">Using PVManager in CSS</a></li>
 *     <li><a href="#c2">Using PVManager in Swing</a></li>
 *     <li><a href="#c3">Configuring JCA/CAJ as the default data source</a></li>
 *     <li><a href="#c4">Configuring multiple data sources with different prefixes</a></li>
 * </ol>
 * <h3>Basic usage</h3>
 * <ol>
 *     <li><a href="#b1">Reading a single channel</a></li>
 *     <li><a href="#b1a">Reading all values values from a channel</a></li>
 *     <li><a href="#b2">Writing a single channel asynchrnously</a></li>
 *     <li><a href="#b3">Writing a single channel synchrnously</a></li>
 *     <li><a href="#b4">Reading and writing a single channel</a></li>
 *     <li><a href="#b5">Handling read errors on notifications</a></li>
 *     <li><a href="#b6">Handling read errors using an ExceptionHandler</a></li>
 *     <li><a href="#b7">Setting read connection timeouts</a></li>
 * </ol>
 * <h3>Multiple channels</h3>
 * <ol>
 *     <li><a href="#m1">Reading a map with multiple channels</a></li>
 *     <li><a href="#m2">Writing a map with multiple channels</a></li>
 *     <li><a href="#m3">Read and write a map with multiple channels</a></li>
 *     <li><a href="#m4">Refer to channel with a different name</a></li>
 *     <li><a href="#m5">Impose write ordering</a></li>
 * </ol>
 * <h3>Working with standard VTypes</h3>
 * <ol>
 *     <li><a href="#v1">Read/Write a specific type</a></li>
 *     <li><a href="#v2">Working with an unknown type: extracting alarm, time, ...</a></li>
 *     <li><a href="#v3">Working with an unknown type: switch on the type</a></li>
 *     <li><a href="#v4">Working with an unknown type: register listener on type</a></li>
 * </ol>
 * <h3>Working with VTable</h3>
 * <ol>
 *     <li><a href="#t1">Assembling a table</a></li>
 * </ol>
 * 
 * <h3 id="c1">Using PVManager in CSS</h3>
 * 
 * In CSS, data sources are configured by adding the appropriate plug-ins,
 * so you <b>must not change the default configuration</b>.
 * If you are developing user interfaces in SWT, you will want to route the notifications
 * on the SWT thread.
 * 
 * <pre>
 * // Import from here
 * import static org.csstudio.utility.pvmanager.ui.SWTUtil.*;
 * 
 * // When creating a pv, remember to ask for notification on the SWT thread
 * PVReader&lt;?&gt; pvReader = PVManager.read(...)..notifyOn(swtThread()).maxRate(ofMillis(100));
 * </pre>
 * 
 * <h3 id="c2">Using PVManager in Swing</h3>
 * 
 * You will first need to configure the data sources yourself (see other examples).
 * You will want to route notification directly on the Event Dispatch Thread. You can
 * do this on a PV by PV basis, or you can change the default.
 * 
 * <pre>
 * // Import from here
 * import static org.epics.pvmanager.util.Executors.*;
 * 
 * // Route notification for this pv on the Swing EDT
 * PVReader&lt;?&gt; pvReader = PVManager.read(...).notifyOn(swingEDT()).maxRate(ofMillis(100));
 * 
 * // Or you can change the default
 * PVManager.setDefaultNotificationExecutor(swingEDT());
 * </pre>
 * 
 * <h3 id="c3">Configuring JCA/CAJ as the default data source</h3>
 * 
 * <pre>
 * // Sets CAJ (pure java implementation) as the default data source,
 * // monitoring both value and alarm changes
 * PVManager.setDefaultDataSource(new JCADataSource());
 * 
 * // For utltimate control, you can create the JCA context yourself
 * // and pass it to the data source
 * ...
 * Context jcaContext = ...
 * PVManager.setDefaultDataSource(new JCADataSource(jcaContext, Monitor.VALUE | Monitor.ALARM));
 * </pre>
 * 
 * For more options, check the constructors for {@link org.epics.pvmanager.jca.JCADataSource}.
 * <p>
 * 
 * <h3 id="c4">Configuring multiple data sources with different prefixes</h3>
 * 
 * <pre>
 * // Create a multiple data source, and add different data sources
 * CompositeDataSource composite = new CompositeDataSource();
 * composite.putDataSource("ca", new JCADataSource());
 * composite.putDataSource("sim", new SimulationDataSource());
 * 
 * // If no prefix is given to a channel, use JCA as default
 * composite.setDefaultDataSource("ca");
 * 
 * // Set the composite as the default
 * PVManager.setDefaultDataSource(composite);
 * </pre>
 * 
 * For more options, check the documentation for {@link org.epics.pvmanager.CompositeDataSource}.
 * 
 * <h3 id="b1">Reading a single channel</h3>
 * 
 * <pre>
 * // Let's statically import so the code looks cleaner
 * import static org.epics.pvmanager.ExpressionLanguage.*;
 * import static org.epics.util.time.TimeDuration.*;
 * 
 * // Read channel "channelName" up to every 100 ms
 * final {@link org.epics.pvmanager.PVReader}&lt;Object&gt; pvReader = PVManager.read(channel("channelName")).maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(new PVReaderListener() {
 *     public void pvChanged() {
 *         // Do something with each value
 *         Object newValue = pvReader.getValue();
 *         System.out.println(newValue);
 *     }
 * });
 * 
 * // Remember to close
 * pvReader.close();
 * </pre>
 * 
 * The interval between updates can be specified in different units (e.g. ms, sec, min, hour, hz).
 * Check the documentation at {@link org.epics.pvmanager.util.TimeDuration}.
 * 
 * <h3 id="b1a">Reading all values values from a channel</h3>
 * 
 * <pre>
 * // Read channel "channelName" up to every 100 ms, and get all
 * // the new values from the last notification.
 * final PVReader&lt;List&lt;Object&gt;&gt; pvReader = PVManager.read({@link org.epics.pvmanager.ExpressionLanguage#newValuesOf(org.epics.pvmanager.expression.SourceRateExpression) newValuesOf}(channel("channelName"))).maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(new PVReaderListener() {
 *     public void pvChanged() {
 *         // Do something with each value
 *         for (Object newValue : pvReader.getValue()) {
 *             System.out.println(newValue);
 *         }
 *     }
 * });
 * 
 * // Remember to close
 * pvReader.close();
 * </pre>
 * 
 * To limit memory consumption, you can specify the maximum amount of values
 * to retain. See all options at {@link org.epics.pvmanager.ExpressionLanguage}.
 * 
 * <h3 id="b2">Writing a single channel asynchronously</h3>
 * 
 * <pre>
 * PVWriter&lt;Object&gt; pvWriter = PVManager.write(channel("channelName")).async();
 * pvWriter.addPVWriterListener(new PVWriterListener() {
 *     public void pvWritten() {
 *         System.out.println("Write finished");
 *     }
 * });
 * // This will return right away, and the notification will be sent
 * // on the listener
 * pvWriter.write("New value");
 * 
 * // Remember to close
 * pvWriter.close();
 * </pre>
 * 
 * <h3 id="b3">Writing a single channel synchronously</h3>
 * 
 * <pre>
 * PVWriter&lt;Object&gt; pvWriter = PVManager.write(channel("channelName")).sync();
 * // This will block until the write is done
 * pvWriter.write("New value");
 * System.out.println("Write finished");
 * 
 * // Remember to close
 * pvWriter.close();
 * </pre>
 * 
 * <h3 id="b4">Reading and writing a single channel</h3>
 * 
 * <pre>
 * // A PV is both a PVReader and a PVWriter
 * final PV&lt;Object, Object&gt; pv = PVManager.readAndWrite(channel("channelName")).asynchWriteAndMaxReadRate(ofMillis(10));
 * pv.addPVReaderListener(new PVReaderListener() {
 *     public void pvChanged() {
 *         // Do something with each value
 *         Object newValue = pv.getValue();
 *         System.out.println(newValue);
 *     }
 * });
 * pv.write("New value");
 * 
 * // Remember to close
 * pv.close();
 * </pre>
 * 
 * 
 * <h3 id="b5">Handling read errors on notifications</h3>
 * 
 * <pre>
 * final PVReader&lt;Object&gt; pvReader = PVManager.read(channel("channelName")).maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(new PVReaderListener() {
 * 
 *     public void pvChanged() {
 *         // By default, read exceptions are made available
 *         // on the reader itself.
 *         // This will give you only the last exception, so if
 *         // more then one exception was generated after the last read,
 *         // some will be lost.
 *         Exception ex = pvReader.lastException();
 *         
 *         // Note that taking the exception, clears it
 *         // so next call you'll get null.
 *         if (pvReader.lastException() == null) {
 *             // Always true
 *         }
 *     }
 * });
 * </pre>
 * 
 * 
 * <h3 id="b6">Handling read errors using an ExceptionHandler</h3>
 * 
 * <pre>
 * // All read exceptions will be passed to the exception handler
 * // on the thread that it generates them. The handler, therefore,
 * // must be thread safe. Overriding the exception handling means
 * // disabling the default handling, so read exception will no longer
 * // be accessible with {@code pvReader.lastException()}
 * final PVReader&lt;Object&gt; pvReader = PVManager.read(channel("channelName"))
 *         .routeExceptionsTo(new ExceptionHandler() {
 *             public void handleException(Exception ex) {
 *                 System.out.println("Error: " + ex.getMessage());
 *             }
 *         }).maxRate(ofMillis(100));
 * </pre>
 * 
 * 
 * <h3 id="b7">Setting read connection timeouts</h3>
 * 
 * <pre>
 * // If after 5 seconds no new value comes (i.e. pvReader.getValue() == null)
 * // then a timeout is sent. PVManager will _still_ try to connect,
 * // until pvReader.close() is called.
 * // The timeout will be notified only on the first connection.
 * final PVReader&lt;Object&gt; pvReader = PVManager.read(channel("channelName")).timeout(sec(5)).maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(new PVReaderListener() {
 * 
 *     public void pvChanged() {
 *         // Timeout are passed as exceptions. This allows you to
 *         // treat them as any other error conditions.
 *         Exception ex = pvReader.lastException();
 *         if (ex instanceof TimeoutException) {
 *             System.out.println("Didn't connected after 5 seconds");
 *         }
 *     }
 * });
 * </pre>
 * 
 * 
 * <h3 id="m1">Reading a map with multiple channels</h3>
 * 
 * <pre>
 * // Read a map with the channels named "one", "two" and "three"
 * final PVReader&lt;Map&lt;String, Object&gt;&gt; pvReader = PVManager.read(mapOf(latestValueOf(channels("one", "two", "three")))).maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(new PVReaderListener() {
 *     public void pvChanged() {
 *         // Print the values if any
 *         Map&lt;String, Object&gt; map = pvReader.getValue();
 *         if (map != null) {
 *             System.out.println("one: " + map.get("one") +
 *                     " - two: " + map.get("two") + 
 *                     " - three: " + map.get("three"));
 *         }
 *     }
 * });
 *  
 * // Remember to close
 * pvReader.close();
 * </pre>
 * 
 * Note that when using a composite datasource, the channels
 * can be from different sources (e.g. "sim://noise" and "ca://mypv").
 * 
 * <h3 id="m2">Writing a map with multiple channels</h3>
 * 
 * <pre>
 * // Write a map to the channels named "one", "two" and "three"
 * PVWriter&lt;Map&lt;String, Object&gt;&gt; pvWriter = PVManager.write(mapOf(channels("one", "two", "three"))).async();
 * 
 * // Prepare the 3 values
 * Map&lt;String, Object&gt; values = new HashMap&lt;String, Object&gt;();
 * values.put("one", 1.0);
 * values.put("two", 2.0);
 * values.put("three", "run");
 * 
 * // Write
 * pvWriter.write(values);
 * 
 * // Remember to close
 * pvWriter.close();
 * </pre>
 * 
 * Note that when using a composite datasource, the channels
 * can be from different sources (e.g. "sim://noise" and "ca://mypv").
 * 
 * 
 * <h3 id="m3">Read and write a map with multiple channels</h3>
 * 
 * <pre>
 * // Read and write a map to the channels named "one", "two" and "three"
 * PV&lt;Map&lt;String, Object&gt;, Map&lt;String, Object&gt;&gt; pv = PVManager.readAndWrite(
 *         mapOf(latestValueOf(channels("one", "two", "three")))).asynchWriteAndMaxReadRate(ofMillis(100));
 * 
 * // Do something
 * // ...
 * 
 * // Remember to close
 * pv.close();
 * </pre>
 * 
 * 
 * <h3 id="m4">Refer to channel with a different name</h3>
 * 
 * <pre>
 * // Read a map with the channels "one", "two" and "three"
 * // reffered in the map as "setpoint", "readback" and "difference"
 * final PVReader&lt;Map&lt;String, Object&gt;&gt; pvReader = PVManager.read(mapOf(
 *         latestValueOf(channel("one").as("setpoint").and(channel("two").as("readback")).and(channel("three").as("difference"))))).maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(new PVReaderListener() {
 * 
 *     public void pvChanged() {
 *         // Print the values if any
 *         Map&lt;String, Object&gt; map = pvReader.getValue();
 *         if (map != null) {
 *             System.out.println("setpoint: " + map.get("setpoint") +
 *                     " - readback: " + map.get("readback") + 
 *                     " - difference: " + map.get("difference"));
 *         }
 *     }
 * });
 * 
 * // Remember to close
 * pvReader.close();
 * </pre>
 * 
 * You can rename channels and any read expression, regardless of how they are combined later.
 * 
 * 
 * <h3 id="m5">Impose write ordering</h3>
 * 
 * <pre>
 * // Write a map to the channels named "one", "two" and "three"
 * // Write "two" after "one" and write "three" after "two"
 * PVWriter&lt;Map&lt;String, Object&gt;&gt; pvWriter = PVManager.write(
 *         mapOf(channel("one")
 *               .and(channel("two").after("one"))
 *               .and(channel("three").after("two")))).async();
 * 
 * // Do something
 * // ...
 * 
 * // Remember to close
 * pvWriter.close();
 * </pre>
 * 
 * Note that when using a composite datasource, the channels
 * can be from different sources (e.g. "sim://noise" and "ca://mypv"). The
 * write ordering will also be respected across sources.
 * 
 * 
 * <h3 id="v1">Read/Write a specific type</h3>
 * 
 * <pre>
 * // Let's statically import so the code looks cleaner
 * import static org.epics.pvmanager.data.ExpressionLanguage.*;
 * 
 * // Read and Write a vDouble
 * // Note that the read type is different form the write type
 * final PV&lt;VDouble, Double&gt; pv = PVManager.readAndWrite(vDouble("currentRB")).asynchWriteAndMaxReadRate(ofMillis(100));
 * pv.addPVReaderListener(new PVReaderListener() {
 * 
 *     public void pvChanged() {
 *         VDouble value = pv.getValue();
 *         if (value != null) {
 *             System.out.println(value.getValue() + " " + value.getAlarmSeverity());
 *         }
 *     }
 * });
 * pv.write(1.0);
 * 
 * // Remember to close
 * pv.close();
 * </pre>
 * 
 * For a full list of types, refer to {@link org.epics.pvmanager.data.ExpressionLanguage}.
 * 
 * 
 * <h3 id="v2">Working with an unknown type: extracting alarm, time, ...</h3>
 * 
 * <pre>
 * // We connect to a channel that produces a VType, but we
 * // don't know which one
 * final PVReader&lt;VType&gt; pvReader = PVManager.read(vType("channelName")).maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(new PVReaderListener() {
 * 
 *     public void pvChanged() {
 *         VType value = pvReader.getValue();
 *         // We can extract the different aspect of the read object,
 *         // so that we can work on them separately
 *         
 *         // This returns the interface implemented (VDouble, VInt, ...)
 *         Class&lt;?&gt; type = ValueUtil.typeOf(value);
 *         // Extracts the alarm if present
 *         Alarm alarm = ValueUtil.alarmOf(value);
 *         // Extracts the time if present
 *         Time time = ValueUtil.timeOf(value);
 *         // Extracts a numeric value if present
 *         Double number = ValueUtil.numericValueOf(value);
 *         // Extract display information if present
 *         Display display = ValueUtil.displayOf(value);
 *         
 *         setAlarm(alarm);
 *         // ...
 *     }
 * });
 * </pre>
 * 
 * 
 * <h3 id="v3">Working with an unknown type: switch on the type</h3>
 * 
 * <pre>
 * final PVReader&lt;VType&gt; pvReader = PVManager.read(vType("channelName")).maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(new PVReaderListener() {
 * 
 *     public void pvChanged() {
 *         // We can switch on the full type
 *         if (pvReader.getValue() instanceof VDouble) {
 *             VDouble vDouble = (VDouble) pvReader.getValue();
 *             // Do something with a VDouble
 *         }
 *         // ...
 *     }
 * });
 * </pre>
 * 
 * 
 * <h3 id="v4">Working with an unknown type: register listener on type</h3>
 * 
 * <pre>
 * final PVReader&lt;VType&gt; pvReader = PVManager.read(vType("channelName")).maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(VDouble.class, new PVReaderListener() {
 * 
 *     public void pvChanged() {
 *         // We are already guaranteed that the cast succeeds
 *         // and that the value is not null
 *         VDouble vDouble = (VDouble) pvReader.getValue();
 *         System.out.println(vDouble.getValue());
 *         // ...
 *     }
 * });
 * </pre>
 * 
 * 
 * <h3 id="t1">Assembling a table</h3>
 * 
 * You can assemble a table by giving a desired rate expression for each cell,
 * organizing them by column. You can use constant expressions for labels or
 * values that do not change. 
 * 
 * <pre>
 * List&lt;String&gt; names = Arrays.asList("one", "two", "trhee");
 * final PVReader&lt;VTable&gt; pvReader = PVManager.read(vTable(
 *         column("Names", vStringConstants(names)),
 *         column("Values", latestValueOf(vType(names)))))
 *         .maxRate(ofMillis(100));
 * pvReader.addPVReaderListener(new PVReaderListener() {
 * 
 *     public void pvChanged() {
 *         VTable vTable = pvReader.getValue();
 *         // First column is the names
 *         String[] names = (String[]) vTable.getColumnArray(0);
 *         // Second column is the values
 *         double[] values = (double[]) vTable.getColumnArray(1);
 *         // ...
 *     }
 * });
 * </pre>
 * 
 * 
 * 
 * <h1> Package description</h1>
 * 
 * This package contains all the basic components of the PVManager framework
 * and the basic support for the language to define the creation.
 * <p>
 * There are two distinct parts in the PVManager framework. The first part
 * includes all the elements that deal with data directly: read from various
 * sources ({@link org.epics.pvmanager.DataSource}), performing computation ({@link org.epics.pvmanager.Function}),
 * collecting data ({@link org.epics.pvmanager.Collector}), scanning at the UI rate ({@link org.epics.pvmanager.Notifier})
 * and notify on appropriate threads.
 * <p>
 * The second part consists of an expression language that allows to define
 * how to connect the first set of objects with each other. {@link org.epics.pvmanager.expression.SourceRateExpression}
 * describes data as it's coming out at the network rate, {@link org.epics.pvmanager.expression.DesiredRateExpression}
 * defines data at the scanning rate for the UI, and {@link org.epics.pvmanager.ExpressionLanguage}
 * defines static methods that define the operator in the expression language.
 * <p>
 * Users can extend both the first part (by extending support for different types,
 * providing different support for different data source or creating new computation
 * elements) and the second part (by extending the language to support other cases.
 * All support for data types is relegated to separate packages: you can use
 * the same style to extend the framework to your needs.
 */
package org.epics.pvmanager;

