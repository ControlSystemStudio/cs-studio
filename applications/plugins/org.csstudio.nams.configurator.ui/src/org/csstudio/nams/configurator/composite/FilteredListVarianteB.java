package org.csstudio.nams.configurator.composite;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FilteredListVarianteB {

	public FilteredListVarianteB(Composite parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(Composite parent, int style) {
		Composite main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		TreeViewer tree = new TreeViewer(main);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(
				tree.getControl());
		tree.setContentProvider(new TreeContentProvider());
		tree.setLabelProvider(new TreeLabelProvider());
		tree.setInput(this.getTreeInput());

		{
			Composite compDown = new Composite(main, SWT.None);
			compDown.setLayout(new GridLayout(2, false));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(compDown);

			{
				new Label(compDown, SWT.READ_ONLY).setText("Suche");

				Text filter = new Text(compDown, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false)
						.applyTo(filter);
			}
		}

	}

	private class TreeContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			Object[] returnValue = ((TreeNode) parentElement).getChildren();
			return returnValue;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return ((TreeNode) element).getChildren().length > 0;
		}

		public Object[] getElements(Object inputElement) {
			return ((TreeNode) inputElement).getChildren();
		}

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}
	}

	private class TreeLabelProvider extends LabelProvider implements
			IBaseLabelProvider {
		@Override
		public String getText(Object element) {
			TreeNode node = (TreeNode) element;
			return node.getValue().toString();
		}
	}

	private TreeNode getTreeInput() {
		TreeNode root = new TreeNode("Alarmconfigurator");

		TreeNode gruppeAMS = new TreeNode("AMS");
		gruppeAMS.setChildren(new TreeNode[] { new TreeNode("Hans Otto"),
				new TreeNode("Max Mayer") });

		TreeNode gruppeC1WPS = new TreeNode("C1-WPS");
		gruppeC1WPS.setChildren(new TreeNode[] {
				new TreeNode("Susi Sonnenschein"), new TreeNode("Kai Uwe"),
				new TreeNode("Thomas B") });

		root.setChildren(new TreeNode[] { gruppeAMS, gruppeC1WPS });

		return root;
	}

}
