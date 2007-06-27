package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformListener;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.ZoomManager;

/**
 * {@inheritDoc}
 */
public final class LinkingContainerFigure extends Panel implements IRefreshableFigure {

	private ScalableFreeformLayeredPane pane;

	private ZoomManager _zoomManager;

	public LinkingContainerFigure() {
		setBorder(new LineBorder(1));
		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setScrollBarVisibility(ScrollPane.NEVER);
		pane = new ScalableFreeformLayeredPane(){
			
		};
		pane.setLayoutManager(new FreeformLayout());

		setLayoutManager(new StackLayout());
		add(scrollpane);
		final FreeformViewport freeformViewport = new FreeformViewport();
		scrollpane.setViewport(freeformViewport);
		scrollpane.setContents(pane);

		_zoomManager = new ZoomManager(pane, freeformViewport);
		_zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
		// _zoomManager.setZoomAsText(ZoomManager.FIT_ALL);

		setBackgroundColor(ColorConstants.blue);
		setForegroundColor(ColorConstants.blue);
		setOpaque(true);

		addFigureListener(new FigureListener(){
			public void figureMoved(IFigure source) {
				CentralLogger.getInstance().info(null, "moved "+getSize());
				freeformViewport.setSize(getSize());
				_zoomManager.setZoom(1.0);
				_zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
			}
			
		});
		
		pane.addFigureListener(new FigureListener(){
			public void figureMoved(IFigure source) {
				CentralLogger.getInstance().info(null, "pane moved "+pane.getSize());
			}
			
		});
	}

	public IFigure getContentsPane() {
		return pane;
	}

	public void updateZoom() {
		CentralLogger.getInstance().info(null, ""+pane.getFreeformExtent());
		_zoomManager.setZoom(1.0);
		_zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
	}
	
	/**
	 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
	 */
	public Rectangle getHandleBounds() {
		return getBounds().getCropped(new Insets(2, 0, 2, 0));
	}

	public Dimension getPreferredSize(int w, int h) {
		Dimension prefSize = super.getPreferredSize(w, h);
		Dimension defaultSize = new Dimension(100, 100);
		prefSize.union(defaultSize);
		return prefSize;
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		Rectangle rect = getBounds().getCopy();
		rect.crop(new Insets(2, 0, 2, 0));
		graphics.fillRectangle(rect);
	}

	public String toString() {
		return "CircuitBoardFigure"; //$NON-NLS-1$
	}

	protected boolean useLocalCoordinates() {
		return false;
	}

	public void randomNoiseRefresh() {
		// TODO Auto-generated method stub

	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
