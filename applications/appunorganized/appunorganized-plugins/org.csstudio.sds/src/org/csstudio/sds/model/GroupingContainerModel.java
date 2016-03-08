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
package org.csstudio.sds.model;

/**
 * Model for a container widget that groups other widgets.
 *
 * @author Sven Wende
 * @version $Revision: 1.9 $
 *
 */
public final class GroupingContainerModel extends ContainerModel {
    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.GroupingContainer"; //$NON-NLS-1$

    /**
     * The ID of the <i>transparent</i> property.
     */
    public static final String PROP_TRANSPARENT = "transparency";

    /**
     * The default value of the height property.
     */
    private static final int DEFAULT_HEIGHT = 20;

    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 80;

    /**
     * Standard constructor.
     */
    public GroupingContainerModel() {
        super(true, true);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureProperties() {
        addBooleanProperty(PROP_TRANSPARENT, "Transparent Background",WidgetPropertyCategory.FORMAT,true, true, PROP_COLOR_FOREGROUND);
    }

    /**
     * Returns, if this widget should have a transparent background.
     * @return boolean
     *                 True, if it should have a transparent background, false otherwise
     */
    public boolean getTransparent() {
        return getBooleanProperty(PROP_TRANSPARENT);
    }

}
