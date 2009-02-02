package org.csstudio.diag.rack.view;

import org.csstudio.diag.rack.gui.GUI;
import org.csstudio.diag.rack.model.RackModel;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class RackView extends ViewPart
	{
	    final public static String ID = RackView.class.getName();
	    //private static final String URL = "jdbc:oracle:thin:sns_reports/sns@//snsdev3.sns.ornl.gov:1521/devl";
	    public static final String URL = "jdbc:oracle:thin:sns_reports/sns@//snsdb1.sns.ornl.gov/prod"; //$NON-NLS-1$
	    private RackModel control = null;
	    private GUI gui;
    	
    	
	    public RackView()
	    {
        	try
	        {
        		control = new RackModel();    
	        }
	        catch (Exception ex)
	        {
	        	CentralLogger.getInstance().getLogger(this).error("Exception", ex);
	        }
    	}


	    @Override
	    public void createPartControl(Composite parent)
	    {
	        if (control != null)
	        {
	            gui = new GUI(parent, control);
	           
	            // Allow Eclipse to listen to PV selection changes
	            final TableViewer rackTable = gui.getRackTableViewer();
	            getSite().setSelectionProvider(rackTable);
	            
	            // Allow dragging PV names & Archive Info out of the name table.
	            new ProcessVariableDragSource(rackTable.getTable(),
	                            rackTable);
	            
	            // Enable 'Drop'
	            final Text dvcOrPVFilter = gui.getDVCOrPVEntry();
	            new ProcessVariableDropTarget(dvcOrPVFilter)
	            {
	                @Override
	                public void handleDrop(IProcessVariable name,
	                                       DropTargetEvent event)
	                {
	                	setDVCOrPVFilter(name.getName());
	                }
	            };
	            
	            // Add empty context menu so that other CSS apps can
	            // add themselves to it
	            MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
	            menuMgr.setRemoveAllWhenShown(true);
	            Menu menu = menuMgr.createContextMenu(rackTable.getControl());
	            rackTable.getControl().setMenu(menu);
	            getSite().registerContextMenu(menuMgr, rackTable);
	            
	            // Add context menu to the name table.
	            // One reason: Get object contribs for the NameTableItems.
	              IWorkbenchPartSite site = getSite();
	              MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
	              manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	              Menu contextMenu = manager.createContextMenu(rackTable.getControl());
	              rackTable.getControl().setMenu(contextMenu);
	              site.registerContextMenu(manager, rackTable);
	        
	        }
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
	            RackView rack_view = (RackView) page.showView(RackView.ID);
	            rack_view.setDVCOrPVFilter(pv_name.getName());
	        }
	        catch (Exception ex)
	        {
	        	CentralLogger.getInstance().getLogger(new RackView()).error("Exception", ex);
	        }
	        return false;
	    }
	    
	    public void setDVCOrPVFilter(String pv_name)
	    {
	    	gui.rackList.deselectAll();
	    	gui.getDVCOrPVEntry().setText(pv_name);
	        control.setSelectedRack(pv_name);

	    }
	}
