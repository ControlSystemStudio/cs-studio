package de.desy.language.editor.ui.internal.parserUI.nodes;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.WorkbenchAdapter;

import de.desy.language.editor.core.parser.RootNode;

public class NodeAdapterFactory implements IAdapterFactory {

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        assert adaptableObject != null;
        assert adapterType != null;
        assert adaptableObject instanceof RootNode : "adaptableObject instanceof RootNode"; //$NON-NLS-1$

        final RootNode adaptedNode = (RootNode) adaptableObject;
        return new WorkbenchAdapter() {
            @Override
            public String getLabel(final Object object) {
                return adaptedNode.humanReadableRepresentation().replace("\n",
                        " ").replace("\r", " ").replace("\t", " ");
            }

            @Override
            public Object[] getChildren(final Object object) {
                return adaptedNode.getChildrenNodes().toArray();
            }
        };
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
        return new Class[] {RootNode.class};
    }

}
