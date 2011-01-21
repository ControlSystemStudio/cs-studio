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
@SuppressWarnings("nls")
public class DataBrowserWidgedModel extends AbstractWidgetModel
{
    /** Widget ID registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.opiwidget";

    /** Property for name of data browser configuration file */
    final public static String PROP_FILENAME = "filename";

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
        addProperty(new FilePathProperty(PROP_FILENAME, "File Name",
                WidgetPropertyCategory.Basic, null,
                new String[] { Model.FILE_EXTENSION }));
    }

    /** @return Path to data browser configuration file */
    public IPath getFilename()
    {
        return (IPath) getPropertyValue(PROP_FILENAME);
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
}
