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

import static org.junit.Assert.assertTrue;

import org.csstudio.sds.components.model.BargraphModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableBargraphFigure;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Kai Meyer
 *
 */
public final class BargraphEditPartTest {

    /**
     * The instance to test.
     */
    private BargraphEditPart _editPart;

    /**
     * Test set up.
     */
    @Before
    public void setUp() {
        _editPart = new BargraphEditPart();
        _editPart.setModel(new BargraphModel());
        ((RefreshableBargraphFigure)_editPart.getFigure()).setDefaultFillColor(CustomMediaFactory.getInstance().getColor(new RGB(100,100,100)));
        ((BargraphModel)_editPart.getModel()).setPropertyValue(BargraphModel.PROP_FILL, 50);
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.components.ui.internal.editparts.BargraphEditPart#doCreateFigure()}.
     */
    @Test
    public void testCreateFigure() {
        IFigure figure = _editPart.doCreateFigure();
        assertTrue(figure instanceof RefreshableBargraphFigure);
    }



}
