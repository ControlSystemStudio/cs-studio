package de.desy.language.snl.ui.adapter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

import de.desy.language.snl.parser.nodes.AbstractSNLNode;

/**
 * This class generate {@link IWorkbenchAdapter} for {@link AbstractSNLNode}s.
 * If a special {@link IAdapterFactory} is registered for a specific type of an
 * {@link AbstractSNLNode}, that factory is used.
 * 
 * @author C1 WPS / KM
 * 
 */
public class SNLNodeAdapterFactory implements IAdapterFactory {

	/**
	 * The known specialized factories.
	 */
	@SuppressWarnings("unchecked")
	private final Map<Class, IAdapterFactory> _adapterMap = new HashMap<Class, IAdapterFactory>();

	/**
	 * Constructor.
	 */
	public SNLNodeAdapterFactory() {
		this.initSubFactories();
	}

	/**
	 * Initializes the known specialized {@link IAdapterFactory}s. This method
	 * should only be called from within the constructor.
	 */
	private void initSubFactories() {
		this.addAdapterFactoryToMap(new ProgramNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new VariableNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new AssignNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new MonitorNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new StateSetNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new StateNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new WhenNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new EventFlagNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new SyncNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new OptionNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new PlaceholderNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new EntryNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new ExitNodeAdapterFactory(),
				this._adapterMap);
		this.addAdapterFactoryToMap(new AllVariablesNodeAdapterFactory(),
				this._adapterMap);
		addAdapterFactoryToMap(new AllDefinesNodeAdapterFactory(), _adapterMap);
		addAdapterFactoryToMap(new DefineNodeAdapterFactory(), _adapterMap);
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
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof AbstractSNLNode : "adaptableObject instanceof AbstractSNLNode"; //$NON-NLS-1$

		final AbstractSNLNode snlNode = (AbstractSNLNode) adaptableObject;

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
	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		Class[] classes = this._adapterMap.keySet().toArray(
				new Class[this._adapterMap.keySet().size() + 1]);
		classes[this._adapterMap.keySet().size()] = AbstractSNLNode.class;
		return classes;
	}

}
