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

import org.csstudio.sds.components.model.PolygonModel;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.utility.adlconverter.internationalization.Messages;
import org.csstudio.utility.adlconverter.utility.ADLHelper;
import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.eclipse.draw2d.geometry.PointList;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 03.09.2007
 */



public class Polygon extends Widget{

    /**
     * @param polygon The ADL String for a Polygon
     * @param storedDynamicAttribute
     * @param storedBasicAttribute
     */
    public Polygon(final ADLWidget polygon, AbstractWidgetModel abstractWidgetModel, ADLWidget storedBasicAttribute, ADLWidget storedDynamicAttribute){
        super(polygon, storedBasicAttribute, storedDynamicAttribute);
        if(getBasicAttribute()!=null){
            getBasicAttribute().setStyle("0"); //$NON-NLS-1$
        }
        _widget.setPropertyValue(PolygonModel.PROP_FILL, 100.0);
        _widget.setPropertyValue(PolygonModel.PROP_BORDER_STYLE, 0);
        _widget.setPropertyValue(PolygonModel.PROP_BORDER_COLOR, _widget.getColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND));
        ADLHelper.checkAndSetLayer(_widget, abstractWidgetModel);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    final void setWidgetType() {
        _widget = createWidgetModel(PolygonModel.ID);
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
        assert oldSize == pl.size() : Messages.Polygon_AssertError; //$NON-NLS-1$
    }
 }

