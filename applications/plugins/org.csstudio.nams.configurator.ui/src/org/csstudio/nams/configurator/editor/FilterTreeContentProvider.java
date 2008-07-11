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
		Object[] result = new Object[0];
		if (parentElement instanceof JunctorConditionForFilterTreeBean) {
			JunctorConditionForFilterTreeBean junctorEditionElement = (JunctorConditionForFilterTreeBean) parentElement;
			Set<FilterbedingungBean> operands = junctorEditionElement.getOperands();
			result = operands.toArray(new Object[operands.size()]);
		} 
		if (parentElement instanceof NotConditionForFilterTreeBean) {
			NotConditionForFilterTreeBean not = (NotConditionForFilterTreeBean) parentElement;
			if (not.getFilterbedingungBean() instanceof JunctorConditionForFilterTreeBean) {
				Set<FilterbedingungBean> operands = ((JunctorConditionForFilterTreeBean) not.getFilterbedingungBean()).getOperands();
				result = operands.toArray(new Object[operands.size()]);
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

	private Object rekursiv(JunctorConditionForFilterTreeBean bean, Object element) {
		Set<FilterbedingungBean> operands = bean.getOperands();
		if (operands != null && operands.size() > 0){
			for (FilterbedingungBean filterbedingungBean : operands) {
				if (filterbedingungBean.equals(element)) {
					return bean;
				} else if (filterbedingungBean instanceof JunctorConditionForFilterTreeBean) {
					Object rekursiv = rekursiv((JunctorConditionForFilterTreeBean) filterbedingungBean, element);
					if (rekursiv != null) {
						return rekursiv;
					}
				} else if (filterbedingungBean instanceof NotConditionForFilterTreeBean) {
					NotConditionForFilterTreeBean notBean = (NotConditionForFilterTreeBean) filterbedingungBean;
					FilterbedingungBean notBeanChild = notBean.getFilterbedingungBean();
					if (notBeanChild.equals(element)) {
						return bean;
					} else if (notBeanChild instanceof JunctorConditionForFilterTreeBean) {
						Object rekursiv = rekursiv((JunctorConditionForFilterTreeBean) notBeanChild, element);
						if (rekursiv != null) {
							return rekursiv;
						}
					}
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
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
