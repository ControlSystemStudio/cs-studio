package org.csstudio.trends.databrowser.sampleview;

import java.util.ArrayList;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableWithSampleDragSource;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.csstudio.trends.databrowser.model.ModelSample;
import org.csstudio.trends.databrowser.model.ModelSamples;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.csstudio.util.swt.AutoSizeColumn;
import org.csstudio.util.swt.AutoSizeControlListener;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;

/** A View that shows all the current Model Samples in a list.
 *  <p>
 *  TODO: Sort of works, but doesn't refresh automatically when samples
 *        are added, and might have some performance issue:
 *        Especially when switching PVs, or closing after looking
 *        at many samples, Eclipse freezes for a while.
 *  @author Kay Kasemir
 *  @author Last modifications by Helge Rickens
 */
public class SampleView extends PlotAwareView
{
    public static final String ID = SampleView.class.getName();
    private Model model = null;
    private ComboViewer pv_name;
    private TableViewer table_viewer;
    private ModelSamples samples = null;
    protected int index =0;
    /** Create the GUI elements. */
    @Override
    public void createPartControl(Composite parent)
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);
        GridData gd;

        // GUI:
        // Drop-down list of channels [Refresh]
        // Table of samples

        // The drop-down list
        Label l = new Label(parent, 0);
        l.setText(Messages.PVLabel);
        gd = new GridData();
        l.setLayoutData(gd);

        pv_name = new ComboViewer(parent, SWT.DROP_DOWN|SWT.READ_ONLY);
        pv_name.getCombo().setToolTipText(Messages.PV_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        pv_name.getCombo().setLayoutData(gd);
        pv_name.getCombo().setEnabled(false);
        makeContextMenu();
        pv_name.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                selectPV(pv_name.getCombo().getText());
                index = pv_name.getCombo().getSelectionIndex();
            }
        });

        new ProcessVariableWithSampleDragSource(pv_name.getCombo(),pv_name);

        Button refresh = new Button(parent, SWT.PUSH);
        refresh.setText(Messages.Refesh);
        refresh.setToolTipText(Messages.Refresh_TT);
        gd = new GridData();
        refresh.setLayoutData(gd);
        refresh.addSelectionListener(new SelectionAdapter()
        {   @Override
            public void widgetSelected(SelectionEvent e)
            {   selectPV(pv_name.getCombo().getText()); }
        });

        // The table
        Table table = new Table(parent,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
        //Albert VIRTUAL doesn't support sort/filters                        | SWT.VIRTUAL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        table.setLayoutData(gd);
        AutoSizeColumn.make(table, Messages.TimeCol, 80, 100);
        AutoSizeColumn.make(table, Messages.ValueCol, 70, 50);
        AutoSizeColumn.make(table, Messages.InfoCol, 100, 100);
        // Configure table to auto-size the columns
        new AutoSizeControlListener(parent, table);

        table_viewer = new TableViewer(table);
        table_viewer.setLabelProvider(new SampleTableLabelProvider());
        /*Albert non VIRTUAL table doesn't support Lazy!  Use next class:    
          table_viewer.setContentProvider(
                 new SampleTableLazyContentProvider(this, table_viewer));
         */   
        table_viewer.setContentProvider(new IStructuredContentProvider() {
        	public Object[] getElements(Object arg0) {
        		ModelSamples samples= (ModelSamples) arg0;
        	    ArrayList <Object> al = new ArrayList<Object>();
        	    for(int i=0;i<samples.size();i++) al.add(samples.get(i));        	    		
        	    return al.toArray();
        	    } 
        	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
        	public void dispose() {}
        });
         
        
        table_viewer.addFilter(new ViewerFilter () {
        	public boolean select(Viewer viewer, Object parentElement, Object element)
            {
        		if (model == null)
                    return true;
        		ModelSample sample = (ModelSample) element;
            	ITimestamp start = model.getStartTime();        
                ITimestamp end = model.getEndTime();            
            	ITimestamp ts=sample.getSample().getTime();
            	if((ts.isGreaterOrEqual(start))&&(ts.isLessOrEqual(end))) 
                return true;
              else
                return false;
        }});	
        // Invoke PlotAwareView's createPartControl to enable updateModel()
        super.createPartControl(parent);
    }

    /** Set the initial focus. */
    @Override
    public void setFocus()
    {
        table_viewer.getTable().setFocus();
    }

    // @see PlotAwareView
    @Override
    protected void updateModel(Model old_model, Model new_model)
    {
        boolean change = old_model != new_model;
        model = new_model;
        pv_name.getCombo().removeAll();
        if (model == null)
        {
            System.out.println("Set Text "+Messages.NoPlot);
//            pv_name.getCombo().setText(Messages.NoPlot);
//        	pv_name.add("");

            pv_name.getCombo().setEnabled(false);
            selectPV(null);
        }
        else
        {
        	ITimestamp start = model.getStartTime();
            ITimestamp end = model.getEndTime();
        	
            String pvs[] = new String[model.getNumItems()];
            for (int i=0; i<pvs.length; ++i){
//                pvs[i] = model.getItem(i).getName();
                int size = model.getRingSize();
                ModelItem m = (ModelItem)model.getItem(i);
                ModelSamples ms = m.getSamples();
                size = ms.size();
                int poroperCount=0;
                for(int j=0;j<size;j++){
                	ITimestamp ts=ms.get(j).getSample().getTime();
                	if( (ts.isGreaterOrEqual(start) ) && (ts.isLessOrEqual(end) ) ) {
                        if( ( ms.get(j).getSample().getSeverity().toString().indexOf("Disconnected") >=0 )
                        		||(ms.get(j).getSample().getSeverity().toString().indexOf("Archive_Off") >=0 )) {
                            	if (true) System.out.println("!!!DisconnectVal="+ms.get(j).getSample().getSeverity().toString());
                            	continue; 
                            }
                		poroperCount++; 	
                	}
                }
                if (poroperCount < 4) {
                	System.out.println("Empty proper region");
                	return;
                }
                double[] value = new double[poroperCount];
                double[]timeStamp= new double[poroperCount];
                String[]status = new String[poroperCount];
                String[]severity = new String[poroperCount];
                
                poroperCount=0;
                for(int j=0;j<size;j++){
                	ITimestamp ts=ms.get(j).getSample().getTime();
                	if(!( (ts.isGreaterOrEqual(start) ) && (ts.isLessOrEqual(end) ) )) continue; 
                    if( ( ms.get(j).getSample().getSeverity().toString().indexOf("Disconnected") >=0 )
                    		||(ms.get(j).getSample().getSeverity().toString().indexOf("Archive_Off") >=0 )) {
                        	if (true) System.out.println("!!!DisconnectSev="+ms.get(j).getSample().getSeverity().toString());
                        	continue; 
                        }
                	
                    IValue sample = ms.get(j).getSample();
                    value[poroperCount] = ValueUtil.getDouble(sample);
                    severity[poroperCount]=ms.get(j).getSample().getSeverity().toString();
                    status[poroperCount]=ms.get(j).getSample().getStatus();
                    timeStamp[poroperCount]=ms.get(j).getSample().getTime().toDouble();
                    poroperCount++;                    	
                }

                pv_name.add(CentralItemFactory.createProcessVariableWithSample(
                        m.getName(), //pv_Name
                        0,							//dbrType TODO: getDBR from EPICS
                        m.getUnits(),
                        m.getAxisLow(),						//low
                        m.getAxisHigh(),					//hige
                        6,					//Precision TODO: getPrecision from PV or METAData 
                        value,
                        timeStamp,
                        status,
                        severity));
           }
           //            System.out.println("set pvs "+pvs);
//            pv_name.getCombo().setItems(pvs);
            int iCount = table_viewer.getTable().getItemCount();
            if (change || 1>iCount)
            {
                index=0;
                pv_name.getCombo().select(index);
                selectPV(pv_name.getCombo().getText());
                System.out.println("change");
//                pv_name.getCombo().setText(""); //$NON-NLS-1$
//              pv_name.add(""); //$NON-NLS-1$
//                selectPV(null);
            }
            pv_name.getCombo().setEnabled(true);
            pv_name.getCombo().select(index);
        }
    }

    /** A PV name was entered or selected.
     *  <p>
     *  Find it in the model, and display its samples.
     *  @param PV Name or <code>null</code> to reset everything.
     */
    private void selectPV(String name)
    {
        if (name == null)
        {
            table_viewer.setItemCount(0);
            samples = null;
            return;
        }
        int i, N = model.getNumItems();
        IModelItem item;
        for (i=0; i<N; ++i)
        {
            item = model.getItem(i);
            if (item.getName().equals(name))
            {
                samples = item.getSamples();
                synchronized (samples)
                {
                	table_viewer.setInput(samples);
                   //Albert  table_viewer.setItemCount(samples.size());
                    table_viewer.refresh();
                }
                return;
            }
        }
        // Invalid PV name, not in model
        selectPV(null);
    }

    /** Get the samples of the current plot and selected PV.
     *  Remember to synchronize on them!
     *  @return the samples or <code>null</code>.
     */
    ModelSamples getSamples()
    {
        return samples;
    }

    /*****************************************************************************
     * Make the MB3-ContextMenu
     *
     */
    private void makeContextMenu() {
        MenuManager manager = new MenuManager("#PopupMenu");
        Control contr = pv_name.getControl();
        manager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        contr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                super.mouseDown(e);
                if (e.button == 3) {
//					StructuredSelection s =  (StructuredSelection) pv_name.getSelection();
//					pv_name.setSelection(new StructuredSelection(CentralItemFactory.createProcessVariableWithSample("pvws", 1, "egu", 1.1, 2.2, 2,new double[]{3.3,4.4},new double[]{5.5,6.6})));
//					s = new StructuredSelection(CentralItemFactory.createProcessVariableWithSample("pvws", 1, "egu", 1.1, 2.2, 2,new double[]{3.3,4.4},new double[]{5.5,6.6}));
                }
            }
        });
        Menu menu = manager.createContextMenu(contr);
        contr.setMenu(menu);
        getSite().registerContextMenu(manager, pv_name);
    }
}
