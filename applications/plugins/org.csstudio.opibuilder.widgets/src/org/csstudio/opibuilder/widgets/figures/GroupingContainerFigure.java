package org.csstudio.opibuilder.widgets.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;

/**The figure of grouping container, which can host children widgets.
 * @author Xihui Chen
 *
 */
public class GroupingContainerFigure extends Figure {
	
	private IFigure pane;
	
	private boolean transparent;

	private ScrollPane scrollPane;
	
	public GroupingContainerFigure() {
		scrollPane = new ScrollPane(){
			@Override
			public boolean isOpaque() {
				return !transparent;
			}
		};
		pane = new FreeformLayer();
		pane.setLayoutManager(new FreeformLayout());
		setLayoutManager(new StackLayout());
		add(scrollPane);
		scrollPane.setViewport(new FreeformViewport());
		scrollPane.setContents(pane);			
	}
	
	public void setShowScrollBar(boolean show){
		scrollPane.setScrollBarVisibility(show ? ScrollPane.AUTOMATIC : ScrollPane.NEVER);
	}
	
	@Override
	public void setOpaque(boolean opaque) {		
		transparent =!opaque;
		pane.setOpaque(opaque);
		super.setOpaque(opaque);
	}
	
	public IFigure getContentPane(){
		return pane;
	}
	
	

	
	
	
}
