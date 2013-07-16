/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.editor;

import org.csstudio.trends.sscan.model.Model;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

/** Helper for Eclipse ViewPart that is aware of
 *  the current Sscan editor and its Model.
 *  <p>
 *  A view that displays or modifies the model behind a plot editor
 *  needs to know which plot editor is currently active, including the
 *  case that there might be none some times.
 *  It does this by implementing a IPartListener2 and reacting
 *  whenever a PlotEditor is the current part.
 *
 *  @author Kay Kasemir
 */
abstract public class SscanAwareView extends ViewPart
{
	public SscanAwareView() {
	}
    /** Used to learn about the current 'editor'. */
    private IPartListener2 part_listener;

    /** The current editor. */
    private SscanEditor editor = null;

    /** Derived classes use doCreatePartControl,
     *  called by this method, which then hooks into the
     *  part listener which then tracks the current plot editor.
     */
    @Override
    final public void createPartControl(final Composite parent)
    {
        doCreatePartControl(parent);
        // Listen to the current 'part', react if it's a PlotEditor
        part_listener = new IPartListener2()
        {
            // Remember the editor when activated...
            @Override
            public void partActivated(IWorkbenchPartReference ref)
            {
                if (ref.getId().equals(SscanEditor.ID))
                {
                    final IWorkbenchPart part = ref.getPart(false);
                    if (part instanceof SscanEditor)
                        updateEditor((SscanEditor) part);
                    // else: Some other editor of no concern
                }
            }
            // ... until another one gets activated, or the current one closes.
            @Override
            public void partClosed(IWorkbenchPartReference ref)
            {
                if (ref.getPart(false) == editor)
                {
                    updateEditor(null);
                }
            }
            // All ignored
            @Override
            public void partDeactivated(IWorkbenchPartReference ref) { /* NOP */ }
            @Override
            public void partBroughtToTop(IWorkbenchPartReference ref) { /* NOP */ }
            @Override
            public void partHidden(IWorkbenchPartReference ref) { /* NOP */ }
            @Override
            public void partInputChanged(IWorkbenchPartReference ref) { /* NOP */ }
            @Override
            public void partOpened(IWorkbenchPartReference ref) { /* NOP */ }
            @Override
            public void partVisible(IWorkbenchPartReference ref) { /* NOP */ }
        };
        getSite().getPage().addPartListener(part_listener);

        // If the active editor is already a plot editor,
        // we will never get the 'activate' signal because
        // it already happened.
        // So check once, then use the PartListener
        final IEditorPart current = getSite().getPage().getActiveEditor();
        if (current instanceof SscanEditor)
            updateEditor((SscanEditor) current);
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
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                getSite().getPage().removePartListener(part_listener);
                updateEditor(null);
            }
        });
    }

    /** Replaces createPartControl() for PlotAwareView */
    abstract protected void doCreatePartControl(Composite parent);

    /** The editor has changed. */
    private void updateEditor(SscanEditor new_editor)
    {
        final Model old_model = (editor == null) ? null : editor.getModel();
        editor = new_editor;
        final Model new_model = (editor == null) ? null : editor.getModel();
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
