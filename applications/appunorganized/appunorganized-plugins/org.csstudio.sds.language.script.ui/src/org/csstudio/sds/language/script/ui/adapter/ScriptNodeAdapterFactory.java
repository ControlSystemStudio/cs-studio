package org.csstudio.sds.language.script.ui.adapter;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.language.script.parser.nodes.AbstractScriptNode;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * This class generate {@link IWorkbenchAdapter} for {@link AbstractScriptNode}s.
 * If a special {@link IAdapterFactory} is registered for a specific type of an
 * {@link AbstractScriptNode}, that factory is used.
 *
 * @author C1 WPS / KM
 *
 */
public class ScriptNodeAdapterFactory implements IAdapterFactory {

    /**
     * The known specialized factories.
     */
    @SuppressWarnings("unchecked")
    private final Map<Class, IAdapterFactory> _adapterMap = new HashMap<Class, IAdapterFactory>();

    /**
     * Constructor.
     */
    public ScriptNodeAdapterFactory() {
        this.initSubFactories();
    }

    /**
     * Initializes the known specialized {@link IAdapterFactory}s. This method
     * should only be called from within the constructor.
     */
    private void initSubFactories() {
        this.addAdapterFactoryToMap(new VariableNodeAdapterFactory(),
                this._adapterMap);
        this.addAdapterFactoryToMap(new RuleNodeAdapterFactory(),
                this._adapterMap);
        this.addAdapterFactoryToMap(new FunctionNodeAdapterFactory(),
                this._adapterMap);
    }

    /**
     * Adds the given {@link IAdapterFactory} to the given {@link Map} for every
     * type the factory supports.
     *
     * @param factory The {@link IAdapterFactory} to be added
     * @param adapterMap The {@link Map} to add the factory
     */
    @SuppressWarnings("unchecked")
    private void addAdapterFactoryToMap(final IAdapterFactory factory,
            final Map<Class, IAdapterFactory> adapterMap) {
        for (final Class clazz : factory.getAdapterList()) {
            adapterMap.put(clazz, factory);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Object adaptableObject,
            final Class adapterType) {
        assert adaptableObject != null;
        assert adapterType != null;
        assert adaptableObject instanceof AbstractScriptNode : "adaptableObject instanceof AbstractScriptNode"; //$NON-NLS-1$

        final AbstractScriptNode snlNode = (AbstractScriptNode) adaptableObject;

        if (adapterType == IWorkbenchAdapter.class) {
            Object adapter = null;
            final IAdapterFactory adapterFactory = this._adapterMap
                    .get(adaptableObject.getClass());
            if (adapterFactory != null) {
                adapter = adapterFactory.getAdapter(adaptableObject,
                        adapterType);
                if (adapter != null) {
                    return adapter;
                }
            }
            return new WorkbenchAdapter() {
                @Override
                public String getLabel(final Object object) {
                    return snlNode.humanReadableRepresentation().replace("\n",
                            " ").replace("\r", " ").replace("\t", " ");
                }

                @SuppressWarnings("deprecation")
                @Override
                public Object[] getChildren(final Object object) {
                    return snlNode.getChildrenNodesAsArray();
                }
            };
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class[] getAdapterList() {
        Class[] classes = this._adapterMap.keySet().toArray(
                new Class[this._adapterMap.keySet().size() + 1]);
        classes[this._adapterMap.keySet().size()] = AbstractScriptNode.class;
        return classes;
    }

}
