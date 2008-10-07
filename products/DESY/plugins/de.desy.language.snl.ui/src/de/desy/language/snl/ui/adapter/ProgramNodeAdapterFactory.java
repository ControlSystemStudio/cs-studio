package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.ProgramNode;

/**
 * The specialized {@link IAdapterFactory} for {@link ProgramNode}s.
 * 
 * @author C1 WPS / KM, MZ
 * 
 */
class ProgramNodeAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;

		if (adaptableObject instanceof ProgramNode) {
			final ProgramNode node = (ProgramNode) adaptableObject;

			if (adapterType == IWorkbenchAdapter.class) {
				return new AbstractSNLWorkbenchAdapter<ProgramNode>(node) {
					@Override
					public String getImageName(final ProgramNode nodeToRender) {
						return "program.gif";
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
		return new Class[] { ProgramNode.class };
	}

}
