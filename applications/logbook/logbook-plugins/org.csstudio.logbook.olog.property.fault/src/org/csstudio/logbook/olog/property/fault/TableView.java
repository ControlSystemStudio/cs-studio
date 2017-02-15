package org.csstudio.logbook.olog.property.fault;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.stream.Collectors;

import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.ui.util.PopupMenuUtil;
import org.diirt.util.time.TimeInterval;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

public class TableView extends ViewPart {

    private FaultTableWidget faultTableWidget;
    private String search;
    private TimeInterval timeInterval;

    private Text text;
    private Text startTime;
    private Text endTime;
    private Button btnNewButton;

    public TableView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout gl_parent = new GridLayout(1, false);
        gl_parent.verticalSpacing = 0;
        gl_parent.marginHeight = 0;
        gl_parent.marginWidth = 0;
        gl_parent.horizontalSpacing = 0;
        parent.setLayout(gl_parent);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout gl_composite = new GridLayout(5, false);
        gl_composite.verticalSpacing = 0;
        gl_composite.marginWidth = 0;
        gl_composite.marginHeight = 0;
        gl_composite.horizontalSpacing = 0;
        composite.setLayout(gl_composite);

        Label lblSearch = new Label(composite, SWT.NONE);
        lblSearch.setText("Search:");
        lblSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        text = new Text(composite, SWT.BORDER);
        text.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                faultTableWidget.setQuery(text.getText());
            }
        });

        text.setToolTipText("\"example search\" fault.Area:Linac");
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        btnNewButton = new Button(composite, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FaultSearchDialog dialog = new FaultSearchDialog(parent.getShell(),
                        FaultConfigurationFactory.getConfiguration());
                dialog.setBlockOnOpen(true);
                if (dialog.open() == Window.OK) {
                    search = dialog.getSearchParameters().entrySet().stream().filter(Objects::nonNull)
                            .map(entry -> {
                                return entry.toString().replace("=", ":");
                            }).collect(Collectors.joining(" "));
                    text.setText(search);
//                    faultTableWidget.setQuery(search);
                }
            }
        });
        btnNewButton.setImage(ResourceManager.getPluginImage("org.csstudio.logbook.olog.property.fault",
                "icons/system-search-4.png"));

        Label lblFilter = new Label(composite, SWT.NONE);
        lblFilter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblFilter.setText("Start:");

        startTime = new Text(composite, SWT.BORDER);
        startTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        startTime.setText(LocalDateTime.now().minusDays(1).toString());
        startTime.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    Instant start = LocalDateTime.parse(startTime.getText()).atZone(ZoneId.systemDefault()).toInstant();
                    Instant end = LocalDateTime.parse(endTime.getText()).atZone(ZoneId.systemDefault()).toInstant();
                    if (start != null && end != null && start.isBefore(end))
                        setTimeInterval(TimeInterval.between(start, start));
                }
            }
        });

        Label lblNewLabel = new Label(composite, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblNewLabel.setText("End:");

        endTime = new Text(composite, SWT.BORDER);
        endTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        endTime.setText(LocalDateTime.now().toString());
        endTime.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    Instant start = LocalDateTime.parse(startTime.getText()).atZone(ZoneId.systemDefault()).toInstant();
                    Instant end = LocalDateTime.parse(endTime.getText()).atZone(ZoneId.systemDefault()).toInstant();
                    if (start != null && end != null && start.isBefore(end))
                        setTimeInterval(TimeInterval.between(start, start));

                }
            }
        });

        Button btnNewButton_2 = new Button(composite, SWT.NONE);
        btnNewButton_2.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StartEndDialog dialog = new StartEndDialog(parent.getShell());
                dialog.setBlockOnOpen(true);
                // Initialize the logbooks and tags
                Display.getDefault().asyncExec(() -> {
                    if (dialog.open() == Window.OK) {
                        startTime.setText(dialog.getStartSpecification());
                        endTime.setText(dialog.getEndSpecification());
                        setTimeInterval(TimeInterval.between(dialog.getStartCalendar().toInstant(),
                                dialog.getEndCalendar().toInstant()));
                    }
                });
            }
        });
        btnNewButton_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnNewButton_2.setText("...");
        faultTableWidget = new FaultTableWidget(composite, SWT.NONE);
        faultTableWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));

        PopupMenuUtil.installPopupForView(faultTableWidget, getSite(), faultTableWidget);
    }

    private void setSearch(String search) {
        // validate
        Display.getDefault().asyncExec(() -> {
            faultTableWidget.setFilterTimeInterval(timeInterval);
        });
    }

    private void setTimeInterval(TimeInterval timeInterval) {
        Display.getDefault().asyncExec(() -> {
            faultTableWidget.setFilterTimeInterval(timeInterval);
        });
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }

}
