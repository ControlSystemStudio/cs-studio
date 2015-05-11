package org.csstudio.dct.ui.graphicalviewer.view;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PolylineConnection;

/**
 * Represents a connection.
 *
 * @author Sven Wende
 *
 */
public class ConnectionFigure extends PolylineConnection {
    public ConnectionFigure() {
        Panel tooltip = new Panel();
        tooltip.setBackgroundColor(CustomMediaFactory.getInstance().getColor(255,0,0));
        tooltip.setSize(200,200);
        tooltip.add(new Label("ddd"));
        tooltip.add(new Label("kkdsflkdsf"));
        tooltip.setLayoutManager(new FlowLayout());
        setToolTip(tooltip);
    }

    @Override
    public void paintFigure(Graphics graphics) {
        super.paintFigure(graphics);
    }
}
