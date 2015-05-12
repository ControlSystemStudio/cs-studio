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
package org.csstudio.utility.adlconverter.utility.widgets;

import org.csstudio.sds.components.model.PolylineModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 11.09.2007
 */
public class Polyline extends Widget{

    /**
     * @param polyline The ADL String for a Polyline
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     */
    public Polyline(final ADLWidget polyline, AbstractWidgetModel abstractWidgetModel, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute) {
        super(polyline, storedBasicAttribute, storedDynamicAttribute);
        if(getBasicAttribute()!=null){
            if(getBasicAttribute().getWidth()!=null){
                _widget.setPropertyValue(PolylineModel.PROP_LINE_WIDTH, getBasicAttribute().getWidth());
            }
            if(getBasicAttribute().getStyle()!=null){
                setLineStyle(getBasicAttribute().getStyle());
            }
            getBasicAttribute().setStyle("0"); //$NON-NLS-1$
        }
        if(getPoints()!=null&&getPoints().getPointsList()!=null) {
            int minX = Integer.MAX_VALUE;
            int maxX = 0;
            int minY = Integer.MAX_VALUE;
            int maxY = 0;
            int size = getPoints().getPointsList().size();
            for (int i=0;i<size;i++) {
                Point point = getPoints().getPointsList().getPoint(i);
                if(point.x<minX) {
                    minX = point.x;
                }
                if(point.x>maxX) {
                    maxX = point.x;
                }
                if(point.y<minY) {
                    minY = point.y;
                }
                if(point.y>maxY) {
                    maxY = point.y;
                }
            }
            _widget.setWidth(maxX-minX);
            _widget.setHeight(maxY-minY);

        }else {
            _widget.setWidth(getObject().getWidth());
            _widget.setHeight(getObject().getHeight());
        }
        _widget.setPropertyValue(PolylineModel.PROP_FILL, 100.0);
        ADLHelper.checkAndSetLayer(_widget, abstractWidgetModel);

    }


    /**
     * Set the style of the polyline.
     * @param style the style to set.
     */
    private void setLineStyle(final String style) {
        //  <property type="sds.option" id="linestyle">
        //      <option id="1" />
        //  </property>
        if(style!=null&&style.equals("5")){ //$NON-NLS-1$
            _widget.setPropertyValue(PolylineModel.PROP_LINE_STYLE, 1);
        }else{
            _widget.setPropertyValue(PolylineModel.PROP_LINE_STYLE, 0);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(PolylineModel.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void convertCoordinate(final int x, final int y) {
        super.convertCoordinate(x, y);
        PointList pl = getPoints().getPointsList();
        org.eclipse.draw2d.geometry.Point point;
        int oldSize = pl.size();
        for (int i = 0; i < pl.size(); i++) {
            point = pl.getPoint(i);
            point.x=(point.x-x);
            point.y=(point.y-y);
            pl.setPoint(point, i);
        }
        assert oldSize == pl.size() : Messages.Polyline_AssertError;
    }
}
