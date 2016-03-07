package de.desy.language.snl.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import de.desy.language.snl.parser.nodes.AllVariablesNode;

/**
 * The specialized {@link IAdapterFactory} for {@link AllVariableNode}s.
 *
 * @author C1 WPS / KM, MZ
 *
 */
class AllVariablesNodeAdapterFactory implements IAdapterFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(final Object adaptableObject,
            final Class adapterType) {
        assert adaptableObject != null;
        assert adapterType != null;

        if (adaptableObject instanceof AllVariablesNode) {
            final AllVariablesNode varNode = (AllVariablesNode) adaptableObject;

            if (adapterType == IWorkbenchAdapter.class) {
                return new AbstractSNLWorkbenchAdapter<AllVariablesNode>(varNode) {
                    @Override
                    protected String getImageName(final AllVariablesNode node) {
                        return "variable.gif";
                    }

                    @Override
                    protected String doGetLabel(final AllVariablesNode node) {
                        return node.getSourceIdentifier();
                    }
                };
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList() {
        return new Class[] { AllVariablesNode.class };
    }

}
