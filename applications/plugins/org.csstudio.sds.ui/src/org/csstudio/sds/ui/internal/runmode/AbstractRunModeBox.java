/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.ui.internal.runmode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.dal.CssApplicationContext;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.csstudio.sds.internal.persistence.DisplayModelLoadAdapter;
import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.internal.runmode.RunModeBoxInput;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.IPropertyChangeListener;
import org.csstudio.sds.model.RuntimeContext;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.internal.editor.dnd.ProcessVariableDragSourceListener;
import org.csstudio.sds.ui.internal.editor.dnd.ProcessVariablesDragSourceListener;
import org.csstudio.sds.ui.internal.editparts.WidgetEditPartFactory;
import org.csstudio.sds.ui.internal.viewer.PatchedGraphicalViewer;
import org.csstudio.sds.ui.runmode.IDisplayLoadedCallback;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A box that manages a shell, which uses a GEF graphical viewer to display SDS
 * displays.
 *
 * @author Sven Wende, Alexander Will
 * @version $Revision: 1.30 $
 */
public abstract class AbstractRunModeBox {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRunModeBox.class);

    private boolean _disposed;

    /**
     * The viewer that displays the model.
     */
    private GraphicalViewer _graphicalViewer;

    /**
     * A List of DisposeListener.
     */
    private List<IRunModeDisposeListener> _disposeListeners;

    /**
     * An input stream for the display xml data.
     */
    private InputStream _inputStream;

    /**
     * The display model which should be shown.
     */
    private DisplayModel _displayModel;

    /**
     * The input for this box.
     */
    private final RunModeBoxInput _input;

    /**
     * Contains all property change listeners that will be added to the display
     * model or widgets.
     */
    private HashMap<WidgetProperty, IPropertyChangeListener> _propertyListeners;

    private IDisplayLoadedCallback callback;

    /**
     * Constructor.
     *
     * @param input
     *            the {@link RunModeBoxInput} for the model file that should be displayed
     */
    public AbstractRunModeBox(final RunModeBoxInput input)
            throws IllegalArgumentException {
        assert input != null;

        _input = input;
        _inputStream = getInputStream(input.getFilePath());
        _disposed = false;

        if (_inputStream == null) {
            throw new IllegalArgumentException("Cannot open display "
                    + input.getFilePath().toPortableString());
        }

        _disposeListeners = new ArrayList<IRunModeDisposeListener>();
        _propertyListeners = new HashMap<WidgetProperty, IPropertyChangeListener>();
    }

    /**
     * Open!
     */
    public void openRunMode(final Runnable runAfterOpen, final IDisplayLoadedCallback callback) {
        // Open the run mode representation

        this.callback = callback;
        // initialize model
        _displayModel = new DisplayModel();
        _displayModel.setLive(true);

        // load and connect the model
        PersistenceUtil.asyncFillModel(_displayModel, _inputStream,
                new DisplayModelLoadAdapter() {

                    @Override
                    public void onDisplayModelLoaded() {
                    }

                    public void onDisplayPropertiesLoaded() {
                        // expose runtime information to the model
                        RuntimeContext runtimeContext = new RuntimeContext(
                                _input.getFilePath(), _input.getAliases());
                        runtimeContext.setRunModeBoxInput(_input);

                        // .. we create a separate broker instance for each running display
                        runtimeContext.setBroker(SimpleDALBroker.newInstance(new CssApplicationContext("CSS")));
                        LOG.info("SimpleDALBroker instance created");

                        _displayModel.setRuntimeContext(runtimeContext);

                        final int x = _displayModel.getX();
                        final int y = _displayModel.getY();
                        final boolean openRelative = _displayModel.getOpenRelative();
                        final int width = _displayModel.getWidth();
                        final int height = _displayModel.getHeight();

                        PlatformUI.getWorkbench().getDisplay().syncExec(
                                new Runnable() {
                                    public void run() {
                                        Map<String, String> aliases = _input
                                                .getAliases();

                                        // create and open the viewer
                                        StringBuffer title = new StringBuffer();

                                        // title
                                        title.append(_input.getFilePath()
                                                .makeRelative()
                                                .toPortableString());

                                        if ((aliases != null)
                                                && !aliases.isEmpty()) {
                                            title.append("?");

                                            Iterator<String> it = aliases
                                                    .keySet().iterator();

                                            while (it.hasNext()) {
                                                String key = it.next();
                                                String val = aliases.get(key);
                                                title.append(key);
                                                title.append("=");
                                                title.append(val);
                                                title.append(it.hasNext() ? "&"
                                                        : "");
                                            }
                                        }

                                        _graphicalViewer = doOpen(x, y, openRelative, width,
                                                height, title.toString());

                                        // configure the viewer
                                        _graphicalViewer
                                                .setContents(_displayModel);

                                        String bgColor = _displayModel.getColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND);

                                        _graphicalViewer
                                                .getControl()
                                                .setBackground(SdsUiPlugin.getDefault().getColorAndFontService().getColor(bgColor));


                                        // execute the runnable
                                        if (runAfterOpen != null) {
                                            runAfterOpen.run();
                                        }
                                        callback.displayLoaded();
                                    }
                                });
                    }
                });
    }

    /**
     * Subclasses should open the necessary workbench elements (usually views or
     * shells), which should display a synoptic display using a GEF
     * {@link GraphicalViewer}.
     *
     * Subclasses should also take care for a clean shutdown handling, by adding
     * the necessary listeners to the created workbench parts which call
     * {@link #dispose()} on this box, in case the part is closed by the user.
     *
     * @param x
     *            x position hint
     * @param y
     *            y position hin
     * @param openRelative
     *               To be opened relative to predecessor displays
     * @param width
     *            width hint
     * @param height
     *            height hint
     * @param title
     *            a title
     * @return the {@link GraphicalViewer} which is used to display the model
     */
    protected abstract GraphicalViewer doOpen(int x, int y, boolean openRelative, int width,
            int height, String title);

    /**
     * Adds the given IRunModeDisposeListener to the internal List of
     * DisposeListeners.
     *
     * @param listener
     *            The IRunModeDisposeListener, which should be added
     */
    public void addDisposeListener(final IRunModeDisposeListener listener) {
        if (!_disposeListeners.contains(listener)) {
            _disposeListeners.add(listener);
        }
    }

    /**
     * Removes the given IRunModeDisposeListener from the internal List of
     * DisposeListeners.
     *
     * @param listener
     *            The IRunModeDisposeListener, which should be removed
     */
    public void removeDisposeListener(final IRunModeDisposeListener listener) {
        if (_disposeListeners.contains(listener)) {
            _disposeListeners.remove(listener);
        }
    }

    /**
     * Notifies all registered IRunModeDisposeListener, that this RunModeBox is
     * disposed.
     */
    private void fireDispose() {
        if (_disposeListeners != null) {
            for (IRunModeDisposeListener l : _disposeListeners) {
                l.dispose();
            }
        }
    }

    /**
     * Disposes the shell.
     */
    public final synchronized void dispose() {
        if(!_disposed) {
            _disposed = true;

            // remove all change listeners
            for (WidgetProperty p : _propertyListeners.keySet()) {
                p.removePropertyChangeListener(_propertyListeners.get(p));
            }

            // let subclasses do their job
            doDispose();

            // inform listeners that this box has been disposed
            fireDispose();

            // kill broker
            RuntimeContext context = _displayModel.getRuntimeContext();

            if(context!=null) {
                SimpleDALBroker broker = context.getBroker();
                broker.releaseAll();
                context.setBroker(null);
                LOG.info("SimpleDALBroker instance released.");
                callback.displayClosed();
            }

            // forget all referenced objects
            _graphicalViewer = null;
            _displayModel = null;
            _disposeListeners = null;
            _inputStream = null;
            _propertyListeners = null;
            _disposed = true;
        }
    }

    protected RunModeBoxInput getInput() {
        return _input;
    }

    protected abstract void doDispose();

    protected abstract void handleWindowPositionChange(int x, int y, int width,
            int height);

    /**
     * Sets the focus on this Shell.
     */
    public abstract void bringToTop();

    /**
     * Creates a graphical viewer that can be used to display SDS models.
     *
     * @param parent
     *            the parent composite
     *
     * @return a graphical viewer that can be used to display SDS models
     */
    static GraphicalViewer createGraphicalViewer(final Composite parent) {

        final PatchedGraphicalViewer viewer = new PatchedGraphicalViewer();
        viewer.createControl(parent);

        viewer.setEditPartFactory(new WidgetEditPartFactory(
                ExecutionMode.RUN_MODE));

        final ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
        viewer.setRootEditPart(root);

        EditDomain editDomain = new EditDomain();

        final SelectionTool tool = new SelectionTool();
        tool.setUnloadWhenFinished(false);
        editDomain.setDefaultTool(tool);
        editDomain.addViewer(viewer);

        // initialize drag support (order matters!)
        viewer.addDragSourceListener(new ProcessVariableDragSourceListener(viewer));
        viewer.addDragSourceListener(new ProcessVariablesDragSourceListener(viewer));
//        viewer.addDragSourceListener(new ProcessVariableDragSourceListener(viewer));
//        viewer.addDragSourceListener(new TextTransferDragSourceListener(viewer));

        return viewer;
    }

    /**
     * Return the {@link InputStream} from the given path.
     *
     * @param path
     *            The {@link IPath} to the file
     * @return The corresponding {@link InputStream}
     */
    private InputStream getInputStream(final IPath path) {
        InputStream result = null;

        // try workspace

        IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(path,
                false);
        if (r instanceof IFile) {
            try {
                result = ((IFile) r).getContents();
            } catch (CoreException e) {
                result = null;
            }
        }

        if (result == null) {
            // try from local file system
            try {
                result = new FileInputStream(path.toFile());
            } catch (FileNotFoundException e) {
                result = null;
            }

        }

        return result;
    }

    public abstract Point getCurrentLocation();

    public DisplayModel getDisplayModel() {
        return _displayModel;
    }
}
