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
package org.csstudio.sds.components.ui.internal.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A timer figure.
 *
 * @author Kai Meyer & Sven Wende
 *
 */
public final class RefreshableTimerFigure extends Panel {

    private double percentage;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintFigure(final Graphics graphics) {
        Rectangle rectangle = this.getBounds().getCopy().crop(new Insets(2));
        graphics.setBackgroundColor(ColorConstants.white);
        graphics.fillOval(rectangle);
        graphics.setForegroundColor(ColorConstants.black);
        graphics.drawOval(rectangle);
        int x = rectangle.x + rectangle.width/2;
        int y = rectangle.y + rectangle.height/2;
        graphics.setBackgroundColor(ColorConstants.red);
        Double d = (double) percentage*360;
        graphics.fillArc(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 30, d.intValue());
        graphics.drawLine(x, rectangle.y, x, rectangle.y+rectangle.height/6);
        graphics.drawLine(x, rectangle.y+rectangle.height, x, rectangle.y+rectangle.height-rectangle.height/6);
        graphics.drawLine(rectangle.x, y, rectangle.x+rectangle.width/6, y);
        graphics.drawLine(rectangle.x+rectangle.width, y, rectangle.x+rectangle.width-rectangle.width/6, y);
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
        repaint();
    }
}
