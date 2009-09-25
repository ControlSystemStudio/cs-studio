package org.csstudio.diag.diles.providers;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.diag.diles.model.Activity;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ModelContentProvider implements IStructuredContentProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		List<Activity> activities = (List<Activity>) inputElement;

		List<List> act = new ArrayList<List>();

		int num = 0;
		test: for (int i = 0; i < activities.size(); i++) {
			List ac = new ArrayList();

			for (int j = 0; j < activities.size(); j++) {
				if (activities.get(j).getNumberId() == i) {
					ac.add(activities.get(j));

					/*
					 * if(activities.get(j).getNumberId() != 0) {
					 * System.out.println
					 * (activities.get(j).getNumberId()+" "+activities
					 * .get(j-1).getNumberId()); if
					 * ((activities.get(j).getNumberId()) ==
					 * (activities.get(j-1)).getNumberId()) { //break test; } }
					 * System.out.print(activities.get(j)+" "+activities.get(j).
					 * getNumberId()+"|");
					 */
				}
			}
			act.add(ac);
		}
		
		Object[] array = act.toArray();
		return array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
