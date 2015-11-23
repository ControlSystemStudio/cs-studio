package org.csstudio.saverestore.ui.browser;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.services.IServiceLocator;

/**
 *
 * <code>SourceProvider</code> provides information how many data provides exist in the system and if selected data
 * provider supports branches.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SourceProvider extends AbstractSourceProvider {

    private static final String[] SOURCE_NAMES = new String[] { "org.csstudio.saverestore.branchessupported",
            "org.csstudio.saverestore.multipledataproviders", "org.csstudio.saverestore.resettablerepository" };

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.AbstractSourceProvider#initialize(org.eclipse.ui.services.IServiceLocator)
     */
    @Override
    public void initialize(IServiceLocator locator) {
        super.initialize(locator);
        SaveRestoreService.getInstance().addPropertyChangeListener(SaveRestoreService.SELECTED_DATA_PROVIDER,
                (e) -> fireSourceChanged(ISources.WORKBENCH, getCurrentState()));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.ISourceProvider#dispose()
     */
    @Override
    public void dispose() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.ISourceProvider#getCurrentState()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map getCurrentState() {
        Map<String, Boolean> state = new HashMap<>(2);
        DataProviderWrapper provider = SaveRestoreService.getInstance().getSelectedDataProvider();
        state.put(SOURCE_NAMES[0], provider == null ? false : provider.provider.areBranchesSupported());
        state.put(SOURCE_NAMES[1], SaveRestoreService.getInstance().getDataProviders().size() > 1);
        state.put(SOURCE_NAMES[2], provider == null ? false : provider.provider.isReinitSupported());
        return state;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.ISourceProvider#getProvidedSourceNames()
     */
    @Override
    public String[] getProvidedSourceNames() {
        return SOURCE_NAMES;
    }
}
