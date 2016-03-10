package org.csstudio.sds.language.script.ui.adapter;

import org.csstudio.sds.language.script.parser.nodes.FunctionNode;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * The specialized {@link IAdapterFactory} for {@link FunctionNode}s.
 *
 * @author C1 WPS / KM, MZ
 *
 */
class FunctionNodeAdapterFactory implements IAdapterFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Object adaptableObject,
            final Class adapterType) {
        assert adaptableObject != null;
        assert adapterType != null;

        if (adaptableObject instanceof FunctionNode) {
            final FunctionNode varNode = (FunctionNode) adaptableObject;

            if (adapterType == IWorkbenchAdapter.class) {
                return new AbstractSNLWorkbenchAdapter<FunctionNode>(varNode) {
                    @Override
                    protected String getImageName(final FunctionNode node) {
                        String name = "function.gif";
                        return name;
                    }

                    @Override
                    protected String doGetLabel(final FunctionNode node) {
                        final StringBuffer result = new StringBuffer("function: ");
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
        return new Class[] { FunctionNode.class };
    }

}
