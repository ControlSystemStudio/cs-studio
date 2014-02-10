/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.macros.InfiniteLoopException;
import org.csstudio.apputil.macros.MacroTable;
import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/** Model for persisting data browser widget configuration.
 *
 *  For the OPI, it holds the Data Browser config file name.
 *  For the Data Browser, it holds the {@link DataBrowserModel}.
 *
 *  @author Kay Kasemir
 */
public class DataBrowserWidgedModel extends AbstractContainerModel
{
    /** Widget ID registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.opiwidget"; //$NON-NLS-1$

    /** Property for name of data browser configuration file */
    final public static String PROP_FILENAME = "filename"; //$NON-NLS-1$

    /** Property to show/hide the toolbar */
    public static final String PROP_SHOW_TOOLBAR = "show_toolbar"; //$NON-NLS-1$
    
    public static final IPath EMPTY_PATH = new Path(""); //$NON-NLS-1$

    public static final String PROP_SELECTION_VALUE_PV = "selection_value_pv";
	public static final String PROP_SHOW_AXIS_TRACE = "show_axis_trace";
	public static final String PROP_SHOW_VALUE_LABELS = "show_value_labels";
    
    /** Initialize */
    public DataBrowserWidgedModel()
    {
        setBorderStyle(BorderStyle.LINE);
        setSize(400, 300);
    }

    /** No child widgets in the original sense
     *  of the {@link AbstractContainerModel}
     *  {@inheritDoc}}
     */
	@Override
	public List<AbstractWidgetModel> getChildren()
	{
		return new ArrayList<AbstractWidgetModel>(0);
	}

    /** No editing of child widgets in the original sense
     *  of the {@link AbstractContainerModel}
     *  {@inheritDoc}}
     */
	@Override
	public boolean isChildrenOperationAllowable()
	{
		return false;
	}

    /** {@inheritDoc}} */
    @Override
    public String getTypeID()
    {
        return ID;
    }

    /** {@inheritDoc}} */
    @Override
    protected void configureProperties()
    {
        addProperty(new FilePathProperty(PROP_FILENAME, Messages.FileName,
                WidgetPropertyCategory.Basic, EMPTY_PATH,
                new String[]
                {
                    Model.FILE_EXTENSION,
                    Model.FILE_EXTENSION_OLD
                }));
        addProperty(new BooleanProperty(PROP_SHOW_TOOLBAR, Messages.ShowToolbar,
                WidgetPropertyCategory.Display, false));
        addProperty(new StringProperty(PROP_SELECTION_VALUE_PV,
				"Selection Value PV (VTable)", WidgetPropertyCategory.Basic, ""));
        addProperty(new BooleanProperty(PROP_SHOW_AXIS_TRACE,
        		"Show Axis Trace", WidgetPropertyCategory.Display, false));
        addProperty(new BooleanProperty(PROP_SHOW_VALUE_LABELS,
        		"Show Value Labels", WidgetPropertyCategory.Display, false));
    }

    /** @return Path to data browser configuration file, including macros. Never <code>null</code>. */
    public IPath getPlainFilename()
    {
        final IPath path = (IPath) getPropertyValue(PROP_FILENAME);
        return path == null ? EMPTY_PATH : path;
    }

    /** @return All macros of this widget, including optional parent settings */
    private MacroTable getAllMacros()
    {
        final Map<String, String> macros = new HashMap<String, String>();
        final MacrosInput macro_input = getMacrosInput();
        if (macro_input.isInclude_parent_macros())
        	macros.putAll(getParentMacroMap());
        macros.putAll(macro_input.getMacrosMap());
        return new MacroTable(macros);
    }

    /** @return Path to data browser configuration file, macros are expanded */
    public IPath getExpandedFilename()
    {
        IPath path = getPlainFilename();
        try
        {
            final String new_path = MacroUtil.replaceMacros(path.toPortableString(), getAllMacros());
            path = SingleSourcePlugin.getResourceHelper().newPath(new_path);
        }
        catch (InfiniteLoopException e)
        {
            Logger.getLogger(Activator.ID).log(Level.WARNING, "Recursive macros in Data Browser widget {0}", getName()); //$NON-NLS-1$
        }

        return path;
    }

    /** @return Tool bar visibility */
    public boolean isToolbarVisible()
    {
        return (Boolean) getPropertyValue(PROP_SHOW_TOOLBAR);
    }

    /** Create a Data Browser model, loaded with the configuration file
     *  @return Data Browser Model
     *  @throws Exception
     */
    public Model createDataBrowserModel() throws CoreException, Exception
    {
        final Model model = new Model();
        model.setMacros(getAllMacros());
        final InputStream input = SingleSourcePlugin.getResourceHelper().getInputStream(getExpandedFilename());
        model.read(input);
        return model;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "DataBrowserWidgetModel: " + getPlainFilename().toString();
    }
    
    /**
     * Returns selection PV value.
     * 
     * @return Selection PV value.
     */
	public String getSelectionValuePv() {
		return getCastedPropertyValue(PROP_SELECTION_VALUE_PV);
	}
	
	/**
	 * Returns true if the axis trace is shown or false otherwise.
	 * 
	 * @return is axis trace displayed
	 */
	public boolean isShowAxisTrace() {
		return getCastedPropertyValue(PROP_SHOW_AXIS_TRACE);
	}
	
	/**
	 * Returns true if the value labels are displayed or false otherwise.
	 * 
	 * @return are value labels displayed
	 */
	public boolean isShowValueLabels() {
		return getCastedPropertyValue(PROP_SHOW_VALUE_LABELS);
	}
}