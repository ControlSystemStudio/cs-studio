/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id: ByteModel.java,v 1.7 2010/05/06 16:23:18 hrickens Exp $
 */
package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 05.10.2007
 */
public class ByteModel extends AbstractWidgetModel {
    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.sds.components.Byte"; //$NON-NLS-1$
    /**
     * The ID of the orientation property.
     */
    public static final String PROP_ORIENTATION = "orientation"; //$NON-NLS-1$

    /**
     * The ID of the figure property.
     */
    public static final String PROP_FIGURE = "figure"; //$NON-NLS-1$

    /**
     * The default value of the height property.
     */
    private static final int DEFAULT_HEIGHT = 10;
    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 20;
    /**
     * The default value of the orientation property.
     */
    private static final boolean DEFAULT_ORIENTATION_HORIZONTAL = true;

    /**
     * The default value of the orientation property.
     */
    private static final int DEFAULT_BIT_SIZE = 16;

    /**
     * Standard constructor.
     */
    public ByteModel() {
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
        addBooleanProperty(PROP_ORIENTATION, "Horizontal Orientation", WidgetPropertyCategory.BEHAVIOR,
                DEFAULT_ORIENTATION_HORIZONTAL, false);
    }

    /**
     * Gets the orientation.
     *
     * @return the orientation.
     */
    public boolean getOrientation() {
        return getBooleanProperty(PROP_ORIENTATION);
    }

}
