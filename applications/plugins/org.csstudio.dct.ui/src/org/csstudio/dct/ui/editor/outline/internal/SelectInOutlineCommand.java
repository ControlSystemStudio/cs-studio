package org.csstudio.dct.ui.editor.outline.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IElement;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.views.contentoutline.ContentOutline;

/**
 * Undoable command which selects certain elements in the content outline view.
 *
 * @author Sven Wende
 *
 */
public final class SelectInOutlineCommand extends Command {
    private List<IElement> oldSelection;
    private ContentOutline outlineView;
    private IElement[] elements;

    /**
     * Constructor.
     *
     * @param prototype
     *            the prototype
     */
    public SelectInOutlineCommand(ContentOutline outline, IElement... elements) {
        assert outline != null;
        this.outlineView = outline;
        this.elements = elements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        ISelection s = outlineView.getSelection();

        oldSelection = new ArrayList<IElement>();

        if(s!=null && s instanceof StructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) s;

            for(Object o : ss.toList()) {
                if(o instanceof IElement) {
                    oldSelection.add((IElement)o);
                }
            }
        }



        outlineView.setSelection(new StructuredSelection(elements));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        outlineView.setSelection(new StructuredSelection(oldSelection));
    }

}
