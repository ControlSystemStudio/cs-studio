package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayeredPane;
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
 * A Widget, which links to another display.
 * @author Sven Wende
 *
 */
public final class LinkingContainerFigure extends Panel implements IAdaptable {

	/**
	 * The content pane of this widget.
	 */
	private ScalableFreeformLayeredPane _pane;
	/**
	 * The zoom manager for this widget.
	 */
	private ZoomManager _zoomManager;

	/**
	 * Constructor.
	 */
	public LinkingContainerFigure() {
		setBorder(new LineBorder(1));
		ScrollPane scrollpane = new ScrollPane();
		scrollpane.setScrollBarVisibility(ScrollPane.NEVER);
		_pane = new ScalableFreeformLayeredPane(){
			
		};
		_pane.setLayoutManager(new FreeformLayout());

		setLayoutManager(new StackLayout());
		add(scrollpane);
		final FreeformViewport freeformViewport = new FreeformViewport();
		scrollpane.setViewport(freeformViewport);
		scrollpane.setContents(_pane);

		_zoomManager = new ZoomManager(_pane, freeformViewport);
		_zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
		// _zoomManager.setZoomAsText(ZoomManager.FIT_ALL);

		setBackgroundColor(ColorConstants.blue);
		setForegroundColor(ColorConstants.blue);
		setOpaque(true);

		addFigureListener(new FigureListener(){
			public void figureMoved(final IFigure source) {
				CentralLogger.getInstance().info(null, "moved "+getSize());
				freeformViewport.setSize(getSize());
				_zoomManager.setZoom(1.0);
				_zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
			}
			
		});
		
		_pane.addFigureListener(new FigureListener(){
			public void figureMoved(final IFigure source) {
				CentralLogger.getInstance().info(null, "pane moved "+_pane.getSize());
			}
			
		});
	}

	/**
	 * Returns the content pane.
	 * @return IFigure
	 * 			The content pane.
	 */
	public LayeredPane getContentsPane() {
		return _pane;
	}

	/**
	 * Refreshes the zoom.
	 */
	public void updateZoom() {
		_zoomManager.setZoom(1.0);
		_zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
	}
	
	/**
	 * Returns the bounds of the handles.
	 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
	 * @return Rectangle
	 * 			The bounds of the handles
	 */
	public Rectangle getHandleBounds() {
		return getBounds().getCropped(new Insets(2, 0, 2, 0));
	}

	/**
	 * {@inheritDoc}
	 */
	public Dimension getPreferredSize(final int w, final int h) {
		Dimension prefSize = super.getPreferredSize(w, h);
		Dimension defaultSize = new Dimension(100, 100);
		prefSize.union(defaultSize);
		return prefSize;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void paintFigure(final Graphics graphics) {
		Rectangle rect = getBounds().getCopy();
		rect.crop(new Insets(2, 0, 2, 0));
		graphics.fillRectangle(rect);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "CircuitBoardFigure"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean useLocalCoordinates() {
		return false;
	}

	/**
	 * This method is a tribute to unit tests, which need a way to test the
	 * performance of the figure implementation. Implementors should produce
	 * some random changes and refresh the figure, when this method is called.
	 * 
	 */
	public void randomNoiseRefresh() {
		//nothing to do yet
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
