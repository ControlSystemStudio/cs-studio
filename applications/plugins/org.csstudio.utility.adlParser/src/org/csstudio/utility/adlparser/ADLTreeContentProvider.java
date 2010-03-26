package org.csstudio.utility.adlparser;

import org.csstudio.utility.adlconverter.utility.ADLWidget;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

public class ADLTreeContentProvider extends TreeNodeContentProvider {
	private ADLWidget rootWidget;
	public ADLTreeContentProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ADLWidget){
			rootWidget = (ADLWidget)parentElement;
			return rootWidget.getObjects().toArray();
			
		}
		return new Object[0];
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ADLWidget){
			rootWidget = (ADLWidget)element;
			if (rootWidget.getObjects().size() >0 ){
				return true;
			}
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ADLWidget){
			rootWidget = (ADLWidget)inputElement;
			return rootWidget.getObjects().toArray();
			
		}
		return new Object[0];
	}
}
