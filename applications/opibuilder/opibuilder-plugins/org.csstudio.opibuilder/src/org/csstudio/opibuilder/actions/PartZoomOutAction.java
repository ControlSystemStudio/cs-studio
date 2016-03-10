package org.csstudio.opibuilder.actions;

import org.eclipse.gef.internal.InternalImages;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomOutAction;

/**Tweak {@link ZoomOutAction}
 * @author danlee, Xihui Chen
 */
@SuppressWarnings("restriction")
public class PartZoomOutAction extends PartZoomAction {

    /**
     * Constructor for ZoomInAction.
     *
     * @param zoomManager
     *            the zoom manager
     */
    public PartZoomOutAction() {
        super("Zoom &Out", InternalImages.DESC_ZOOM_OUT    );
        setToolTipText("Zoom Out");
        setId(GEFActionConstants.ZOOM_OUT);
        setActionDefinitionId(GEFActionConstants.ZOOM_OUT);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        if(zoomManager != null)
            zoomManager.zoomOut();
    }

    /**
     * @see org.eclipse.gef.editparts.ZoomListener#zoomChanged(double)
     */
    @Override
    public void zoomChanged(double zoom) {
        setEnabled(zoomManager.canZoomOut());
    }

}
