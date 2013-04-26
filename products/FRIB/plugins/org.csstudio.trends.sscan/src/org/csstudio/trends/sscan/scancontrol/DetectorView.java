package org.csstudio.trends.sscan.scancontrol;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;
import static org.epics.pvmanager.util.TimeDuration.hz;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Table;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.ValueUtil;
import org.csstudio.csdata.ProcessVariable;


public class DetectorView extends Composite {
	private Table detectorTable;
	
	private ProcessVariable PVName;
	private Map<Integer,Object> mapPv = new HashMap<Integer,Object>();
	private TableEditor editor;
	// editing the second column
	private final int EDITABLECOLUMN = 1;
	private final int KEYCOLUMN = 0;
	private String keyColumn;
	private Text newEditor;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DetectorView(Composite parent, int style, String SSCAN_PV_TAG) {
		super(parent, style);
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 23, 257, 2);
		
		Label lblDetectors = new Label(this, SWT.NONE);
		lblDetectors.setText("Detectors");
		lblDetectors.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.BOLD));
		lblDetectors.setBounds(0, 0, 257, 25);
		
		detectorTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		detectorTable.setLinesVisible(true);
		detectorTable.setHeaderVisible(true);
		detectorTable.setBounds(10, 31, 237, 259);
		detectorTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableColumn[] columns = new TableColumn[5];
		TableColumn tableColumn = new TableColumn(detectorTable, SWT.NONE);
		tableColumn.setWidth(54);
		tableColumn.setMoveable(true);
		tableColumn.setText("Detector");
		columns[0] = tableColumn;

		TableColumn tableColumn_1 = new TableColumn(detectorTable, SWT.NONE);
		tableColumn_1.setWidth(86);
		tableColumn_1.setText("Device");
		columns[1] = tableColumn_1;

		TableColumn tableColumn_2 = new TableColumn(detectorTable, SWT.NONE);
		tableColumn_2.setWidth(93);
		tableColumn_2.setText("Reading");
		columns[2] = tableColumn_2;

		TableColumn tableColumn_3 = new TableColumn(detectorTable, SWT.NONE);
		tableColumn_3.setWidth(43);
		tableColumn_3.setText("Units");
		columns[3] = tableColumn_3;

		TableColumn tableColumn_4 = new TableColumn(detectorTable, SWT.NONE);
		tableColumn_4.setWidth(38);
		tableColumn_4.setText("Prec");
		columns[4] = tableColumn_4;

		
		editor = new TableEditor(detectorTable);
		
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		
		detectorTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Control oldEditor = editor.getEditor();
				if (oldEditor != null) oldEditor.dispose();
		
				// Identify the selected row
				TableItem item = (TableItem)e.item;
				if (item == null) return;
				
				// The control that will be the editor must be a child of the Table
				newEditor = new Text(detectorTable, SWT.NONE);
				newEditor.setText(item.getText(EDITABLECOLUMN));
				keyColumn = item.getText(KEYCOLUMN);
				newEditor.addSelectionListener(new SelectionAdapter()  {
					@Override
					public void widgetDefaultSelected(SelectionEvent e){
						Text text = (Text)editor.getEditor();
						//editor.getItem().setText(EDITABLECOLUMN, text.getText());

						PVWriter<Object> pvWriter = PVManager.write(channel(PVName.getName()+".D0"+keyColumn+"PV")).async();		 
						pvWriter.write(text.getText());
						pvWriter.close();
						newEditor.setVisible(false);
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, EDITABLECOLUMN);
			}
		});
		
	}
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	/**
	 * Changes the PV currently displayed by control composite.
	 * 
	 * @param pvName
	 *            the new pv name or null
	 */
	public void setPVName(ProcessVariable pvName) {
		PVReaderListener listener = new PVReaderListener() {
			@Override
			public void pvChanged() {
				if (!detectorTable.isDisposed()) {
					//setLastException(pv.lastException());
					detectorTable.removeAll();
					for (Map.Entry<Integer, Object> entry : mapPv.entrySet()){
						if(entry.getValue()!=null){
							PVReader<Map<String, Object>> pvReader = (PVReader<Map<String, Object>>)entry.getValue();
							Map<String, Object> map = pvReader.getValue();
						 
							if(map != null){
								TableItem item = new TableItem(detectorTable, SWT.NONE);
								Integer detectorNumber = entry.getKey();
							
								Object dPV = map.get("detectorPV");
								Object dUnits = map.get("detectorUnits");
								Object dReading = map.get("detectorReading");
								Object dPrec = map.get("detectorPrec");
							
								int c = 0;
								if(detectorNumber!=null)
									item.setText(c++, detectorNumber.toString());
								if(dPV!=null)
									item.setText(c++, ((VString)dPV).getValue());
								if(dReading!=null)
									item.setText(c++, ValueUtil.numericValueOf(dReading).toString());
								if(dUnits!=null)
									item.setText(c++, ((VString)dUnits).getValue());
								if(dPrec!=null)
									item.setText(c++, ValueUtil.numericValueOf(dPrec).toString());
							}
						}
					}
				}
			}
		};
		mapPv.clear();
		PVReader<Map<String, Object>> pv;
		for(Integer detectorIndex = 1; detectorIndex <= 9; detectorIndex = detectorIndex + 1) {
			pv = PVManager.read(mapOf(latestValueOf(channel(pvName.getName()+".D0"+detectorIndex.toString()+"PR").as("detectorPrec")
					.and(channel(pvName.getName()+".D0"+detectorIndex.toString()+"CV").as("detectorReading"))
					.and(channel(pvName.getName()+".D0"+detectorIndex.toString()+"EU").as("detectorUnits"))
					.and(channel(pvName.getName()+".D0"+detectorIndex.toString()+"PV").as("detectorPV")))))
					.notifyOn(swtThread())
					.every(hz(50));
			
			pv.addPVReaderListener(listener);
			mapPv.put(detectorIndex, pv);
		}
		
		this.PVName=pvName;
	}
	
	/**
	 * Returns the currently displayed PV.
	 * 
	 * @return pv name or null
	 */
	public ProcessVariable getPVName() {
		return this.PVName;
	}
}
