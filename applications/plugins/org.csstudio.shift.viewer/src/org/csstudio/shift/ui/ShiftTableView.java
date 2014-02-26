package org.csstudio.shift.ui;


import gov.bnl.shiftClient.Shift;
import gov.bnl.shiftClient.ShiftClient;
import gov.bnl.shiftClient.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.csstudio.autocomplete.ui.AutoCompleteWidget;
import org.csstudio.shift.ShiftClientManager;
import org.csstudio.shift.util.ShiftSearchUtil;
import org.csstudio.ui.util.PopupMenuUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.epics.util.time.TimeInterval;
import org.epics.util.time.TimeParser;

/**
 * A view to search for shifts and then display them in a tabluar form
 * 
 * 
 */
public class ShiftTableView extends ViewPart {
    private Text text;
    private ShiftTable shiftTable;

    /** View ID defined in plugin.xml */
    public static final String ID = "org.csstudio.shift.ui.ShiftTableView"; //$NON-NLS-1$

    private ShiftClient shiftClient;
    private Label label;

    private List<String> shifts = Collections.emptyList();
    private List<String> types = Collections.emptyList();

    public ShiftTableView() {
    }

    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new FormLayout());

        final Label lbShiftQuery = new Label(parent, SWT.NONE);
        final FormData fd_lbshiftQuery = new FormData();
        fd_lbshiftQuery.top = new FormAttachment(0, 5);
        lbShiftQuery.setLayoutData(fd_lbshiftQuery);
        lbShiftQuery.setText("Shift Query:");

        final Button btnNewButton = new Button(parent, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
            	final Runnable openSearchDialog = new Runnable() {

	                @Override
	                public void run() {
	                try {
	                    if (shifts.isEmpty() && initializeClient()) {
	                        shifts = new ArrayList<String>();
	                        for (Shift shift : shiftClient.listShifts()) {
	                            shifts.add(shift.getId().toString());
	                        }
	                    }
	                    if(types.isEmpty() && initializeClient()) {
	                    	types = new ArrayList<String>();
	                    	for(Type type : shiftClient.listTypes()) {
	                    		types.add(type.getName());
	                    	}
	                    }
	                    Display.getDefault().asyncExec(new Runnable() {
	                        public void run() {
	                            final ShiftSearchDialog dialog = new ShiftSearchDialog(parent.getShell(), shifts, types, 
	                            		ShiftSearchUtil.parseSearchString(text.getText()));
	                            dialog.setBlockOnOpen(true);
	                            if (dialog.open() == IDialogConstants.OK_ID) {
		                            text.setText(dialog.getSearchString());
		                            text.getParent().update();
		                            search();
	                            }
	                        }
	                    });
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                }
	            };
	            BusyIndicator.showWhile(Display.getDefault(), openSearchDialog);
            }
        });
        final FormData fd_btnNewButton = new FormData();
        fd_btnNewButton.top = new FormAttachment(0, 3);
        fd_btnNewButton.right = new FormAttachment(100, -5);
        btnNewButton.setLayoutData(fd_btnNewButton);
        btnNewButton.setText("Adv Search");

        text = new Text(parent, SWT.BORDER);
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
	            if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
	                search();
	            }
            }
        });
        final FormData fd_text = new FormData();
        fd_text.right = new FormAttachment(btnNewButton, -5);
        fd_text.left = new FormAttachment(lbShiftQuery, 5);
        fd_text.top = new FormAttachment(0, 5);
        text.setLayoutData(fd_text);

        // Add AutoComplete support, use type shiftSearch
        new AutoCompleteWidget(text, "ShiftSearch");

        label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(final MouseEvent e) {
            	final FormData fd = (FormData) label.getLayoutData();
                final long calNumerator = fd.top.numerator + (e.y * 100) / e.display.getActiveShell().getClientArea().height;
                fd.top = new FormAttachment((int) calNumerator);
                label.setLayoutData(fd);
                label.getParent().layout();
                shiftTable.layout();
            }
        });
        label.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZENS));
        final FormData fd_label = new FormData();
        fd_label.top = new FormAttachment(100);
        fd_label.right = new FormAttachment(100, -2);
        fd_label.left = new FormAttachment(0, 2);
        label.setLayoutData(fd_label);

        shiftTable = new ShiftTable(parent,SWT.NONE | SWT.SINGLE);
        shiftTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(final MouseEvent evt) {
                final IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
                try {
                    handlerService.executeCommand(OpenShiftViewer.ID, null);
                } catch (Exception ex) {
                    throw new RuntimeException("start.command not found");
                    // Give message
                }
            }
        });
        fd_lbshiftQuery.left = new FormAttachment(shiftTable, 0, SWT.LEFT);
        final FormData fd_shiftEntryTable = new FormData();
        fd_shiftEntryTable.top = new FormAttachment(text, 5);
        fd_shiftEntryTable.right = new FormAttachment(100, -3);
        fd_shiftEntryTable.left = new FormAttachment(0, 3);
        fd_shiftEntryTable.bottom = new FormAttachment(label, -5);

        shiftTable.setLayoutData(fd_shiftEntryTable);

        PopupMenuUtil.installPopupForView(shiftTable, getSite(), shiftTable);
        initializeClient();
    }

    private boolean initializeClient() {
        if (shiftClient == null) {
            try {
            shiftClient = ShiftClientManager.getShiftClientFactory().getClient();
            return true;
            } catch (Exception e1) {
            e1.printStackTrace();
            return false;
            }
        } else {
            return true;
        }
    }

    private void search() {
        final String searchString = text.getText();
        final Job search = new Job("Searching") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
            if (initializeClient()) {
                try {
                    final List<Shift> shiftsToDisplay = findShiftsBySearch(searchString);
                    Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        shiftTable.setShifts(shiftsToDisplay);
                    }
                });
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return Status.OK_STATUS;
            }
        };
        search.schedule();
    }

    @Override
    public void setFocus() {
    }
    
    private List<Shift> findShiftsBySearch(final String searchString) {
    	final Map<String, String> searchParameters = ShiftSearchUtil.parseSearchString(searchString);
	    if(searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_START) || searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_END)) {
		    TimeInterval timeInterval = null;
			if (searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_START) && searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_END)) {
				timeInterval = TimeParser.getTimeInterval(searchParameters.get(ShiftSearchUtil.SEARCH_KEYWORD_START),
						searchParameters.get(ShiftSearchUtil.SEARCH_KEYWORD_END));
			    searchParameters.put("from", String.valueOf(timeInterval.getStart().getSec()));
			    searchParameters.put("to", String.valueOf(timeInterval.getEnd().getSec()));
			} else if (searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_START)) {
				timeInterval = TimeParser.getTimeInterval(searchParameters.get(ShiftSearchUtil.SEARCH_KEYWORD_START), "now");
			    searchParameters.put("from", String.valueOf(timeInterval.getStart().getSec()));
			    searchParameters.put("to", String.valueOf(timeInterval.getEnd().getSec()));
			} else if (searchParameters.containsKey(ShiftSearchUtil.SEARCH_KEYWORD_END)) {
				timeInterval = TimeParser.getTimeInterval("now", searchParameters.get(ShiftSearchUtil.SEARCH_KEYWORD_END));
			    searchParameters.put("to", String.valueOf(timeInterval.getEnd().getSec()));
			}
		}
	    return new ArrayList<Shift>(shiftClient.findShifts(searchParameters));
    }
}
