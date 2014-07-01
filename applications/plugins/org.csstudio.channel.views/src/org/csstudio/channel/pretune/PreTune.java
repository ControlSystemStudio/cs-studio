/**
 * 
 */
package org.csstudio.channel.pretune;

import static org.epics.pvmanager.ExpressionLanguage.channels;
import static org.epics.pvmanager.ExpressionLanguage.mapOf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.csstudio.graphene.MultiAxisLineGraph2DWidget;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.VTableDisplay;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.epics.graphene.InterpolationScheme;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.formula.ExpressionLanguage;
import org.epics.util.array.ListNumber;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.VTable;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @author Kunal Shroff
 * 
 */
public class PreTune extends ViewPart {
    
    @SuppressWarnings("unused")
    private static final String ID = "org.csstudio.channel.pretune";

    private Label label;

    private Composite composite;
    private Text text;
    private VTableDisplay tableDisplay;
    private ErrorBar errorBar;
    private Button btnLogConfig;
    private Button btnFileBrowse;

    private MultiAxisLineGraph2DWidget widget;

    public PreTune() {
    }

    @Override
    public void createPartControl(Composite parent) {
	parent.setLayout(new FormLayout());
	
	label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
	FormData fd_label = new FormData();
	fd_label.top = new FormAttachment(50);
	fd_label.left = new FormAttachment(0);
	fd_label.right = new FormAttachment(100);
	label.setLayoutData(fd_label);
	label.addMouseMoveListener(new MouseMoveListener() {
	    // TODO add upper and lower bounds
	    public void mouseMove(MouseEvent e) {
		FormData fd = (FormData) label.getLayoutData();
		int calNumerator = (int) (fd.top.numerator + (e.y * 100)
			/ e.display.getActiveShell().getClientArea().height);
		fd.top = new FormAttachment(calNumerator <= 100 ? calNumerator
			: 100, fd.top.offset);
		label.setLayoutData(fd);
		label.getParent().layout();
	    }
	});
	label.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZENS));
	
	composite = new Composite(parent, SWT.NONE);
	FormData fd_composite = new FormData();
	fd_composite.bottom = new FormAttachment(label);
	fd_composite.right = new FormAttachment(100);
	fd_composite.top = new FormAttachment(0);
	fd_composite.left = new FormAttachment(0);
	composite.setLayoutData(fd_composite);
	composite.setLayout(new GridLayout(4, false));

	errorBar = new ErrorBar(composite, SWT.NONE);
	errorBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
	errorBar.setMarginBottom(5);
	
	Label lblNewLabel = new Label(composite, SWT.NONE);
	lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblNewLabel.setText("Config File:");

	text = new Text(composite, SWT.BORDER);
	text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	btnFileBrowse = new Button(composite, SWT.NONE);
	btnFileBrowse.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		FileDialog dialog = new FileDialog(getSite().getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String [] {"*.json", "*.JSON"});
		dialog.setFilterPath(System.getProperty("user.dir"));
		String result = dialog.open();
		text.setText(result);
	    }
	});
	btnFileBrowse.setText("Browse");

	btnLogConfig = new Button(composite, SWT.NONE);
	btnLogConfig.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// load the json file
		try {
		    model = new PreTuneModel(text.getText());		    
		} catch (Exception ex) {
		    errorBar.setException(ex);		    
		} finally {
		    reconnect();
		}
	    }
	});
	btnLogConfig.setText("Load");
	tableDisplay = new VTableDisplay(composite);
	tableDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
	
	Label lblNewLabel_1 = new Label(composite, SWT.NONE);
	lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	lblNewLabel_1.setText("Formula for calculating Set Point:");
	
	formulaCombo = new Combo(composite, SWT.BORDER);
	formulaCombo.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
		    String newFormula =formulaCombo.getText(); 
		    formulaStrings.add(newFormula);		
		    formulaCombo.setItems(formulaStrings.toArray(new String[formulaStrings.size()]));
		    formulaCombo.select(formulaStrings.indexOf(newFormula));
		    reconnect();
		}
	    }
	});
	formulaCombo.setToolTipText("use the regular pv manager formulas\r\ne.g. ${SP} + (${SP} * ${Weight})\r\nThe ${SP} will be replaced with the set point pv assocaited with the channel\r\nThe ${Weight} will be replaced with the weight of that channel");
	formulaCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	formulaStrings.add("${SP} + (${SP} * ${Weight})");
	formulaStrings.add("${SP} + (${SP} / ${Weight})");
	formulaStrings.add("${SP} + (${StepSize} * ${Weight})");
	formulaStrings.add("${SP} - (${StepSize} * ${Weight})");
	    
	
	formulaCombo.setItems(formulaStrings.toArray(new String[formulaStrings.size()]));
	formulaCombo.select(0);
	
	Button btnNewButton = new Button(composite, SWT.NONE);
	btnNewButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// write to the list of pv's
		Map<String, Object> values = new HashMap<String, Object>();
		VTable table = tableDisplay.getVTable();
		List<String> names = new ArrayList<String>();
		ListNumber writeValues = null;
		for (int i = 0; i < table.getColumnCount(); i++) {
		    if(table.getColumnName(i).equalsIgnoreCase(SetPointPVLabel + "_name")){
			names = (List<String>) table.getColumnData(i);
		    }
		    else if (table.getColumnName(i).equalsIgnoreCase("weighted_setpoints")){
			writeValues = ((ListNumber) table.getColumnData(i));			
		    }
		}
		if(writeValues != null && names.size() == writeValues.size()){
		    for (int j = 0; j < names.size(); j++) {
			values.put(names.get(j), writeValues.getDouble(j));
		    }
		    if(pvWriter != null){
			pvWriter.write(values);
		    }
		}
	    }
	});
	btnNewButton.setText("Write");
	
	Composite composite_multi_line = new Composite(parent, SWT.NONE);
	composite_multi_line.setLayout(new FormLayout());
	FormData fd_composite_multi_line = new FormData();
	fd_composite_multi_line.right = new FormAttachment(100);
	fd_composite_multi_line.left = new FormAttachment(0);
	fd_composite_multi_line.bottom = new FormAttachment(100);
	fd_composite_multi_line.top = new FormAttachment(label);
	composite_multi_line.setLayoutData(fd_composite_multi_line);
	
	widget = new MultiAxisLineGraph2DWidget(composite_multi_line, SWT.None);
	FormData fd_widget = new FormData();
	fd_widget.top = new FormAttachment(0);
	fd_widget.bottom = new FormAttachment(100);
	fd_widget.left = new FormAttachment(0);
	fd_widget.right = new FormAttachment(100);
	widget.setLayoutData(fd_widget);
    }

    // predefined special column names to parse 
    public static final String SetPointPVLabel = "pvsp";
    public static final String ReadbackPointPVLabel = "pvrb";
    public static final String WeightLabel = "config_weight";    
    // Potential Formula
    List<String> formulaStrings = new ArrayList<String>(); 

    // Configuration read from the json config file
    private PreTuneModel model;

    // Pv's associated with this View
    private PVReader<?> pv;     
    private PVWriter<Map<String, Object>> pvWriter;
    private Combo formulaCombo;
    
    List<String> columns = new ArrayList<String>();
    List<String> plotColumns = new ArrayList<String>();

    List<String> setPointPvNames = new ArrayList<String>();

    List<String> readBackPvNames = new ArrayList<String>();

    ArrayList<Double> weights = new ArrayList<Double>();
    List<String> weightedSetPointPvNames = new ArrayList<String>();
    Map<String, Double> values = new HashMap<String, Double>();
    
    private void reconnect(){
	if (pv != null) {
	    pv.close();
	    pv = null;
	}
	if (pvWriter != null){
	    pvWriter.close();
	    pvWriter = null;
	}
	// Build a Formula from the config file, user formula, ....
	StringBuilder pvFormula = new StringBuilder();
	pvFormula.append("=");
	StringBuilder fileTableFormula = new StringBuilder();
	fileTableFormula.append("tableOf(");
	
	columns = new ArrayList<String>();
	plotColumns = new ArrayList<String>();
	
	setPointPvNames = new ArrayList<String>();
	
	readBackPvNames = new ArrayList<String>();
	
        weights = new ArrayList<Double>();
        weightedSetPointPvNames = new ArrayList<String>();
        
	String setPointFormula = formulaCombo.getText();
	
	for (String columnHeader : model.columnHeaders) {	   
	    int index;
	    StringBuilder columnFormula = new StringBuilder();
	    switch (columnHeader) {
	    case SetPointPVLabel:
		index = model.getColumnHeaders().indexOf(columnHeader);
		for (List<Object> channel : model.channels) {
		    setPointPvNames.add((String) channel.get(index));
		}		
		// Create a column to display the pv names
		columnFormula.append("column(\"" + columnHeader + "_name\"");
		columnFormula.append(",");
		columnFormula.append("arrayOf(");
		columnFormula.append(Joiner.on(",").join(
			Lists.transform(setPointPvNames,
				new Function<String, String>() {

				    @Override
				    public String apply(String setPointPv) {
					return "\"" + setPointPv + "\"";
				    }
				})));
		columnFormula.append(")");
		columnFormula.append(")");
		columns.add(columnFormula.toString());
		// Create a column for active PVs
		columnFormula = new StringBuilder();
		columnFormula.append("column(\"" + columnHeader + "\"");
		columnFormula.append(",");
		columnFormula.append("arrayOf(");
		// 
		columnFormula.append(Joiner.on(",").join(
			Lists.transform(setPointPvNames,
				new Function<String, String>() {

				    @Override
				    public String apply(String setPointPv) {
					return "'" + setPointPv + "'";
				    }
				})));
		columnFormula.append(")");
		columnFormula.append(")");
		columns.add(columnFormula.toString());	
		plotColumns.add(columnFormula.toString());	
		break;
	    case ReadbackPointPVLabel:	
		index = model.getColumnHeaders().indexOf(columnHeader);
		for (List<Object> channel : model.channels) {
		    readBackPvNames.add((String) channel.get(index));
		}		
		columnFormula.append("column(\"" + columnHeader + "_name\"");
		columnFormula.append(",");
		columnFormula.append("arrayOf(");
		columnFormula.append(Joiner.on(",").join(
			Lists.transform(readBackPvNames,
				new Function<String, String>() {

				    @Override
				    public String apply(String input) {
					return "\"" + input + "\"";
				    }
				})));
		columnFormula.append(")");
		columnFormula.append(")");
		columns.add(columnFormula.toString());
		// Create a active PV array
		columnFormula = new StringBuilder();
		columnFormula.append("column(\"" + columnHeader + "\"");
		columnFormula.append(",");
		columnFormula.append("arrayOf(");		
		columnFormula.append(Joiner.on(",").join(
			Lists.transform(readBackPvNames,
				new Function<String, String>() {

				    @Override
				    public String apply(String input) {
					return "'" + input + "'";
				    }
				})));
		columnFormula.append(")");
		columnFormula.append(")");
		columns.add(columnFormula.toString());
		plotColumns.add(columnFormula.toString());
		break;
	    case WeightLabel:	
		index = model.getColumnHeaders().indexOf(columnHeader);
		weights = new ArrayList<Double>();
		for (List<Object> channel : model.channels) {
		    weights.add((Double)channel.get(index));
		}		
		columnFormula.append("column(\"" + columnHeader + "\"");
		columnFormula.append(",");
		columnFormula.append("arrayOf(");		
		columnFormula.append(Joiner.on(",").join(weights));
		columnFormula.append(")");
		columnFormula.append(")");
		columns.add(columnFormula.toString());
		break;
	    default:
		index = model.getColumnHeaders().indexOf(columnHeader);
		List<String> values = new ArrayList<String>();
		for (List<Object> channel : model.channels) {
		    values.add("\"" + channel.get(index) + "\"");
		}
		// Create a active PV array
		columnFormula.append("column(\"" + columnHeader + "\"");
		columnFormula.append(",");
		columnFormula.append("arrayOf(");		
		columnFormula.append(Joiner.on(",").join(values));
		columnFormula.append(")");
		columnFormula.append(")");
		columns.add(columnFormula.toString());
		break;
	    }
	}
	// Attach a column with the calculated set points
	if (setPointPvNames.size() == weights.size()) {
	    for (String pv : setPointPvNames) {
		String formula = setPointFormula;
		formula = formula.replaceAll("\\$\\{SP\\}", "'"+pv+"'");
		formula = formula.replaceAll("\\$\\{Weight\\}", String.valueOf(weights.get(setPointPvNames.indexOf(pv))));
		weightedSetPointPvNames.add(formula);
	    }
	}
	StringBuilder columnFormula = new StringBuilder();
	columnFormula.append("column(\"weighted_setpoints\"");
	columnFormula.append(",");
	columnFormula.append("arrayOf(");		
	columnFormula.append(Joiner.on(",").join(weightedSetPointPvNames));
	columnFormula.append(")");
	columnFormula.append(")");
	columns.add(columnFormula.toString());
	
	
	fileTableFormula.append(Joiner.on(",").join(columns));
	fileTableFormula.append(")");
		
	pvFormula.append(fileTableFormula.toString());
	
	System.out.println(pvFormula.toString());
	pv = PVManager.read(ExpressionLanguage.formula(pvFormula.toString(), VTable.class))
		.notifyOn(SWTUtil.swtThread(this))
		.readListener(new PVReaderListener<Object>() {

			@Override
			public void pvChanged(final PVReaderEvent<Object> event) {
				errorBar.setException(event.getPvReader().lastException());
				Object value = event.getPvReader().getValue();
				tableDisplay.setVTable((VTable) value);
			}
		}).maxRate(TimeDuration.ofHertz(10));
	pvWriter = PVManager
		.write(mapOf(channels(setPointPvNames)))
		.writeListener(new PVWriterListener<Map<String, Object>>() {

		    @Override
		    public void pvChanged(
			    final PVWriterEvent<Map<String, Object>> event) {
			if (event.isExceptionChanged()) {
			    Display.getCurrent().asyncExec(new Runnable() {

				@Override
				public void run() {
				    errorBar.setException(event.getPvWriter()
					    .lastWriteException());
				}
			    });
			}
		    }
		}).async();
	// Create the PV for the multi line plot
	StringBuilder dataFormula = new StringBuilder();
	dataFormula.append("=tableOf(");
	dataFormula.append(Joiner.on(",").join(plotColumns));
	dataFormula.append(")");
	
	widget.setDataFormula(dataFormula.toString());
	widget.setInterpolation(InterpolationScheme.CUBIC);
	widget.setConfigurable(true);
    }
    
    @Override
    public void setFocus() {
	// TODO Auto-generated method stub

    }
    
     @Override
     public void dispose() {
	if (pv != null) {
	    pv.close();
	}
	if (pvWriter != null){
	    pvWriter.close();
	}
     };

    /**
     * 
     * @author Kunal Shroff
     *
     */
    public static class PreTuneModel {
	
	private final List<String> columnHeaders;
	private final List<List<Object>> channels;	

	@SuppressWarnings("unchecked")
	public PreTuneModel(String filePath) throws JsonParseException, JsonMappingException, IOException {
	    ObjectMapper mapper = new ObjectMapper();
	    Map<String, Object> map = mapper.readValue(new File(filePath), Map.class);
	    columnHeaders = (List<String>) map.get("column_names");
	    channels = (List<List<Object>>) map.get("channels");
	}
	
	public List<String> getColumnHeaders() {
	    return columnHeaders;
	}

	public List<List<Object>> getChannels() {
	    return channels;
	}
	
    }
}
