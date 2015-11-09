package org.csstudio.saverestore.ui.browser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.Engine;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.services.IServiceLocator;

/**
 *
 * <code>SourceProvider</code> provides information how many data provides exist in the system and if selected
 * data provider supports branches.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SourceProvider extends AbstractSourceProvider implements PropertyChangeListener {

    private static final String[] SOURCE_NAMES = new String[]{
            "org.csstudio.saverestore.branchessupported",
            "org.csstudio.saverestore.multipledataproviders"
    };

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.AbstractSourceProvider#initialize(org.eclipse.ui.services.IServiceLocator)
     */
    @Override
    public void initialize(IServiceLocator locator) {
        super.initialize(locator);
        Engine.getInstance().addPropertyChangeListener(Engine.SELECTED_DATA_PROVIDER, this);
    }

    /*
     * (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireSourceChanged(ISources.WORKBENCH, getCurrentState());
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.ISourceProvider#dispose()
     */
    @Override
    public void dispose() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.ISourceProvider#getCurrentState()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map getCurrentState() {
        Map<String,Boolean> state = new HashMap<>(2);
        DataProviderWrapper provider = Engine.getInstance().getSelectedDataProvider();
        state.put(SOURCE_NAMES[0], provider == null ? false : provider.provider.areBranchesSupported());
        state.put(SOURCE_NAMES[1], Engine.getInstance().getDataProviders().size() > 1);
        return state;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.ISourceProvider#getProvidedSourceNames()
     */
    @Override
    public String[] getProvidedSourceNames() {
        return SOURCE_NAMES;
    }

}
