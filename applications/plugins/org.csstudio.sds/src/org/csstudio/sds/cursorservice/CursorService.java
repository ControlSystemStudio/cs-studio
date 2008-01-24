package org.csstudio.sds.cursorservice;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.model.optionEnums.CursorStyleEnum;
import org.csstudio.sds.model.properties.actions.WidgetAction;
import org.csstudio.sds.preferences.PreferenceConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * This service holds and provides the {@link Cursor}s, which are hold in the
 * {@link CursorStyleEnum} or stored as .gif-images in the workspace.
 * 
 * @author Kai Meyer
 * 
 */
public final class CursorService implements IPropertyChangeListener {

	/**
	 * The name of the project, where cursor images are searched.
	 */
	public static final String CURSORS_PROJECT_NAME = "SDS Cursors";
	/**
	 * The supported file extensions.
	 */
	private static final String[] SUPPORTED_FILE_EXTENSIONS = new String[] { "gif" };

	/**
	 * The current instance of this service.
	 */
	private static CursorService _instance = null;

	/**
	 * The list of known {@link Cursor}s.
	 */
	private List<Cursor> _cursorList;
	/**
	 * The list of known names for the cursors.
	 */
	private List<String> _nameList;
	/**
	 * The list of known {@link ICursorServiceListener}s.
	 */
	private List<ICursorServiceListener> _listener;

	/**
	 * The count of known cursors.
	 */
	private int _cursorCount = 0;

	/**
	 * The default cursor.
	 */
	private Cursor _defaultCursor = CursorStyleEnum.ARROW.getCursor();
	/**
	 * The cursor for widgets, which holds at least one {@link WidgetAction} and
	 * it is enabled.
	 */
	private Cursor _enabledActionCursor = CursorStyleEnum.HAND.getCursor();
	/**
	 * The cursor for widgets, which holds at least one {@link WidgetAction} and
	 * it is disabled.
	 */
	private Cursor _disabledActionCursor;

	/**
	 * Returns the current instance of this service.
	 * 
	 * @return The current {@link CursorService}
	 */
	public static CursorService getInstance() {
		if (_instance == null) {
			_instance = new CursorService();
		}
		return _instance;
	}

	/**
	 * Constructor. Fetches the cursors hold by the {@link CursorStyleEnum} and
	 * searches for cursors defined by images in the
	 * {@link CursorService#CURSORS_PROJECT_NAME}-project. Registers the
	 * service as listener by the {@link IPreferenceStore} provided by the
	 * {@link SdsPlugin}.
	 */
	private CursorService() {
		String qualifier = SdsPlugin.getDefault().getBundle().getSymbolicName();
		IPreferenceStore preferenceStore = new ScopedPreferenceStore(
				new InstanceScope(), qualifier);
		preferenceStore.addPropertyChangeListener(this);
		_cursorList = new LinkedList<Cursor>();
		_nameList = new LinkedList<String>();
		_listener = new LinkedList<ICursorServiceListener>();
		this.initEnumCursor();
		this.initWorkSpaceCursor();
		this.getPreferenceStoreCursor(preferenceStore);
	}

	/**
	 * Initializes the cursors hold by the {@link CursorStyleEnum}.
	 */
	private void initEnumCursor() {
		for (int i = 0; i < CursorStyleEnum.values().length; i++) {
			CursorStyleEnum styleEnum = CursorStyleEnum.getEnumForIndex(i);
			_cursorList.add(styleEnum.getIndex(), styleEnum.getCursor());
			_nameList.add(styleEnum.getIndex(), styleEnum.getDisplayName());
			_cursorCount++;
		}
		assert _nameList.size() == _cursorList.size() : "PostCondition violated: _nameList.size()==_cursorList.size()";
	}

	/**
	 * Initializes the cursors, which are defined by images in the workspace.
	 */
	private void initWorkSpaceCursor() {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				CURSORS_PROJECT_NAME);
		if (project != null) {
			try {
				this.getCursorFromResources(project.members());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		assert _nameList.size() == _cursorList.size() : "PostCondition violated: _nameList.size()==_cursorList.size()";
		assert _cursorList.size() == _cursorCount : "PostCondition violated: _cursorList.size()==_cursorCount";
	}

	/**
	 * Fetches all cursors defined by a resource of the given {@link IResource}-array.
	 * 
	 * @param resources
	 *            An array of {@link IResource}s
	 * @throws CoreException
	 *             Thrown when problems occurred during navigating through the
	 *             workspace.
	 */
	private void getCursorFromResources(final IResource[] resources)
			throws CoreException {
		for (IResource resource : resources) {
			if (resource.getType() == IResource.FILE) {
				IFile file = (IFile) resource;
				if (file.getFileExtension() != null
						&& this.isSupportedFileExtension(file
								.getFileExtension())) {
					String filePath = file.getLocation().toString();
					try {
						ImageData imageData = new ImageData(filePath);
						if (imageData == null) {
							CentralLogger.getInstance().warn(
									this,
									"Couldn't get an ImageData from file: "
											+ filePath);
						} else {
							Cursor newCursor = new Cursor(Display.getCurrent(),
									imageData, 1, 1);
							if (newCursor != null) {
								_cursorList.add(_cursorCount, newCursor);
								_nameList.add(_cursorCount, this
										.createNameForCursor(file.getFullPath()
												.toString()));
								_cursorCount++;
							}
						}
					} catch (Exception e) {
						CentralLogger.getInstance().error(
								this,
								"Couldn't get an ImageData from file: "
										+ filePath, e);
					}
				}
			} else if (resource.getType() == IResource.FOLDER) {
				IFolder folder = (IFolder) resource;
				this.getCursorFromResources(folder.members());
			}
		}
	}

	/**
	 * Reruns if the requested file extension is a supported type.
	 * 
	 * @param requestedExtension
	 *            The file extension
	 * @return True if the given file extension is supported, false otherwise
	 */
	private boolean isSupportedFileExtension(final String requestedExtension) {
		for (String extension : SUPPORTED_FILE_EXTENSIONS) {
			if (extension.equalsIgnoreCase(requestedExtension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates and returns a name for the cursor of the given path.
	 * 
	 * @param fullPath
	 *            The path to the image of the cursor.
	 * @return The create name
	 */
	private String createNameForCursor(final String fullPath) {
		String result = fullPath;
		if (result.startsWith("/")) {
			result = result.replaceFirst("/", "");
		}
		if (result.startsWith(CURSORS_PROJECT_NAME)) {
			result = result.replaceFirst(CURSORS_PROJECT_NAME, "");
		}
		if (result.startsWith("/")) {
			result = result.replaceFirst("/", "");
		}
		return result;
	}

	/**
	 * Gets the cursor defined in the preference page.
	 * 
	 * @param preferenceStore
	 *            The {@link IPreferenceStore}, which holds the cursors
	 */
	private void getPreferenceStoreCursor(final IPreferenceStore preferenceStore) {
		String defaultName = preferenceStore
				.getString(PreferenceConstants.PROP_DEFAULT_CURSOR);
		_defaultCursor = this.getCursor(defaultName);
		String enabledActionName = preferenceStore
				.getString(PreferenceConstants.PROP_ENABLED_ACTION_CURSOR);
		_enabledActionCursor = this.getCursor(enabledActionName);
		String disabledActionName = preferenceStore
				.getString(PreferenceConstants.PROP_ENABLED_ACTION_CURSOR);
		_disabledActionCursor = this.getCursor(disabledActionName);
	}

	/**
	 * Adds a {@link ICursorServiceListener}.
	 * 
	 * @param listener
	 *            The {@link ICursorServiceListener} to be added
	 */
	public void addCursorServiceListener(final ICursorServiceListener listener) {
		_listener.add(listener);
	}

	/**
	 * Removes the given {@link ICursorServiceListener}.
	 * 
	 * @param listener
	 *            The {@link ICursorServiceListener} to be removed
	 */
	public void removeCursorServiceListener(
			final ICursorServiceListener listener) {
		_listener.remove(listener);
	}

	/**
	 * Returns the current count of known {@link Cursor}.
	 * 
	 * @return The count of cursor
	 */
	public int getCursorCount() {
		return _cursorCount;
	}

	/**
	 * Returns all names of the known cursors.
	 * 
	 * @return An array of the names
	 */
	public String[] getDisplayNames() {
		return _nameList.toArray(new String[_nameList.size()]);
	}

	/**
	 * Returns all known cursors.
	 * 
	 * @return The cursors
	 */
	public Cursor[] getCursors() {
		return _cursorList.toArray(new Cursor[_cursorList.size()]);
	}

	/**
	 * Returns the name to display of the cursor with the given index.
	 * 
	 * @param index
	 *            The index of the cursor.
	 * @return The name of the cursor specified by the index
	 */
	public String getDisplayName(final int index) {
		if (index > -1 && index < _nameList.size()) {
			return _nameList.get(index);
		}
		return "";
	}

	/**
	 * Returns the cursor with the given index.
	 * 
	 * @param index
	 *            The index of the cursor.
	 * @return The cursor specified by the index
	 */
	public Cursor getCursor(final int index) {
		if (index > -1 && index < _cursorList.size()) {
			return _cursorList.get(index);
		}
		return _defaultCursor;
	}

	/**
	 * Returns the cursor with the given name.
	 * 
	 * @param name
	 *            The name of the cursor.
	 * @return The cursor specified by the name
	 */
	public Cursor getCursor(final String name) {
		int index = this.getIndex(name);
		return this.getCursor(index);
	}

	/**
	 * Returns the index of the cursor with the given name.
	 * 
	 * @param name
	 *            The name of the cursor.
	 * @return The index of the specified cursor
	 */
	public int getIndex(final String name) {
		return _nameList.indexOf(name);
	}

	/**
	 * Returns the index of the given cursor.
	 * 
	 * @param cursor
	 *            The cursor.
	 * @return The index of the specified cursor
	 */
	public int getIndex(final Cursor cursor) {
		return _cursorList.indexOf(cursor);
	}

	/**
	 * Returns the default cursor defined by the preference page.
	 * 
	 * @return The default cursor
	 */
	public Cursor getDefaultCursor() {
		return _defaultCursor;
	}

	/**
	 * Returns the cursor used for widget which has {@link WidgetAction}s
	 * and is enabled (defined by the preference page).
	 * 
	 * @return The action cursor
	 */
	public Cursor getEnabledActionCursor() {
		return _enabledActionCursor;
	}
	
	/**
	 * Returns the cursor used for widget which has {@link WidgetAction}s
	 * and is disabled (defined by the preference page).
	 * 
	 * @return The action cursor
	 */
	public Cursor getDisabledActionCursor() {
		return _disabledActionCursor;
	}

	/**
	 * Returns the index of default cursor defined by the preference page.
	 * 
	 * @return The index of the default cursor
	 */
	public int getDefaultCursorIndex() {
		return this.getIndex(_defaultCursor);
	}

	/**
	 * Returns the index of the cursor used for widget which has {@link WidgetAction}s
	 * and is enabled (defined by the preference page).
	 * 
	 * @return The index of the action cursor
	 */
	public int getEnabledActionCursorIndex() {
		return this.getIndex(_enabledActionCursor);
	}
	
	/**
	 * Returns the index of the cursor used for widget which has {@link WidgetAction}s
	 * and is disabled (defined by the preference page).
	 * 
	 * @return The index of the action cursor
	 */
	public int getDisabledActionCursorIndex() {
		return this.getIndex(_disabledActionCursor);
	}

	/**
	 * Notifies all registered {@link ICursorServiceListener}.
	 */
	private void notifyListener() {
		for (ICursorServiceListener listener : _listener) {
			listener.cursorChanged();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.PROP_ENABLED_ACTION_CURSOR)) {
			String cursorName = (String) event.getNewValue();
			_enabledActionCursor = this.getCursor(cursorName);
			this.notifyListener();
		}
		if (event.getProperty().equals(PreferenceConstants.PROP_DISABLED_ACTION_CURSOR)) {
			String cursorName = (String) event.getNewValue();
			_disabledActionCursor = this.getCursor(cursorName);
			this.notifyListener();
		}
		if (event.getProperty().equals(PreferenceConstants.PROP_DEFAULT_CURSOR)) {
			String cursorName = (String) event.getNewValue();
			_defaultCursor = this.getCursor(cursorName);
			this.notifyListener();
		}
	}
}
