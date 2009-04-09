package org.csstudio.diag.pvutil.view;

import org.csstudio.diag.pvutil.gui.GUI;
import org.csstudio.diag.pvutil.model.PVUtilModel;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IFrontEndControllerName;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** Eclipse "View" for the PV Utility
 *  <p>
 *  Creates the PVUtilDataAPI and displays the GUI within an Eclipse "View" site.
 *  @author 9pj
 */
public class PVUtilView extends ViewPart
{
    // View ID defined in plugin.xml
    final public static String ID = "org.csstudio.diag.pvutil.view.PVUtilView";
    //private static final String URL = "jdbc:oracle:thin:sns_reports/sns@//snsdev3.sns.ornl.gov:1521/devl";
    public static final String URL = "jdbc:oracle:thin:sns_reports/sns@//snsdb1.sns.ornl.gov/prod"; //$NON-NLS-1$
    private PVUtilModel model = null;
    private GUI gui;
    
    public PVUtilView()
    {
        try
        {
            model = new PVUtilModel();
        }
        catch (Exception ex)
        {
        	CentralLogger.getInstance().getLogger(this).error("Exception", ex);
        }
    }

    @Override
    public void createPartControl(Composite parent)
    {
        if (model == null)
        {
            new Label(parent, 0).setText("Cannot initialize"); //$NON-NLS-1$
            return;
        }
        gui = new GUI(parent, model);
       
        // Allow Eclipse to listen to PV selection changes
        final TableViewer pv_table = gui.getPVTableViewer();
        getSite().setSelectionProvider(pv_table);
        
        // Allow dragging PV names & Archive Info out of the name table.
        new ProcessVariableDragSource(pv_table.getTable(),
                        pv_table);
        
        // Enable 'Drop'
        final Text pv_filter = gui.getPVFilterText();
        new ProcessVariableDropTarget(pv_filter)
        {
            @Override
            public void handleDrop(IProcessVariable name,
                                   DropTargetEvent event)
            {
                setPVFilter(name.getName());
            }
        };
       
        // Add empty context menu so that other CSS apps can
        // add themselves to it
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        Menu menu = menuMgr.createContextMenu(pv_table.getControl());
        pv_table.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, pv_table);
        
        // Add context menu to the name table.
        // One reason: Get object contribs for the NameTableItems.
        IWorkbenchPartSite site = getSite();
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Menu contextMenu = manager.createContextMenu(pv_table.getControl());
        pv_table.getControl().setMenu(contextMenu);
        site.registerContextMenu(manager, pv_table);
    }
   
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }

    public static boolean activateWithPV(IProcessVariable pv_name)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            PVUtilView pv_view = (PVUtilView) page.showView(PVUtilView.ID);
            pv_view.setPVFilter(pv_name.getName());
        }
        catch (Exception ex)
        {
        	CentralLogger.getInstance().getLogger(new PVUtilView()).error("Exception", ex);
        }
        return false;
    }
    
    public static boolean activateWithDevice(IFrontEndControllerName device_name)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            PVUtilView pv_view = (PVUtilView) page.showView(PVUtilView.ID);
            pv_view.setDeviceFilter(device_name.getName());
        }
        catch (Exception ex)
        {
        	CentralLogger.getInstance().getLogger(new PVUtilView()).error("Exception", ex);
        }
        return false;
    }
    
    private void setPVFilter(final String pv_name)
    {
    	gui.getDeviceFilterText().setText("");
        model.setFECFilter("");
    	gui.getPVFilterText().setText(pv_name);
        model.setPVFilter(pv_name);
        
    }

    private void setDeviceFilter(final String device_name)
    {
        gui.getDeviceFilterText().setText(device_name);
        model.setFECFilter(device_name);
    }
}
