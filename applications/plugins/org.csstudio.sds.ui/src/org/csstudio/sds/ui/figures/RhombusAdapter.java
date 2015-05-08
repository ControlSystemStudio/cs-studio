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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * Adapter that paint a rhombus over the Widget. <br>
 * For example, it used to be identified simulated values.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 18.06.2010
 */
public class RhombusAdapter implements IRhombusEquippedWidget {

    private final Figure _rhombusFigure;
    Color _rhombusColor = new Color(null, 255,128,0);
    int _rhombusLineWidth = 2;
    private boolean _visible;

    /**
     * Constructor.
     */
    public RhombusAdapter(final Figure figure) {
        _rhombusFigure = figure;
    }

    @Override
    public void setColor(final Color rhombusColor) {
        _rhombusColor = rhombusColor;
        refreshRhombus();
    }

    /**
     * @param rhombusLineWidth the rhombusLineWidth to set
     */
    @Override
    public void setRhombusLineWidth(final int rhombusLineWidth) {
        _rhombusLineWidth = rhombusLineWidth;
        refreshRhombus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(final boolean visible) {
        _visible = visible;
        refreshRhombus();
    }


    /**
     *
     */
    void refreshRhombus() {
        if(_visible) {
//        _rhombusFigure.remove(_rhombus);
//        _rhombus = createRhombus();
        }
    }

    @Override
    public void paint(final Graphics graphics) {
        paint(graphics, true);
    }

    public void paint(final Graphics graphics, final boolean absolute) {
        if(_visible) {
            Rectangle bounds = _rhombusFigure.getBounds();
            int x = 0;
            int y = 0;
            if(absolute) {
                x = bounds.x;
                y = bounds.y;
            }
            int width = bounds.width;
            int height = bounds.height;
            graphics.setBackgroundColor(_rhombusColor);
            graphics.setForegroundColor(_rhombusColor);
            graphics.setLineWidth(_rhombusLineWidth);
            graphics.drawLine(x, y+height/3,  x+width/3, y);
            graphics.drawLine(x, y+height*2/3, x+width/3, y+height);
            graphics.drawLine(x+width*2/3, y,  x+width, y+height/3);
            graphics.drawLine(x+width*2/3, y+height, x+width, y+height*2/3);

        }
    }


}
