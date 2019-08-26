package org.csstudio.phoebus.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * A simple service for launching applications in phoebus.
 *
 * @author Kunal Shroff
 *
 */
public class PhoebusLauncherService {

    private static final Logger logger = Logger.getLogger(PhoebusLauncherService.class.getName());

    static final IPreferencesService prefs = Platform.getPreferencesService();

    // Use the pre installed phoebus launcher script
    private static boolean phoebus_use_system_launcher = prefs.getBoolean(Activator.PLUGIN_ID, PreferenceConstants.PhoebusUseSystemLauncher, true, null);
    private static String phoebus_system_launcher_name = prefs.getString(Activator.PLUGIN_ID, PreferenceConstants.PhoebusSystemLauncherName, "phoebus", null);

    // Launch phoebus from the produce jar
    private static String phoebus_location = prefs.getString(Activator.PLUGIN_ID, PreferenceConstants.PhoebusHome, "/phoebus", null);
    private static String phoebus_version = prefs.getString(Activator.PLUGIN_ID, PreferenceConstants.PhoebusVersion, "0.0.1-SNAPSHOT", null);

    private static int phoebus_port = prefs.getInt(Activator.PLUGIN_ID, PreferenceConstants.PhoebusPort, -1, null);

    private static final int port;

    static {
        port = findFreePort();
    }

    private PhoebusLauncherService() {
    }

    static private synchronized void launchPhoebus(List<String> processArguments) {
        try {
            ProcessBuilder pb = new ProcessBuilder(processArguments);
            pb.redirectErrorStream(true);
            pb.directory(new File(phoebus_location));

            Process process = pb.start();
            // And print each line
            StreamLogger streamLogger = new StreamLogger(process.getInputStream(), logger);
            Executors.newSingleThreadExecutor().submit(streamLogger);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launch the phoebus empty application.
     */
    public static void launch() {
        launchPhoebus(basicArguments());
    }

    /**
     * Launch the phoebus framework with the listed applications started.
     *
     * @param appNames
     *            list of applicaiton names to be launched.
     */
    public static void launchApplication(String... appNames) {
        List<String> args = basicArguments();
        for (String appName : appNames) {
            args.add("-app");
            args.add(appName);
        }

        launchPhoebus(args);
    }

    /**
     * Launch the phoebus framework with for the list of resources provided.
     *
     * @param resources
     *            list of resources to be launched with their associated phoebus
     *            applications.
     */
    public static void launchResource(String... resources) {
        List<String> args = basicArguments();
        for (String resource : resources) {
            args.add("-resource");
            args.add(resource);
        }

        launchPhoebus(args);
    }

    /**
     * creates a list of basic arguments needed to lauch the phoebus framework
     *
     * @return
     */
    private static List<String> basicArguments() {
        List<String> processArguments = new ArrayList<>();
        if (phoebus_use_system_launcher && findExecutableOnPath(phoebus_system_launcher_name).isPresent()) {
            File phoebusFile = findExecutableOnPath(phoebus_system_launcher_name).get();
            phoebus_location = phoebusFile.getParent();
            processArguments.add(phoebusFile.getAbsolutePath());
            processArguments.add("-server");
            processArguments.add(String.valueOf(port));
        } else {
            processArguments.add("java");
            processArguments.add("-jar");
            processArguments.add("product-" + phoebus_version + ".jar");
            if (phoebus_port > 0) {
                processArguments.add("-server");
                processArguments.add(String.valueOf(phoebus_port));
            }
        }
        return processArguments;
    }

    /**
     *
     * @param name
     * @return
     */
    private static Optional<File> findExecutableOnPath(String name) {
        for (String dirname : System.getenv("PATH").split(File.pathSeparator)) {
            File file = new File(dirname, name);
            if (file.isFile() && file.canExecute()) {
                return Optional.ofNullable(file);
            }
        }
        return Optional.empty();
    }

    /**
     * Search for a free port
     * @return portNumber
     */
    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
        }
        return -1;
    }

    /**
     * Consumes an input stream and outputs each line to a logger
     *
     * @author Kunal Shroff
     *
     */
    private static class StreamLogger implements Runnable {
        private InputStream inputStream;
        private Logger logger;

        public StreamLogger(InputStream inputStream, Logger logger) {
            this.inputStream = inputStream;
            this.logger = logger;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(s -> logger.info(s));
        }
    }
}
