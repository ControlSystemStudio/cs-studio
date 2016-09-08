package org.csstudio.alarm.beast.history.views;

import java.beans.PropertyChangeSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.csstudio.alarm.beast.history.views.PeriodicAlarmHistoryQuery.AlarmHistoryResult;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import static org.csstudio.alarm.beast.history.views.AlarmHistoryQueryParameters.AlarmHistoryQueryBuilder.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
/**
 *
 */

public class AlarmHistoryView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.csstudio.alarm.beast.history.views.AlarmHistoryView";

    private TableViewer viewer;

    private Action action1;
    private Action doubleClickAction;

    private AlarmHistoryQueryParameters alarmHistoryQueryParameters;
    private PeriodicAlarmHistoryQuery alarmHistoryQuery;

    // List of all possible columns
    private static final List<String> columnName = Arrays.asList("EVENTTIME", "NAME", "HOST", "TEXT", "VALUE", "USER",
            "CONFIG", "STATUS", "APPLICATION-ID", "CURRENT_STATUS", "CURRENT_SEVERITY", "TYPE", "SEVERITY");
    private List<String> visibleColumns = Arrays.asList("EVENTTIME", "NAME", "TEXT", "USER", "STATUS",
            "SEVERITY");

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);


    /**
     * The constructor.
     */
    public AlarmHistoryView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
        createUI(parent);
        changeSupport.addPropertyChangeListener((evt) -> {
            switch (evt.getPropertyName()) {
            case "alarmHistoryQueryParameters":
                alarmHistoryQuery.setQuery((AlarmHistoryQueryParameters) evt.getNewValue());
                break;
            case "visibleColumns":
                viewer.getTable().dispose();
                createUI(parent);
                if (alarmHistoryQuery != null) {
                    alarmHistoryQuery.setQuery(getAlarmHistoryQueryParameters());
                }
                break;
            default:
                break;
            }
        });
        initialize();
    }

    private void createUI(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        viewer = new TableViewer(parent,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
     // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "org.csstudio.alarm.beast.history.viewer");
        getSite().setSelectionProvider(viewer);
        updateUI(parent);
    }

    private void updateUI(Composite parent) {

        createColumns(parent, viewer);

        final Table table = viewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        viewer.setContentProvider(new ArrayContentProvider());
        parent.layout();
    }
    /*
     * Example of the data coming from the alarm message server
     *
     * {"EVENTTIME":"2016-02-25 17:15:29.000",
     * "HOST":"training",
     * "TEXT":"STATE",
     * "VALUE":"",
     * "USER":"root",
     * "NAME":"XF:31IDA-OP{Tbl-Ax:X1}Mtr",
     * "CONFIG":"Annunciator",
     * "STATUS":"Disconnected",
     * "APPLICATION-ID":"AlarmServer",
     * "CURRENT_STATUS":"No Connection",
     * "CURRENT_SEVERITY":"UNDEFINED",
     * "TYPE":"alarm",
     * "SEVERITY":"UNDEFINED"}
     */

    private void createColumns(Composite parent, TableViewer viewer) {

        for (String columnName : visibleColumns) {
            TableViewerColumn col = createTableViewerColumn(columnName, 150, 0);
            col.setLabelProvider(new ColumnLabelProvider() {
                @Override
                public String getText(Object element) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> p = (Map<String, String>) element;
                    return p.get(columnName);
                }
            });
        }
    }

    private TableViewerColumn createTableViewerColumn(String title, int bound, int j) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        column.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.getSource() instanceof TableColumn) {
                    TableColumn sortingColumn = (TableColumn) e.getSource();
                    if (sortingColumn.getText().equals("EVENTTIME")) {
                        viewer.setComparator(new TimeComparator());
                    } else {
                        viewer.setComparator(new ViewerComparator());
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }
        });
        return viewerColumn;
    }


    private AlarmHistoryQueryListener listener = new AlarmHistoryQueryListener() {

        @Override
        public void queryExecuted(AlarmHistoryResult result) {
            Display.getDefault().asyncExec(() -> {
                if (result.lastException != null) {
                    // Display exception
                } else {
                    viewer.setInput(result.alarmMessages);
                }
            });
        }
    };

    /**
     * create rest client and jobs using preferences
     */
    private void initialize(){
        // Start the Query jobs
        Client client = Client.create();
        String url = Platform.getPreferencesService().
                getString("org.csstudio.alarm.beast.history", "alarm_history_url", "http://localhost:9200/alarms/beast/_search", null);
        WebResource r = client.resource(url);
        alarmHistoryQueryParameters = buildQuery().build();
        alarmHistoryQuery = new PeriodicAlarmHistoryQuery(alarmHistoryQueryParameters, r, 10, TimeUnit.SECONDS);
        alarmHistoryQuery.addQueryListener(listener);
        alarmHistoryQuery.start();
    }

    @Override
    public void dispose(){
        if(alarmHistoryQuery!=null){
            alarmHistoryQuery.removeQueryListener(listener);
            alarmHistoryQuery.stop();
        }
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    public List<String> getVisibleColumns() {
        return this.visibleColumns;
    }

    public void setVisibleColumns(List<String> visibleColumns) {
        Object oldValue = this.visibleColumns;
        this.visibleColumns = visibleColumns;
        changeSupport.firePropertyChange("visibleColumns", oldValue, this.visibleColumns);
    }

    public static List<String> getColumnname() {
        return columnName;
    }

    public AlarmHistoryQueryParameters getAlarmHistoryQueryParameters() {
        return alarmHistoryQueryParameters;
    }

    public void setAlarmHistoryQueryParameters(AlarmHistoryQueryParameters parameters) {
        Object oldValue = this.alarmHistoryQueryParameters;
        this.alarmHistoryQueryParameters = parameters;
        changeSupport.firePropertyChange("alarmHistoryQueryParameters", oldValue, this.alarmHistoryQueryParameters);
    }

    private static class TimeComparator extends ViewerComparator {

        public TimeComparator() {
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            // Default sorting is by Time
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.SSS");
            try {
                Date date1 = simpleDateFormat.parse(((Map<String, String>) e1).get("EVENTTIME"));
                Date date2 = simpleDateFormat.parse(((Map<String, String>) e2).get("EVENTTIME"));
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

}
