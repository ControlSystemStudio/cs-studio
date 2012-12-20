/*******************************************************************************
 * Copyright (c) 2006, 2007 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl - initial API and implementation
 *******************************************************************************/

package org.csstudio.alarm.beast.ui.alarmtree;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple TreeViewer to demonstrate usage of an ILazyContentProvider.
 */
@SuppressWarnings("nls")
public class Snippet047VirtualLazyTreeViewer {
	// Totally beside the point but was confusing when
	// trying to understand this snippet:
	// The model is a hack, not an obvious tree.
	// The tree usually doesn't show its 'root' element.
	// Here elements[] is the input to the tree viewer,
	// but because getParent() returns elements[] as parent of elements[],
	// those elements[] in the input serve as both the (not visible) root
	// of the tree and the first (visible) level.

	private class MyContentProvider implements ILazyTreeContentProvider {
		private TreeViewer viewer;
		private IntermediateNode[] elements;

		public MyContentProvider(TreeViewer viewer) {
			this.viewer = viewer;
		}

		@Override
        public void dispose() {

		}

		@Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.elements = (IntermediateNode[]) newInput;
		}

		@Override
        public Object getParent(Object element) {
			if (element instanceof LeafNode)
				return ((LeafNode) element).parent;
			return elements;
		}

		@Override
        public void updateElement(Object parent, int index) {
			Object element;
			if (parent instanceof IntermediateNode)
				element = ((IntermediateNode) parent).children[index];

			else
				element =  elements[index];
			viewer.replace(parent, index, element);
			updateChildCount(element, -1);
		}

		@Override
        public void updateChildCount(Object element, int currentChildCount) {

			int length = 0;
			if (element instanceof IntermediateNode) {
				IntermediateNode node = (IntermediateNode) element;
				length =  node.children.length;
			}
			if(element == elements)
				length = elements.length;
			viewer.setChildCount(element, length);
		}
	}

	public class LeafNode {
		public int counter;
		public IntermediateNode parent;

		public LeafNode(int counter, IntermediateNode parent) {
			this.counter = counter;
			this.parent = parent;
		}

        @Override
        public String toString() {
			return "Leaf " + this.counter;
		}
	}

	public class IntermediateNode {
		public int counter;
		public LeafNode[] children = new LeafNode[0];

		public IntermediateNode(int counter) {
			this.counter = counter;
		}

		@Override
        public String toString() {
			return "Node " + this.counter;
		}

		public void generateChildren(int i) {
			children = new LeafNode[i];
			for (int j = 0; j < i; j++) {
				children[j] = new LeafNode(j, this);
			}

		}
	}

	public Snippet047VirtualLazyTreeViewer(Shell shell) {
		// THIS IS THE KEY POINT:
		// With SWT.MULTI expanding tree items is slow as snot
		// (at least on Windows, Eclipse 3.6.2)
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=259141
		// The original snippet without SWT.MULTI is fine.
		final TreeViewer v = new TreeViewer(shell, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI);
		v.setLabelProvider(new LabelProvider());
		v.setContentProvider(new MyContentProvider(v));
		v.setUseHashlookup(true);
		IntermediateNode[] model = createModel();
		v.setInput(model);
		v.getTree().setItemCount(model.length);

	}

	private IntermediateNode[] createModel() {
		IntermediateNode[] elements = new IntermediateNode[10];

		for (int i = 0; i < 10; i++) {
			elements[i] = new IntermediateNode(i);
			elements[i].generateChildren(1000);
		}

		return elements;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		new Snippet047VirtualLazyTreeViewer(shell);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();

	}

}