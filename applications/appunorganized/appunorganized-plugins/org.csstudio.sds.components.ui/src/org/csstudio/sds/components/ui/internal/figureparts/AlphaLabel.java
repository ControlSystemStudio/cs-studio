package org.csstudio.sds.components.ui.internal.figureparts;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;

/**
 * A label whose background could be set with alpha.
 * Alpha may range from 0 to 255. A value of 0 is completely transparent
 * @author Xihui Chen
 *
 */
public class AlphaLabel extends Label {

    private int alpha = 100;

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected void paintFigure(Graphics graphics) {
        graphics.pushState();
        graphics.setAlpha(alpha);
        graphics.fillRectangle(bounds);
        graphics.popState();
        super.paintFigure(graphics);
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }
}
