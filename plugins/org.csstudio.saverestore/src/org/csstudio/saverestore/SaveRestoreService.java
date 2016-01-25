package org.csstudio.saverestore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 *
 * <code>Engine</code> provides the common utilities used by save and restore, such as the list of available data
 * providers, selected data providers, executor, logger.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SaveRestoreService {

    /** mutex rule takes care that no two save and restore jobs can be executed at the same time */
    private static ISchedulingRule mutexRule = new ISchedulingRule() {
        @Override
        public boolean isConflicting(ISchedulingRule rule) {
            return rule == this;
        }

        @Override
        public boolean contains(ISchedulingRule rule) {
            return rule == this;
        }
    };

    private static final int WAIT_PERIOD = 100;

    /**
     * <code>RunnableWrapper</code> is a wrapper for {@link Runnable} tasks, which upon completion notifies all
     * monitors locked on the object instance.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    private static class RunnableWrapper implements Runnable {
        private final Runnable task;
        private boolean completed = false;

        RunnableWrapper(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            task.run();
            synchronized (this) {
                completed = false;
                this.notifyAll();
            }
        }
    }

    /**
     * <code>SaveRestoreJob</code> is a cancellable job for executing save and restore tasks. Once the job has been
     * cancelled the {@link #isCancelled()} method returns true, which allows other objects to check the current state
     * of the job.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    private class SaveRestoreJob extends Job {

        private final String taskName;
        private final Runnable task;
        private volatile boolean cancelled = false;

        SaveRestoreJob(String taskName, Runnable task) {
            super("Save and Restore: " + taskName);
            this.taskName = taskName;
            this.task = task;
            setRule(mutexRule);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            monitor.beginTask(taskName, 1);
            setCurrentJob(this);
            try {
                setBusy(true);
                RunnableWrapper wrapper = new RunnableWrapper(task);
                Future<?> done = executor.submit(wrapper);
                while (!done.isDone()) {
                    synchronized (wrapper) {
                        // could be synchronized on done, but is is not recommended to lock on an object from
                        // java.util.concurrent
                        if (wrapper.completed) {
                            break;
                        } else {
                            wrapper.wait(WAIT_PERIOD);
                        }
                    }
                    if (monitor.isCanceled()) {
                        cancelled = true;
                    }
                }
                return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
            } catch (InterruptedException e) {
                monitor.setCanceled(true);
                return Status.CANCEL_STATUS;
            } finally {
                setCurrentJob(null);
                monitor.done();
                setBusy(false);
            }
        }

        boolean isCancelled() {
            return cancelled;
        }
    }

    /** Property that defines the maximum number of snapshots loaded in a single call */
    public static final String PREF_NUMBER_OF_SNAPSHOTS = "maxNumberOfSnapshotsInBatch";
    private static final String PLUGIN_ID = "org.csstudio.saverestore";

    /** The common logger */
    public static final Logger LOGGER = Logger.getLogger(SaveRestoreService.class.getName());
    /** The name of the selectedDataProvider property */
    public static final String SELECTED_DATA_PROVIDER = "selectedDataProvider";
    /** The name of the is engine busy property */
    public static final String BUSY = "busy";

    private boolean busy = false;
    private List<DataProviderWrapper> dataProviders;
    private DataProviderWrapper selectedDataProvider;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);
    private IPreferenceStore preferences;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private SaveRestoreJob currentJob;
    private static final SaveRestoreService INSTANCE = new SaveRestoreService();

    /**
     * Returns the singleton instance of this service.
     *
     * @return the singleton instance
     */
    public static final SaveRestoreService getInstance() {
        return INSTANCE;
    }

    private SaveRestoreService() {
    }

    /**
     * Returns the number of currently loaded data providers. If the providers have not been loaded yet, 0 is returned.
     *
     * @return number of registered and loaded data providers
     */
    public int getDataProvidersCount() {
        return dataProviders == null ? 0 : dataProviders.size();
    }

    /**
     * Loads the data provider extension points and returns them as a list.
     *
     * @return the list of all registered data providers
     */
    public List<DataProviderWrapper> getDataProviders() {
        if (dataProviders == null) {
            List<DataProviderWrapper> dpw = new ArrayList<>();
            IExtensionRegistry extReg = org.eclipse.core.runtime.Platform.getExtensionRegistry();
            IConfigurationElement[] confElements = extReg.getConfigurationElementsFor(DataProvider.EXT_POINT);
            for (IConfigurationElement element : confElements) {
                String name = element.getAttribute("name");
                try {
                    String id = element.getAttribute("id");
                    String description = element.getAttribute("description");
                    DataProvider provider = (DataProvider) element.createExecutableExtension("dataprovider");
                    dpw.add(new DataProviderWrapper(id, name, description, provider));
                } catch (CoreException e) {
                    SaveRestoreService.LOGGER.log(Level.SEVERE,
                        "Save and restore data provider '" + name + "' could not be loaded.", e);
                }
            }
            dataProviders = Collections.unmodifiableList(dpw);
            LOGGER.log(Level.FINE, "Data providers loaded: " + dataProviders);
            if (dataProviders.isEmpty()) {
                SaveRestoreService.LOGGER.log(Level.SEVERE, "Save and restore data providers not found.");
            }
        }
        return dataProviders;
    }

    /**
     * Set the selected data provider and fires a property change event.
     *
     * @param selectedDataProvider the data provider to select
     */
    public void setSelectedDataProvider(DataProviderWrapper selectedDataProvider) {
        if (this.selectedDataProvider != null && this.selectedDataProvider.equals(selectedDataProvider))
            return;
        DataProviderWrapper oldValue = this.selectedDataProvider;
        this.selectedDataProvider = selectedDataProvider;
        if (this.selectedDataProvider != null) {
            final DataProvider provider = this.selectedDataProvider.provider;
            SaveRestoreService.getInstance().execute("Data Provider Initialise", () -> provider.initialise());
            LOGGER.log(Level.FINE, "Selected data provider: " + selectedDataProvider.getPresentationName());
        }
        support.firePropertyChange(SELECTED_DATA_PROVIDER, oldValue, this.selectedDataProvider);
    }

    /**
     * Returns the data provider wrapper that is currently selected.
     *
     * @return the selected data provider
     */
    public DataProviderWrapper getSelectedDataProvider() {
        return selectedDataProvider;
    }

    /**
     * Returns the data provider for the specified id. If no data provider for that id is found, the selected one is
     * returned.
     *
     * @param id the requested data provider id
     * @return data provider for the given id
     */
    public DataProviderWrapper getDataProvider(String id) {
        if (id != null) {
            for (DataProviderWrapper dpw : dataProviders) {
                if (dpw.id.equals(id)) {
                    return dpw;
                }
            }
        }
        return getSelectedDataProvider();
    }

    /**
     * Adds a property change listener that receives notifications when the value of a property changes.
     *
     * @param propertyName the name of the property to register to
     * @param listener the listener to register
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        support.addPropertyChangeListener(propertyName, listener);
        if (SELECTED_DATA_PROVIDER.equals(propertyName) && this.selectedDataProvider != null) {
            listener.propertyChange(new PropertyChangeEvent(this, propertyName, null, this.selectedDataProvider));
        }
    }

    /**
     * Whenever the service is executing a task it is flagged as busy, which can be used as an indicator on the UI to
     * tell the user that something is happening.
     *
     * @return true if the service is currently busy or false otherwise
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * Sets the busy flag for the engine.
     *
     * @param busy true if the engine is busy or false otherwise
     */
    private void setBusy(boolean busy) {
        if (this.busy == busy) {
            return;
        }
        this.busy = busy;
        support.firePropertyChange(BUSY, !busy, busy);
    }

    /**
     * Service can load the snapshots all at once or in batches (to reduce the network load). In case when the snapshots
     * are loaded in batches, the number returned by this method defines the size of the batch.
     *
     * @return number of snapshots loaded from the repository at once (in a single call)
     */
    public int getNumberOfSnapshots() {
        return getPreferences().getInt(PREF_NUMBER_OF_SNAPSHOTS);
    }

    /**
     * Returns the preference store for this plugin.
     *
     * @return the preferences store of this plugin
     */
    public IPreferenceStore getPreferences() {
        if (preferences == null) {
            preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, PLUGIN_ID);
        }
        return preferences;
    }

    /**
     * Execute the runnable task on the background task executor. It is guaranteed that the tasks will be executed in
     * the order as they have been submitted and not two tasks will ever run simultaneously.
     *
     * @param task the task to execute
     */
    public void execute(final String taskName, final Runnable task) {
        new SaveRestoreJob(taskName, task).schedule();
    }

    /**
     * Sets the job that is currently being executed. The jobs are defined in a way that at any given time only one job
     * is being executed.
     *
     * @param job the job that is currently being executed (can be null)
     */
    private void setCurrentJob(SaveRestoreJob job) {
        synchronized (this) {
            this.currentJob = job;
        }
    }

    /**
     * Checks if the job that is currently being executed has been cancelled or not. If the job has been cancelled,
     * method returns true, if the job is still running, method returns false. If there is no job currently running,
     * method also returns true. Tasks that usually take a long time to execute, should periodically check this value to
     * see if the task has been requested to cancel. If the method returns true, the executed task should terminate
     * gracefully.
     *
     * @return true if the current job has been cancelled, or there is no job currently running, or false if the current
     *         job is still running
     */
    public boolean isCurrentJobCancelled() {
        synchronized (this) {
            return this.currentJob == null ? true : this.currentJob.isCancelled();
        }
    }
}
