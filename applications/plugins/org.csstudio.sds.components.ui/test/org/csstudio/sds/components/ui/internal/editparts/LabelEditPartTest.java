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
package org.csstudio.sds.components.ui.internal.editparts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.cursorservice.AbstractCursor;
import org.csstudio.sds.model.AbstractTextTypeWidgetModel;
import org.csstudio.sds.model.LabelModel;
import org.csstudio.sds.model.TextTypeEnum;
import org.eclipse.draw2d.IFigure;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Hofer
 *
 * @version $Revision: 1.3 $
 *
 */
public final class LabelEditPartTest {

    /**
     * The instance to test.
     */
    private LabelEditPart _editPart;

    /**
     * Test set up.
     */
    @Before
    public void setUp() {
        _editPart = new LabelEditPart();
        final LabelModel labelModel = new LabelModel(new ArrayList<AbstractCursor>());
        _editPart.setModel(labelModel);
    }

    /**
     * Test method for {@link org.csstudio.sds.components.ui.internal.editparts.LabelEditPart#doCreateFigure()}.
     */
    @Test
    public void testCreateFigure() {
        final IFigure figure = _editPart.doCreateFigure();
        assertTrue(figure instanceof RefreshableLabelFigure);
    }

    @Test
    public void testTextTypeEnumHEX() throws Exception {
        _editPart.getCastedModel().setPropertyValue(AbstractTextTypeWidgetModel.PROP_TEXT_TYPE, TextTypeEnum.EXP.getIndex());

        _editPart.getCastedModel().setTextValue("1");
        String determineLabel = _editPart.determineLabel(LabelModel.PROP_TEXTVALUE);
        assertEquals("1,0E00", determineLabel);

        _editPart.getCastedModel().setTextValue("10");
        determineLabel = _editPart.determineLabel(LabelModel.PROP_TEXTVALUE);
        assertEquals("1,0E01", determineLabel);

        _editPart.getCastedModel().setTextValue("1000");
        determineLabel = _editPart.determineLabel(LabelModel.PROP_TEXTVALUE);
        assertEquals("1,0E03", determineLabel);

        _editPart.getCastedModel().setTextValue("1000000000");
        determineLabel = _editPart.determineLabel(LabelModel.PROP_TEXTVALUE);
        assertEquals("1,0E09", determineLabel);


        _editPart.getCastedModel().setTextValue("0.1");
        determineLabel = _editPart.determineLabel(LabelModel.PROP_TEXTVALUE);
        assertEquals("1,0E-01", determineLabel);

        _editPart.getCastedModel().setTextValue("0.001");
        determineLabel = _editPart.determineLabel(LabelModel.PROP_TEXTVALUE);
        assertEquals("1,0E-03", determineLabel);

        _editPart.getCastedModel().setTextValue("0.000000001");
        determineLabel = _editPart.determineLabel(LabelModel.PROP_TEXTVALUE);
        assertEquals("1,0E-09", determineLabel);
    }

}
