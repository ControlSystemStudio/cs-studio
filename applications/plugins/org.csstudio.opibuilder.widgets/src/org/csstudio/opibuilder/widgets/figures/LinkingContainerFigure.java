package org.csstudio.opibuilder.widgets.figures;

import org.csstudio.opibuilder.util.UIBundlingThread;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.gef.editparts.ZoomManager;

/**The figure of linking container, which can host children widgets from another OPI file.
 * @author Xihui Chen
 *
 */
public class LinkingContainerFigure extends Figure {
	
	private ScalableFreeformLayeredPane pane;
	
	
	private ZoomManager zoomManager;
	
	private boolean zoomToFitAll;
	
	@SuppressWarnings("deprecation")
	public LinkingContainerFigure() {
		ScrollPane scrollPane = new ScrollPane();
		pane = new ScalableFreeformLayeredPane();
		pane.setLayoutManager(new FreeformLayout());
		setLayoutManager(new StackLayout());
		add(scrollPane);
		FreeformViewport viewPort = new FreeformViewport();
		scrollPane.setViewport(viewPort);
		scrollPane.setContents(pane);	
		
		zoomManager = new ZoomManager(pane, viewPort);
		
		addFigureListener(new FigureListener(){
			public void figureMoved(IFigure source) {
				UIBundlingThread.getInstance().addRunnable(new Runnable(){
					public void run() {
						updateZoom();
					}
				});
				
			}
		});
		
		
		updateZoom();
	}
	
	public IFigure getContentPane(){
		return pane;
	}
	
	/**
	 * Refreshes the zoom.
	 */
	public void updateZoom() {
		zoomManager.setZoom(1.0);

		if (zoomToFitAll) {
			zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
		}
	}
	
	public void setZoomToFitAll(boolean zoomToFitAll) {
		this.zoomToFitAll = zoomToFitAll;
	}

	
	

	
	
	
}
