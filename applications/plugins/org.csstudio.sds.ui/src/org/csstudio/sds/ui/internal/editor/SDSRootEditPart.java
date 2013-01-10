package org.csstudio.sds.ui.internal.editor;

import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.AutoexposeHelper;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ViewportAutoexposeHelper;

class SDSRootEditPart extends ScalableFreeformRootEditPart {
    
    private SDSZoomManager _zoomManager;
    
    public SDSRootEditPart() {
        _zoomManager = new SDSZoomManager((ScalableFigure) getScaledLayers(),
                                          ((Viewport) getFigure()));
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    public Object getAdapter(final Class key) {
        if (key == AutoexposeHelper.class) {
            return new ViewportAutoexposeHelper(this);
        }
        return super.getAdapter(key);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SDSZoomManager getZoomManager() {
        return _zoomManager;
    }
}