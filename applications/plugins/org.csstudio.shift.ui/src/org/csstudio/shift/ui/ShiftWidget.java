package org.csstudio.shift.ui;

import static org.csstudio.shift.ShiftBuilder.shift;
import gov.bnl.shiftClient.Shift;
import gov.bnl.shiftClient.ShiftClient;
import gov.bnl.shiftClient.Type;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.csstudio.shift.ShiftBuilder;
import org.csstudio.shift.ShiftClientManager;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class ShiftWidget extends Composite {

    private boolean editable;
    
    // Model
    private ShiftBuilder shiftBuilder; 

    private ShiftClient shiftClient;
    private List<String> types = Collections.emptyList();

    // UI components
    private Text text;
    private Text textDate;
    private Text leadOperator;
    private Text shiftPersonal;
    private Text report;
    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
            this);

    private Composite composite;
    private ErrorBar errorBar;

    private Label lblNewLabel;
    private Combo type;

    private Label lblShiftPersonal;

    private Label lblLeadOperator;
    private final String defaultText = "";

	private Label lblStatus;

	private Text status;

	private Label lblOwner;

	private Text owner;

	private boolean extraFieldsEditable;
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public ShiftWidget(final Composite parent, int style, final boolean editable, final boolean extraFieldsEditable) {
        super(parent, style);
        
        this.shiftBuilder = ShiftBuilder.withType(defaultText);
                
        this.editable = editable;
        this.extraFieldsEditable = extraFieldsEditable;
        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 2;
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 2;
        gridLayout.horizontalSpacing = 2;
        setLayout(gridLayout);

        errorBar = new ErrorBar(this, SWT.NONE);

        composite = new Composite(this, SWT.NONE | SWT.DOUBLE_BUFFERED);
        
        final GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_composite.heightHint = 638;
        composite.setLayoutData(gd_composite);
        composite.setLayout(new GridLayout(5, false));

        final Label lblDate = new Label(composite, SWT.NONE);
        lblDate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblDate.setText("Start Date:");

        textDate = new Text(composite, SWT.NONE);
        textDate.setEditable(false);
        textDate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));


    	lblNewLabel = new Label(composite, SWT.NONE);
    	lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    	lblNewLabel.setText("Type:");
    	
    	type = new Combo(composite, SWT.READ_ONLY);
    	type.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    	

        lblStatus = new Label(composite, SWT.NONE);
        lblStatus.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblStatus.setText("Status:");
    	
        status = new Text(composite, SWT.NONE);
        status.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));
        status.setEditable(false);
        status.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    status.getParent().layout();
                }
            }
        });
        
        lblOwner = new Label(composite, SWT.NONE);
        lblOwner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblOwner.setText("Owner:");
    	
        owner = new Text(composite, SWT.BORDER);
        owner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
        owner.setEditable(editable);
        owner.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                	owner.getParent().layout();
                }
            }
        });
        
        text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        text.setEditable(extraFieldsEditable);
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    text.getParent().layout();
                }
            }
        });
        
        lblLeadOperator = new Label(composite, SWT.NONE);
        lblLeadOperator.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        lblLeadOperator.setText("Lead Operator:");
        
    	leadOperator = new Text(composite, SWT.BORDER);
    	leadOperator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        leadOperator.setEditable(editable);
        leadOperator.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                	leadOperator.getParent().layout();
                }
            }
        });
        
        lblShiftPersonal = new Label(composite, SWT.NONE);
    	lblShiftPersonal.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    	lblShiftPersonal.setText("Personal on Shift:");
    	
    	shiftPersonal = new Text(composite, SWT.BORDER);
    	shiftPersonal.setEditable(extraFieldsEditable);
    	shiftPersonal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                	shiftPersonal.getParent().layout();
                }
            }
        });    	
    	shiftPersonal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
    	
    	final Label lblReport = new Label(composite, SWT.NONE);
    	lblReport.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    	lblReport.setText("Report:");
    	
    	report = new Text(composite, SWT.BORDER);
    	report.setEditable(extraFieldsEditable);
    	report.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                	report.getParent().layout();
                }
            }
        });
    	report.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
    	
    	final AtomicReference<PropertyChangeEvent> eventRef = new AtomicReference<PropertyChangeEvent>();  		
    	this.addPropertyChangeListener(new PropertyChangeListener() {

	    @Override
	    public void propertyChange(PropertyChangeEvent evt) {
		eventRef.set(evt);
		switch (evt.getPropertyName()) {
		case "shift":
		    getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
			    if (eventRef.getAndSet(null) == null) {
				return;
			    } else {
				updateUI();
			    }
			}
		    });
		    break;
		default:
		    break;
		}
	    }
	});

    	try {
    	    shiftClient = ShiftClientManager.getShiftClientFactory().getClient();
    	} catch (Exception e1) {
    	    setLastException(e1);
    	}
    	final Runnable initialize = new Runnable() {

    	    @Override
    	    public void run() {
    		if (shiftClient != null) {
    		    try {    		    
    			types = new ArrayList<String>();
    			for(Type type : shiftClient.listTypes()) {
    				types.add(type.getName());
    			}
   			
    			getDisplay().asyncExec(new Runnable() {

    			    @Override
    			    public void run() {
    			    	updateUI();
    			    }
    			});
    		    } catch (final Exception e) {
    		    	setLastException(e);
    		    }
    		}
    	    }
    	};
    	Executors.newCachedThreadPool().execute(initialize);
    }
    
    private void updateUI() {
		Shift shift = null;
		try {
		    shift = this.shiftBuilder.build();
		} catch (IOException e1) {
		    setLastException(e1);
		}
		if (shift != null) {
		    // Show the shift
		    text.setText(shift.getDescription() == null ? defaultText : shift.getDescription());
		    owner.setText(shift.getOwner() == null ? defaultText : shift.getOwner());	    
		    leadOperator.setText(shift.getLeadOperator() == null ? defaultText : shift.getLeadOperator());	    
		    shiftPersonal.setText(shift.getOnShiftPersonal() == null ? defaultText : shift.getOnShiftPersonal());	    
		    report.setText(shift.getReport() == null ? defaultText : shift.getReport());
		    status.setText(shift.getStatus() == null ? "New" : shift.getStatus());
		    if(!type.getItems().equals(types)){
		    	type.setItems(types.toArray(new String[types.size()]));
		    }
		    if(types.contains(shift.getType().getName())){
		    	type.select(types.indexOf(shift.getType().getName()));
		    }
		    textDate.setText(DateFormat.getDateInstance().format(
			    shift.getStartDate() == null ? new Date() : shift.getStartDate()));
		} else {
		    if(!type.getItems().equals(types)){
		    	type.setItems(types.toArray(new String[types.size()]));
		    }
		    text.setText(defaultText);
		    shiftPersonal.setText(defaultText);
		    leadOperator.setText(defaultText);
		    textDate.setText(DateFormat.getDateInstance().format(new Date()));
		}
		composite.layout();
    }

    public void setLastException(final Exception exception) {
    	getDisplay().asyncExec(new Runnable() {

		    @Override
		    public void run() {
		    	errorBar.setException(exception);
		    }
    	});
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(final boolean editable) {
        boolean oldValue = this.editable;
        this.editable = editable;
        changeSupport.firePropertyChange("editable", oldValue, this.editable);
    }

    public Shift getShift() throws IOException {
    	this.shiftBuilder.setDescription(text.getText()).setType(type.getText()).setLeadOperator(leadOperator.getText())
			.setOnShiftPersonal(shiftPersonal.getText()).setReport(report.getText())
			.setOwner(owner.getText());
        return this.shiftBuilder.build();
    }

    public void setShift(final Shift shift) {
		try {
		    Shift oldValue = this.shiftBuilder.build();
		    this.shiftBuilder = shift(shift);
		    changeSupport.firePropertyChange("shift", oldValue, shift);
		} catch (IOException e) {
		    setLastException(e);
		}
    }

    public List<String> getTypes() {
        return types;
    }

    public void setShiftTypes(final List<String> types) {
    	final List<String> oldValue = this.types;
        this.types = types;
        changeSupport.firePropertyChange("shiftNames", oldValue, this.types);
    }

}