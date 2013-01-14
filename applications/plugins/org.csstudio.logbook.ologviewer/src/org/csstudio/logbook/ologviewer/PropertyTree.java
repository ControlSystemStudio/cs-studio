package org.csstudio.logbook.ologviewer;

import static edu.msu.nscl.olog.api.PropertyBuilder.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import edu.msu.nscl.olog.api.Property;

public class PropertyTree extends Composite implements ISelectionProvider {

	private Collection<Property> properties;
	private ISelectionProvider selectionProvider;

	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	TreeViewer treeViewer;
	Tree tree;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public Collection<Property> getProperties() {
		return properties;
	}

	public void setProperties(Collection<Property> properties) {
		if (getProperties() != null && getProperties().equals(properties))
			return;
		if (getProperties() == null && properties == null)
			return;

		Collection<Property> oldValue = this.properties;
		this.properties = properties;
		changeSupport.firePropertyChange("properties", oldValue, properties);
	}

	public PropertyTree(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		Composite composite = new Composite(this, SWT.NONE);
		TreeColumnLayout tcl_composite = new TreeColumnLayout();
		composite.setLayout(tcl_composite);
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(0);
		fd_composite.bottom = new FormAttachment(100);
		fd_composite.right = new FormAttachment(100);
		composite.setLayoutData(fd_composite);

		treeViewer = new TreeViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tree = treeViewer.getTree();
		tree.setLinesVisible(true);

		TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(
				treeViewer, new FocusCellOwnerDrawHighlighter(treeViewer));

		TreeViewerColumn treeViewerKeyColumn = new TreeViewerColumn(treeViewer,
				SWT.NONE);
		TreeColumn keyColumn = treeViewerKeyColumn.getColumn();
		tcl_composite.setColumnData(keyColumn, new ColumnWeightData(100, 150,
				true));
		keyColumn.setText("Key");
		treeViewerKeyColumn.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				if (element instanceof Property) {
					return ((Property) element).getName();
				} else if (element instanceof Entry) {
					return ((Entry) element).getKey().toString();
				} else {
					return "";
				}
			}

		});

		TreeViewerColumn treeViewerValueColumn = new TreeViewerColumn(
				treeViewer, SWT.NONE);
		TreeColumn valueColumn = treeViewerValueColumn.getColumn();
		tcl_composite.setColumnData(valueColumn, new ColumnWeightData(100, 150,
				true));
		valueColumn.setText("Value");
		treeViewerValueColumn.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				if (element instanceof Entry) {
					return ((Entry) element).getValue().toString();
				} else {
					return "";
				}
			}

		});

		treeViewer.setContentProvider(new PropertyTreeContentProvider());

		selectionProvider = treeViewer;
		selectionProvider
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						ISelection selection = event.getSelection();
						ISelectionProvider provider = event
								.getSelectionProvider();
						Object source = event.getSource();
						System.out.println(event);
					}
				});
		addPropertyChangeListener(new PropertyChangeListener() {

			List<String> properties = Arrays.asList("properties");

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (properties.contains(evt.getPropertyName())) {
					updateTree();
				}
			}
		});
	}

	private void updateTree() {
		treeViewer.setInput(this.properties);
	}

	private class PropertyTreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}

		@SuppressWarnings("unchecked")
		@Override
		public Object[] getElements(Object inputElement) {
			return ((Collection<Property>) inputElement).toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Property) {
				return ((Property) parentElement).getEntrySet().toArray();
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof Property) {
				return ((Property) element).getEntrySet().size() > 0;
			}
			return false;
		}

	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		PropertyTree propertyTree = new PropertyTree(shell, SWT.None);
		shell.open();
		Collection<Property> properties = new ArrayList<Property>();
		properties.add(property("prop").attribute("trac",
				"http://www.google.com").build());
		properties.add(property("prop2").attribute("trac2", "www.google.com")
				.attribute("xxx", "dsklfjsldk").build());
		propertyTree.setProperties(properties);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionProvider.addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection() {
		return selectionProvider.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionProvider.removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		selectionProvider.setSelection(selection);
	}
}
