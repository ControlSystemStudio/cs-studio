package org.csstudio.dct.ui.graphicalviewer.view;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class ContainerNodeFigure extends Panel {
    private Panel recordArea;

    private Panel left, middle, right;

    private Panel instanceArea;

    private Clickable clickable;
    public ContainerNodeFigure(String caption) {
        setBackgroundColor(CustomMediaFactory.getInstance().getColor(221, 221, 221));
        ToolbarLayout layout = new ToolbarLayout(false);
        layout.setStretchMinorAxis(true);
        setLayoutManager(layout);

        LineBorder border = new LineBorder(2) {
            @Override
            public void paint(IFigure figure, Graphics graphics, Insets insets) {
                graphics.setLineStyle(SWT.LINE_DOT);
                super.paint(figure, graphics, insets);
            }

            @Override
            public Insets getInsets(IFigure figure) {
                return new Insets(10);
            }
        };

        setBorder(border);
        setOpaque(false);

        Panel p = new Panel();
        p.setLayoutManager(new FlowLayout(true));
        add(p);

        Label label = new Label(caption);
        label.setForegroundColor(CustomMediaFactory.getInstance().getColor(0, 0, 0));
        label.setFont(CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.BOLD));

        clickable = new Clickable(label);
        p.add(clickable);

        recordArea = new Panel();
        ToolbarLayout layout2 = new ToolbarLayout(true);
        layout2.setSpacing(25);
        recordArea.setLayoutManager(layout2);

        add(recordArea);

        left = createArea(new RGB(255, 0, 0));
        middle = createArea(new RGB(0, 0, 255));
        right = createArea(new RGB(0, 255, 0));

        instanceArea = new Panel();
        GridLayout layout3 = new GridLayout();
        layout3.numColumns = 4;
        instanceArea.setLayoutManager(layout3);
        add(instanceArea);
    }

    private Panel createArea(RGB rgb) {
        Panel p = new Panel();

        ToolbarLayout layout2 = new ToolbarLayout();
        layout2.setSpacing(20);
        layout2.setStretchMinorAxis(false);
        layout2.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
        p.setLayoutManager(layout2);

        recordArea.add(p);

        return p;
    }

    public Panel getRecordArea() {
        return recordArea;
    }

    public Panel getLeft() {
        return left;
    }

    public Panel getMiddle() {
        return middle;
    }

    public Panel getRight() {
        return right;
    }

    public Panel getInstanceArea() {
        return instanceArea;
    }

    @Override
    public void addMouseListener(MouseListener listener) {
        clickable.addMouseListener(listener);
    }
}
