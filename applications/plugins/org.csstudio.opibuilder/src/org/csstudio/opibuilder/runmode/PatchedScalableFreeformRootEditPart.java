package org.csstudio.opibuilder.runmode;

import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;

/**Patch {@link ScalableFreeformRootEditPart} to change the zoom combo items
 * sort to have predefined zoom contributions on top.
 * @author Xihui Chen
 *
 */
public class PatchedScalableFreeformRootEditPart extends
		ScalableFreeformRootEditPart {

	private ZoomManager zoomManager;
	
	public PatchedScalableFreeformRootEditPart() {
		zoomManager = new ZoomManager((ScalableFigure)getScaledLayers(),
									((Viewport)getFigure())){
			@Override
			public String[] getZoomLevelsAsText() {
				String[] originItems = super.getZoomLevelsAsText();
				if (getZoomLevelContributions() != null) {					
					String[] result = new String[originItems.length];
					int contriSize = getZoomLevelContributions().size();
					for(int i=0; i<originItems.length; i++){
						result[i] = originItems[(originItems.length - contriSize + i) % originItems.length];
					}
					return result;
				}else
					return super.getZoomLevelsAsText();
			}
				
		};
	}
	
	@Override
	public ZoomManager getZoomManager() {
		return zoomManager;
	}
	
}
