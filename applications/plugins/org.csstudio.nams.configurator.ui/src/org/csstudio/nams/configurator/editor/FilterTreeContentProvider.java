package org.csstudio.nams.configurator.editor;

import java.util.List;
import java.util.Set;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FilterTreeContentProvider implements ITreeContentProvider {

	private JunctorConditionForFilterTreeBean[] results;

	public Object[] getChildren(Object parentElement) {
		FilterbedingungBean[] result = new FilterbedingungBean[0];
		if (parentElement instanceof JunctorConditionForFilterTreeBean) {
			JunctorConditionForFilterTreeBean junctorEditionElement = (JunctorConditionForFilterTreeBean) parentElement;
			Set<FilterbedingungBean> operands = junctorEditionElement.getOperands();
			result = operands.toArray(new FilterbedingungBean[operands.size()]);
		} 
		if (parentElement instanceof NotConditionForFilterTreeBean) {
			NotConditionForFilterTreeBean not = (NotConditionForFilterTreeBean) parentElement;
			if (not.getFilterbedingungBean() instanceof JunctorConditionForFilterTreeBean) {
				Set<FilterbedingungBean> operands = ((JunctorConditionForFilterTreeBean) not.getFilterbedingungBean()).getOperands();
				result = operands.toArray(new FilterbedingungBean[operands.size()]);
			}
		}
		return result;
	}

	public Object getParent(Object element) {
		if (results != null) {
			JunctorConditionForFilterTreeBean root = results[0]; 
			return rekursiv(root, element);
		}
		return null;
	}

	private Object rekursiv(Object potentialParent, Object element) {

		if (hasChildren(potentialParent)) {
			FilterbedingungBean[] children = (FilterbedingungBean[]) getChildren(potentialParent);
			for (FilterbedingungBean child : children) {
				if (child == element) {
					return potentialParent;
				}
				Object result = rekursiv(child, element);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof JunctorConditionForFilterTreeBean) {  
			return ((JunctorConditionForFilterTreeBean) element).hasOperands();
		} 
		if (element instanceof NotConditionForFilterTreeBean) {
			NotConditionForFilterTreeBean not = (NotConditionForFilterTreeBean) element;
			if (not.getFilterbedingungBean() instanceof JunctorConditionForFilterTreeBean) {
				return ((JunctorConditionForFilterTreeBean) not.getFilterbedingungBean()).hasOperands();
			}
			
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		if (results == null) {
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
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
