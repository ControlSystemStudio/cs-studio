package org.csstudio.archive.config.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.archive.config.EngineConfig;

public class XMLFileUtil {

    protected static int MAX_DEPTH = 128;

    protected static Pattern xmlPattern = Pattern.compile("(.*)\\.xml$");

    protected final List<String> imported_files;

    protected final boolean verbose;

    public XMLFileUtil(boolean verbose) {
        imported_files = new ArrayList<String>();
        this.verbose = verbose;
    }

    public XMLFileUtil()
    {
        this(false);
    }

    public List<String> getImportedFiles() {
        return imported_files;
    }

    public abstract static class URLMap {
        public abstract String getURL(final String engine_name);
    }

    public static class SingleURLMap extends URLMap {
        final String the_url;

        public SingleURLMap(final String the_url) {
            this.the_url = the_url;
        }

        @Override
        public String getURL(final String engine_name) {
            return the_url;
        }
    }

    public static String getEngineName(final String fname) {
        Matcher m = xmlPattern.matcher(fname);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    protected void importFile(XMLArchiveConfig config, File file, final URLMap urlMap) throws Exception {
        final String engine_name = getEngineName(file.getName());
        if (engine_name == null) {
            Activator.getLogger().log(Level.INFO, "Ignoring non-engine file: " + file.getName());
        } else {
            if (verbose)
                System.out.println("Importing engine: " + engine_name);

            config.setParams(file.getCanonicalPath(), urlMap.getURL(engine_name));

            final EngineConfig econf = config.findEngine(null);
            if ((econf == null) || (!econf.getName().equals(engine_name))) {
                Activator.getLogger().log(Level.SEVERE,
                        "Failed to import engine [" + engine_name + "] from file: " + file.getName());
            } else {
                if (verbose)
                    System.out.println("Success importing from file: " + file.getCanonicalPath());
                imported_files.add(file.getCanonicalPath());
            }
        }
    }

    protected void recursiveImportAll(XMLArchiveConfig config, final File dir, final URLMap urlMap, int depth)
            throws Exception {
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                if (depth < MAX_DEPTH)
                    recursiveImportAll(config, file, urlMap, depth + 1);
                else
                    Activator.getLogger().log(Level.WARNING,
                            "Reached max depth on recursive import of all XML config files");
            } else {
                importFile(config, file, urlMap);
            }
        }
    }

    public void importAll(XMLArchiveConfig config, final String base_path, final URLMap urlMap)
            throws Exception {
        File f0 = new File(base_path);

        if (!f0.exists()) {
            throw new Exception("File or directory does not exist: " + f0.getAbsolutePath());
        }

        if (f0.isDirectory()) {
            recursiveImportAll(config, f0, urlMap, 0);
        }
        else {
            importFile(config, f0, urlMap);
        }
    }

}
