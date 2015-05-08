package org.csstudio.dct.ui.graphicalviewer.view;

import java.util.LinkedHashMap;

import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.graphicalviewer.model.RecordNode;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.internal.ui.palette.editparts.RaisedBorder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Represents a {@link RecordNode}.
 *
 * @author Sven Wende
 *
 */
public class RecordFigure extends Panel {
    private Panel connectionIndictor;
    private Panel connectionStatePanel;
    private Panel recordInformationPanel;

    /**
     * Constructor.
     *
     * @param caption the caption
     */
    public RecordFigure(String caption) {
        setLayoutManager(new XYLayout());

        // .. border
        setBorder(new RaisedBorder());

        // .. record icon
        ImageFigure image = new ImageFigure();
        image.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/record.png"));
        add(image);
        setConstraint(image, new Rectangle(0, 0, 20, 20));

        // .. caption
        Panel textPanel = new Panel();
        textPanel.setFont(CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE));
        textPanel.add(new Label(caption));
        textPanel.setLayoutManager(new FlowLayout());
        add(textPanel);
        setConstraint(textPanel, new Rectangle(20, 2, 165, 20));


        // .. connection indicator
        connectionIndictor = new Panel();
        connectionIndictor.setBackgroundColor(CustomMediaFactory.getInstance().getColor(255,0,0));
        connectionIndictor.setBorder(new LineBorder(CustomMediaFactory.getInstance().getColor(0,0,0), 1));
        add(connectionIndictor);
        setConstraint(connectionIndictor, new Rectangle(186,4,10,10));

        // .. tooltip for record information
        recordInformationPanel = new Panel();
        setToolTip(createTooltip(recordInformationPanel));

        // .. tooltip for connection state
        connectionStatePanel = new Panel();
        connectionIndictor.setToolTip(createTooltip(connectionStatePanel));
    }

    public void setConnectionInformation(LinkedHashMap<String, String> infos) {
        updateTooltipInformation(connectionStatePanel, infos);
    }

    public void setRecordInformation(LinkedHashMap<String, String> infos) {
        updateTooltipInformation(recordInformationPanel, infos);
    }

    private void updateTooltipInformation(Panel tooltipPanel, LinkedHashMap<String, String> infos) {
        tooltipPanel.removeAll();

        tooltipPanel.setLayoutManager(new GridLayout(2, false));

        for(String key : infos.keySet()) {

            Label column1 = new Label(key+":");
            column1.setFont(CustomMediaFactory.getInstance().getDefaultFont(SWT.BOLD));

            Label column2 = new Label(infos.get(key));

            tooltipPanel.add(column1);
            tooltipPanel.add(column2);
        }

    }

    public void setConnectionIndictorColor(Color color) {
        connectionIndictor.setBackgroundColor(color);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize(int hint, int hint2) {
        return new Dimension(200, 20);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize(int w, int h) {
        return new Dimension(200, 20);
    }

    private Panel createTooltip(Panel content) {
        Panel tooltip = new Panel();
        GridLayout layout = new GridLayout(1, true);
        layout.marginHeight=10;
        layout.marginWidth=10;
        tooltip.setLayoutManager(layout);

        tooltip.setBackgroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        tooltip.add(content);

        return tooltip;
    }
}
