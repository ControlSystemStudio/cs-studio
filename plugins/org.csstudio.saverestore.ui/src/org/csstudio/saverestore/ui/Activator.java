package org.csstudio.saverestore.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.ui.util.DismissableBlockingQueue;
import org.csstudio.saverestore.ui.util.RunnableWithID;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 *
 * <code>Activator</code> is the bundle activator for the saverestore ui plugin.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Activator extends AbstractUIPlugin {

    private static final String MAX_NUMBER_OF_FILTERS = "maxNumberOfFilters";
    private static final String FILTERS = "filters";
    /** The shared instance */
    private static Activator plugin;

    private ThreadPoolExecutor backgroundWorker;

    /**
     * Returns the executor to be used for background tasks. This executor is configured to work with
     * {@link RunnableWithID} implementations.
     *
     * @return the UI background tasks executor
     */
    public ExecutorService getBackgroundWorker() {
        if (backgroundWorker == null) {
            backgroundWorker = new ThreadPoolExecutor(1, 1, 0, TimeUnit.NANOSECONDS,
                new DismissableBlockingQueue(1000, true)) {
                @Override
                protected void afterExecute(Runnable r, Throwable t) {
                    super.afterExecute(r, t);
                    if (t != null) {
                        SaveRestoreService.LOGGER.log(Level.WARNING, "Error executing request.", t);
                    }
                }
            };
            backgroundWorker.setRejectedExecutionHandler(
                (r, t) -> SaveRestoreService.LOGGER.warning("Execution of " + r + " rejected."));
        }
        return backgroundWorker;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        Activator.plugin = this;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        if (backgroundWorker != null) {
            backgroundWorker.shutdownNow();
            backgroundWorker = null;
        }
        super.stop(context);
    }

    /**
     * @return the shared instance.
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns the regular expression filters used for filtering the list of PVs in the editor.
     *
     * @return the filters (never null)
     */
    public String[] getFilters() {
        String[] filters = getDialogSettings().getArray("filters");
        if (filters == null) {
            filters = new String[0];
        } else if (filters.length > getMaxNumberOfFilters()) {
            String[] newArray = new String[getMaxNumberOfFilters()];
            System.arraycopy(filters, 0, newArray, 0, newArray.length);
            filters = newArray;
        }
        return filters;
    }

    /**
     * Store the filters to make them available at next start of CSS.
     *
     * @param filters the filters to store
     */
    public void storeFilters(String[] filters) {
        if (filters == null) {
            filters = new String[0];
        } else if (filters.length > getMaxNumberOfFilters()) {
            String[] newArray = new String[getMaxNumberOfFilters()];
            System.arraycopy(filters, 0, newArray, 0, newArray.length);
        }
        getDialogSettings().put(FILTERS, filters);
    }

    /**
     * Returns the maximum number of filters that are displayed in the UI and stored.
     *
     * @return the maximum number of filters
     */
    public int getMaxNumberOfFilters() {
        int num = getPreferenceStore().getInt(MAX_NUMBER_OF_FILTERS);
        if (num < 2) {
            num = 20;
        }
        return num;
    }
}
