package de.desy.language.editor.ui.outline;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.desy.language.editor.core.parser.Node;
import de.desy.language.editor.core.parser.RootNode;
import de.desy.language.editor.ui.editor.HighlightingListener;

public class LanguageOutlinePage extends ContentOutlinePage implements
        IContentOutlinePage, CaretListener {

    private Node _rootNode;
    private Node _programNode;
    private final HighlightingListener _highlightingListener;
    private boolean propagateSelectionChanged = true;

    public LanguageOutlinePage(final HighlightingListener highlightingListener) {
        this._highlightingListener = highlightingListener;
    }

    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
        super.selectionChanged(event);

        if (event.getSelection() instanceof IStructuredSelection) {
            final IStructuredSelection structuredSelection = (IStructuredSelection) event
                    .getSelection();

            if (structuredSelection.getFirstElement() instanceof Node) {
                final Node node = (Node) structuredSelection.getFirstElement();

                if (propagateSelectionChanged && node.hasOffsets()) {
                    this._highlightingListener.highlightRegion(node
                            .getStatementStartOffset(), node
                            .getStatementEndOffset(), false);
                }
            }
        }
    }

    public void setEditorInput(final Node rootNode) {
        this._rootNode = new RootNode();
        this._rootNode.addChild(rootNode);
        _programNode = rootNode;
        this.refreshViewer();
    }

    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);
        final TreeViewer viewer = this.getTreeViewer();

        viewer.setContentProvider(new WorkbenchContentProvider());
        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.addSelectionChangedListener(this);
        this.refreshViewer();
    }

    private void refreshViewer() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            @SuppressWarnings("synthetic-access")
            public synchronized void run() {
                if (getTreeViewer().getTree()
                        .isDisposed()) {
                    return;
                }
                if (_rootNode != null) {
                    getTreeViewer().setInput(_rootNode);
                    getTreeViewer().expandToLevel(2);
                }
            }
        });
    }

    @Override
    public void caretMoved(CaretEvent event) {
        try {
            propagateSelectionChanged = false;
            List<Node> nodes = findSurroundingNode(_programNode,
                    event.caretOffset);

            if (nodes != null) {
                Collections.reverse(nodes);
                ISelection selection = new StructuredSelection(nodes.toArray());
                getTreeViewer().setSelection(selection, true);
            }
            propagateSelectionChanged = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Node> findSurroundingNode(Node rootNode, int caretPosition) {
        if (rootNode.hasChildren()) {
            for (Node node : rootNode.getChildrenNodes()) {
                if (node.hasOffsets()
                        && node.getStatementStartOffset() <= caretPosition
                        && caretPosition <= node.getStatementEndOffset()) {
                    List<Node> result = findSurroundingNode(node, caretPosition);
                    if (result == null) {
                        result = new LinkedList<Node>();
                    }
                    result.add(node);
                    return result;
                } else if (node.hasChildren()) {
                    List<Node> result = findSurroundingNode(node, caretPosition);
                    if (result != null) {
                        result.add(node);
                        return result;
                    }
                }
            }
        }
        return null;
    }

}
