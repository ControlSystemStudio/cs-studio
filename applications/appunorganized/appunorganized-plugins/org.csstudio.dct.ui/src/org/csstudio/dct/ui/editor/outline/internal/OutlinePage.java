package org.csstudio.dct.ui.editor.outline.internal;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * An outline page implementation that displays a {@link IProject} in the
 * outline view using a tree.
 *
 * @author Sven Wende
 *
 */
public final class OutlinePage extends ContentOutlinePage implements CommandStackListener {
    private IProject input;
    private CommandStack commandStack;
    private TreeViewer viewer;
    final Map<Class, AbstractDnDHandler> dndHandlers;

    /**
     * Constructor.
     *
     * @param input
     *            the project to display
     * @param commandStack
     *            a command stack
     */
    public OutlinePage(final IProject input, final CommandStack commandStack) {
        this.input = input;
        this.commandStack = commandStack;
        dndHandlers = new HashMap<Class, AbstractDnDHandler>();
        dndHandlers.put(IPrototype.class, new PrototypeDndHandler());
        dndHandlers.put(IRecord.class, new RecordDndHandler());
        dndHandlers.put(IInstance.class, new InstanceDndHandler());
    }

    /**
     * Sets the input for the outline.
     *
     * @param input
     *            the project to display
     */
    public void setInput(final IProject input) {
        this.input = input;

        if (getTreeViewer() != null) {
            getTreeViewer().setInput(input);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);
        viewer = getTreeViewer();
        viewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
        viewer.setUseHashlookup(true);

        viewer.setContentProvider(new WorkbenchContentProvider());
        viewer.setAutoExpandLevel(2);

        viewer.setInput(new WorkbenchAdapter() {
            @Override
            public Object[] getChildren(final Object o) {
                return new Object[] { input };
            }
        });

        final MenuManager menuManager = new MenuManager();
        menuManager.add(new Separator("add.ext"));
        menuManager.add(new Separator("remove.ext"));
        menuManager.add(new Action("Refresh") {

            @Override
            public void run() {
                viewer.refresh();
            }

        });

        initDragAndDrop(viewer);

        viewer.getControl().setMenu(menuManager.createContextMenu(viewer.getControl()));
        getSite().registerContextMenu("css.dct.outline", menuManager, viewer);
    }

    private IElement dndSource;

    private void initDragAndDrop(final TreeViewer viewer) {

        viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] { TextTransfer.getInstance() }, new DragSourceListener() {
            @Override
            public void dragFinished(final DragSourceEvent event) {

            }

            @Override
            public void dragSetData(final DragSourceEvent event) {
                event.doit = true;
                event.data = "do_not_delete_because_its_empty_but_important";

            }

            @Override
            public void dragStart(final DragSourceEvent event) {
                // .. save current selection in local var
                final IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();

                if (sel != null && sel.toList().size()==1 && sel.getFirstElement() instanceof IElement) {
                    dndSource = (IElement) sel.getFirstElement();
                    event.doit = getDndHandler(dndSource) != null;
                } else {
                    dndSource = null;
                    event.doit = false;
                }

            }

        });

        viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] { TextTransfer.getInstance() }, new DropTargetListener() {
            @Override
            private void updateFeedback(final DropTargetEvent event) {
                final AbstractDnDHandler handler = getDndHandler(dndSource);

                // .. determine drop target element
                final TreeItem item = (TreeItem) event.item;

                if (item != null && item.getData() instanceof IElement && handler != null) {
                    final IElement dndTarget = (IElement) item.getData();
                    handler.updateDragFeedback(dndSource, dndTarget, event);
                } else {
                    event.detail = DND.DROP_NONE;
                    event.feedback = DND.FEEDBACK_NONE;
                }
            }

            @Override
            public void dragEnter(final DropTargetEvent event) {
                updateFeedback(event);
            }

            @Override
            public void dragLeave(final DropTargetEvent event) {
                updateFeedback(event);
            }

            @Override
            public void dragOperationChanged(final DropTargetEvent event) {
                updateFeedback(event);
            }

            @Override
            public void dragOver(final DropTargetEvent event) {
                updateFeedback(event);
            }

            @Override
            public void drop(final DropTargetEvent event) {
                final TreeItem item = (TreeItem) event.item;

                if (item != null && item.getData() instanceof IElement) {
                    final IElement dndTarget = (IElement) item.getData();

                    final AbstractDnDHandler handler = getDndHandler(dndSource);

                    if (handler != null) {
                        Command cmd = null;
                        if (event.detail == DND.DROP_MOVE) {
                            cmd = handler.createMoveCommand(dndSource, dndTarget);
                        } else if (event.detail == DND.DROP_COPY) {
                            cmd = handler.createCopyCommand(dndSource, dndTarget);
                        }

                        if (cmd != null) {
                            getCommandStack().execute(cmd);
                        }
                    }

                }
            }

            @Override
            public void dropAccept(final DropTargetEvent event) {
            }

        });

    }

    /**
     * Sets the command stack.
     *
     * @param commandStack
     *            the command stack
     */
    public void setCommandStack(final CommandStack commandStack) {
        this.commandStack = commandStack;
        this.commandStack.addCommandStackListener(this);

    }

    /**
     * Returns the command stack.
     *
     * @return the command stack
     */
    public CommandStack getCommandStack() {
        return commandStack;
    }

    /**
     * Returns the current input.
     *
     * @return the current project
     */
    public IProject getInput() {
        return input;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commandStackChanged(final EventObject event) {
        if (getTreeViewer() != null) {
            getTreeViewer().refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelection(final ISelection selection) {
        if (getTreeViewer() != null) {
            getTreeViewer().refresh();
            getTreeViewer().setSelection(selection, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
        super.selectionChanged(event);

        final IStructuredSelection sel = (IStructuredSelection) event.getSelection();

        if (sel != null && sel.getFirstElement() != null) {
            //FIXME: Mach Probleme beim DnD  - vielleicht lässt sich auf das Ausklappen verzichten!?
//            viewer.setExpandedState(sel.getFirstElement(), true);
//            viewer.refresh(sel.getFirstElement(), false);
        }
    }

    /**
     * Returns the tree viewer which is used to display the outline contents.
     *
     * @return the tree viewer
     */
    public TreeViewer getViewer() {
        return viewer;
    }

    private AbstractDnDHandler getDndHandler(final IElement source) {
        for (final Class type : dndHandlers.keySet()) {
            if (type.isAssignableFrom(source.getClass())) {
                final AbstractDnDHandler handler = dndHandlers.get(type);
                if (handler.supports(source)) {
                    return handler;
                }
            }
        }
        return null;
    }
}
