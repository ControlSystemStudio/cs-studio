package org.csstudio.shift.ui;

import static org.csstudio.shift.util.ShiftSearchUtil.SEARCH_KEYWORD_END;
import static org.csstudio.shift.util.ShiftSearchUtil.SEARCH_KEYWORD_START;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.autocomplete.ui.AutoCompleteWidget;
import org.csstudio.shift.ShiftClientManager;
import org.csstudio.shift.util.ShiftSearchUtil;
import org.csstudio.ui.util.PopupMenuUtil;
import org.csstudio.ui.util.dialogs.StringListSelectionDialog;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

import gov.bnl.shiftClient.Shift;
import gov.bnl.shiftClient.ShiftClient;
import gov.bnl.shiftClient.Type;
/**
 * A view to search for shifts and then display them in a tabluar form
 *
 *
 */
public class ShiftTableView extends ViewPart {
    private Text text;
    private ShiftTable shiftTable;

    private Button configureButton;

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
        GridLayout gl_parent = new GridLayout(4, false);
        gl_parent.marginWidth = 1;
        gl_parent.marginHeight = 1;
        gl_parent.horizontalSpacing = 1;
        parent.setLayout(gl_parent);

        final Label lbShiftQuery = new Label(parent, SWT.NONE);
        lbShiftQuery.setText("Shift Query:");

        text = new Text(parent, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    search();
                }
            }
        });

        // Add AutoComplete support, use type shiftSearch
        new AutoCompleteWidget(text, "ShiftSearch");

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
                            if (types.isEmpty() && initializeClient()) {
                                types = new ArrayList<String>();
                                for (Type type : shiftClient.listTypes()) {
                                    types.add(type.getName());
                                }
                            }
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                    final ShiftSearchDialog dialog = new ShiftSearchDialog(
                                            parent.getShell(), shifts, types,
                                            ShiftSearchUtil
                                                    .parseSearchString(text
                                                            .getText()));
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
        btnNewButton.setText("Adv Search");

        configureButton = new Button(parent, SWT.NONE);
        configureButton.setToolTipText("Configure");
        configureButton.setImage(ResourceManager.getPluginImage(
                "org.csstudio.channel.widgets", "icons/gear-16.png"));
        configureButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Open a dialog which allows users to select logbooks
                StringListSelectionDialog dialog = new StringListSelectionDialog(
                        parent.getShell(), shiftTable.getColumns(), shiftTable
                                .getVisibleColumns(),
                        "Select Columns to Display");
                if (dialog.open() == IDialogConstants.OK_ID) {
                    shiftTable.setVisibleColumns(dialog.getSelectedValues());
                }
            }
        });

        label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
        label.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZENS));

        shiftTable = new ShiftTable(parent, SWT.NONE | SWT.SINGLE);
        GridLayout gridLayout = (GridLayout) shiftTable.getLayout();
        gridLayout.numColumns = 4;
        shiftTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
        shiftTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(final MouseEvent evt) {
                final IHandlerService handlerService = (IHandlerService) getSite()
                        .getService(IHandlerService.class);
                try {
                    handlerService.executeCommand(OpenShiftViewer.ID, null);
                } catch (Exception ex) {
                    throw new RuntimeException("start.command not found");
                    // Give message
                }
            }
        });

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

    private List<Shift> findShiftsBySearch(final String searchString) throws Exception {
        final Map<String, String> searchParameters = ShiftSearchUtil
                .parseSearchString(searchString);
        if (searchParameters.containsKey(SEARCH_KEYWORD_START)) {
            // Check if both start and end are specified.
            StartEndTimeParser startEndTimeParser;
            if (searchParameters.containsKey(SEARCH_KEYWORD_END)) {
                startEndTimeParser = new StartEndTimeParser(
                        searchParameters.get(SEARCH_KEYWORD_START),
                        searchParameters.get(SEARCH_KEYWORD_END));
                searchParameters.remove(SEARCH_KEYWORD_END);
            } else {
                startEndTimeParser = new StartEndTimeParser(
                        searchParameters.get(SEARCH_KEYWORD_START), "now");
            }
            searchParameters.remove(SEARCH_KEYWORD_START);
            if (startEndTimeParser != null && startEndTimeParser.getStart() != null
                    && startEndTimeParser.getEnd() != null) {
                searchParameters.put("from", String.valueOf(startEndTimeParser
                        .getStart().toInstant().getEpochSecond()));
                searchParameters.put("to", String.valueOf(startEndTimeParser
                        .getEnd().toInstant().getEpochSecond()));
            }
        }
        return new ArrayList<Shift>(shiftClient.findShifts(searchParameters));
    }

}
