package org.csstudio.sds.ui.internal.editor.outline;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class ThumbnailViewOutlinePage extends ContentOutlinePage implements IContentOutlinePage {
    
    private Canvas _overview;
    
    public ThumbnailViewOutlinePage(EditPartViewer viewer) {
        super(viewer);
    }
    
    @Override
    public void createControl(Composite parent) {
        _overview = new Canvas(parent, SWT.NONE);
        LightweightSystem lws = new LightweightSystem(_overview);
        RootEditPart rep = getViewer().getRootEditPart();
        if (rep instanceof ScalableFreeformRootEditPart) {
            Thumbnail thumbnail = createThumbnail((ScalableFreeformRootEditPart) rep);
            lws.setContents(thumbnail);
            
            DisposeListener disposeListener = createDisposeListenerToViewerControl(thumbnail);
            getViewer().getControl().addDisposeListener(disposeListener);
        }
    }
    
    private Thumbnail createThumbnail(ScalableFreeformRootEditPart root) {
        Thumbnail thumbnail = new ScrollableThumbnail((Viewport) root.getFigure());
        thumbnail.setBorder(new MarginBorder(3));
        thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
        
        return thumbnail;
    }
    
    private DisposeListener createDisposeListenerToViewerControl(final Thumbnail _thumbnail) {
        return new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                if (_thumbnail != null) {
                    _thumbnail.deactivate();
                }
            }
        };
    }
    
    @Override
    public Control getControl() {
        return _overview;
    }
}