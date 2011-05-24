/**
 * 
 */
package org.csstudio.utility.channel.actions;

import gov.bnl.channelfinder.api.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author shroffk
 * 
 */
public class InfoAction implements IObjectActionDelegate {

	private Shell shell;
	private Collection<Channel> channels = new HashSet<Channel>();
	/**
	 * 
	 */
	public InfoAction() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 * 
	 * Display a dialog box with details about the channel.
	 */
	@Override
	public void run(IAction action) {
		try {
			DisplayTreeDialog displayTreeDialog = new DisplayTreeDialog(shell,
					new ChannelTreeLabelProvider(),
					new ChannelTreeContentProvider());
			//displayTreeDialog.setInput(createTestChannelModel());
			displayTreeDialog.setInput(createChannelModel(channels));
			displayTreeDialog.setBlockOnOpen(true);
			displayTreeDialog.setMessage(Messages.treeDialogMessage);
			displayTreeDialog.setTitle(Messages.treeDialogTitle);
			displayTreeDialog.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	private Object createTestChannelModel() {
//		ChannelModel root = new ChannelModel(0,null);
//		XmlChannel channel = new XmlChannel("name1", "owner1"); //$NON-NLS-1$ //$NON-NLS-2$
//		channel.addProperty(new XmlProperty("prop1a", "owner1", "VAL1a")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//		channel.addProperty(new XmlProperty("prop1b", "owner1", "VAL1b")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//		channel.addTag(new XmlTag("tag1", "owner1")); //$NON-NLS-1$ //$NON-NLS-2$
//		
//		XmlChannel channel2 = new XmlChannel("name2", "owner2"); //$NON-NLS-1$ //$NON-NLS-2$
//		channel2.addProperty(new XmlProperty("prop2", "owner2", "VAL2")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//		channel2.addTag(new XmlTag("tag2", "owner2")); //$NON-NLS-1$ //$NON-NLS-2$
//		
//		root.getChild().add(channel);
//		root.getChild().add(channel2);
//		
//		return root;
//	}

	
	private Object createChannelModel(Collection<Channel> channels){
		ChannelTreeModel root = new ChannelTreeModel(0,null);		
		for (Channel channel : channels) {
			root.getChild().add(channel);
		}
		return root;		
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			channels.clear();
			for (Iterator<Channel> iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				channels.add(iterator.next());
			}
		}
	}

	private class TreeDialog extends TitleAreaDialog {

		public TreeDialog(Shell shell) {
			super(shell);
		}

		// TODO Auto-generated constructor stub
		@Override
		protected Control createDialogArea(Composite parent) {
			Composite comp = (Composite) super.createDialogArea(parent);
			TreeViewer v = new TreeViewer(parent, SWT.NONE);
			v.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
			v.setLabelProvider(new LabelProvider());
			v.setContentProvider(new MyContentProvider());
			v.setInput(createModel());

			return comp;
		}
	}

	private MyModel createModel() {

		MyModel root = new MyModel(0, null);
		root.counter = 0;

		MyModel tmp;
		for (int i = 1; i < 10; i++) {
			tmp = new MyModel(i, root);
			root.child.add(tmp);
			for (int j = 1; j < i; j++) {
				tmp.child.add(new MyModel(j, tmp));
			}
		}

		return root;
	}

	private class MyContentProvider implements ITreeContentProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return ((MyModel) inputElement).child.toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang
		 * .Object)
		 */
		public Object[] getChildren(Object parentElement) {
			return getElements(parentElement);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang
		 * .Object)
		 */
		public Object getParent(Object element) {
			if (element == null) {
				return null;
			}
			return ((MyModel) element).parent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang
		 * .Object)
		 */
		public boolean hasChildren(Object element) {
			return ((MyModel) element).child.size() > 0;
		}

	}

	public class MyModel {
		public MyModel parent;
		public ArrayList child = new ArrayList();
		public int counter;

		public MyModel(int counter, MyModel parent) {
			this.parent = parent;
			this.counter = counter;
		}

		public String toString() {
			String rv = "Item "; //$NON-NLS-1$
			if (parent != null) {
				rv = parent.toString() + "."; //$NON-NLS-1$
			}

			rv += counter;

			return rv;
		}
	}
}
