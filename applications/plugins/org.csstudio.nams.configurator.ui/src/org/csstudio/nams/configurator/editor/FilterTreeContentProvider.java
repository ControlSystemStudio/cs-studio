
package org.csstudio.nams.configurator.editor;

import java.util.LinkedList;
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

	@Override
    public void dispose() {
	    // Not used yet
	}

	@Override
    public Object[] getChildren(final Object parentElement) {
		FilterbedingungBean[] result = new FilterbedingungBean[0];
		if (parentElement instanceof JunctorConditionForFilterTreeBean) {
			final JunctorConditionForFilterTreeBean junctorEditionElement = (JunctorConditionForFilterTreeBean) parentElement;
			final Set<FilterbedingungBean> operands = junctorEditionElement
					.getOperands();
			result = operands.toArray(new FilterbedingungBean[operands.size()]);
		}
		if (parentElement instanceof NotConditionForFilterTreeBean) {
			final NotConditionForFilterTreeBean not = (NotConditionForFilterTreeBean) parentElement;
			if (not.getFilterbedingungBean() instanceof JunctorConditionForFilterTreeBean) {
				final Set<FilterbedingungBean> operands = ((JunctorConditionForFilterTreeBean) not
						.getFilterbedingungBean()).getOperands();
				result = operands.toArray(new FilterbedingungBean[operands
						.size()]);
			}
		}
		return result;
	}

	/**
	 * Gives the content of the root-and condition.
	 */
	public List<FilterbedingungBean> getContentsOfRootANDCondition() {
		final List<FilterbedingungBean> result = new LinkedList<FilterbedingungBean>();

		if (this.results != null) {
			result.addAll(this.results[0].getOperands());
		}

		return result;
	}

	@Override
    @SuppressWarnings("unchecked") //$NON-NLS-1$
	public Object[] getElements(final Object inputElement) {
		if (this.results == null) {
			final List<FilterbedingungBean> inputList = (List<FilterbedingungBean>) inputElement;
			final JunctorConditionForFilterTreeBean root = new JunctorConditionForFilterTreeBean();
			root.setJunctorConditionType(JunctorConditionType.AND);
			for (final FilterbedingungBean configurationBean : inputList) {
				root.addOperand(configurationBean);
			}
			this.results = new JunctorConditionForFilterTreeBean[1];
			this.results[0] = root;
		}
		return this.results;
	}

	@Override
    public Object getParent(final Object element) {
		if (this.results != null) {
			final JunctorConditionForFilterTreeBean root = this.results[0];
			return this.rekursiv(root, element);
		}
		return null;
	}

	@Override
    public boolean hasChildren(final Object element) {
		if (element instanceof JunctorConditionForFilterTreeBean) {
			return ((JunctorConditionForFilterTreeBean) element).hasOperands();
		} else {
			if (element instanceof NotConditionForFilterTreeBean) {
				final NotConditionForFilterTreeBean not = (NotConditionForFilterTreeBean) element;
				if (not.getFilterbedingungBean() instanceof JunctorConditionForFilterTreeBean) {
					return ((JunctorConditionForFilterTreeBean) not
							.getFilterbedingungBean()).hasOperands();
				}

			}
		}
		return false;
	}

	@Override
    public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	    // Not used yet
	}

	private Object rekursiv(final Object potentialParent, final Object element) {

		if (this.hasChildren(potentialParent)) {
			final FilterbedingungBean[] children = (FilterbedingungBean[]) this
					.getChildren(potentialParent);
			for (final FilterbedingungBean child : children) {
				if (child == element) {
					return potentialParent;
				}
				final Object result = this.rekursiv(child, element);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

}
