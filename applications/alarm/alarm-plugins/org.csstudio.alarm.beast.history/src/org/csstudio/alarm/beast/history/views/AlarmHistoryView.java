package org.csstudio.alarm.beast.history.views;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.csstudio.alarm.beast.history.views.AlarmHistoryQueryParameters.AlarmHistoryQueryBuilder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * 
 * <p>
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
        viewer = new TableViewer(parent,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        updateUI(parent);
        // Create the help context id for the viewer's control
        PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "org.csstudio.alarm.beast.history.viewer");
        getSite().setSelectionProvider(viewer);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        changeSupport.addPropertyChangeListener((evt) -> {
            switch (evt.getPropertyName()) {
            case "alarmHistoryQueryParameters":
                
                break;
            default:
                break;
            }
        });
    }

    private void updateUI(Composite parent) {

        createColumns(parent, viewer);

        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        viewer.setContentProvider(new ArrayContentProvider());
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

    // List of all possible columns
    private static final List<String> columnName = Arrays.asList("EVENTTIME", "NAME", "HOST", "TEXT", "VALUE", "USER",
            "CONFIG", "STATUS", "APPLICATION-ID", "CURRENT_STATUS", "CURRENT_SEVERITY", "TYPE", "SEVERITY");
    private static List<String> visibleColumns = Arrays.asList("EVENTTIME", "NAME", "TEXT", "USER", "STATUS",
            "SEVERITY");

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

    public void setAlarmHistoryQueryParameters(AlarmHistoryQueryParameters parameters) {
        Object oldValue = this.alarmHistoryQueryParameters;
        this.alarmHistoryQueryParameters = alarmHistoryQueryParameters;
        changeSupport.firePropertyChange("alarmHistoryQueryParameters", oldValue, this.alarmHistoryQueryParameters);
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                AlarmHistoryView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(action1);
        manager.add(new Separator());
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(action1);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(action1);
    }

    private void makeActions() {
        action1 = new Action() {
            public void run() {
                Client client = Client.create();
                WebResource r = client.resource("http://130.199.219.79:9999/alarms/beast/_search");
                AlarmHistoryQueryParameters parameter = AlarmHistoryQueryBuilder.buildQuery().build();

                String response = r.accept(MediaType.APPLICATION_JSON).post(String.class, parameter.getQueryString());

                List<Map<String, String>> alarmMessages = new ArrayList<Map<String, String>>();
                try {
                    JsonFactory factory = new JsonFactory();

                    ObjectMapper mapper = new ObjectMapper(factory);
                    JsonNode rootNode = mapper.readTree(response);

                    JsonNode node = rootNode.get("hits").get("hits");
                    for (JsonNode jsonNode : node) {
                        alarmMessages.add(
                                mapper.readValue(jsonNode.get("_source"), new TypeReference<Map<String, String>>() {
                        }));
                    }
                    viewer.setInput(alarmMessages);
                    viewer.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        action1.setText("Action 1");
        action1.setToolTipText("Action 1 tooltip");
        action1.setImageDescriptor(
                PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        doubleClickAction = new Action() {
            public void run() {
                ISelection selection = viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                showMessage("Double-click detected on " + obj.toString());
            }
        };
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    private void showMessage(String message) {
        MessageDialog.openInformation(viewer.getControl().getShell(), "Alarm History View", message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
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
