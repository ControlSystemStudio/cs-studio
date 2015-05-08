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
package org.csstudio.sds.ui.runmode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.internal.runmode.RunModeBoxInput;
import org.csstudio.sds.internal.runmode.RunModeType;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.internal.runmode.AbstractRunModeBox;
import org.csstudio.sds.ui.internal.runmode.DisplayViewPart;
import org.csstudio.sds.ui.internal.runmode.IRunModeDisposeListener;
import org.csstudio.sds.ui.internal.runmode.ShellRunModeBox;
import org.csstudio.sds.ui.internal.runmode.ViewRunModeBox;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMemento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Service, which manages the run mode versions of graphical viewers.
 *
 * @author Sven Wende
 */
public final class RunModeService {
    private static final Logger LOG = LoggerFactory.getLogger(RunModeService.class);

    private static final String SEPARATOR = "°°°";

    /**
     * The singleton instance.
     */
    private static RunModeService _instance;

    /**
     * A Map of the already displayed IFiles and their RunModeBoxes.
     */
    private HashMap<RunModeBoxInput, AbstractRunModeBox> _activeBoxes;

    private ArrayList<IOpenDisplayListener> _openDisplayListener;

    /**
     * Constructor.
     */
    private RunModeService() {
        _activeBoxes = new HashMap<RunModeBoxInput, AbstractRunModeBox>();
        _openDisplayListener = new ArrayList<IOpenDisplayListener>();
    }

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static RunModeService getInstance() {
        if (_instance == null) {
            _instance = new RunModeService();
        }

        return _instance;
    }

    public void openDisplayShellInRunMode(final IPath path,
            final Map<String, String> aliases) {
        openDisplayShellInRunMode(path, aliases, null);
    }

    /**
     * Opens a Display in a new Shell and adds the new Aliases.
     *
     * @param path
     *            The IPath of the Display
     * @param aliases
     *            A Map of new Aliases for the Display (can be null)
     */
    public void openDisplayShellInRunMode(final IPath path,
            final Map<String, String> aliases, RunModeBoxInput predecessor) {
        assert path != null;

        final RunModeBoxInput runModeBoxInput = new RunModeBoxInput(path,
                aliases, RunModeType.SHELL);

        runModeBoxInput.setPredecessorBox(predecessor);

        Point location = null;
        if (_activeBoxes.containsKey(predecessor)) {
            AbstractRunModeBox runModeBox = _activeBoxes.get(predecessor);
            location = runModeBox.getCurrentLocation();
        }

        if (_activeBoxes.containsKey(runModeBoxInput)) {
            AbstractRunModeBox box = _activeBoxes.get(runModeBoxInput);
            box.bringToTop();
        } else {
            try {
                final AbstractRunModeBox runModeBox = new ShellRunModeBox(
                        runModeBoxInput, location);

                // memorize box
                _activeBoxes.put(runModeBoxInput, runModeBox);

                // when the box is disposed, forget the box
                runModeBox.addDisposeListener(new IRunModeDisposeListener() {
                    public void dispose() {
                        _activeBoxes.remove(runModeBoxInput);
                        assert !_activeBoxes.containsKey(runModeBox) : "!_activeBoxes.containsKey(runModeBox)";
                    }
                });

                // open the box
                runModeBox.openRunMode(null, new IDisplayLoadedCallback() {
                    @Override
                    public void displayLoaded() {
                        notifyOpenDisplayListener();
                    }

                    @Override
                    public void displayClosed() {
                        notifyOpenDisplayListener();
                    }
                });
            } catch (IllegalArgumentException e) {
                LOG.info("Cannot open run mode: " + path.toOSString()
                                + " does not exist.");
                MessageDialog.openError(null, "Control System Studio",
                        "The display file was not found: " + path.toString());
            }
        }
    }

    /**
     * Closes the RunModeBox corresponding to the given {@link RunModeBoxInput}.
     *
     * @param modeBoxInput
     *            The {@link RunModeBoxInput}
     */
    public void closeRunModeBox(final RunModeBoxInput modeBoxInput) {
        LOG.debug("Close RunModeBox: " + modeBoxInput.getFilePath());
        AbstractRunModeBox runModeBox = _activeBoxes.get(modeBoxInput);
        runModeBox.dispose();
        notifyOpenDisplayListener();
    }

    /**
     * Opens a Display in a new View.
     *
     * @param location
     *            The IPath to the Display
     */
    public void openDisplayViewInRunMode(final IPath location) {
        this.openDisplayViewInRunMode(location, new HashMap<String, String>());
    }

    /**
     * Opens a Display in a new View and adds the new Aliases.
     *
     * @param path
     *            The IPath to the Display
     * @param aliases
     *            The new Aliases for the Display (can be null)
     */
    public void openDisplayViewInRunMode(final IPath path,
            final Map<String, String> aliases) {
        assert path != null;
        openBoxForWorkbenchView(path, aliases, null);
    }

    /**
     * Opens a Display in a new View with the informations of the given
     * {@link IMemento}.
     *
     * @param displayViewPart
     *            the {@link DisplayViewPart}
     * @param memento
     *            the {@link IMemento} for the view
     * @required memento!=null
     */
    public void openDisplayViewInRunMode(final DisplayViewPart displayViewPart,
            final IMemento memento) {
        assert memento != null : "Precondition violated: memento!=null";
        String storedPath = memento.getString("FILE");

        if (storedPath != null) {
            Map<String, String> aliases = new HashMap<String, String>();
            String tmp = memento.getString("ALIASES");

            if (tmp != null) {
                String[] tmpA = tmp.split(SEPARATOR);

                if ((tmpA.length % 2 == 0)) {
                    for (int i = 0; i <= tmpA.length - 1; i += 2) {
                        String key = tmpA[i];
                        String value = tmpA[i + 1];
                        assert key != null;
                        assert value != null;
                        aliases.put(key, value);
                    }
                }
            }
            openBoxForWorkbenchView(new Path(storedPath), aliases,
                    displayViewPart);
        }
    }

    private void openBoxForWorkbenchView(final IPath path,
            final Map<String, String> aliases, final DisplayViewPart view) {
        assert path != null;

        final RunModeBoxInput runModeBoxInput = new RunModeBoxInput(path,
                aliases, RunModeType.VIEW);

        if (_activeBoxes.containsKey(runModeBoxInput)) {
            AbstractRunModeBox box = _activeBoxes.get(runModeBoxInput);
            box.bringToTop();
        } else {
            try {
                final ViewRunModeBox runModeBox = new ViewRunModeBox(
                        runModeBoxInput, view);

                // memorize box
                _activeBoxes.put(runModeBoxInput, runModeBox);

                // when the box is disposed, forget the box
                runModeBox.addDisposeListener(new IRunModeDisposeListener() {
                    public void dispose() {
                        _activeBoxes.remove(runModeBoxInput);
                    }
                });

                // create a runnable that is executed, when the view is fully
                // launched
                Runnable runnable = new Runnable() {
                    public void run() {
                        // IMPORTANT: set the memento infos on the view
                        Map<String, String> mementoInfos = new HashMap<String, String>();

                        // the file path
                        mementoInfos.put("FILE", path.toOSString());

                        if (aliases != null) {
                            StringBuffer sb = new StringBuffer();
                            for (String key : aliases.keySet()) {
                                sb.append(key + SEPARATOR + aliases.get(key));
                            }
                            mementoInfos.put("ALIASES", sb.toString());
                        }

                        // TODO: Funktioniert so nicht, da die View asynchron
                        // geladen wird
                        runModeBox.getView().setMementoInfos(mementoInfos);
                    }
                };

                // open the box
                runModeBox.openRunMode(runnable, new IDisplayLoadedCallback() {
                    @Override
                    public void displayLoaded() {
                        notifyOpenDisplayListener();
                    }

                    @Override
                    public void displayClosed() {
                        notifyOpenDisplayListener();
                    }
                });
            } catch (IllegalArgumentException e) {
                LOG.info("Cannot open run mode: " + path.toOSString()
                                + " does not exist.");
                MessageDialog.openError(null, "Control System Studio",
                        "The display file was not found: " + path.toString());
            }
        }

    }

    /**
     * Opens a Display in a new Shell.
     *
     * @param filePath
     *            The IPath of the Display
     */
    public void openDisplayShellInRunMode(final IPath filePath) {
        openDisplayShellInRunMode(filePath, new HashMap<String, String>());
    }

    /**
     * Closes all running instances of the specified display.
     *
     * @param path
     *            the display path
     */
    public void closeDisplayShellInRunMode(IPath path) {
        for (RunModeBoxInput in : _activeBoxes.keySet()) {
            if (path.equals(in.getFilePath())) {
                _activeBoxes.get(in).dispose();
                notifyOpenDisplayListener();
            }
        }
    }

    public DisplayModel[] getAllActivDisplayModels() {
        List<DisplayModel> displays = new ArrayList<DisplayModel>();
        for (AbstractRunModeBox box : _activeBoxes.values()) {
            displays.add(box.getDisplayModel());
        }
        return displays.toArray(new DisplayModel[displays.size()]);
    }

    public void addOpenDisplayListener(
            IOpenDisplayListener openDisplayListener) {
        _openDisplayListener.add(openDisplayListener);
    }

    public void removeOpenDisplayListener(
            IOpenDisplayListener openDisplayListener) {
        _openDisplayListener.remove(openDisplayListener);
    }

    private void notifyOpenDisplayListener() {
        for (IOpenDisplayListener listener : _openDisplayListener) {
            listener.openDisplayChanged();
        }
    }

}
