package org.csstudio.swt.rtplot.internal;

import java.util.Arrays;

import org.csstudio.swt.rtplot.Annotation;
import org.csstudio.swt.rtplot.PlotListenerAdapter;
import org.csstudio.swt.rtplot.RTPlot;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.Trace;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class LegendHandler<XTYPE extends Comparable<XTYPE>> {

    private RTPlot<XTYPE> plot;
    private Composite legendbar;
    
    final private SWTMediaPool media;

    private final int GAP = 2;
    private Font font;
    private Color background;

    /**
     * Construct tool bar
     * 
     * @param rtPlot
     *            {@link RTPlot} to control from tool bar
     */
    public LegendHandler(final RTPlot<XTYPE> rtPlot) {
        plot = rtPlot;
        media = new SWTMediaPool(rtPlot.getParent().getDisplay());
        legendbar = new Composite(rtPlot, SWT.BORDER | SWT.WRAP);
        rtPlot.addListener(new PlotListenerAdapter<XTYPE>() {

            @Override
            public void changedTraces() {
                Display.getCurrent().asyncExec(() -> {
                    updateLegend();
                });

            }
        });
        rtPlot.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                updateLegend();
            }
        });
        rtPlot.addDisposeListener((event) ->{
            media.dispose();
        });
        updateLegend();
    }

    /**
     * {@link RTPlot} creates {@link ToolbarHandler} in two stages: Construct,
     * then call init, so that tool bar can refer back to the
     * {@link ToggleToolbarAction}
     */
    public void addContextMenu(final Action toggle_action) {
        final MenuManager mm = new MenuManager();
        mm.add(toggle_action);
        legendbar.setMenu(mm.createContextMenu(legendbar));
    }

    public void setFont(FontData font) {
        this.font = media.get(font);
        updateLegend();
    }

    public void setBackground(RGB background) {
        this.background = media.get(background);
        updateLegend();
    }

    private void updateLegend() {
        clearLegend();
        
        int width = plot.getClientArea().width;
        // Calculate average label size
        if (plot.getTraceCount() > 0) {
            int sum = 0;
            for (Trace<XTYPE> trace : plot.getTraces()) {
                sum = sum + FigureUtilities.getTextWidth(trace.getName(), font);
            }
            int avg = Math.floorDiv(sum, plot.getTraceCount());
            int columns = Math.floorDiv(width, (int) (avg + (2 * GAP)));
            legendbar.setBackground(background);
            legendbar.setLayout(new GridLayout(columns, true));
            for (Trace<XTYPE> trace : plot.getTraces()) {
                final Label label = new Label(legendbar, SWT.NONE);
                label.setFont(font);
                label.setBackground(background);
                label.setForeground(media.get(trace.getColor()));
                label.setText(trace.getName());
                label.setToolTipText(trace.getName());
                GridData gd = new GridData();
                gd.grabExcessHorizontalSpace = true;
                gd.horizontalAlignment = GridData.CENTER;
                label.setLayoutData(gd);
            }
        }

        legendbar.layout();
        legendbar.getParent().layout();
    }

    private void clearLegend() {
        for (Control child : Arrays.asList(legendbar.getChildren())) {
            child.dispose();
        }
    }

    public Control getControl() {
        return legendbar;
    }
    
}
