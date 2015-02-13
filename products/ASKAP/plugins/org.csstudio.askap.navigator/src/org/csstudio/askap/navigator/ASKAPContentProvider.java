package org.csstudio.askap.navigator;

import org.csstudio.askap.navigator.model.ASKAP;
import org.csstudio.askap.navigator.model.Branch;
import org.csstudio.askap.navigator.model.Node;
import org.csstudio.askap.navigator.model.OPI;
import org.csstudio.askap.navigator.model.View;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

public class ASKAPContentProvider extends LabelProvider implements ITreeContentProvider {
	
	String viewName;
	ASKAP askap;
	
	View currentView;
	
	public ASKAPContentProvider(ASKAP askap) {
		this.askap = askap;
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// content is static, so this is not supported
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (askap==null)
			return null;
		
		if (inputElement instanceof String) {
			View views[] = askap.getViewList();
			for (View view : views) {
				if (view.getName().equals(inputElement)) {
					currentView = view;
					Node node = currentView.getNode();
					return new Node[]{node};
				}
			}
			
			return null;
		}
		
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Node) {
			Node node = (Node) parentElement;
			return node.getBranches();
		}
		
		if (parentElement instanceof Branch) {
			Branch branch = (Branch) parentElement;
			// if it's a branch
			if (branch.getBranches() != null)
				return branch.getBranches();			
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
		return (getChildren(element)!=null);
	}
	
	@Override	
	public Image getImage(Object element) {
		String opi = getOpi(element);
		if (opi==null || opi.trim().length()==0)
			return null;
		
		
		return Activator.getDefault().getImage("icons/OPIRunner.png");
	}

	
	@Override
	public String getText(Object element) {
		if (askap==null)
			return "";
		
		if (element instanceof String) {
			View views[] = askap.getViewList();
			for (View view : views) {
				if (view.getName().equals(element)) {
					currentView = view;
					Node node = currentView.getNode();
					return node.getName();
				}
			}
			
			return "";
		}
		
		if (element instanceof Node) {
			Node node = (Node) element;
			return node.getName();
		}
		
		if (element instanceof Branch) {
			Branch branch = (Branch) element;
			return branch.getName();
		}
		
		return "";
		
	}
	
	public String getOpi(Object element) {
		if (askap==null)
			return "";
		
		String opiName = "";
		
		if (element instanceof Node) {
			opiName = ((Node) element).getOpiName();
		}
		
		if (element instanceof Branch) {
			opiName = ((Branch) element).getOpiName();
		}
		
		if (element instanceof Branch) {
			opiName = ((Branch) element).getOpiName();
		}
		
		for (OPI opi : askap.getOpiList()) {
			if (opi.getName().equals(opiName))
				return opi.getOpiFile();
		}
		return "";
	}

	public String[][] getMacros(Object element) {
		if (askap==null)
			return null;
		
		if (element instanceof Node) {
			return ((Node) element).getMacros();
		}
		
		if (element instanceof Branch) {
			return  ((Branch) element).getMacros();
		}
		
		if (element instanceof Branch) {
			return ((Branch) element).getMacros();
		}
		
		return null;
	}
}
