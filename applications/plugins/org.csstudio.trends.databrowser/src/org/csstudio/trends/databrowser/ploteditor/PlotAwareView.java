package org.csstudio.trends.databrowser.ploteditor;

import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
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
    
    /** Create the GUI elements.
     *  <p>
     *  <b>Derived classes must invoke this method</b> to hook into the
     *  part listener which then tracks the current plot editor.
     */
    @Override
    public void createPartControl(Composite parent)
    {
        // Listen to the current 'part', react if it's a PlotEditor
        part_listener = new IPartListener2()
        {
            // Remember the editor when activated...
            public void partActivated(IWorkbenchPartReference ref)
            {
                if (ref.getId().equals(PlotEditor.ID))
                {
                    //System.out.println("Activate " + ref.getPartName());
                    updateEditor((PlotEditor) ref.getPart(false));
                }
            }
            // ... until another one gets activated, or the current one closes.
            public void partClosed(IWorkbenchPartReference ref)
            {
                if (ref.getPart(false) == editor)
                {
                    //System.out.println("Closed " + ref.getPartName());
                    updateEditor(null);
                }
            }
            // All ignored
            public void partDeactivated(IWorkbenchPartReference ref) {}
            public void partBroughtToTop(IWorkbenchPartReference ref) {}
            public void partHidden(IWorkbenchPartReference ref) {}
            public void partInputChanged(IWorkbenchPartReference ref) {}
            public void partOpened(IWorkbenchPartReference ref) {}
            public void partVisible(IWorkbenchPartReference ref) {}
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
    }

    /** Remove this view from the list of part listeners.
     *  <p>
     *  <b>Derived classes must invoke this method</b> on disposal. 
     *  @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose()
    {
        getSite().getPage().removePartListener(part_listener);
        super.dispose();
    }

    /** The editor has changed. */
    private void updateEditor(PlotEditor new_editor)
    {
        Model old_model = (editor == null) ? null : editor.getModel();
        editor = new_editor;
        Model new_model = (editor == null) ? null : editor.getModel();
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
