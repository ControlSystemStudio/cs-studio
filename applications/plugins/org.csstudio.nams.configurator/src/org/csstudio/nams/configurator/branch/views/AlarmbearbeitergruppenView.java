package org.csstudio.nams.configurator.branch.views;

import javax.swing.text.TableView;

import org.csstudio.nams.configurator.branch.composite.FilteredListVarianteA;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class AlarmbearbeitergruppenView extends ViewPart {

	public AlarmbearbeitergruppenView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {

		new FilteredListVarianteA(parent, SWT.None);
		// // TODO Auto-generated method stub
		// TreeViewer viewer = new TreeViewer(parent );
		//		
		// viewer.setInput(Object[]);
		// viewer.setLabelProvider(new )
		//		
		// viewer.setFilters(new ViewerFilter[]{ new ViewerFilter(){
		//			
		// @Override
		// public Object[] filter(Viewer viewer, Object parent,
		// Object[] elements) {
		// // TODO Auto-generated method stub
		// return super.filter(viewer, parent, elements);
		// }
		//			
		// }});
		//		
		// TableViewer table = new TableViewer(parent);
		// table.setFilters(new ViewerFilter[]{ })
		//		

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
