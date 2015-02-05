/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.jms.Connection;

import org.csstudio.alarm.beast.annunciator.model.AnnunciationMessage;
import org.csstudio.alarm.beast.annunciator.model.JMSAnnunciator;
import org.csstudio.alarm.beast.annunciator.model.JMSAnnunciatorListener;
import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.platform.utility.jms.JMSConnectionFactory;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** Eclipse Application for the JMS-to-speech tool
 *
 *  @author Kay Kasemir
 *  @author Katia Danilova
 *
 *     reviewed by Delphy 1/29/09
 */
@SuppressWarnings("nls")
public class Application implements IApplication, JMSAnnunciatorListener
{
    /** Flag to control if the application runs or stops */
    private boolean run = true;
    
    /** {@inheritDoc} */
	@Override
    public Object start(IApplicationContext context) throws Exception
	{
	    // Stupid, silly, annoying:
	    // Not sure what exactly happened, but after a JVM update from Apple
	    // around Oct. 2008 the FreeTTS library would hang when called from
	    // an Eclipse application. Looks like it uses some AWT features
	    // which are available from a plain old main() program as well
	    // as Swing apps, but not headless Eclipse apps.
	    // With OS X 10.6.8 and the 1.6.0_26 JDK this is no longer
	    // necessary, in fact it will cause the annunciator to hang
	    // in new JFrame()
//	    if (System.getProperty("os.name").startsWith("Mac"))
//	    {
//    	    //Create Swing window, which triggers some
//	        // Java CocoaComponent compatibility crap ...
//            JFrame frame = new JFrame("JMS2SPEECH");
//            frame.pack();
//            frame.setVisible(true);
//	    }

		final String url = Preferences.getURL();
		final String topics[] = Preferences.getTopics();

		// Display configuration info
        final String version = (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
        final String app_info = context.getBrandingName() + " " + version;

		// Create parser for arguments and run it.
		final String args[] = (String[]) context.getArguments().get("application.args");

		final ArgParser parser = new ArgParser();
		final BooleanOption help_opt = new BooleanOption(parser, "-help", "Display help");
		final BooleanOption version_opt = new BooleanOption(parser, "-version", "Display version info");
		parser.addEclipseParameters();
		try {
			parser.parse(args);
		} catch (final Exception ex) {
			System.out.println(ex.getMessage() + "\n" + parser.getHelp());
			return IApplication.EXIT_OK;
		}
		if (help_opt.get()) {
			System.out.println(app_info + "\n\n" + parser.getHelp());
			return IApplication.EXIT_OK;
		}
		if (version_opt.get()) {
			System.out.println(app_info);
			return IApplication.EXIT_OK;
		}

        // Put startup info with JMS topic & URL into log
        Logger.getLogger(Activator.PLUGIN_ID).info
		    ("JMS2SPEECH " + version + ", topic " + Arrays.toString(topics) + " @ " + url);

        final Connection connection = JMSConnectionFactory.connect(url);

        final JMSAnnunciator annunciator = new JMSAnnunciator(this, connection, topics,
                Preferences.getTranslationsFile(),
                Preferences.getThreshold());

        annunciator.start();

		// Run for some time. Actually forever.
        while (run)
            Thread.sleep(1000);

        // Stop receiver
        annunciator.close();

        return IApplication.EXIT_OK;
	}

    /** {@inheritDoc} */
	@Override
    public void stop()
	{
	    run = false;
    }

	/** {@inheritDoc} */
    @Override
    public void performedAnnunciation(final AnnunciationMessage annunciation)
    {
        System.out.println(annunciation);
    }

    /** {@inheritDoc} */
    @Override
    public void annunciatorError(final Throwable ex)
    {
        ex.printStackTrace();
        run = false;
    }
}
