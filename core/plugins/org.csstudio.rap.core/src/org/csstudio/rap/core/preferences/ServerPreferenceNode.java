package org.csstudio.rap.core.preferences;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.csstudio.rap.core.RAPCorePlugin;
import org.eclipse.core.internal.preferences.Base64;
import org.eclipse.core.internal.preferences.ImmutableMap;
import org.eclipse.core.internal.preferences.PrefsMessages;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferenceNodeVisitor;
import org.eclipse.osgi.util.NLS;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
/**
 * A preference node for server side configuration, 
 * which is stored on my_product.war/plugin_customization.ini
 * @author Xihui Chen
 *
 */
@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
public class ServerPreferenceNode implements IEclipsePreferences {

	private static final String SERVER_PREFERENCE_PROPERTY = "org.csstudio.rap.preference"; //$NON-NLS-1$
	private static final String SERVER_PREFERENCE_FILE_NAME = "css_rap.ini"; //$NON-NLS-1$
	private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$
	private static final String DOUBLE_PATH_SEPARATOR = "//"; //$NON-NLS-1$
	private static final String TRUE = "true"; //$NON-NLS-1$
	private static final String FALSE = "false"; //$NON-NLS-1$
	protected boolean removed = false;

	protected static final String[] EMPTY_STRING_ARRAY = new String[0];

	private final String name;
	private final IEclipsePreferences parent;
	
	/* cache the absolutePath once it has been computed */
	private String absolutePath;

	
	private final Map children = Collections.synchronizedMap(new HashMap());

	private ListenerList nodeChangeListeners;
	private ListenerList preferenceChangeListeners;
	
	protected ImmutableMap properties = ImmutableMap.EMPTY;

	public ServerPreferenceNode(final IEclipsePreferences parent,
			final String name) {
		checkName(name);
		this.parent = parent;
		this.name = name;
		load();
		
	}

	public String absolutePath() {
		if (absolutePath == null) {
			if (parent == null) {
				absolutePath = name;
			} else {
				String parentPath = parent.absolutePath();
				absolutePath = parentPath.endsWith(PATH_SEPARATOR) ? parentPath
						+ name : parentPath + PATH_SEPARATOR + name;
			}
		}
		return absolutePath;
	}
	
	
	public void accept(IPreferenceNodeVisitor visitor)
			throws BackingStoreException {
		boolean withChildren = visitor.visit(this);
		if (withChildren) {
			Object[] childrenArray;
			synchronized (this) {
				childrenArray = children.values().toArray();
			}
			for (int i = 0; i < childrenArray.length; i++) {
				IEclipsePreferences child = (IEclipsePreferences) childrenArray[i];
				child.accept(visitor);
			}
		}
	}
	
	public void addNodeChangeListener(INodeChangeListener listener) {
		checkRemoved();
		if (nodeChangeListeners == null)
			nodeChangeListeners = new ListenerList();
		nodeChangeListeners.add(listener);
	}

	public void addPreferenceChangeListener(IPreferenceChangeListener listener) {
		checkRemoved();
		if (preferenceChangeListeners == null)
			preferenceChangeListeners = new ListenerList();
		preferenceChangeListeners.add(listener);
	}

	/*
	 * The file is of the format:
	 * 	pluginID/key=value
	 */
	private void applyDefaults(Properties defaultValues) {
		for (Enumeration e = defaultValues.keys(); e.hasMoreElements();) {
			String fullKey = (String) e.nextElement();
			String value = defaultValues.getProperty(fullKey);
			if (value == null)
				continue;
			IPath childPath = new Path(fullKey);
			String key = childPath.lastSegment();
			childPath = childPath.removeLastSegments(1);
			String localQualifier = childPath.segment(0);
			childPath = childPath.removeFirstSegments(1);			
			if (name().equals(localQualifier)) {
				((ServerPreferenceNode)node(childPath.toString())).internalPut(key, value);
			}
		}
	}

	private void checkName(final String nodeName) {
		if (nodeName.indexOf(PATH_SEPARATOR) != -1) {
			String unboundMsg = "Name ''{0}'' cannot contain or end with ''{1}''"; //$NON-NLS-1$
			String msg = NLS.bind(unboundMsg, nodeName, PATH_SEPARATOR);
			throw new IllegalArgumentException(msg);
		}
	}

	private void checkPath(final String path) {
		if (path.indexOf(DOUBLE_PATH_SEPARATOR) != -1) {
			String unboundMsg = "''{0}'' is not allowed in path ''{1}''"; //$NON-NLS-1$
			String msg = NLS.bind(unboundMsg, DOUBLE_PATH_SEPARATOR, path);
			throw new IllegalArgumentException(msg);
		}
		if (path.length() > 1 && path.endsWith(PATH_SEPARATOR)) {
			String unboundMsg = "path ''{0}'' cannot end with ''{1}''"; //$NON-NLS-1$
			String msg = NLS.bind(unboundMsg, path, PATH_SEPARATOR);
			throw new IllegalArgumentException(msg);
		}
	}

	/*
	 * Convenience method for throwing an exception when methods are called on a
	 * removed node.
	 */
	protected void checkRemoved() {
		if (removed)
			throw new IllegalStateException(NLS.bind(
					PrefsMessages.preferences_removedNode, name));
	}

	/*
	 * Return a boolean value indicating whether or not a child with the given
	 * name is known to this node.
	 */
	protected synchronized boolean childExists(String childName) {
		if (children == null)
			return false;
		return children.get(childName) != null;
	}

	public String[] childrenNames() throws BackingStoreException {
		// illegal state if this node has been removed
		checkRemoved();
		return internalChildNames();
	}

	public void clear() throws BackingStoreException {
		// illegal state if this node has been removed
		checkRemoved();
		// call each one separately (instead of Properties.clear) so
		// clients get change notification
		String[] keys = properties.keys();
		for (int i = 0; i < keys.length; i++)
			remove(keys[i]);
		// makeDirty();
	}

	private synchronized ServerPreferenceNode createChild(final String childName) {
		ServerPreferenceNode result = new ServerPreferenceNode(this, childName);
		children.put(childName, result);
		fireNodeEvent(new NodeChangeEvent(this, result), true);
		return result;
	}

	private Preferences findRoot() {
		Preferences result = this;
		while (result.parent() != null) {
			result = result.parent();
		}
		return result;
	}

	protected void fireNodeEvent(final NodeChangeEvent event,
			final boolean added) {
		if (nodeChangeListeners == null)
			return;
		Object[] listeners = nodeChangeListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			final INodeChangeListener listener = (INodeChangeListener) listeners[i];
			ISafeRunnable job = new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// already logged in Platform#run()
				}

				public void run() throws Exception {
					if (added)
						listener.added(event);
					else
						listener.removed(event);
				}
			};
			SafeRunner.run(job);
		}
	}

	/*
	 * Convenience method for notifying preference change listeners.
	 */
	protected void firePreferenceEvent(String key, Object oldValue, Object newValue) {
		if (preferenceChangeListeners == null)
			return;
		Object[] listeners = preferenceChangeListeners.getListeners();
		final PreferenceChangeEvent event = new PreferenceChangeEvent(this, key, oldValue, newValue);
		for (int i = 0; i < listeners.length; i++) {
			final IPreferenceChangeListener listener = (IPreferenceChangeListener) listeners[i];
			ISafeRunnable job = new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// already logged in Platform#run()
				}

				public void run() throws Exception {
					listener.preferenceChange(event);
				}
			};
			SafeRunner.run(job);
		}
	}

	public void flush() throws BackingStoreException {

	}

	public String get(String key, String def) {
		String value = internalGet(key);
		return value == null ? def : value;
	}

	public boolean getBoolean(String key, boolean def) {
		String value = internalGet(key);
		return value == null ? def : TRUE.equalsIgnoreCase(value);
	}

	public byte[] getByteArray(String key, byte[] def) {
		String value = internalGet(key);
		return value == null ? def : Base64.decode(value.getBytes());
	}

	private synchronized ServerPreferenceNode getChild(final String childName,
			final boolean doCreate) {
		ServerPreferenceNode result = (ServerPreferenceNode) children
				.get(childName);
		if (result == null && doCreate) {
			result = createChild(childName);
		}
		return result;
	}

	public double getDouble(String key, double def) {
		String value = internalGet(key);
		double result = def;
		if (value != null)
			try {
				result = Double.parseDouble(value);
			} catch (NumberFormatException e) {
				// use default
			}
		return result;
	}

	public float getFloat(String key, float def) {
		String value = internalGet(key);
		float result = def;
		if (value != null)
			try {
				result = Float.parseFloat(value);
			} catch (NumberFormatException e) {
				// use default
			}
		return result;
	}

	public int getInt(String key, int def) {
		String value = internalGet(key);
		int result = def;
		if (value != null)
			try {
				result = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				// use default
			}
		return result;
	}

	public long getLong(String key, long def) {
		String value = internalGet(key);
		long result = def;
		if (value != null)
			try {
				result = Long.parseLong(value);
			} catch (NumberFormatException e) {
				// use default
			}
		return result;
	}

	protected String[] internalChildNames() {
		Map temp = children;
		if (temp == null || temp.size() == 0)
			return EMPTY_STRING_ARRAY;
		return (String[]) temp.keySet().toArray(EMPTY_STRING_ARRAY);
	}

	/**
	 * Returns the existing value at the given key, or null if no such value
	 * exists.
	 */
	protected String internalGet(String key) {
		// throw NPE if key is null
		if (key == null)
			throw new NullPointerException();
		// illegal state if this node has been removed
		checkRemoved();
		String result = properties.get(key);
		return result;
	}

	/**
	 * Stores the given (key,value) pair, performing lazy initialization of the
	 * properties field if necessary. Returns the old value for the given key,
	 * or null if no value existed.
	 */
	protected String internalPut(String key, String newValue) {
		// illegal state if this node has been removed
		checkRemoved();
		String oldValue = properties.get(key);
		if (oldValue != null && oldValue.equals(newValue))
			return oldValue;
		properties = properties.put(key, newValue);
		return oldValue;
	}

	public String[] keys() throws BackingStoreException {
		// illegal state if this node has been removed
		checkRemoved();
		return properties.keys();
	}
	
	
	protected void load() {

		try {
			// The directory where the war file is placed.
			// On Tomcat, it is $(CATALINA.HOME)/webapps
			// In Eclipse, it is source location of this plugin

			Properties properties = new Properties();
			String propDir = System.getProperty(SERVER_PREFERENCE_PROPERTY); //$NON-NLS-1$

			try { //try property "css.rap.preference"
				if(propDir == null || propDir.trim().isEmpty())
					throw new IOException();
				properties = loadProperties(propDir); //$NON-NLS-1$
			} catch (IOException e1) {
				String message;
				if (propDir == null || propDir.trim().isEmpty())
					message = NLS
							.bind("System property {0} is not set, try user.home.", //$NON-NLS-1$
									SERVER_PREFERENCE_PROPERTY);
				else
					message = NLS
							.bind("Preference file is not found at {0}, try user.home.", //$NON-NLS-1$
									propDir);
				RAPCorePlugin.getLogger().log(Level.WARNING, message);
					
//				IPath iniPath = null;
//				try { //try war file folder
//					String path = ServerPreferenceNode.class
//							.getProtectionDomain().getCodeSource()
//							.getLocation().getPath();
//					String decodedPath = URLDecoder.decode(path, "UTF-8"); //$NON-NLS-1$
//					iniPath = new Path(decodedPath).removeLastSegments(3)
//							.append(SERVER_PREFERENCE_FILE_NAME);
//					System.out.println(iniPath);
//					properties = loadProperties(iniPath.toOSString()); //$NON-NLS-1$
//				} catch (IOException e) {
//					RAPCorePlugin
//							.getLogger()
//							.log(Level.WARNING,
//									NLS.bind(
//											"RAP CSS configuration file is not found at war folder {0}, try user.home",
//											iniPath));
					//try "user.home"
					propDir = System.getProperty("user.home");//$NON-NLS-1$
					properties = loadProperties(new Path(propDir).append(SERVER_PREFERENCE_FILE_NAME).toOSString());
//				}

			}

			// Cannot use context since it doesn't exist if no UI created.
			// ServletContext sc = RWT.getRequest().getSession()
			// .getServletContext();
			//			String realPath = sc.getRealPath("/"); //$NON-NLS-1$
			// Properties properties = loadProperties(realPath
			//					+ "plugin_customization.ini"); //$NON-NLS-1$

			applyDefaults(properties);
		} catch (Exception e) {
			RAPCorePlugin.getLogger().log(Level.SEVERE,
					"Server Side Preference loading failed.", e);
		}

	}

	private Properties loadProperties(String filename) throws IOException {
		Properties result = new Properties();
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(filename));
			result.load(input);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					// ignore
				}
		}
		return result;
	}

	public String name() {
		return name;
	}

	public Preferences node(String path) {
		checkPath(path);
		checkRemoved();
		Preferences result;
		if ("".equals(path)) { // "" //$NON-NLS-1$
			result = this;
		} else if (path.startsWith(PATH_SEPARATOR)) { // "/absolute/path"
			result = findRoot().node(path.substring(1));
		} else if (path.indexOf(PATH_SEPARATOR) > 0) { // "foo/bar/baz"
			int index = path.indexOf(PATH_SEPARATOR);
			String nodeName = path.substring(0, index);
			String rest = path.substring(index + 1, path.length());
			result = getChild(nodeName, true).node(rest);
		} else { // "foo"
			result = getChild(path, true);
		}
		return result;
	}

	public boolean nodeExists(String path) throws BackingStoreException {
		// short circuit for checking this node
		if (path.length() == 0)
			return !removed;

		// illegal state if this node has been removed.
		// do this AFTER checking for the empty string.
		checkRemoved();

		// use the root relative to this node instead of the global root
		// in case we have a different hierarchy. (e.g. export)
		if (path.charAt(0) == IPath.SEPARATOR)
			return findRoot().nodeExists(path.substring(1));

		int index = path.indexOf(IPath.SEPARATOR);
		boolean noSlash = index == -1;

		// if we are looking for a simple child then just look in the table and return
		if (noSlash)
			return childExists(path);

		// otherwise load the parent of the child and then recursively ask
		String childName = path.substring(0, index);
		if (!childExists(childName))
			return false;
		IEclipsePreferences child = getChild(childName, true);
		if (child == null)
			return false;
		return child.nodeExists(path.substring(index + 1));
	}

	public Preferences parent() {
		// illegal state if this node has been removed
		checkRemoved();
		return parent;
	}
	
	
	
	public void put(String key, String newValue) {
		if (key == null || newValue == null)
			throw new NullPointerException();
		String oldValue = internalPut(key, newValue);
		if (!newValue.equals(oldValue)) {
			firePreferenceEvent(key, oldValue, newValue);
		}
	}


	public void putBoolean(String key, boolean value) {
		if (key == null)
			throw new NullPointerException();
		String newValue = value ? TRUE : FALSE;
		String oldValue = internalPut(key, newValue);
		if (!newValue.equals(oldValue)) {
			firePreferenceEvent(key, oldValue, newValue);
		}
	}

	public void putByteArray(String key, byte[] value) {
		if (key == null || value == null)
			throw new NullPointerException();
		String newValue = new String(Base64.encode(value));
		String oldValue = internalPut(key, newValue);
		if (!newValue.equals(oldValue)) {
			firePreferenceEvent(key, oldValue, newValue);
		}
	}

	public void putDouble(String key, double value) {
		if (key == null)
			throw new NullPointerException();
		String newValue = Double.toString(value);
		String oldValue = internalPut(key, newValue);
		if (!newValue.equals(oldValue)) {
			firePreferenceEvent(key, oldValue, newValue);
		}
	}

	public void putFloat(String key, float value) {
		if (key == null)
			throw new NullPointerException();
		String newValue = Float.toString(value);
		String oldValue = internalPut(key, newValue);
		if (!newValue.equals(oldValue)) {
			firePreferenceEvent(key, oldValue, newValue);
		}
	}

	public void putInt(String key, int value) {
		if (key == null)
			throw new NullPointerException();
		String newValue = Integer.toString(value);
		String oldValue = internalPut(key, newValue);
		if (!newValue.equals(oldValue)) {
			firePreferenceEvent(key, oldValue, newValue);
		}
	}

	public void putLong(String key, long value) {
		if (key == null)
			throw new NullPointerException();
		String newValue = Long.toString(value);
		String oldValue = internalPut(key, newValue);
		if (!newValue.equals(oldValue)) {
			firePreferenceEvent(key, oldValue, newValue);
		}
	}

	public void remove(String key) {
		String oldValue = properties.get(key);
		if (oldValue == null)
			return;
		properties = properties.removeKey(key);
		firePreferenceEvent(key, oldValue, null);
	}

	public void removeNode() throws BackingStoreException {
		checkRemoved();
	    // remove all preferences
	    clear(); 
	    // remove all children
	    Object[] childNodes = children.values().toArray();
	    for( int i = 0; i < childNodes.length; i++ ) {
	      Preferences child = ( Preferences )childNodes[ i ];
	      if( child.nodeExists( "" ) ) { // if !removed //$NON-NLS-1$
	        child.removeNode();
	      }
	    }
	    // remove from parent; this is ugly, because the interface 
	    // Preference has no API for removing oneself from the parent.
	    // In general the parent will be a SessionPreferencesNode.
	    // The only case in the workbench where this is not true, is one level
	    // below the root (i.e. at /session ), but the scope root must not
	    // be removable (see IEclipsePreferences#removeNode())
	    if( parent instanceof ServerPreferenceNode ) {
	      // this means: 
	      // (a) we know what kind of parent we have, and 
	      // (b) we are not the scope root, since that has a 
	      /// RootPreference as a parent
	    	ServerPreferenceNode spnParent 
	        = ( ( ServerPreferenceNode ) parent );
	      spnParent.children.remove( name );
	      spnParent.fireNodeEvent( new NodeChangeEvent(spnParent, this), false );

	      // the listeners are not needed anymore
	      preferenceChangeListeners.clear();
	      nodeChangeListeners.clear();
	      children.clear();
	      removed = true;
	    }
	}
	
	public void removeNodeChangeListener(INodeChangeListener listener) {
		checkRemoved();
		if (nodeChangeListeners == null)
			return;
		nodeChangeListeners.remove(listener);
		if (nodeChangeListeners.size() == 0)
			nodeChangeListeners = null;
	}
	
	public void removePreferenceChangeListener(
			IPreferenceChangeListener listener) {
		checkRemoved();
		if (preferenceChangeListeners == null)
			return;
		preferenceChangeListeners.remove(listener);
		if (preferenceChangeListeners.size() == 0)
			preferenceChangeListeners = null;

	}
	
	public void sync() throws BackingStoreException {
		// default values are not persisted
	}

}
