/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 package org.csstudio.sds.model.initializers;


/**
 * Base class for widget model initializers that provides a convenient API for
 * initializing widget model for a certain control system.
 *
 * @author Sven Wende
 * @version $Revision: 1.8 $
 *
 */
public abstract class AbstractWidgetModelInitializer extends AbstractInitializer {
    /**
     * Subclasses should implement the proper widget initialization in this
     * method. Some Control System wide settings might have been stored in a
     * global schema, which is shared by all initializers for that schema. A
     * reference to this schema is assigned.
     *
     * Subclasses can use the following methods for initialization of a widget:
     *
     * {@link #initializeDynamicProperty(String, String)}
     * {@link #initializeDynamicProperty(String, String[])}
     * {@link #initializeDynamicProperty(String, String, String, String)}
     * {@link #initializeAlias(String, String)}
     * {@link #initializeStaticProperty(String, Object)}
     *
     * A typical implementation might look like this:
     *
     * <code>
     *     protected void initialize(final AbstractControlSystemSchema schema) {
     *         initializeStaticProperty(RectangleModel.PROP_FILL, 50.0);
     *         initializeDynamicProperty(RectangleModel.PROP_FILL, "$channel$.VAL");
     *  }
     * </code>
     *
     * @param schema
     *            the control system schema
     */
    protected abstract void initialize(final AbstractControlSystemSchema schema);
}