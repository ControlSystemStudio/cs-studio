package org.csstudio.sds.ui.internal.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;

public class SDSZoomManager extends ZoomManager {
    
    private List<ZoomListener> zoomFinishedListeners = new LinkedList<ZoomListener>();
    
    public SDSZoomManager(ScalableFigure pane, Viewport viewport) {
        super(pane, viewport);
    }
    
    /**
     * The original ZoomManager notifies its ZoomListeners (@see ZoomManager#addZoomListener(ZoomListener))
     * during the zoom process. This is not always suitable. The listeners added by this method are
     * notified when the zooming is completely finished. 
     *  
     * @param ZoomListener listener
     */
    public void addZoomFinishedListener(ZoomListener listener) {
        zoomFinishedListeners.add(listener);
    }
    
    /**
     * @see #addZoomFinishedListener(ZoomListener)
     * 
     * @param ZoomListener listener
     */
    public void removeZoomFinishedListener(ZoomListener listener) {
        zoomFinishedListeners.remove(listener);
    }
    
    @Override
    protected void primSetZoom(double zoom) {
        super.primSetZoom(zoom);
        fireZoomFinished(zoom);
    }
    
    private void fireZoomFinished(double zoom) {
        for (ZoomListener listener : zoomFinishedListeners) {
            listener.zoomChanged(zoom);
        }
    }
}
