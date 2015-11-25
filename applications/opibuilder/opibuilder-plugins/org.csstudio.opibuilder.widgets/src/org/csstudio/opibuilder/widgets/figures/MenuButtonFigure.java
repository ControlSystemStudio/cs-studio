package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.widgets.Activator;
import org.csstudio.swt.widgets.figures.ITextFigure;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * Figure for the menu button.
 *
 * @author Matthew Furseman
 *
 */
public class MenuButtonFigure extends Label implements ITextFigure{

    public static final int ICON_WIDTH = 15;

    private static final Image downArrow = CustomMediaFactory.getInstance().
            getImageFromPlugin(Activator.PLUGIN_ID, "icons/downArrow.png");

    private boolean showDownArrow = false;

    @Override
    public void setText(String s) {
        super.setText(s);
        layoutIcons();
    }

    @Override
    public void setBounds(Rectangle rect) {
        super.setBounds(rect);
        layoutIcons();
    }

    /**
     * Control the appearance of the down arrow indicator
     * on the menu button.
     *
     * @param showArrow if the arrow is to be shown.
     */
    public void showDownArrow(boolean showArrow) {
        this.showDownArrow = showArrow;
        if(showArrow) {
            setIcon(downArrow);
        } else {
            setIcon(null);
        }
        layoutIcons();
    }

    /**
     * Layout the contents of the widget so that, if an icon is displayed, it is
     * right aligned and the text remains centred.
     */
    private void layoutIcons() {
        if(showDownArrow) {
            setLabelAlignment(PositionConstants.RIGHT);
            setTextPlacement(PositionConstants.WEST);
        } else {
            setLabelAlignment(PositionConstants.CENTER);
        }
    }
}
