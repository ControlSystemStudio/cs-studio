/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.util.GraphicsUtil;
import org.csstudio.ui.util.ColorConstants;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ButtonModel;
import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Toggle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**The figure of Radio Box.
 * @author Xihui Chen
 *
 */
public class RadioBoxFigure extends AbstractChoiceFigure {

    public RadioBoxFigure(boolean runMode) {
        super(runMode);
        selectedColor = ColorConstants.black;
    }

    @Override
    protected Toggle createToggle(String text) {
        return new RadioBox(text);
    }



class RadioBox    extends Toggle {

        private RadioFigure radio = null;

        /**
         * Constructs a CheckBox with no text.
         *
         * @since 2.0
         */
        public RadioBox() {
            this(""); //$NON-NLS-1$
        }

        /**
         * Constructs a CheckBox with the passed text in its label.
         * @param text The label text
         * @since 2.0
         */
        public RadioBox(String text) {
            radio = new RadioFigure(text);
            setContents(radio);
            if (runMode && !Activator.isRAP()){
                addMouseMotionListener(new MouseMotionListener.Stub() {

                    @Override
                    public void mouseEntered(MouseEvent me) {
                        Color backColor = getBackgroundColor();
                        RGB darkColor = GraphicsUtil.mixColors(
                                backColor.getRGB(), new RGB(94, 151, 230), 0.7);
                        setBackgroundColor(CustomMediaFactory.getInstance()
                                .getColor(darkColor));
                    }

                    @Override
                    public void mouseExited(MouseEvent me) {
                        setBackgroundColor(RadioBoxFigure.this
                                .getBackgroundColor());
                    }
                });
            }
        }




        /**
         * Adjusts CheckBox's icon depending on selection status.
         *
         * @since 2.0
         */
        protected void handleSelectionChanged() {
            radio.setSelected(isSelected());

        }

        /**
         * Initializes this Clickable by setting a default model and adding a clickable event
         * handler for that model. Also adds a ChangeListener to update icon when  selection
         * status changes.
         *
         * @since 2.0
         */
        protected void init() {
            super.init();
            addChangeListener(new ChangeListener () {
                public void handleStateChanged(ChangeEvent changeEvent) {
                    if (changeEvent.getPropertyName().equals(ButtonModel.SELECTED_PROPERTY))
                        handleSelectionChanged();
                }
            });
        }
}

class RadioFigure extends Figure{

        private static final int RADIO_RADIUS = 7;
        private static final int DOT_RADIUS = 2;
        private static final int GAP =4 ;

        private boolean selected = false;

        private String text;
        private Boolean support3d;


        public RadioFigure(String text) {
            this.text = text;
            if(runMode)
                setCursor(Cursors.HAND);
        }

        @Override
        protected void paintClientArea(Graphics graphics) {
            super.paintClientArea(graphics);
            if(support3d == null)
                support3d = GraphicsUtil.testPatternSupported(graphics);
            graphics.setAntialias(support3d ? SWT.ON : SWT.OFF);
            Rectangle clientArea = getClientArea();
            Rectangle circle = new Rectangle(
                    clientArea.x , clientArea.getCenter().y - RADIO_RADIUS,
                    2*RADIO_RADIUS, 2*RADIO_RADIUS);
            graphics.pushState();
            if(support3d)
                graphics.setBackgroundPattern(
                    GraphicsUtil.createScaledPattern(graphics, Display.getCurrent(), circle.x, circle.y,
                            circle.x+circle.width, circle.y+circle.height,
                            ColorConstants.white, graphics.getBackgroundColor()));
            graphics.fillArc(circle, 0 ,360);
            graphics.setForegroundColor(
                    CustomMediaFactory.getInstance().getColor(120, 120, 120));
            graphics.drawArc(circle, 0, 360);
            if(selected){
                graphics.setBackgroundColor(selectedColor);
                graphics.fillArc(new Rectangle(
                        circle.getCenter().x - DOT_RADIUS, circle.getCenter().y - DOT_RADIUS,
                        2*DOT_RADIUS+1, 2*DOT_RADIUS+1), 0, 360);
            }
            graphics.popState();
            Dimension textSize = FigureUtilities.getTextExtents(text, graphics.getFont());
            if (!isEnabled()) {
                graphics.translate(1, 1);
                graphics.setForegroundColor(ColorConstants.buttonLightest);
                graphics.drawText(text, circle.getRight().getTranslated(GAP, -textSize.height/2));
                graphics.translate(-1, -1);
                graphics.setForegroundColor(ColorConstants.buttonDarker);
            }

            graphics.drawText(text, circle.getRight().getTranslated(GAP, -textSize.height/2));

        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        public void setText(String text) {
            this.text = text;
            repaint();
        }

    }

}




