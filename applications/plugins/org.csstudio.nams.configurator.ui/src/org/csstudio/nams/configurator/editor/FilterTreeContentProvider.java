package org.csstudio.nams.configurator.editor;

import java.util.List;
import java.util.Set;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FilterTreeContentProvider implements ITreeContentProvider {

	private Object[] results;

	public Object[] getChildren(Object parentElement) {
		Object[] result = new Object[0];
		if (parentElement instanceof JunctorConditionForFilterTreeBean) {
			JunctorConditionForFilterTreeBean junctorEditionElement = (JunctorConditionForFilterTreeBean) parentElement;
			Set<FilterbedingungBean> operands = junctorEditionElement.getOperands();
			result = operands.toArray(new Object[operands.size()]);
		} 
		return result;
	}

	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof JunctorConditionForFilterTreeBean) {  
			return ((JunctorConditionForFilterTreeBean) element).hasOperands();
		} 
		return false;
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		if (results == null) {
			results = new Object[0];
			List<FilterbedingungBean> inputList = (List<FilterbedingungBean>) inputElement; 
			JunctorConditionForFilterTreeBean root = new JunctorConditionForFilterTreeBean();
			root.setJunctorConditionType(JunctorConditionType.AND);
			for (FilterbedingungBean configurationBean : inputList) {
				root.addOperand(configurationBean);
			}
			results = new JunctorConditionForFilterTreeBean[1];
			results[0] = root;
		}
		return results;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
