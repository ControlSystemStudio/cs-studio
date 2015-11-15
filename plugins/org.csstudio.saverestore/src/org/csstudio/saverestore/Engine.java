package org.csstudio.saverestore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

/**
 *
 * <code>Engine</code> provides the common utilities used by save and restore, such as the list of available data
 * providers, selected data providers, executor, logger.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Engine {

    private static final String DATA_PROVIDER_EXT_POINT = "org.csstudio.saverestore.dataprovider";
    /** The common logger */
    public static final Logger LOGGER = Logger.getLogger(Engine.class.getName());
    /** The name of the selectedDataProvider property */
    public static final String SELECTED_DATA_PROVIDER = "selectedDataProvider";
    /** The name of the is engine busy property */
    public static final String BUSY = "busy";

    private boolean busy = false;
    private List<DataProviderWrapper> dataProviders;
    private DataProviderWrapper selectedDataProvider;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    private static final Engine INSTANCE = new Engine();

    /**
     * @return the singleton instance of the engine
     */
    public static final Engine getInstance() {
        return INSTANCE;
    }

    private Engine() {
    }

    /**
     * @return the list of all registered data providers
     */
    public List<DataProviderWrapper> getDataProviders() {
        if (dataProviders == null) {
            List<DataProviderWrapper> dpw = new ArrayList<>();
            IExtensionRegistry extReg = org.eclipse.core.runtime.Platform.getExtensionRegistry();
            IConfigurationElement[] confElements = extReg.getConfigurationElementsFor(DATA_PROVIDER_EXT_POINT);
            for (IConfigurationElement element : confElements) {
                String name = element.getAttribute("name");
                try {
                    String id = element.getAttribute("id");
                    String description = element.getAttribute("description");
                    DataProvider provider = (DataProvider) element.createExecutableExtension("dataprovider");
                    dpw.add(new DataProviderWrapper(id, name, description, provider));
                } catch (CoreException e) {
                    Engine.LOGGER.log(Level.SEVERE, "Save and restore data provider '" + name + "' could not be loaded.", e);
                }
            }
            dataProviders = Collections.unmodifiableList(dpw);
            if (dataProviders.isEmpty()) {
                Engine.LOGGER.log(Level.SEVERE, "Save and restore data providers not found.");
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
        DataProviderWrapper oldValue = this.selectedDataProvider;
        this.selectedDataProvider = selectedDataProvider;
        support.firePropertyChange(SELECTED_DATA_PROVIDER, oldValue, this.selectedDataProvider);
    }

    /**
     * @return the selected data provider
     */
    public DataProviderWrapper getSelectedDataProvider() {
        return selectedDataProvider;
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
     * @return true if the engine is currently busy or false otherwise
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
     * Execute the runnable task on the common save and restore executor.
     *
     * @param task the task to execute
     */
    public void execute(final String taskName, final Runnable task) {
        Job job = new Job("Save & Restore: " + taskName) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(taskName, 1);
                try {
                    setBusy(true);
                    BusyIndicator.showWhile(Display.getCurrent(), task);
                    return Status.OK_STATUS;
                } finally {
                    monitor.done();
                    setBusy(false);
                }
            }
        };
        job.schedule();
    }
}
