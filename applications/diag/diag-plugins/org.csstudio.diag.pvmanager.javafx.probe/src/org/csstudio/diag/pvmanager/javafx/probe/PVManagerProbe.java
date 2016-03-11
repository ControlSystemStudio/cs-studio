package org.csstudio.diag.pvmanager.javafx.probe;

import java.util.logging.Logger;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import org.csstudio.csdata.ProcessVariable;
import org.diirt.javafx.tools.Probe;
import org.eclipse.fx.ui.workbench3.FXViewPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

/**
 * Probe view.
 */
public class PVManagerProbe extends FXViewPart {

    public PVManagerProbe() {
    }

    private static final Logger log = Logger.getLogger(PVManagerProbe.class
            .getName());

    // The ID of the view as specified by the extension point
    public static final String VIEW_ID = "org.csstudio.diag.pvmanager.javafx.probe"; //$NON-NLS-1$

    // Next secondary view ID, i.e. next instance of probe should use this number.
    // SYNC on PVManagerProbe.class for access
    private static int next_instance = 1;

    private SimpleDataTextExport export = new SimpleDataTextExport();

    // Memento keys
    private IMemento memento = null;
    private static final String MEMENTO_PVFORMULA_LIST = "pvFormulaList"; //$NON-NLS-1$
    private static final String MEMENTO_PVFORMULA = "pvFormula"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_VIEWER = "showViewer"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_VALUE = "showValue"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_CHANGE_VALUE = "showChangeValue"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_METADATA = "showMetadata"; //$NON-NLS-1$
    private static final String MEMENTO_SHOW_DETAILS = "showDetails"; //$NON-NLS-1$

    @Override
    public void init(final IViewSite site, final IMemento memento)
            throws PartInitException {
        super.init(site, memento);

        // For new instances opened while CSS is running,
        // createNewInstance() tracks the secondary view ID.
        // But if this view was 'restored' from a saved workspace,
        // we need to adjust the instance counter to not re-use
        // IDs of restored views.
        int this_instance = 1;
        try
        {
            this_instance = Integer.parseInt(site.getSecondaryId());
        }
        catch (NumberFormatException ex)
        {
            // Ignore, just assume 1
        }
        synchronized (PVManagerProbe.class)
        {
            if (this_instance >= next_instance)
                next_instance = this_instance + 1;
        }

        // Save the memento
        this.memento = memento;
    }

    @Override
    public void saveState(final IMemento memento) {
        super.saveState(memento);
//          memento.putString(MEMENTO_PVFORMULA, pvFormula);
//          memento.putBoolean(MEMENTO_SHOW_VIEWER, sectionToMenu.get(viewerPanel)
//                  .getSelection());
//          memento.putBoolean(MEMENTO_SHOW_VALUE, sectionToMenu.get(valuePanel)
//                  .getSelection());
//          memento.putBoolean(MEMENTO_SHOW_CHANGE_VALUE,
//                  sectionToMenu.get(changeValuePanel).getSelection());
//          memento.putBoolean(MEMENTO_SHOW_METADATA, sectionToMenu.get(metadataPanel)
//                  .getSelection());
//          memento.putBoolean(MEMENTO_SHOW_DETAILS, sectionToMenu.get(detailsPanel)
//                  .getSelection());
    }

    @Override
    protected Scene createFxScene() {
        try {
            return Probe.createScene();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new Scene(new AnchorPane());
        }
    }

    @Override
    protected void setFxFocus() {
        // TODO Auto-generated method stub

    }


    /**
     * Changes the PV currently displayed by probe.
     *
     * @param pvName
     *        the new pv name or null
     */
    public void setPVName(ProcessVariable pvName) {
        setPVFormula(pvName.getName());
    }

    public void setPVFormula(String pvFormula) {
        // TODO implement
    }

    public static String createNewInstance() {
        synchronized (PVManagerProbe.class)
        {
            return Integer.toString(next_instance++);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
    }
}
