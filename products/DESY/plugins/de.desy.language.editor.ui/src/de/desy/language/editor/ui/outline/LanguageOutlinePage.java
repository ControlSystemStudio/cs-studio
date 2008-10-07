package de.desy.language.editor.ui.outline;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
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
		IContentOutlinePage {

	private Node _rootNode;
	private final HighlightingListener _highlightingListener;

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

				if (node.hasOffsets()) {
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
			@SuppressWarnings("synthetic-access")
			public synchronized void run() {
				if (LanguageOutlinePage.this.getTreeViewer().getTree()
						.isDisposed()) {
					return;
				}
				if (LanguageOutlinePage.this._rootNode != null) {
					LanguageOutlinePage.this.getTreeViewer().setInput(
							LanguageOutlinePage.this._rootNode);
					LanguageOutlinePage.this.getTreeViewer().expandToLevel(2);
					System.out.println("LanguageOutlinePage.refreshViewer(): async-run(): update!");
				}
			}
		});
	}

}
