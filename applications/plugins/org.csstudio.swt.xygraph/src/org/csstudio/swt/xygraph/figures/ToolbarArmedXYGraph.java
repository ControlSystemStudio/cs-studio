package org.csstudio.swt.xygraph.figures;

import org.csstudio.swt.xygraph.toolbar.XYGraphToolbar;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;


public class ToolbarArmedXYGraph extends Figure {

	private XYGraph xyGraph;

	private XYGraphToolbar toolbar;

	private boolean transparent;
	private final static int MARGIN = 3;
	
	public ToolbarArmedXYGraph() {
		this(new XYGraph());
	}
	public ToolbarArmedXYGraph(XYGraph xyGraph) {
		
		this.xyGraph = xyGraph;
		toolbar = new XYGraphToolbar(this.xyGraph);
		xyGraph.setOpaque(false);
		toolbar.setOpaque(false);
		add(toolbar);		
		add(xyGraph);
		setRequestFocusEnabled(true);
		setFocusTraversable(true);
		addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent fe) {
				repaint();				
			}
			public void focusLost(FocusEvent fe) {
				repaint();
			}			
		});
	}
	
	@Override
	protected void layout() {
		Rectangle clientArea = getClientArea().getCopy();
		if(toolbar.isVisible()){
			Dimension size = toolbar.getPreferredSize();
			toolbar.setBounds(new Rectangle(clientArea.x + MARGIN, clientArea.y + MARGIN, 
					size.width, size.height));
			clientArea.y += size.height + 2*MARGIN;
			clientArea.height -= size.height + 2*MARGIN;
		}
		xyGraph.setBounds(new Rectangle(clientArea));
			
		super.layout();
	}

	/**
	 * If this XY-Graph has focus, this method paints a focus rectangle.
	 * 
	 * @param graphics Graphics handle for painting
	 */
	protected void paintBorder(Graphics graphics) {
		super.paintBorder(graphics);
		if (hasFocus()) {
			graphics.setForegroundColor(ColorConstants.black);
			graphics.setBackgroundColor(ColorConstants.white);
			Rectangle area = getClientArea();			
			graphics.drawFocus(area.x, area.y, area.width-1, area.height-1);			
		}
	}
	
	/**
	 * @param showToolbar the showToolbar to set
	 */
	public void setShowToolbar(boolean showToolbar) {
			toolbar.setVisible(showToolbar);
			revalidate();
	}

	/**
	 * @return the showToolbar
	 */
	public boolean isShowToolbar() {
		return toolbar.isVisible();
	}
	
	/**
	 * @return the xyGraph
	 */
	public XYGraph getXYGraph() {
		return xyGraph;
	}
	

	@Override
	public boolean isOpaque() {
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	public void paintFigure(final Graphics graphics) {		
		if (!transparent)		
			graphics.fillRectangle(getClientArea());		
		super.paintFigure(graphics);
	}
	/**
	 * @return the transparent
	 */
	public boolean isTransparent() {
		return transparent;
	}
	/**
	 * @param transparent the transparent to set
	 */
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
		xyGraph.setTransparent(transparent);
	}
	
}
