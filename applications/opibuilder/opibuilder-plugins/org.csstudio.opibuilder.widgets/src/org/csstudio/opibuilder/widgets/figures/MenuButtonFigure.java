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

    @Override
    public void setText(String s) {
        super.setText(s);
        updateLayout();
    }

    @Override
    public void setBounds(Rectangle rect) {
        super.setBounds(rect);
        updateLayout();
    }

    /**
     * Control the appearance of the down arrow indicator
     * on the menu button.
     *
     * @param isVisible if the arrow is to be shown.
     */
    public void setDownArrowVisible(boolean isVisible) {
        if(isVisible) {
            setIcon(downArrow);
            setLabelAlignment(PositionConstants.RIGHT);
            setTextPlacement(PositionConstants.WEST);
        } else {
            setIcon(null);
            setLabelAlignment(PositionConstants.CENTER);
        }
        updateLayout();
    }

    /**
     * Layout the contents of the widget so that, if an icon is displayed, it is
     * right aligned and the text remains centred.
     */
    private void updateLayout() {
        /*
         * In Draw2d there appears to be no way adding a right aligned arrow to a
         * label. We fake the effect here by checking the widths of the text and
         * of the label then adding an appropriate gap so that the text looks as
         * if it has been centred.
         */
        setIconTextGap((getBounds().width - this.getTextBounds().width - ICON_WIDTH)/2);
    }
}
