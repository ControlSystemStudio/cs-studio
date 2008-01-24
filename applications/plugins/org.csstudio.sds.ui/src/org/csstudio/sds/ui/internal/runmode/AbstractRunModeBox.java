package org.csstudio.sds.ui.internal.runmode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.internal.connection.ConnectionService;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.persistence.DisplayModelLoadAdapter;
import org.csstudio.sds.model.persistence.PersistenceUtil;
import org.csstudio.sds.model.properties.IPropertyChangeListener;
import org.csstudio.sds.model.properties.PropertyChangeAdapter;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * A box that manages a shell, which uses a GEF graphical viewer to display SDS
 * displays.
 * 
 * @author Sven Wende, Alexander Will
 * @version $Revision$
 */
public abstract class AbstractRunModeBox {
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
	 * A title for the box.
	 */
	private String _title;

	/**
	 * Contains all property change listeners that will be added to the display
	 * model or widgets.
	 */
	private HashMap<WidgetProperty, IPropertyChangeListener> _propertyListeners;

	/**
	 * Constructor.
	 * 
	 * @param inputStream
	 *            xml input stream for the model file that should be displayed
	 * @param title
	 *            a title for the shell
	 */
	public AbstractRunModeBox(final InputStream inputStream,
			final String title, ConnectionService connectionService) {
		assert inputStream != null;
		assert title != null;
		assert connectionService != null;
		_inputStream = inputStream;
		_title = title;
		_disposeListeners = new ArrayList<IRunModeDisposeListener>();
		_propertyListeners = new HashMap<WidgetProperty, IPropertyChangeListener>();
	}

	/**
	 * Open!
	 */
	public void openRunMode(final Map<String, String> aliases, final Runnable runAfterOpen) {
		// Open the run mode representation

		// initialize model
		_displayModel = new DisplayModel();
		_displayModel.setLive(true);

		// create the graphical viewer

		final int x = 0;
		final int y = 0;
		final int width = 200;
		final int height = 200;
		
		// load and connect the model
		PersistenceUtil.asyncFillModel(_displayModel, _inputStream,
				new DisplayModelLoadAdapter() {

					@Override
					public void onDisplayModelLoaded() {
					}

					public void onDisplayPropertiesLoaded() {
						// Initialize the Aliases (Note: It is important, that the display has been fully loaded - otherwise the aliases are not applied to all child widgets)
						if (aliases != null) {
							for (String key : aliases.keySet()) {
								_displayModel.addAlias(key,
										aliases.get(key));
							}
						}
						
						final int x = _displayModel.getX();
						final int y = _displayModel.getY();
						final int width = _displayModel.getWidth();
						final int height = _displayModel.getHeight();

						PlatformUI.getWorkbench().getDisplay().syncExec(
								new Runnable() {
									public void run() {
										_graphicalViewer = doOpen(x, y, width,
												height, _title);

										_graphicalViewer
												.setContents(_displayModel);
										
										_graphicalViewer
												.getControl()
												.setBackground(
														CustomMediaFactory
																.getInstance()
																.getColor(
																		_displayModel
																				.getBackgroundColor()));
										
										// execute the runnable
										if(runAfterOpen!=null) {
											runAfterOpen.run();
										}
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
	 * @param width
	 *            width hint
	 * @param height
	 *            height hint
	 * @param title
	 *            a title
	 * @return the {@link GraphicalViewer} which is used to display the model
	 */
	protected abstract GraphicalViewer doOpen(int x, int y, int width,
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
		for (IRunModeDisposeListener l : _disposeListeners) {
			l.dispose();
		}
	}

	/**
	 * Disposes the shell.
	 */
	public final void dispose() {
		// remove all change listeners
		for (WidgetProperty p : _propertyListeners.keySet()) {
			p.removePropertyChangeListener(_propertyListeners.get(p));
		}

		// do no longer reference the display model (important for garbage
		// collection!)
		_displayModel = null;

		// let subclasses do their job
		doDispose();

		// inform listeners that this box has been disposed
		fireDispose();
	}

	protected abstract void doDispose();

	protected abstract void handleWindowPositionChange(int x, int y, int width,
			int height);

	/**
	 * Sets the focus on this Shell.
	 */
	public abstract void bringToTop();

}
