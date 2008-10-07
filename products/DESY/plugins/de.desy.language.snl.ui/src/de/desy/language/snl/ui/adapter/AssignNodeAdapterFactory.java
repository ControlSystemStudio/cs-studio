package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.AssignStatementNode;

/**
 * The specialized {@link IAdapterFactory} for {@link AssignStatementNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class AssignNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof AssignStatementNode) {
			final AssignStatementNode node = (AssignStatementNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<AssignStatementNode>(
						node) {
					@Override
					public String getImageName(
							final AssignStatementNode nodeToRender) {
						if (nodeToRender.containsWarnings()) {
							return "assign_warning.gif";
						}
						return "assign.gif";
					}

					@Override
					protected String doGetLabel(
							final AssignStatementNode nodeToRender) {
						return nodeToRender.getSourceIdentifier() + " => "
								+ nodeToRender.getContent();
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
		return new Class[] { AssignStatementNode.class };
	}

}
