package org.csstudio.multichannelviewer;

import org.csstudio.multichannelviewer.model.CSSChannelGroup;
import org.csstudio.multichannelviewer.model.CSSChannelGroupPV;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.data.VMultiDouble;

public abstract class MultiChannelPlotAwareView extends ViewPart {

    private IPartListener2 part_listener;    
    private MultiChannelPlot editor = null;
    
	@Override
	public void createPartControl(Composite parent) {
		 doCreatePartControl(parent);
	        // Listen to the current 'part', react if it's a PlotEditor
	        part_listener = new IPartListener2()
	        {
	            // Remember the editor when activated...
	            public void partActivated(IWorkbenchPartReference ref)
	            {
	                if (ref.getId().equals(MultiChannelPlot.EDITOR_ID))
	                {
	                    final IWorkbenchPart part = ref.getPart(false);
	                    if (part instanceof MultiChannelPlot)
	                        updateEditor((MultiChannelPlot) part);
	                    // else: Some other editor of no concern
	                }
	            }
	            // ... until another one gets activated, or the current one closes.
	            public void partClosed(IWorkbenchPartReference ref)
	            {
	                if (ref.getPart(false) == editor)
	                {
	                    updateEditor(null);
	                }
	            }
	            // All ignored
	            public void partDeactivated(IWorkbenchPartReference ref) { /* NOP */ }
	            public void partBroughtToTop(IWorkbenchPartReference ref) { /* NOP */ }
	            public void partHidden(IWorkbenchPartReference ref) { /* NOP */ }
	            public void partInputChanged(IWorkbenchPartReference ref) { /* NOP */ }
	            public void partOpened(IWorkbenchPartReference ref) { /* NOP */ }
	            public void partVisible(IWorkbenchPartReference ref) { /* NOP */ }
	        };
	        getSite().getPage().addPartListener(part_listener);

	        // If the active editor is already a plot editor,
	        // we will never get the 'activate' signal because
	        // it already happened.
	        // So check once, then use the PartListener
	        final IEditorPart current = getSite().getPage().getActiveEditor();
	        if (current instanceof MultiChannelPlot)
	            updateEditor((MultiChannelPlot) current);
	        else
	            updateEditor(null);
	        
	        // When view is destroyed, we fake a null editor update.
	        // Since derived views already handle <code>updateModel</code>
	        // with a new <code>null</code> model, this will cause
	        // them to unsubscribe etc.
	        // Of course they can still add their own dispose listener.
	        //
	        // Note:
	        // Eclipse performs its own view lifecycle optimization.
	        // A view that's "closed" might actually just get hidden,
	        // and thus this dispose listener only takes effect when
	        // the application closes and really disposes the view.
	        //
	        // Unclear why the "Waveform" view gets disposed on close,
	        // while the "Config" view is kept around.
	        parent.addDisposeListener(new DisposeListener()
	        {
	            public void widgetDisposed(DisposeEvent e)
	            {
	                getSite().getPage().removePartListener(part_listener);
	                updateEditor(null);
	            }
	        });
	    }

	    /** Replaces createPartControl() for PlotAwareView */
	    abstract protected void doCreatePartControl(Composite parent);

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	/** The editor has changed. */
    private void updateEditor(MultiChannelPlot new_editor)
    {
        final CSSChannelGroup old_group = (editor == null) ? null : editor.getCSSChannelGroup();
//        final PV<VMultiDouble> old_pv = (editor == null) ? null : editor.getChannelGroupPV();
        editor = new_editor;
        final CSSChannelGroup new_group = (editor == null) ? null : editor.getCSSChannelGroup();
//        final PV<VMultiDouble> new_pv = (editor == null) ? null : editor.getChannelGroupPV();
        updateChannelGroup(old_group, new_group);
//        updatePV(old_pv, new_pv);
    }
    
    protected abstract void updateChannelGroup(CSSChannelGroup old_group, CSSChannelGroup new_group);
    
    protected abstract void updatePV(PVReader<VMultiDouble> old_pv,PVReader<VMultiDouble> new_pv);

}
