package org.csstudio.platform.ui.internal.xmlprefs;

import org.csstudio.platform.internal.xmlprefs.XMLStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * This Store manages all preferences saved by the XMLStore, which holds all 
 * preferences in an XML-file.
 * 
 * @author Andre Grunow
 */
public class XMLPreferenceStore
		implements IPreferenceStore
{
	// --------------------------------------------------------------------------------------------

	/**	The store to access the xml file with the preferences. */
	private static XMLStore store = XMLStore.getInstance();
	
	/** This pluginId is used to identify the preferences by each Plugin, which provides this ID. */
	private String pluginId;
	
	// --------------------------------------------------------------------------------------------

	/**
	 * Creates the preference store with the specified pluginId. This pluginId is the name of the tag,
	 * which contains all preferences of that Plugin.
	 * 
	 * @param pluginId the ID of the plugin, which preferences will be managed
	 */
	public XMLPreferenceStore(String pluginId)
	{
		this.pluginId = pluginId;
	}
	
	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#addPropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener)
	{
		// nothing to do here
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#contains(java.lang.String)
	 */
	public boolean contains(String name)
	{
		return store.getPropertyValue(pluginId, name, false) != null;
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#firePropertyChangeEvent(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue)
	{
		// nothing to do here
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String name)
	{
		String result = store.getPropertyValue(pluginId, name, false);

		if (result == null)
			return false;
		
		else
			return Boolean.parseBoolean(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultBoolean(java.lang.String)
	 */
	public boolean getDefaultBoolean(String name)
	{
		String result = store.getPropertyValue(pluginId, name, true);
		
		if (result == null)
			return false;
		
		else
			return Boolean.parseBoolean(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultDouble(java.lang.String)
	 */
	public double getDefaultDouble(String name)
	{
		String result = store.getPropertyValue(pluginId, name, true);
		
		if (result == null)
			return 0;
		
		else
			return Double.parseDouble(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultFloat(java.lang.String)
	 */
	public float getDefaultFloat(String name)
	{
		String result = store.getPropertyValue(pluginId, name, true);
		
		if (result == null)
			return 0;
		
		else
			return Float.parseFloat(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultInt(java.lang.String)
	 */
	public int getDefaultInt(String name)
	{
		String result = store.getPropertyValue(pluginId, name, true);
		
		if (result == null)
			return 0;
		
		else
			return Integer.parseInt(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultLong(java.lang.String)
	 */
	public long getDefaultLong(String name)
	{
		String result = store.getPropertyValue(pluginId, name, true);
		
		if (result == null)
			return 0;
		
		else
			return Long.parseLong(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultString(java.lang.String)
	 */
	public String getDefaultString(String name)
	{
		String result = store.getPropertyValue(pluginId, name, true);
		
		if (result == null)
			return "";
		
		else
			return result;
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getDouble(java.lang.String)
	 */
	public double getDouble(String name)
	{
		String result = store.getPropertyValue(pluginId, name, false);
		
		if (result == null)
			return 0;
		
		else
			return Double.parseDouble(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getFloat(java.lang.String)
	 */
	public float getFloat(String name)
	{
		String result = store.getPropertyValue(pluginId, name, false);
		
		if (result == null)
			return 0;
		
		else
			return Float.parseFloat(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getInt(java.lang.String)
	 */
	public int getInt(String name)
	{
		String result = store.getPropertyValue(pluginId, name, false);
		
		if (result == null)
			return 0;
		
		else
			return Integer.parseInt(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getLong(java.lang.String)
	 */
	public long getLong(String name)
	{
		String result = store.getPropertyValue(pluginId, name, false);
		
		if (result == null)
			return 0;
		
		else
			return Long.parseLong(result);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#getString(java.lang.String)
	 */
	public String getString(String name)
	{
		String result = store.getPropertyValue(pluginId, name, false);
		return result == null ? "" : result;
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#isDefault(java.lang.String)
	 */
	public boolean isDefault(String name)
	{
		return store.getPropertyValue(pluginId, name, false).equals(store.getPropertyValue(pluginId, name, true));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#needsSaving()
	 */
	public boolean needsSaving()
	{
		// save always!
		
		return true;
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#putValue(java.lang.String, java.lang.String)
	 */
	public void putValue(String name, String value)
	{
		store.setProperty(pluginId, name, value);
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#removePropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
	 */
	public void removePropertyChangeListener(IPropertyChangeListener listener)
	{
		// nothing to do here
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, double)
	 */
	public void setDefault(String name, double value)
	{
		String defaultValue = store.getPropertyValue(pluginId, name, true);
		
		if (defaultValue == null || defaultValue.equals(""))
			store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, float)
	 */
	public void setDefault(String name, float value)
	{
		String defaultValue = store.getPropertyValue(pluginId, name, true);
		
		if (defaultValue == null || defaultValue.equals(""))
			store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, int)
	 */
	public void setDefault(String name, int value)
	{
		String defaultValue = store.getPropertyValue(pluginId, name, true);
		
		if (defaultValue == null || defaultValue.equals(""))
			store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, long)
	 */
	public void setDefault(String name, long value)
	{
		String defaultValue = store.getPropertyValue(pluginId, name, true);
		
		if (defaultValue == null || defaultValue.equals(""))
			store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, java.lang.String)
	 */
	public void setDefault(String name, String defaultObject)
	{
		String defaultValue = store.getPropertyValue(pluginId, name, true);
		
		if (defaultValue == null || defaultValue.equals(""))
			store.setDefaultProperty(pluginId, name, String.valueOf(defaultObject));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setDefault(java.lang.String, boolean)
	 */
	public void setDefault(String name, boolean value)
	{
		String defaultValue = store.getPropertyValue(pluginId, name, true);
		
		if (defaultValue == null || defaultValue.equals(""))
			store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setToDefault(java.lang.String)
	 */
	public void setToDefault(String name)
	{
		store.setProperty(pluginId, name, store.getPropertyValue(pluginId, name, true));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, double)
	 */
	public void setValue(String name, double value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, float)
	 */
	public void setValue(String name, float value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, int)
	 */
	public void setValue(String name, int value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, long)
	 */
	public void setValue(String name, long value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, java.lang.String)
	 */
	public void setValue(String name, String value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferenceStore#setValue(java.lang.String, boolean)
	 */
	public void setValue(String name, boolean value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}
}
