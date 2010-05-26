package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.DefineStatementNode;

/**
 * The specialized {@link IAdapterFactory} for {@link VariableNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class DefineNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof DefineStatementNode) {
			final DefineStatementNode varNode = (DefineStatementNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<DefineStatementNode>(varNode) {
					@Override
					protected String getImageName(final DefineStatementNode node) {
						return "define.gif";
					}

					@Override
					protected String doGetLabel(final DefineStatementNode node) {
						final StringBuffer result = new StringBuffer(node
								.getSourceIdentifier());
						result.append(" -> ");
						result.append(node.getValue());
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
		return new Class[] { DefineStatementNode.class };
	}

}
