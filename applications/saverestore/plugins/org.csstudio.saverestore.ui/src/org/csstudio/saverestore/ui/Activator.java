/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.ui.util.IDBackedBlockingQueue;
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

    /**
     *
     * <code>IDBackedExecutor</code> is an executor backed by the {@link IDBackedBlockingQueue} and used primarily to
     * execute repetitive UI background tasks.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    private static class IDBackedExecutor extends ThreadPoolExecutor {

        IDBackedExecutor() {
            super(1, 1, 0, TimeUnit.NANOSECONDS, new IDBackedBlockingQueue(1000, true));
            setRejectedExecutionHandler((r, t) -> SaveRestoreService.LOGGER.log(Level.WARNING,
                "Execution of {0} rejected.", new Object[] { r }));
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            if (t != null) {
                SaveRestoreService.LOGGER.log(Level.WARNING, "Error executing request.", t);
            }
        }
    }

    private static final String MAX_NUMBER_OF_FILTERS = "maxNumberOfFilters";
    private static final String FILTERS = "filters";
    private static Activator defaultInstance;

    private ThreadPoolExecutor backgroundWorker;

    /**
     * Returns the executor to be used for background tasks. This executor is configured to work with
     * {@link RunnableWithID} implementations.
     *
     * @return the UI background tasks executor
     */
    public ExecutorService getBackgroundWorker() {
        if (backgroundWorker == null) {
            backgroundWorker = new IDBackedExecutor();
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
        defaultInstance = this;
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
        return defaultInstance;
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
    public void storeFilters(String[] newFilters) {
        String[] filters = newFilters;
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
        return num < 2 ? 20 : num;
    }
}
