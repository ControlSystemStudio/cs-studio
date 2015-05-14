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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.internal.model.test.TestWidgetModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Testcases für {@link AbstractControlSystemSchema}
 * @author swende
 *
 */
public final class AbstractControlSystemSchemaTest {

    private static final String ERROR_COLOR = "#010000";
    private static final String FOREGROUND_COLOR = "#020202";
    private static final String BACKGROUND_COLOR = "#000000";
    private static final String ALIAS = "$channel$";

    /**
     * The instance under test.
     */
    private AbstractControlSystemSchema _schema;

    /**
     * Test set up.
     *
     */
    @Before
    public void setUp() {
        _schema = new DummySchema();

    }

    @Test
    public void testSchema() {
        TestWidgetModel widget = new TestWidgetModel();
        _schema.setWidgetModel(widget);
        _schema.initialize();

        assertEquals(1, widget.getAliases().size());
        assertTrue(widget.getAliases().containsKey(ALIAS));
    }

    /**
     * A dummy initialization schema for unit tests.
     *
     * @author Stefan Hofer
     * @version $Revision: 1.6 $
     *
     */
    static final class DummySchema extends AbstractControlSystemSchema {


        @Override
        protected String getDefaultBackgroundColor() {
            return BACKGROUND_COLOR;
        }

        @Override
        protected String getDefaultErrorColor() {
            return ERROR_COLOR;
        }

        @Override
        protected String getDefaultForegroundColor() {
            return FOREGROUND_COLOR;
        }

        @Override
        protected String getDefaultRecordAlias() {
            return ALIAS;
        }

        @Override
        protected void initializeWidget() {

        }

    }

}
