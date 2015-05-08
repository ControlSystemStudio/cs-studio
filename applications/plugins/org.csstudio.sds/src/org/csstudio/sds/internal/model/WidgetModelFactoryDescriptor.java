/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.internal.model;

import org.csstudio.sds.model.IWidgetModelFactory;

/**
 * This class defines the descriptor for extensions of the
 * <code>widgetElementFactories</code> extension point.
 *
 * @author Alexander Will
 * @version $Revision: 1.5 $
 *
 */
public class WidgetModelFactoryDescriptor {
    /**
     * The description of the widget model factory.
     */
    private String _description;

    /**
     * The name of the widget model factory.
     */
    private String _name;

    /**
     * The path of the icon ressource of the widget model factory.
     */
    private String _icon;

    /**
     * The widget model factory.
     */
    private IWidgetModelFactory _factory;

    /**
     * The ID of the plugin that contributes the widget model factory.
     */
    private String _pluginId;

    /**
     * Standard constructor.
     *
     * @param description
     *            The description of the widget model factory.
     * @param name
     *            The name of the widget model factory.
     * @param icon
     *            The path of the icon ressource of the widget model factory.
     * @param factory
     *            The widget model factory.
     * @param pluginId
     *            The ID of the plugin that contributes the widget model
     *            factory.
     * @param inUse
     *            Determines if the corresponding widget should be used within
     *            the editor
     */
    public WidgetModelFactoryDescriptor(final String description,
            final String name, final String icon,
            final IWidgetModelFactory factory, final String pluginId) {
        super();
        _description = description;
        _name = name;
        _icon = icon;
        _factory = factory;
        _pluginId = pluginId;
    }

    /**
     * Return the description of the widget model factory.
     *
     * @return The description of the widget model factory.
     */
    public final String getDescription() {
        return _description;
    }

    /**
     * Return the path of the icon ressource of the widget model factory.
     *
     * @return The path of the icon ressource of the widget model factory.
     */
    public final String getIcon() {
        return _icon;
    }

    /**
     * Return the name of the widget model factory.
     *
     * @return The name of the widget model factory.
     */
    public final String getName() {
        return _name;
    }

    /**
     * Return the widget model factory.
     *
     * @return The widget model factory.
     */
    public final IWidgetModelFactory getFactory() {
        return _factory;
    }

    /**
     * Return the ID of the plugin that contributes the widget model factory.
     *
     * @return The ID of the plugin that contributes the widget model factory.
     */
    public final String getPluginId() {
        return _pluginId;
    }


}
