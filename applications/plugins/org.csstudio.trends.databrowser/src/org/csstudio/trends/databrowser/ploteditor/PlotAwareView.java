package org.csstudio.trends.databrowser.ploteditor;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

/** Helper for Eclipse ViewPart that is aware of the current plot editor.
 *  <p>
 *  A view that displays or modifies the model behind a plot editor
 *  needs to know which plot editor is currently active, including the
 *  case that there might be none some times.
 *  It does this by implementing a IPartListener2 and reacting
 *  whenever a PlotEditor is the current part.
 *
 *  @author Kay Kasemir
 */
public abstract class PlotAwareView extends ViewPart
{
    /** Compile time debug flag */
    final protected static boolean debug = false;
    
    /** Used to learn about the current 'editor'. */
    private IPartListener2 part_listener;    
    
    /** The current editor. */
    private PlotEditor editor = null;
    
    /** Through this call, derived or other classes can always obtain
     *  the 'current' editor, which might be <code>null</code>.
     *  @see #getModel()
     */
    public PlotEditor getPlotEditor()
    {
        return editor;
    }
    
    /** Through this call, derived or other classes can always obtain
     *  the 'current' model, which might be <code>null</code>.
     *  @return Returns the model behind the current editor or <code>null</code>. 
     *  @see #updateModel()
     */
    public Model getModel()
    {
        if (editor == null)
            return null;
        return editor.getModel();
    }
    
    /** Derived classes use doCreatePartControl,
     *  called by this method, which then hooks into the
     *  part listener which then tracks the current plot editor.
     */
    @Override
    @SuppressWarnings("nls")
    final public void createPartControl(final Composite parent)
    {
        doCreatePartControl(parent);
        
        // Listen to the current 'part', react if it's a PlotEditor
        part_listener = new IPartListener2()
        {
            // Remember the editor when activated...
            @SuppressWarnings("nls")
            public void partActivated(IWorkbenchPartReference ref)
            {
                if (ref.getId().equals(PlotEditor.ID))
                {
                    if (debug)
                        System.out.println("PlotAwareView: Activate " + ref.getPartName());
                    final IWorkbenchPart part = ref.getPart(false);
                    if (part instanceof PlotEditor)
                        updateEditor((PlotEditor) part);
                    else
                        Plugin.logError("PlotAwareView: expected PlotEditor, " +
                                        "got " + part.getClass().getName());
                }
            }
            // ... until another one gets activated, or the current one closes.
            public void partClosed(IWorkbenchPartReference ref)
            {
                if (ref.getPart(false) == editor)
                {
                    if (debug)
                        System.out.println("PlotAwareView: Closed " + ref.getPartName());
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
        IEditorPart current = getSite().getPage().getActiveEditor();
        if (current instanceof PlotEditor)
            updateEditor((PlotEditor) current);
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
                if (debug)
                    System.out.println("PlotAwareView: disposed " + getPartName());
                getSite().getPage().removePartListener(part_listener);
                updateEditor(null);
            }
        });
    }

    /** Replaces createPartControl() for PlotAwareView */
    abstract protected void doCreatePartControl(Composite parent);

    /** The editor has changed. */
    @SuppressWarnings("nls")
    private void updateEditor(PlotEditor new_editor)
    {
        Model old_model = (editor == null) ? null : editor.getModel();
        editor = new_editor;
        Model new_model = (editor == null) ? null : editor.getModel();
        if (debug)
            System.out.println("PlotAwareView: switching " + old_model + " to " + new_model);
        updateModel(old_model, new_model);
    }
    
    /** A new editor has been selected, and this means we now have a new
     *  model.
     *  <p>
     *  Derived classes implement this routine to add/remove model listeners.
     *  @param old_model The previous model (might be <code>null</code>).
     *  @param model The current model (might be <code>null</code>).
     */
    protected abstract void updateModel(Model old_model, Model model);
}
