package org.csstudio.sds.language.script.ui.adapter;

import org.csstudio.sds.language.script.parser.nodes.VariableNode;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

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
    @Override
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
                        return name;
                    }

                    @Override
                    protected String doGetLabel(final VariableNode node) {
                        final StringBuffer result = new StringBuffer("var: ");
                        result.append(node.getSourceIdentifier());
                        if (node.isPredefined()) {
                            result.append(" (predefined)");
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
    @Override
    @SuppressWarnings("unchecked")
    public Class[] getAdapterList() {
        return new Class[] { VariableNode.class };
    }

}
