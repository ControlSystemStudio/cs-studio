/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.opiwidget;

import java.io.InputStream;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/** Model for persisting data browser widget configuration.
 *
 *  For the OPI, it holds the Data Browser config file name.
 *  For the Data Browser, it holds the {@link DataBrowserModel}.
 *
 *  @author Kay Kasemir
 */
public class DataBrowserWidgedModel extends AbstractWidgetModel
{
    /** Widget ID registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.opiwidget"; //$NON-NLS-1$

    /** Property for name of data browser configuration file */
    final public static String PROP_FILENAME = "filename"; //$NON-NLS-1$

    /** Property to show/hide the toolbar */
    public static final String PROP_SHOW_TOOLBAR = "show_toolbar"; //$NON-NLS-1$

    /** Initialize */
    public DataBrowserWidgedModel()
    {
        setBorderStyle(BorderStyle.LINE);
        setSize(400, 300);
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
                WidgetPropertyCategory.Basic, null,
                new String[] { Model.FILE_EXTENSION }));
        addProperty(new BooleanProperty(PROP_SHOW_TOOLBAR, Messages.ShowToolbar,
                WidgetPropertyCategory.Display, false));
    }

    /** @return Path to data browser configuration file */
    public IPath getFilename()
    {
        return (IPath) getPropertyValue(PROP_FILENAME);
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
        final InputStream input = ResourceUtil.pathToInputStream(getFilename());
        model.read(input);
        return model;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "DataBrowserWidgetModel: " + getFilename().toString();
    }
}
