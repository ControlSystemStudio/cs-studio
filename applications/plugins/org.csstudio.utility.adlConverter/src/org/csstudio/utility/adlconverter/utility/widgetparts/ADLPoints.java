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
 * $Id$
 */
package org.csstudio.utility.adlconverter.utility.widgetparts;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.csstudio.utility.adlconverter.utility.FileLine;
import org.csstudio.utility.adlconverter.utility.WrongADLFormatException;
import org.eclipse.draw2d.geometry.PointList;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 07.09.2007
 */
public class ADLPoints extends WidgetPart{
    /**
     * List with all coordinate points.
     */
    private PointList _pointsList;

    /**
     * The default constructor.
     *
     * @param adlPoints An ADLWidget that correspond a ADL Points.
     * @param parentWidgetModel The Widget that set the parameter from ADLWidget.
     * @throws WrongADLFormatException Wrong ADL format or untreated parameter found.
     */
    public ADLPoints (final ADLWidget adlPoints , final AbstractWidgetModel parentWidgetModel) throws WrongADLFormatException {
        super(adlPoints, parentWidgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void init() {
        /* Not to initialization*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void parseWidgetPart(final ADLWidget adlPoints) throws WrongADLFormatException {

        assert adlPoints.isType("points") : Messages.ADLPoints_AssertError_Begin+adlPoints.getType()+Messages.ADLPoints_AssertError_End; //$NON-NLS-1$

        _pointsList = new PointList(adlPoints.getBody().size());
        for (FileLine  fileLine : adlPoints.getBody()) {
            String points = fileLine.getLine();
            String[] row = points.replaceAll("[()]", "").trim().split(","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            if(row.length!=2){
                throw new WrongADLFormatException(Messages.ADLPoints_WrongADLFormatException_Begin+points+Messages.ADLPoints_WrongADLFormatException_End);
            }

            _pointsList.addPoint(Integer.parseInt(row[0]),Integer.parseInt(row[1]));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final void generateElements() {
        _widgetModel.setPropertyValue("points", _pointsList); //$NON-NLS-1$
    }

    /**
     *
     * @return the Coordinate list.
     */
    public final PointList getPointsList() {
        return _pointsList;
    }

    /**
     *
     * @param pointsList set the Coordinate list.
     */
    public final void setPointsList(final PointList pointsList) {
        _pointsList = pointsList;
        _widgetModel.setPropertyValue("points", _pointsList); //$NON-NLS-1$
    }

}
