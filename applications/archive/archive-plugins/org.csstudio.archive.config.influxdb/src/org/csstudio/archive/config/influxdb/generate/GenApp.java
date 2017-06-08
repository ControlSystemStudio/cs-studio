/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.influxdb.generate;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.args.ArgParser;
import org.csstudio.apputil.args.BooleanOption;
import org.csstudio.apputil.args.StringOption;
import org.csstudio.archive.config.influxdb.Activator;
import org.csstudio.archive.config.xml.XMLArchiveConfig;
import org.csstudio.archive.config.xml.XMLFileUtil;
import org.csstudio.archive.config.xml.XMLFileUtil.SingleURLMap;
import org.csstudio.archive.influxdb.InfluxDBUtil.ConnectionInfo;
import org.csstudio.archive.writer.influxdb.InfluxDBArchiveWriter;
import org.csstudio.security.PasswordInput;
import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Eclipse Application for CSS archive engine
 *
 * @author Megan Grodowitz
 */
public class GenApp implements IApplication
{
    /** Request file */
    private String root_file;

    private boolean verbose;
    private boolean skip_pv_sample;
    private boolean skip_db_check;

    private Instant start_ts, stop_ts;

    String[] getPrefValue(final String option) {
        if (option != null) { // Split "plugin/key=value"
            String[] pref_val = new String[2];
            final int sep = option.indexOf("=");
            if (sep >= 0) {
                pref_val[0] = option.substring(0, sep);
                pref_val[1] = option.substring(sep + 1);
            } else {
                pref_val[0] = option;
                pref_val[1] = null;
            }
            return pref_val;
        }
        return null;
    }

    void printVersion(final IApplicationContext context) {
        final String version = context.getBrandingBundle().getHeaders().get("Bundle-Version");
        final String app_info = context.getBrandingName() + " " + version;
        System.out.println(app_info);
    }

    void printHelp(final IApplicationContext context, final ArgParser parser) {
        printVersion(context);
        System.out.println("\n\n" + parser.getHelp());
    }

    void printUsageError(final IApplicationContext context, final ArgParser parser, final String msg) {
        printHelp(context, parser);
        System.err.println(msg);
    }

    Duration parseDuration(final String durstr) {
        String[] splitstr = durstr.split(":");
        if ((splitstr.length < 1) || (splitstr.length > 4))
            return null;

        Integer[] durarr = new Integer[splitstr.length];
        for (int idx = 0; idx < splitstr.length; idx++)
        {
            try {
                durarr[splitstr.length - idx - 1] = Integer.valueOf(splitstr[idx]);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        Duration dur = Duration.ofSeconds(durarr[0]);
        if (durarr.length < 2)
            return dur;

        dur = dur.plusMinutes(durarr[1]);
        if (durarr.length < 3)
            return dur;

        dur = dur.plusHours(durarr[2]);
        if (durarr.length < 4)
            return dur;

        dur = dur.plusDays(durarr[3]);
        return dur;
    }

    Instant parseStartTime(final String startstr) {
        try {
            return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(startstr));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Obtain settings from preferences and command-line arguments
     *  @param args Command-line arguments
     *  @return <code>true</code> if continue, <code>false</code> to end application
     */
    @SuppressWarnings("nls")
    private boolean getSettings(final String args[], final IApplicationContext context)
    {
        // Create the parser and run it.
        final ArgParser parser = new ArgParser();
        final BooleanOption help_opt = new BooleanOption(parser, "-help", "Display help", false);
        final BooleanOption version_opt = new BooleanOption(parser, "-version", "Display version info", false);
        final BooleanOption verbose_opt = new BooleanOption(parser, "-verbose", "Verbose status output", false);
        final BooleanOption skip_db_check_opt = new BooleanOption(parser, "-skip_db_check",
                "Do not check if generated databases already exist", false);
        final BooleanOption skip_pv_sample_opt = new BooleanOption(parser, "-skip_pv_sample",
                "Skip sampling of PVs for real values, all PVs will default to double types", false);
        final StringOption root_file_opt = new StringOption(parser, "-root_file", "path/to/fileordir",
                "Engine file to import or directory tree root with engine files to import", null);
        final StringOption preference_opt = new StringOption(parser, "-set_pref", "plugin.name/preference=value",
                "Set a preference for a specific plugin", null);
        final StringOption set_password_opt = new StringOption(parser,
                "-set_password", "plugin/key=value", "Set secure preferences", null);
        final StringOption duration_opt = new StringOption(parser, "-duration", "DD:HH:mm:ss",
                "Duration of data to generate", null);
        final StringOption start_time_opt = new StringOption(parser, "-start_date", "ISO_INSTANT",
                "First timestamp of generated data in ISO/UTC, defaults to now - duration. E.g: 2016-10-31T06:52:20.020Z",
                null);

        parser.addEclipseParameters();
        try
        {
            parser.parse(args);
        }
        catch (final Exception ex)
        {   // Bad options
            printUsageError(context, parser, ex.getMessage());
            return false;
        }

        verbose = verbose_opt.get();
        skip_pv_sample = skip_pv_sample_opt.get();
        skip_db_check = skip_db_check_opt.get();

        if (help_opt.get())
        {   // Help requested
            printHelp(context, parser);
            return false;
        }
        if (version_opt.get())
        {   // Version requested
            printVersion(context);
            return false;
        }

        final String durstr = duration_opt.get();
        if (durstr == null) {
            printUsageError(context, parser, "Must specificy duration option: " + duration_opt.getOption());
            return false;
        }
        final Duration dur = parseDuration(durstr);
        if (dur == null) {
            printUsageError(context, parser, "Format error in duration: " + durstr);
            return false;
        }
        final String startstr = start_time_opt.get();
        if (startstr == null) {
            start_ts = Instant.now().minus(dur);
        }
        else {
            start_ts = parseStartTime(startstr);
            if (start_ts == null) {
                printUsageError(context, parser, "Format error in start time: " + startstr);
                return false;
            }
        }
        stop_ts = start_ts.plus(dur);

        if (verbose) {
            System.out.println("Simulating " + dur + " worth of data");
            System.out.println("\tStart @: " + start_ts);
            System.out.println("\tStop  @: " + stop_ts);
        }

        root_file = root_file_opt.get();
        if (root_file == null) {
            printUsageError(context, parser, "Must specificy root file option: " + root_file_opt.getOption());
            return false;
        }

        String[] pref_val = getPrefValue(set_password_opt.get());
        if (pref_val != null)
        {
            if (pref_val[1] == null)
            {
                pref_val[1] = PasswordInput.readPassword("Value for " + pref_val[0] + ":");
            }
            try
            {
                SecurePreferences.set(pref_val[0], pref_val[1]);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return false;
        }

        for (final String pref_opt : preference_opt.getMany())
        {
            pref_val = getPrefValue(pref_opt);
            if (pref_val != null)
            {
                final String pref = pref_val[0];
                final String value = pref_val[1];

                if (value == null) {
                    printUsageError(context, parser, "Malformed option " + preference_opt.getOption() + " " + pref_opt);
                    return false;
                }
                final int sep = pref.indexOf('/');
                if (sep < 0) {
                    printUsageError(context, parser,
                            "Malformed plugin/preference for option " + preference_opt.getOption() + " " + pref_opt);
                    return false;
                }
                final String plugin = pref.substring(0, sep);
                final String preference = pref.substring(sep + 1);

                final IEclipsePreferences pref_node = InstanceScope.INSTANCE.getNode(plugin);
                pref_node.put(preference, value);
                try {
                    pref_node.flush();
                } catch (BackingStoreException e) {
                    Activator.getLogger().log(Level.SEVERE,
                            "Could not set plugin preference " + plugin + "/" + preference, e);
                }
            }
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("nls")
    public Object start(final IApplicationContext context) throws Exception
    {
        final String args[] =
            (String []) context.getArguments().get("application.args");
        if (!getSettings(args, context))
            return Integer.valueOf(-2);

        // Initialize logging
        // LogConfigurator.configureFromPreferences();

        final Logger logger = Activator.getLogger();
        final XMLFileUtil util = new XMLFileUtil(verbose);

        final String dummy_url = "foo://foo.bar";
        final XMLArchiveConfig config = new XMLArchiveConfig();

        util.importAll(config, root_file, new SingleURLMap(dummy_url));

        if (verbose) {
            final List<String> files = util.getImportedFiles();
            System.out.println("Files Imported: ");
            for (String file : files) {
                System.out.println("\t" + file);
            }
        }

        final InfluxDBArchiveWriter writer;

        try {
            writer = new InfluxDBArchiveWriter();
        } catch (Exception e) {
            throw new Exception("Could not create archive writer", e);
        }

        final ConnectionInfo ci = writer.getConnectionInfo();
        System.out.println("Generating databases into InfluxDB instance: " + ci);

        if (!skip_db_check) {
            final List<String> existing_dbs = ci.dbs;
            final List<String> gen_dbs = writer.getQueries().getAllDBNames();

            for (String db : gen_dbs) {
                if (existing_dbs.contains(db)) {
                    System.err.println("InfluxDB instance already contains database: " + db);
                    System.err.println("See -help for option to skip database checking");
                    return Integer.valueOf(-2);
                }
            }
        }

        // Create the databases
        writer.getQueries().initDatabases(ci.influxdb);

        ChannelGenerator gen = new ChannelGenerator(config, writer, start_ts, skip_pv_sample);
        long steps = 0;
        long max_steps = Long.MAX_VALUE;

        Double total_secs = (double) Duration.between(start_ts, stop_ts).getSeconds();
        Double perc = 0.0;

        try
        {
            boolean run = true;
            while (run)
            {
                final Instant current = gen.step();

                if (current.isAfter(stop_ts))
                    run = false;

                steps++;
                if (steps >= max_steps)
                    run = false;

                if (verbose) {
                    Double current_secs = (double) Duration.between(start_ts, current).getSeconds();
                    Double new_perc = (current_secs / total_secs) * 100.0;
                    if ((new_perc - perc) > 5.0) {
                        perc = new_perc;
                        System.out.format("%4.1f%%: " + current + "\n", perc);
                    }
                }
            }
            logger.info("ArchiveEngine stopped");
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Unhandled Main Loop Error", ex);
            return Integer.valueOf(-1);
        }

        return EXIT_OK;
    }

    /** {@inheritDoc} */
    @Override
    public void stop()
    {
    }
}
