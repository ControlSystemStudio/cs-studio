package org.csstudio.dct.ui.editor.outline.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IProject;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

/**
 * Base class for actions that are used in the outline´s popup menu.
 *
 * @author Sven Wende
 *
 */
public abstract class AbstractOutlineAction implements IViewActionDelegate {
    private List<IElement> selectedElements;
    private ContentOutline outlineView;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void init(IViewPart view) {
        outlineView = (ContentOutline) view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void run(IAction action) {
        Command command = createCommand(selectedElements);
        if (command != null) {
            execute(command);
        }
        doRun(selectedElements);
    }

    /**
     * Empty default implementation. Can be overridden by subclasses to
     * implement actions which are not undoable and should be independent of the
     * command stack.
     *
     * @param selection
     *            the selected elements
     */
    protected void doRun(List<IElement> selection) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public final void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;

            selectedElements = new ArrayList<IElement>();

            Iterator<Object> it = ssel.iterator();

            while (it.hasNext()) {
                Object s = it.next();

                if (s instanceof IElement) {
                    selectedElements.add((IElement) s);
                }
            }
        }

        afterSelectionChanged(selectedElements, action);
    }

    /**
     * Template method. Subclasses have to return a command that does the real
     * action.
     *
     * @param selection
     *            the currently selected element
     *
     * @return a command which does the real action
     */
    protected abstract Command createCommand(List<IElement> selection);

    /**
     * Hook for subclasses to manipulate the action state based on the current
     * selection.
     *
     * @param selection
     *            the current selection
     * @param action
     *            the action
     *
     */
    protected void afterSelectionChanged(List<IElement> selection, IAction action) {
    }

    /**
     * Returns the {@link IProject} that is currently displayed in the outline.
     *
     * @return the current project
     */
    protected final IProject getProject() {
        IProject result = null;
        IPage currentPage = outlineView.getCurrentPage();

        if (currentPage instanceof OutlinePage) {
            result = ((OutlinePage) currentPage).getInput();
        }

        return result;
    }

    /**
     * Executes the specified command.
     *
     * @param command
     *            a command
     */
    private void execute(Command command) {
        CommandStack commandStack = getCommandStack();
        if (commandStack != null) {
            commandStack.execute(command);
        } else {
            throw new IllegalArgumentException("Could not execute command. No command stack available");
        }
    }

    /**
     * Returns the command stack.
     *
     * @return the central command stack
     */
    private CommandStack getCommandStack() {
        CommandStack result = null;

        IPage currentPage = outlineView.getCurrentPage();

        if (currentPage instanceof OutlinePage) {
            result = ((OutlinePage) currentPage).getCommandStack();
        }

        return result;
    }

    public ContentOutline getOutlineView() {
        return outlineView;
    }
}
