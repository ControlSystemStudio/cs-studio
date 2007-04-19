package org.csstudio.diag.probe;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.util.swt.ComboHistoryHelper;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.utility.pv.epics.EPICS_V3_PV;
import org.csstudio.value.Value;
import org.csstudio.value.ValueUtil;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Main Eclipse ViewPart of the Probe plug-in.
 *
 * @author Original by Ken Evans (APS)
 * @author Modifications by Kay Kasemir
 * @author Last modifications by Jan Hatje und Helge Rickens
 */
public class Probe extends ViewPart implements PVListener
{
    public static final String ID = Probe.class.getName();
    private static final String PV_LIST_TAG = "pv_list"; //$NON-NLS-1$
    private static final String PV_TAG = "PVName"; //$NON-NLS-1$
    public static final boolean debug = false;

    private IMemento memento = null;

    // GUI
    private ComboViewer txt_name;
    private ComboHistoryHelper name_helper;
    private Label lbl_value;
    private Label lbl_time;
    private Button btn_info;
    private Button btn_adjust;
    private Label lbl_status;

    /** The process variable that we monitor. */
    private PV pv = null;
    /** The most recent value of the PV. */
    private String value_txt = null;
    /** The most recent time stamp of the PV. */
    private ITimestamp time = null;
    /** Smoothed period in seconds between received values. */
    private SmoothedDouble value_period = new SmoothedDouble();
    private NumberFormat period_format;

//    /** Create or re-display a probe view with the given PV name.
//     *  <p>
//     *  Invoked by the PVpopupAction.
//     *
//     *  @param pv_name The PV to 'probe'
//     *  @return Returns <code>true</code> when successful.
//     */
//    public static boolean activateWithPV(String pv_name)
//    {
//        try
//        {
//            IWorkbench workbench = PlatformUI.getWorkbench();
//            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
//            IWorkbenchPage page = window.getActivePage();
//            Probe probe = (Probe) page.showView(Probe.ID);
//            probe.setPVName(pv_name);
//            return true;
//        }
//        catch (Exception e)
//        {
//            Plugin.logException("activateWithPV", e); //$NON-NLS-1$
//            e.printStackTrace();
//        }
//        return false;
//    }
    class UsedSorter extends ViewerSorter{
    	// Sort a table at the last two selected tableheader
    	public int compare(Object o1, Object o2) {
    		System.out.println("compare");
    		if (o1 instanceof IProcessVariable&&o2 instanceof IProcessVariable) {
    			return 1;
    		}else
    			return 0;
    	}

    }

    public static boolean activateWithPV(IProcessVariable iPV)
    {
        try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            Probe probe = (Probe) page.showView(Probe.ID);
            probe.setPVName(iPV);
            return true;
        }
        catch (Exception e)
        {
            Plugin.logException("activateWithPV", e); //$NON-NLS-1$
            e.printStackTrace();
        }
        return false;
    }


    public Probe()
    {
        period_format = NumberFormat.getNumberInstance();
        period_format.setMinimumFractionDigits(2);
        period_format.setMaximumFractionDigits(2);
    }

    /** ViewPart interface, keep the memento. */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento = memento;
    }

    /** ViewPart interface, persist state */
    @Override
    public void saveState(IMemento memento)
    {
        super.saveState(memento);
        memento.putString(PV_TAG, txt_name.getCombo().getText());
    }

    /** ViewPart interface, create UI. */
    public void createPartControl(Composite parent)
    {
        createGUI(parent);

        // Enable 'Drop'
        new ProcessVariableDropTarget(txt_name.getControl())
        {
            @Override
            public void handleDrop(IProcessVariable name,
                                   DropTargetEvent event)
            {
                setPVName(name);
            }
        };

        // In principle, this could allow 'dragging' of PV names
        // from the text box.
        // In practice, however, any mouse click & drag only selects
        // portions of the text and moves the cursor. It won't
        // initiate a 'drag'.
        // Maybe it works on some OS? Maybe there's another magic
        // modifier key to force a 'drag'?
        new ProcessVariableDragSource(txt_name.getControl(), txt_name);

//        new ProcessVariableDragSource(txt_name.getControl(), new ISelectionProvider()
//        {
//            public void addSelectionChangedListener(ISelectionChangedListener listener)
//            {}
//
//            public void removeSelectionChangedListener(ISelectionChangedListener listener)
//            {}
//
//            public void setSelection(ISelection selection)
//            {}
//
//            public ISelection getSelection()
//            {
//                Object pvs[] = new Object[1];
//                pvs[0] = CentralItemFactory.createProcessVariable(txt_name.getText());
//                return new StructuredSelection(pvs);
//            }
//        });

        makeContextMenu();
    }

    // ViewPart interface
    public void setFocus()
    {
        txt_name.getCombo().setFocus();
    }

    /** Construct GUI. */
    private void createGUI(Composite parent)
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);
        GridData gd;

        // PV Name: ____ name ___________ [Info]
        // Value  : ____ value ________ [Adjust]
        //
        // ------- status --------

        // Row 1
        Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.S_PVName);
        gd = new GridData();
        label.setLayoutData(gd);

        txt_name = new ComboViewer(parent, SWT.SINGLE | SWT.BORDER);
//        txt_name.setSorter(new UsedSorter());
//        txt_name.setSorter(new ViewerSorter());
        txt_name.getCombo().setToolTipText(Messages.S_EnterPVName);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        txt_name.getCombo().setLayoutData(gd);
        name_helper = new ComboHistoryHelper(
                        Plugin.getDefault().getDialogSettings(),
                        PV_LIST_TAG, txt_name)
        {
            public void newSelection(String pv_name)
            {   setPVName(CentralItemFactory.createProcessVariable(pv_name));       }
        };
        txt_name.getCombo().addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                disposeChannel();
                name_helper.saveSettings();
            }
        });

        btn_info = new Button(parent, SWT.PUSH);
        btn_info.setText(Messages.S_Info);
        btn_info.setToolTipText(Messages.S_ObtainInfo);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL; // but don't grab...
        btn_info.setLayoutData(gd);
        btn_info.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent ev)
            {
                showInfo();
            }
        });

        // Row 2
        label = new Label(parent, SWT.NONE);
        label.setText(Messages.S_Value);
        gd = new GridData();
        label.setLayoutData(gd);

        lbl_value = new Label(parent, SWT.NONE);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        lbl_value.setLayoutData(gd);

        btn_adjust = new Button(parent, SWT.PUSH);
        btn_adjust.setText(Messages.S_Adjust);
        btn_adjust.setToolTipText(Messages.S_ModValue);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL; // but don't grab...
        btn_adjust.setLayoutData(gd);
        btn_adjust.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent ev)
            {   adjustValue();   }
        });

        // Row 3
        label = new Label(parent, SWT.NONE);
        label.setText(Messages.S_Timestamp);
        gd = new GridData();
        label.setLayoutData(gd);

        lbl_time = new Label(parent, SWT.NONE);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns-1;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        lbl_time.setLayoutData(gd);

        // Status bar
        label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.verticalAlignment = SWT.BOTTOM;
        gd.grabExcessVerticalSpace = true;
        label.setLayoutData(gd);

        label = new Label(parent, SWT.NONE);
        label.setText(Messages.S_Status);
        gd = new GridData();
        label.setLayoutData(gd);

        lbl_status = new Label(parent, SWT.BORDER);
        lbl_status.setText(Messages.S_Waiting);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns - 1;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        lbl_status.setLayoutData(gd);

        name_helper.loadSettings();

        if (memento != null)
        {
            String pv_name = memento.getString(PV_TAG);
            if (pv_name != null&&pv_name.length() > 0){
            	setPVName(CentralItemFactory.createProcessVariable(pv_name));
//                setPVName(pv_name);
            }
        }
    }

//    /** Update the PV name that is probed.
//     *  <p>
//     *  Opens a new channel, closing any old one first
//     *  @param name
//     */
//    public boolean setPVName(String name)
//    {
//    	System.out.println("setPVName mit String");
//        if (Probe.debug)
//            Plugin.logInfo("setPVName(" + name + ")"); //$NON-NLS-1$ //$NON-NLS-2$
//
//        // Update displayed name, unless it's already current
//        if (! txt_name.getCombo().getText().equals(name))
//            txt_name.getCombo().setText(name);
//        // Reset rest of GUI
//        lbl_value.setText("");  //$NON-NLS-1$
//        lbl_time.setText("");  //$NON-NLS-1$
//        time = null;
//
//        // Close a previous channel
//        disposeChannel();
//
//        // Check the name
//        if (name == null || name.equals(""))  //$NON-NLS-1$
//        {
//            updateStatus(Messages.S_Waiting);
//            return false;
//        }
//
//        // Create a new channel
//        try
//        {
//            updateStatus(Messages.S_Searching);
//            pv = new EPICS_V3_PV(name);
//            pv.addListener(this);
//            pv.start();
//        }
//        catch (Exception ex)
//        {
//            Plugin.logException(Messages.S_CreateError, ex);
//            updateStatus(Messages.S_CreateError + ex.getMessage());
//            return false;
//        }
//        return true;
//    }

    /** Update the PV name that is probed.
     *  <p>
     *  Opens a new channel, closing any old one first
     *  @param name
     */
    public boolean setPVName(IProcessVariable iPV)
    {
        if (Probe.debug)
            Plugin.logInfo("setPVName(" + iPV.getName()+ ")"); //$NON-NLS-1$ //$NON-NLS-2$

        // Update displayed name, unless it's already current
//        if (! (txt_name.getName().equals(pv.getName()))
//            txt_name.add(pv);

        // Reset rest of GUI
        lbl_value.setText("");  //$NON-NLS-1$
        lbl_time.setText("");  //$NON-NLS-1$
        time = null;

        // Close a previous channel
        disposeChannel();

        // Check the name
        if (iPV == null || iPV.getName().equals(""))  //$NON-NLS-1$
        {
            updateStatus(Messages.S_Waiting);
            return false;
        }
//        txt_name.add(iPV);
        if(txt_name.getCombo().indexOf(iPV.getName())<0){
        	if(txt_name.getCombo().getItemCount()>9){
//        		txt_name.remove(txt_name.getElementAt(txt_name.getCombo().getItemCount()-1)); // remove last
        		txt_name.remove(txt_name.getElementAt(0));	// remove first
        	}
        	txt_name.add(iPV);
        }
        txt_name.setSelection(new StructuredSelection(iPV));
        // Create a new channel
        try
        {
            updateStatus(Messages.S_Searching);
            pv = new EPICS_V3_PV(iPV.getName());
            pv.addListener(this);
            pv.start();
        }
        catch (Exception ex)
        {
            Plugin.logException(Messages.S_CreateError, ex);
            updateStatus(Messages.S_CreateError + ex.getMessage());
            return false;
        }
        return true;
    }

    // PVListener
    public void pvDisconnected(PV pv)
    {
        updateStatus(Messages.S_Disconnected);
    }

    // PVListener
    public void pvValueUpdate(PV pv)
    {
        if (Probe.debug)
            Plugin.logInfo("Probe pvValueUpdate: " + pv.getName()); //$NON-NLS-1$
        // We might receive events after the view is already disposed....
        if (lbl_value.isDisposed())
            return;
        try
        {
            Value value = pv.getValue();
            value_txt = ValueUtil.formatValueAndSeverity(value);
            ITimestamp new_time = value.getTime();
            if (time != null)
            {
                double period = new_time.toDouble() - time.toDouble();
                value_period.add(period);
            }
            else
                value_period.reset();
            time = new_time;
            // Perform update in GUI thread.
            Display.getDefault().asyncExec(new Runnable()
            {
                public void run()
                {   // Might run after the view is already disposed...
                    if (lbl_value.isDisposed())
                        return;
                    lbl_value.setText(value_txt);
                    if (time == null)
                        lbl_time.setText(""); //$NON-NLS-1$
                    else
                        lbl_time.setText(time.toString());

                    if (Probe.debug)
                        System.out.println("Probe displays " //$NON-NLS-1$
                                        + lbl_time.getText()
                                        + " " + lbl_value.getText()); //$NON-NLS-1$

                    if (value_period.get() > 0)
                        lbl_status.setText(Messages.S_Period
                                    + period_format.format(value_period.get())
                                    + Messages.S_Seconds);
                    else
                        lbl_status.setText(Messages.S_OK);
                }
            });
        }
        catch (Exception e)
        {
            Plugin.logException("pvValueUpdate error", e); //$NON-NLS-1$
            updateStatus(e.getMessage());
        }
    }

    /** Closes a channel and releases resource */
    private void disposeChannel()
    {
        if (pv != null)
        {
            if (debug)
                System.out.println("Probe: disposeChannel " + pv.getName()); //$NON-NLS-1$
            pv.removeListener(this);
            pv.stop();
            pv = null;
        }
    }

    /** Updates the status bar with given string.
     *  <p>
     *  Thread safe.
     */
    private void updateStatus(final String text)
    {
        if (text != null)
        {   // Make it run in the SWT UI thread
            Display.getDefault().asyncExec(new Runnable()
            {
                public void run()
                {
                    lbl_status.setText(text);
                }
            });
        }
    }

    /**
     * Info button selection handler
     * @param ev
     */
    private void showInfo()
    {
        final String nl = "\n"; //$NON-NLS-1$

        StringBuffer info = new StringBuffer();
        if (pv == null)
        {
            info.append(Messages.S_NotConnected + nl);
        }
        else
        {
            info.append(nl + Messages.S_ChannelInfo + nl);
            info.append(Messages.S_CHANNEL + pv.getName() + nl);
            if (pv.isConnected())
                info.append(Messages.S_STATEConn + nl);
            else
                info.append(Messages.S_STATEDisconn + nl);
        }
        if (info.length() == 0)
            info.append(Messages.S_NoInfo);
        MessageBox box =
            new MessageBox(lbl_value.getShell(), SWT.ICON_INFORMATION);
        box.setText(Messages.S_Info);
        box.setMessage(info.toString());
        box.open();
    }

    /** Interactively adjust the PV's value. */
    private void adjustValue()
    {
        try
        {
            if (pv == null)
            {
                updateStatus(Messages.S_NoChannel);
                return;
            }
            if (!pv.isConnected())
            {
                updateStatus(Messages.S_NotConnected);
                return;
            }
            InputDialog inputDialog =
                    new InputDialog(lbl_value.getShell(),
                        Messages.S_AdjustValue, Messages.S_Value,
                        value_txt, null);
                if (inputDialog.open() == Window.OK)
                    pv.setValue(inputDialog.getValue());
        }
        catch (Exception ex)
        {
            Plugin.logException(Messages.S_AdjustFailed, ex);
            updateStatus(Messages.S_AdjustFailed + ex.getMessage());
        }
    }

    /*****************************************************************************
	 * Make the MB3-ContextMenu
	 *
	 */
	private void makeContextMenu() {
		MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		Control contr = txt_name.getControl();
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
//		final ComboViewer cv = new ComboViewer(txt_name);
		contr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);

				if (e.button == 3) {
			        Object pvs[] = new Object[1];
//					pvs[0] = CentralItemFactory.createProcessVariable(txt_name.getText());
//	                return new StructuredSelection(pvs);
					StructuredSelection s =  new StructuredSelection(pvs);
					Object o = s.getFirstElement();
					if (o instanceof ArrayList) {
						System.out.println("First is"+((ArrayList)o).get(0));

					}

//	                pvs[0] = CentralItemFactory.createProcessVariable(txt_name.getText());
//	                cv.setSelection(new StructuredSelection(pvs));
//	                return new StructuredSelection(pvs);

//					System.out.println("S= "+s);
//					list.getList().setSelection(e.y/list.getList().getItemHeight());
				}
			}
		});
		Menu menu = manager.createContextMenu(contr);
		contr.setMenu(menu);
		getSite().registerContextMenu(manager, txt_name);
	}
}
