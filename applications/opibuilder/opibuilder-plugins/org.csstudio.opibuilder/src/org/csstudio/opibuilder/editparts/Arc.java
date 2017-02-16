package org.csstudio.opibuilder.editparts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;

public class Arc extends Figure {

    private int x;
    private int y;
    private int height;
    private int width;
    private int startAngle;
    private int totalAngle;

    public Arc(int x, int y, int height, int width, int startAngle, int totalAngle) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.startAngle = startAngle;
        this.totalAngle = totalAngle;
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.drawArc(x, y,
                height, width, startAngle, totalAngle);
    }
}
