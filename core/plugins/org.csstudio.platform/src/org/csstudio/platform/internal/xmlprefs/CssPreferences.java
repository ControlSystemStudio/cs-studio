package org.csstudio.platform.internal.xmlprefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.Preferences;

public class CssPreferences
		extends Preferences
{
	// --------------------------------------------------------------------------------------------

	private XMLStore store = XMLStore.getInstance();

	// --------------------------------------------------------------------------------------------

	private String pluginId;

	// --------------------------------------------------------------------------------------------

	public CssPreferences(String pluginId)
	{
		this.pluginId = pluginId;
	}
	
	// --------------------------------------------------------------------------------------------

	@Override
	public boolean contains(String name)
	{
		return store.getPropertyValue(pluginId, name, true) != null;
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public String[] defaultPropertyNames()
	{
		return store.getPropertyNames(pluginId);
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public boolean getBoolean(String name)
	{
		return Boolean.parseBoolean(store.getPropertyValue(pluginId, name, false));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public boolean getDefaultBoolean(String name)
	{
		return Boolean.parseBoolean(store.getPropertyValue(pluginId, name, true));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public double getDefaultDouble(String name)
	{
		return Double.parseDouble(store.getPropertyValue(pluginId, name, true));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public float getDefaultFloat(String name)
	{
		return Float.parseFloat(store.getPropertyValue(pluginId, name, true));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public int getDefaultInt(String name)
	{
		return Integer.parseInt(store.getPropertyValue(pluginId, name, true));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public long getDefaultLong(String name)
	{
		return Long.parseLong(store.getPropertyValue(pluginId, name, true));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public String getDefaultString(String name)
	{
		return store.getPropertyValue(pluginId, name, true);
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public double getDouble(String name)
	{
		return Double.parseDouble(store.getPropertyValue(pluginId, name, false));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public float getFloat(String name)
	{
		return Float.parseFloat(store.getPropertyValue(pluginId, name, false));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public int getInt(String name)
	{
		return Integer.parseInt(store.getPropertyValue(pluginId, name, false));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public long getLong(String name)
	{
		return Long.parseLong(store.getPropertyValue(pluginId, name, false));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public String getString(String name)
	{
		// TODO Auto-generated method stub
		return store.getPropertyValue(pluginId, name, false);
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public boolean isDefault(String name)
	{
		return store.getPropertyValue(pluginId, name, false).equals(store.getPropertyValue(pluginId, name, false));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public boolean needsSaving()
	{
		return true;
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public String[] propertyNames()
	{
		return store.getPropertyNames(pluginId);
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setDefault(String name, boolean value)
	{
		store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setDefault(String name, double value)
	{
		store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setDefault(String name, float value)
	{
		store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setDefault(String name, int value)
	{
		store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setDefault(String name, long value)
	{
		store.setDefaultProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setDefault(String name, String value)
	{
		store.setDefaultProperty(pluginId, name, value);
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setToDefault(String name)
	{
		store.setDefaultProperty(pluginId, name, store.getPropertyValue(pluginId, name, true));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setValue(String name, boolean value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setValue(String name, double value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setValue(String name, float value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setValue(String name, int value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setValue(String name, long value)
	{
		store.setProperty(pluginId, name, String.valueOf(value));
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void setValue(String name, String value)
	{
		store.setProperty(pluginId, name, value);
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void load(InputStream in) throws IOException
	{
		super.load(in);
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void store(OutputStream out, String header) throws IOException
	{
		super.store(out, header);
	}
}
