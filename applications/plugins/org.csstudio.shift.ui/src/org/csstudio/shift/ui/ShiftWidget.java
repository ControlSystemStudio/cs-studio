package org.csstudio.shift.ui;

import static org.csstudio.shift.ShiftBuilder.shift;

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

import org.csstudio.shift.Shift;
import org.csstudio.shift.ShiftBuilder;
import org.csstudio.shift.ShiftClient;
import org.csstudio.shift.ShiftClientManager;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class ShiftWidget extends Composite {

    private boolean editable;
    // SWT.DOWN is collapsed which SWT.UP is expanded
    private boolean expanded = false;

    // Model
    private ShiftChangeset shiftChangeset = new ShiftChangeset();

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
    private final boolean newWindow;

    private Label lblNewLabel;
    private Label label;
    private Combo type;

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public ShiftWidget(final Composite parent, int style,final boolean newWindow,final boolean editable) {
        super(parent, style);
        this.newWindow = newWindow;
        this.editable = editable;
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
        composite.setLayout(new FormLayout());

        label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        final FormData fd_label = new FormData();
        fd_label.left = new FormAttachment(0, 1);
        fd_label.right = new FormAttachment(100, -1);
        if (expanded) {
            fd_label.top = new FormAttachment(70, -28);
        } else {
            fd_label.top = new FormAttachment(70, -28);
        }
        label.setLayoutData(fd_label);
        label.addMouseMoveListener(new MouseMoveListener() {
            // TODO add upper and lower bounds
            public void mouseMove(final MouseEvent e) {
                final FormData fd = (FormData) label.getLayoutData();
                int calNumerator = (int) (fd.top.numerator + (e.y * 100) / e.display.getActiveShell().getClientArea().height);
                fd.top = new FormAttachment(calNumerator <= 100 ? calNumerator : 100, fd.top.offset);
                label.setLayoutData(fd);
                label.getParent().layout();
            }
        });
        label.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZENS));

        final Label lblDate = new Label(composite, SWT.NONE);
        final FormData fd_lblDate = new FormData();
        fd_lblDate.left = new FormAttachment(0, 4);
        lblDate.setLayoutData(fd_lblDate);
        lblDate.setText("Date:");

        textDate = new Text(composite, SWT.NONE);
        textDate.setEditable(false);
        final FormData fd_textDate = new FormData();
        fd_textDate.left = new FormAttachment(lblDate, 6);
        textDate.setLayoutData(fd_textDate);

        text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL);
        text.setEditable(editable);
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {

                try {
                    final ShiftBuilder shiftBuilder = shift(shiftChangeset.getShift()).setDescription(text.getText());
                    shiftChangeset.setShiftBuilder(shiftBuilder);
                } catch (IOException e1) {
                    setLastException(e1);
                }
            }
        });
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    text.getParent().layout();
                }
            }
        });
        
        final Label lblLeadOperator = new Label(composite, SWT.NONE);
        final FormData fd_lblLeadOperator = new FormData();
    	fd_lblLeadOperator.left = new FormAttachment(0, 5);
    	lblLeadOperator.setLayoutData(fd_lblLeadOperator);
    	lblLeadOperator.setText("Lead Operator:");
    	leadOperator = new Text(composite, SWT.BORDER);
        leadOperator.setEditable(editable);
        leadOperator.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {

                try {
                    final ShiftBuilder shiftBuilder = shift(shiftChangeset.getShift()).setLeadOperator(leadOperator.getText());
                    shiftChangeset.setShiftBuilder(shiftBuilder);
                } catch (IOException e1) {
                    setLastException(e1);
                }
            }
        });
        leadOperator.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    text.getParent().layout();
                }
            }
        });
        fd_lblLeadOperator.top = new FormAttachment(leadOperator, 5, SWT.TOP);

        final FormData fd_text = new FormData();
        fd_text.right = new FormAttachment(100, -5);
        fd_text.left = new FormAttachment(0, 5);
        text.setLayoutData(fd_text);
    	type = new Combo(composite, SWT.NONE);
    	fd_text.top = new FormAttachment(type, 6);
    	fd_lblDate.top = new FormAttachment(type, 4, SWT.TOP);
    	fd_textDate.top = new FormAttachment(type, 4, SWT.TOP);
    	FormData fd_combo = new FormData();
    	fd_combo.top = new FormAttachment(0, 5);
    	fd_combo.right = new FormAttachment(100, -5);
    	type.setLayoutData(fd_combo);
    	type.addSelectionListener(new SelectionAdapter() {
    	    
    	    @Override
    	    public void widgetSelected(SelectionEvent e) {

    		try {
    			final ShiftBuilder shiftBuilder = shift(shiftChangeset.getShift()).setType(type.getItem(type.getSelectionIndex()));
    		    shiftChangeset.setShiftBuilder(shiftBuilder);
    		} catch (IOException e1) {
    		    setLastException(e1);
    		}
    	    }
    	});

    	lblNewLabel = new Label(composite, SWT.NONE);
    	final FormData fd_lblNewLabel = new FormData();
    	fd_lblNewLabel.top = new FormAttachment(type, 4, SWT.TOP);
    	fd_lblNewLabel.right = new FormAttachment(type, -5);
    	lblNewLabel.setLayoutData(fd_lblNewLabel);
    	lblNewLabel.setText("Type:");
        fd_text.bottom = new FormAttachment(leadOperator, -4);
        final FormData fd_lblShifts = new FormData();
        fd_lblShifts.top = new FormAttachment(leadOperator, 5, SWT.TOP);
        final FormData fd_shift = new FormData();
        fd_shift.bottom = new FormAttachment(label, -46);
        fd_shift.right = new FormAttachment(SWT.RIGHT, -5);
        fd_shift.left = new FormAttachment(lblLeadOperator, 6);
        leadOperator.setLayoutData(fd_shift);
        final Label lblShiftPersonal = new Label(composite, SWT.NONE);
    	final FormData fd_lbshiftPersonal = new FormData();
    	fd_lbshiftPersonal.left = new FormAttachment(0, 5);
    	fd_lbshiftPersonal.top = new FormAttachment(leadOperator, 10);
    	lblShiftPersonal.setLayoutData(fd_lbshiftPersonal);
    	lblShiftPersonal.setText("Personal on Shift:");
    	shiftPersonal = new Text(composite, SWT.BORDER);
    	shiftPersonal.setEditable(editable);
    	shiftPersonal.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {

                try {
                    final ShiftBuilder shiftBuilder = shift(shiftChangeset.getShift()).setOnShiftPersonal(shiftPersonal.getText());
                    shiftChangeset.setShiftBuilder(shiftBuilder);
                } catch (IOException e1) {
                    setLastException(e1);
                }
            }
        });
    	shiftPersonal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    text.getParent().layout();
                }
            }
        });
    	final FormData personalOnShift = new FormData();
        personalOnShift.top = new FormAttachment(leadOperator, 10);
    	personalOnShift.right = new FormAttachment(leadOperator, 0, SWT.RIGHT);
    	personalOnShift.left = new FormAttachment(lblShiftPersonal, 6);
    	shiftPersonal.setLayoutData(personalOnShift);
    	final Label lblReport = new Label(composite, SWT.NONE);
    	final FormData fd_lbReport = new FormData();
    	fd_lbReport.left = new FormAttachment(0, 5);
    	fd_lbReport.top = new FormAttachment(shiftPersonal, 10);
    	lblReport.setLayoutData(fd_lbReport);
    	lblReport.setText("Report:");
    	report = new Text(composite, SWT.BORDER);
    	report.setEditable(editable);
    	report.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {

                try {
                    final ShiftBuilder shiftBuilder = shift(shiftChangeset.getShift()).setReport(report.getText());
                    shiftChangeset.setShiftBuilder(shiftBuilder);
                } catch (IOException e1) {
                    setLastException(e1);
                }
            }
        });
    	report.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    text.getParent().layout();
                }
            }
        });
    	final FormData reportShift = new FormData();
    	reportShift.top = new FormAttachment(shiftPersonal, 10);
    	reportShift.right = new FormAttachment(shiftPersonal, 0, SWT.RIGHT);
    	reportShift.left = new FormAttachment(lblReport, 6);

    	report.setLayoutData(reportShift);
    	final AtomicReference<PropertyChangeEvent> eventRef = new AtomicReference<PropertyChangeEvent>();  		
    	this.addPropertyChangeListener(new PropertyChangeListener() {

    	    @Override
    	    public void propertyChange(PropertyChangeEvent evt) {
	    		eventRef.set(evt);
	    		switch (evt.getPropertyName()) {
	    		case "expand":
	    			final FormData fd = ((FormData) label.getLayoutData());
	    		    if (expanded) {
	    		    	fd.top = new FormAttachment(70, -28);
	    		    } else {
	    		    	fd.top = new FormAttachment(70, -28);
	    		    }
	    		    label.setLayoutData(fd);
	    		    label.getParent().layout();
	    		    break;
	    		case "shift":
	    		    getDisplay().asyncExec(new Runnable() {

    			@Override
    			public void run() {
    			    if (eventRef.getAndSet(null) == null) {
    				return;
    			    } else {
    				init();
    			    }
    			}
    		    });
    		    break;
    		case "shiftBuilder":
    		    updateUI();
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
    	// Attachment buttons need to be enabled/disabled
    	if (!editable) {
    		//TODO: add here the rest of the stuff
    	} else {
    		//TODO same here
    	}
    	final Runnable initialize = new Runnable() {

    	    @Override
    	    public void run() {
    		if (shiftClient != null) {
    		    try {
    			types = new ArrayList(shiftClient.listTypes());
   			
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

    private void init() {
	try {
	    final Shift shift = this.shiftChangeset.getShift();
	    shiftChangeset.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
			    getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
				    updateUI();
				}
			    });
			}
		    });
	    if (shift != null) {
	    	final ShiftBuilder shiftBuilder = ShiftBuilder.shift(shift);
			// TODO temporary fix, in future releases the attachments will
			// be listed with the logEntry itself
			if (shift.getId() != null && shiftClient != null) {
			    Runnable retriveAttachments = new Runnable() {
				@Override
				public void run() {
				    try {
				    	ShiftBuilder shiftBuilder = ShiftBuilder.shift(shiftChangeset.getShift());		
				    	shiftChangeset.setShiftBuilder(shiftBuilder);
				    } catch (Exception ex) {
	
				    }
				}
			    };
		    	Executors.newCachedThreadPool().execute(retriveAttachments);
			}
			this.shiftChangeset.setShiftBuilder(shiftBuilder);
	    }
	} catch (Exception ex) {
	    // Failed to get a client to the logbook
	    // Display exception and disable editing.
	    setLastException(ex);
	}
    }
    
    private void updateUI() {
	// Dispose the contributed tabs,

	Shift shift = null;
	try {
	    shift = this.shiftChangeset.getShift();
	} catch (IOException e1) {
	    setLastException(e1);
	}
	if (shift != null) {
	    // Show the shift
	    text.setText(shift.getDescription() == null ? "" : shift.getDescription());
	    leadOperator.setText(shift.getLeadOperator() == null ? "" : shift.getLeadOperator());	    
	    shiftPersonal.setText(shift.getOnShiftPersonal() == null ? "" : shift.getOnShiftPersonal());	    
	    report.setText(shift.getReport() == null ? "" : shift.getReport());
	    if(!type.getItems().equals(types)){
	    	type.setItems(types.toArray(new String[types.size()]));
	    }
	    if(types.contains(shift.getType())){
	    	type.select(types.indexOf(shift.getType()));
	    }
	    textDate.setText(DateFormat.getDateInstance().format(
		    shift.getStartDate() == null ? new Date() : shift.getStartDate()));

		setExpanded(true);
	} else {
	    if(!type.getItems().equals(types)){
	    	type.setItems(types.toArray(new String[types.size()]));
	    }
	    text.setText("");
	    shiftPersonal.setText("");
	    leadOperator.setText("");

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
        return this.shiftChangeset.getShift();
    }

    public void setShift(final Shift shift) {

        try {
        	final Shift oldValue = this.shiftChangeset.getShift();
            this.shiftChangeset = new ShiftChangeset(shift);
            changeSupport.firePropertyChange("shift", oldValue, this.shiftChangeset.getShift());
        } catch (IOException e) {
            setLastException(e);
        }

    }

    public List<String> getTypes() {
        return types;
    }

    public void setShiftNames(final List<String> types) {
    	final List<String> oldValue = this.types;
        this.types = types;
        changeSupport.firePropertyChange("shiftNames", oldValue, this.types);
    }

    /**
     * @return the expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * @param expanded
     *            the expanded to set
     */
    public void setExpanded(final boolean expanded) {
        boolean oldValue = this.expanded;
        this.expanded = expanded;
        changeSupport.firePropertyChange("expand", oldValue, this.expanded);
    }
}
