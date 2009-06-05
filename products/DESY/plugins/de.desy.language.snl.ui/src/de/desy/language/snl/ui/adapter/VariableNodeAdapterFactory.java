package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.VariableNode;

/**
 * The specialized {@link IAdapterFactory} for {@link VariableNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class VariableNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof VariableNode) {
			final VariableNode varNode = (VariableNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<VariableNode>(varNode) {
					@Override
					protected String getImageName(final VariableNode node) {
						String name = "variable.gif";
						
						if (node.isAssigned() && node.isMonitored()) {
							name = "variable_assigned_monitored.gif";
						} else if (node.isAssigned() && !node.isMonitored()) {
							name = "variable_assigned.gif";
						} else if (!node.isAssigned() && node.isMonitored()) {
							name = "variable_monitored.gif";
						}
						return name;
					}

					@Override
					protected String doGetLabel(final VariableNode node) {
						final StringBuffer result = new StringBuffer(node
								.getSourceIdentifier());
						result.append(" : ");
						result.append(node.getTypeName());

						if (node.isAssigned() || node.isMonitored()) {
							result.append(" (");

							if (node.isAssigned()) {
								result.append("assigned");
							}

							if (node.isAssigned() && node.isMonitored()) {
								result.append(", ");
							}

							if (node.isMonitored()) {
								result.append("monitored");
							}

							result.append(")");
						}

						return result.toString();
					}
				};
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { VariableNode.class };
	}

}
