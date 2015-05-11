/*
        * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
        * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
        *
        * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
        * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
        NOT LIMITED
        * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
        AND
        * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
        BE LIABLE
        * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
        CONTRACT,
        * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
        SOFTWARE OR
        * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
        DEFECTIVE
        * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
        REPAIR OR
        * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
        OF THIS LICENSE.
        * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
        DISCLAIMER.
        * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
        ENHANCEMENTS,
        * OR MODIFICATIONS.
        * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
        MODIFICATION,
        * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
        DISTRIBUTION OF THIS
        * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
        MAY FIND A COPY
        * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
        */
package org.csstudio.sds.ui.figures;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 18.06.2010
 */
public class CrossedOutAdapter implements ICrossedFigure {

    private boolean _crossedOut = false;
    private Color _lineColor = new Color(null, 0,0,0);
    private int _crossLineWidth = 2;
    private final Figure _figure;

    /**
     * Constructor.
     */
    public CrossedOutAdapter(final Figure figure) {
        _figure = figure;
        IPreferencesService preferencesService = Platform.getPreferencesService();
        String prefColor = preferencesService.getString(SdsPlugin.getDefault().getBundle().getSymbolicName(),PreferenceConstants.PROP_CROSSED_WIDGET_COLOR , "255,0,255", null);
        RGB rgb;
        try {
            rgb = StringConverter.asRGB(prefColor);
        } catch (Exception e) {
            rgb = new RGB(255, 0, 255);
        }
        _lineColor = CustomMediaFactory.getInstance().getColor(rgb);
        _crossLineWidth = preferencesService.getInt(SdsPlugin.getDefault().getBundle().getSymbolicName(),PreferenceConstants.PROP_CROSSED_WIDGET_LINE_WIDTH , 3, null);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCrossedOut(final boolean crossedOut) {
        _crossedOut = crossedOut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(final Graphics graphics) {
        paint(graphics, true);
    }
    public void paint(final Graphics graphics, final boolean absolute) {
        if (_crossedOut) {
            //          graphics.pushState();
            Rectangle figureBounds = _figure.getBounds();
            graphics.setBackgroundColor(_lineColor);
            graphics.setForegroundColor(_lineColor);
            graphics.setLineWidth(_crossLineWidth);
            int x = 0;
            int y = 0;
            if (absolute) {
                x = figureBounds.x;
                y = figureBounds.y;
            }
            int w = x + figureBounds.width;
            int h = y + figureBounds.height;
            graphics.drawLine(x, y, w, h);
            graphics.drawLine(w, y, x, h);
            //          graphics.popState();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColor(final Color lineColor) {
        _lineColor = lineColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCrossLineWidth(final int crossLineWidth) {
        _crossLineWidth = crossLineWidth;
    }

}
