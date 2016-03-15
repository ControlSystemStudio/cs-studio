package org.csstudio.dct.ui.editor.outline.internal;

import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.ui.graphicalviewer.GraphicalRepresentationUtil;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;

/**
 * Popup menu action for the outline view that opens a graphical representation
 * of a prototype.
 *
 * @author Sven Wende
 *
 */
public final class OpenGraphicsAction extends AbstractOutlineAction {

    @Override
    public final void doRun(List<IElement> selection) {
        assert selection != null;
        assert selection.size() == 1;
        //
        // String secondaryId = "" + System.currentTimeMillis();
        // final IWorkbenchPage page = PlatformUI.getWorkbench()
        // .getActiveWorkbenchWindow().getActivePage();
        // try {
        // GraphicalDctView view = (GraphicalDctView) page.showView(
        // GraphicalDctView.PRIMARY_ID, secondaryId,
        // IWorkbenchPage.VIEW_ACTIVATE);
        //
        // if(selection.get(0) instanceof IPrototype) {
        // view.setPrototype(getProject(), (IPrototype) selection.get(0));
        // } else if (selection.get(0) instanceof IInstance) {
        // view.setInstance(getProject(), (IInstance) selection.get(0));
        // }
        // } catch (final PartInitException e) {
        // e.printStackTrace();
        // }

        GraphicalViewer viewer = GraphicalRepresentationUtil.openShell(50, 50, 809, 800, "DCT Graphical Representation");

        if (selection.get(0) instanceof IPrototype) {
            viewer.setContents(GraphicalRepresentationUtil.createGraphicalModel(getProject(), (IPrototype) selection.get(0)));
        } else if (selection.get(0) instanceof IInstance) {
            viewer.setContents(GraphicalRepresentationUtil.createGraphicalModel(getProject(), (IInstance) selection.get(0)));
        }
    }

    @Override
    protected Command createCommand(List<IElement> selection) {
        return null;
    }

}
