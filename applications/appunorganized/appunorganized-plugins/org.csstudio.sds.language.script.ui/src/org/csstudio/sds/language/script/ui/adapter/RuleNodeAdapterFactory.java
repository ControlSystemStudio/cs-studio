package org.csstudio.sds.language.script.ui.adapter;

import org.csstudio.sds.language.script.parser.nodes.RuleNode;
import org.csstudio.sds.language.script.parser.nodes.VariableNode;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * The specialized {@link IAdapterFactory} for {@link VariableNode}s.
 *
 * @author C1 WPS / KM, MZ
 *
 */
class RuleNodeAdapterFactory implements IAdapterFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Object adaptableObject,
            final Class adapterType) {
        assert adaptableObject != null;
        assert adapterType != null;

        if (adaptableObject instanceof RuleNode) {
            final RuleNode ruleNode = (RuleNode) adaptableObject;

            if (adapterType == IWorkbenchAdapter.class) {
                return new AbstractSNLWorkbenchAdapter<RuleNode>(ruleNode) {
                    @Override
                    protected String getImageName(final RuleNode node) {
                        String name = "rule_script.gif";
                        return name;
                    }

                    @Override
                    protected String doGetLabel(final RuleNode node) {
                        final StringBuffer result = new StringBuffer("Scripted Rule: ");
                        result.append(node.getSourceIdentifier());
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
        return new Class[] { RuleNode.class };
    }

}
