package org.csstudio.logbook.olog.property.fault;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.logbook.olog.property.fault.Fault.BeamLossState;
import org.csstudio.logbook.ui.LogQueryListener;
import org.csstudio.logbook.ui.PeriodicLogQuery;
import org.csstudio.logbook.ui.PeriodicLogQuery.LogResult;
import org.csstudio.ui.util.AbstractSelectionProviderWrapper;
import org.csstudio.ui.util.composites.BeanComposite;
import org.diirt.util.time.TimeInterval;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.TableViewerColumnSorter;

public class FaultTableWidget extends BeanComposite implements ISelectionProvider {

    // Model
    private List<Fault> faults;

    private PeriodicLogQuery logQuery;
    private LogbookClient logbookClient;

    private LogQueryListener listener = new LogQueryListener() {

        @Override
        public void queryExecuted(LogResult result) {
            List<Fault> newFaults = new ArrayList<Fault>();
            for (LogEntry log : result.logs) {
                newFaults.add(FaultAdapter.extractFaultFromLogEntry(log));
            }
            setFaults(newFaults);
        }
    };

    // Controls
    private Table table;
    private TableViewer tableViewer;

    private AbstractSelectionProviderWrapper selectionProvider;

    public FaultTableWidget(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 0;
        setLayout(gridLayout);

        tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        tableViewer.setContentProvider(new ArrayContentProvider());

        TableViewerColumn tableViewerColumnArea = new TableViewerColumn(tableViewer, SWT.DOUBLE_BUFFERED);
        new TableViewerColumnSorter(tableViewerColumnArea) {
            @Override
            protected Object getValue(Object o) {
                String area = ((Fault)o).getArea();
                return area == null ? "" : area;
            }
        };
        TableColumn tblclmnAreaColumn = tableViewerColumnArea.getColumn();
        tableViewerColumnArea.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                Fault item = ((Fault) element);
                if (item == null)
                    return "";
                return item.getArea() == null || item.getArea().isEmpty() ? "" : item.getArea();
            }
        });
        tblclmnAreaColumn.setMoveable(true);
        tblclmnAreaColumn.setWidth(100);
        tblclmnAreaColumn.setText("Area");

        TableViewerColumn tableViewerColumnSystem = new TableViewerColumn(tableViewer, SWT.NONE);
        new TableViewerColumnSorter(tableViewerColumnSystem) {

            @Override
            protected Object getValue(Object o) {
                String subsystem = ((Fault) o).getSubsystem();
                return subsystem == null ? "" : subsystem;
            }
        };
        TableColumn tblclmnSystemColumn = tableViewerColumnSystem.getColumn();
        tableViewerColumnSystem.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                Fault item = ((Fault) element);
                if (item == null)
                    return "";
                return item.getSubsystem() == null || item.getSubsystem().isEmpty() ? "" : item.getSubsystem();
            }
        });
        tblclmnSystemColumn.setMoveable(true);
        tblclmnSystemColumn.setWidth(100);
        tblclmnSystemColumn.setText("System");

        TableViewerColumn tableViewerColumnDevice = new TableViewerColumn(tableViewer, SWT.NONE);
        new TableViewerColumnSorter(tableViewerColumnDevice) {
            @Override
            protected Object getValue(Object o) {
                String device = ((Fault)o).getDevice(); 
                return device == null ? "" : device;
            }
        };
        TableColumn tblclmnDeviceColumn = tableViewerColumnDevice.getColumn();
        tableViewerColumnDevice.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                Fault item = ((Fault) element);
                if (item == null)
                    return "";
                return item.getDevice() == null || item.getDevice().isEmpty() ? "" : item.getDevice();
            }
        });
        tblclmnDeviceColumn.setMoveable(true);
        tblclmnDeviceColumn.setWidth(100);
        tblclmnDeviceColumn.setText("Device");

        TableViewerColumn tableViewerColumnFaultStart = new TableViewerColumn(tableViewer, SWT.NONE);
        new TableViewerColumnSorter(tableViewerColumnFaultStart) {
            @Override
            protected Object getValue(Object o) {
                Instant occour = ((Fault)o).getFaultOccuredTime();
                return occour == null ? Instant.MAX : occour;
            }
        };
        TableColumn tblclmnFaultStartColumn = tableViewerColumnFaultStart.getColumn();
        tableViewerColumnFaultStart.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                Fault item = ((Fault) element);
                if (item == null)
                    return "";
                return item.getFaultOccuredTime() != null
                        ? ZonedDateTime.ofInstant(item.getFaultOccuredTime(), ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : "";
            }
        });
        tblclmnFaultStartColumn.setMoveable(true);
        tblclmnFaultStartColumn.setWidth(100);
        tblclmnFaultStartColumn.setText("Fault Start");

        TableViewerColumn tableViewerColumnFaultDuration = new TableViewerColumn(tableViewer, SWT.NONE);
        new TableViewerColumnSorter(tableViewerColumnFaultDuration) {
            @Override
            protected Object getValue(Object o) {
                Fault fault = (Fault)o;
                if (fault.getFaultOccuredTime() != null && fault.getFaultClearedTime()!= null) {
                    return Duration.between(fault.getFaultOccuredTime(), fault.getFaultClearedTime());
                }
                return Duration.ZERO;
            }
        };
        TableColumn tblclmnFaultDurationColumn = tableViewerColumnFaultDuration.getColumn();
        tableViewerColumnFaultDuration.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                Fault item = ((Fault) element);
                if (item == null)
                    return "";
                if (item.getFaultOccuredTime() != null && item.getFaultClearedTime()!= null) {
                    return formatDuration(Duration.between(item.getFaultOccuredTime(), item.getFaultClearedTime()));
                } else {
                    return "";
                }
            }
        });
        tblclmnFaultDurationColumn.setMoveable(true);
        tblclmnFaultDurationColumn.setWidth(100);
        tblclmnFaultDurationColumn.setText("Fault Duration");

        TableViewerColumn tableViewerColumnBeamloss = new TableViewerColumn(tableViewer, SWT.NONE);
        new TableViewerColumnSorter(tableViewerColumnBeamloss) {
            @Override
            protected Object getValue(Object o) {
                BeamLossState state = ((Fault)o).getBeamLossState(); 
                return state == null ? "" : state.toString();
            }
        };
        TableColumn tblclmnBeamLossColumn = tableViewerColumnBeamloss.getColumn();
        tableViewerColumnBeamloss.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                Fault item = ((Fault) element);
                if (item == null)
                    return "";
                return item.getBeamLossState() == null ? "" : item.getBeamLossState().toString();
            }
        });
        tblclmnBeamLossColumn.setMoveable(true);
        tblclmnBeamLossColumn.setWidth(100);
        tblclmnBeamLossColumn.setText("Beamloss");

        TableViewerColumn tableViewerColumnBeamlossStart = new TableViewerColumn(tableViewer, SWT.NONE);
        new TableViewerColumnSorter(tableViewerColumnBeamlossStart) {
            @Override
            protected Object getValue(Object o) {
                Instant t = ((Fault)o).getBeamlostTime();
                return t == null ? Instant.MAX : t;
            }
        };
        TableColumn tblclmnBeamlossStartColumn = tableViewerColumnBeamlossStart.getColumn();
        tableViewerColumnBeamlossStart.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                Fault item = ((Fault) element);
                if (item == null)
                    return "";
                return item.getBeamlostTime() != null
                        ? ZonedDateTime.ofInstant(item.getBeamlostTime(), ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : "";
            }
        });
        tblclmnBeamlossStartColumn.setMoveable(true);
        tblclmnBeamlossStartColumn.setWidth(100);
        tblclmnBeamlossStartColumn.setText("Beamloss Start");

        TableViewerColumn tableViewerColumnBeamlossDuration = new TableViewerColumn(tableViewer, SWT.NONE);
        new TableViewerColumnSorter(tableViewerColumnBeamlossDuration) {
            @Override
            protected Object getValue(Object o) {
                Fault fault = (Fault)o;
                if (fault.getBeamlostTime() != null && fault.getBeamRestoredTime()!= null) {
                    return Duration.between(fault.getBeamlostTime(), fault.getBeamRestoredTime());
                }
                return Duration.ZERO;
            }
        };
        TableColumn tblclmnBeamlossDurationColumn = tableViewerColumnBeamlossDuration.getColumn();
        tableViewerColumnBeamlossDuration.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                Fault item = ((Fault) element);
                if (item == null)
                    return "";
                if (item.getBeamlostTime() != null && item.getBeamRestoredTime() != null) {
                    return formatDuration(Duration.between(item.getBeamlostTime(), item.getBeamRestoredTime()));
                } else {
                    return "";
                }
            }
        });
        tblclmnBeamlossDurationColumn.setMoveable(true);
        tblclmnBeamlossDurationColumn.setWidth(100);
        tblclmnBeamlossDurationColumn.setText("Beamloss Duration");

        TableViewerColumn tableViewerColumnDesc = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnDescColumn = tableViewerColumnDesc.getColumn();
        tableViewerColumnDesc.setLabelProvider(new ColumnLabelProvider() {

            public String getText(Object element) {
                Fault item = ((Fault) element);
                if (item == null)
                    return "";
                return FaultAdapter.createFaultText(item);
            }
        });
        tblclmnDescColumn.setMoveable(true);
        tblclmnDescColumn.setWidth(100);
        tblclmnDescColumn.setText("Desc");

        selectionProvider = new AbstractSelectionProviderWrapper(tableViewer, this) {

            @Override
            protected ISelection transform(IStructuredSelection selection) {
                return selection;
            }

        };
        
        // create the periodic logbook query
        ExecutorService ex = Executors.newFixedThreadPool(1);
        ex.execute(() -> {
            try {
                logbookClient = LogbookClientManager.getLogbookClientFactory().getClient();
                logQuery = new PeriodicLogQuery("property:fault limit:500", logbookClient, 60, TimeUnit.SECONDS);
                logQuery.addLogQueryListener(listener);
                logQuery.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        changeSupport.addPropertyChangeListener(e -> {
            String propertyName = e.getPropertyName();
            List<Fault> filteredFaults;
            switch (propertyName) {
            case "faults":
            case "timeInterval":
                filteredFaults = faults.stream().filter((Fault f) -> {
                    return timeInterval.contains(f.getFaultOccuredTime() != null ? f.getFaultOccuredTime() : Instant.MIN)
                            || timeInterval.contains(f.getFaultClearedTime() != null ? f.getFaultClearedTime() : Instant.MIN)
                            || timeInterval.contains(f.getBeamlostTime() != null ? f.getBeamlostTime() : Instant.MIN)
                            || timeInterval.contains(f.getBeamRestoredTime() != null ? f.getBeamRestoredTime() : Instant.MIN);
                }).collect(Collectors.toList());
                Display.getDefault().asyncExec(() -> {
                    tableViewer.setInput(filteredFaults.toArray());
                });
                break;
            case "query":
                logQuery.setQuery(e.getNewValue().toString()+" property:fault limit:500");
                break;
            default:
                break;
            }
        });

    }

    private String query = "property:fault limit:500";
    private TimeInterval timeInterval = TimeInterval.between(Instant.MIN, Instant.now());

    public void setQuery(String query){
        Object oldValue = this.query;
        this.query = query;
        changeSupport.firePropertyChange("query", oldValue, this.query);
    }

    public String getQueryString(){
        return query;
    }

    public void setFilterTimeInterval(TimeInterval timeInterval) {
        Object oldValue = this.timeInterval;
        this.timeInterval = timeInterval;
        changeSupport.firePropertyChange("timeInterval", oldValue, this.timeInterval);
    }

    protected void setFaults(List<Fault> faults) {
        Object oldValue = this.faults;
        this.faults = faults;
        changeSupport.firePropertyChange("faults", oldValue, this.faults);
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
            "%d:%02d:%02d",
            absSeconds / 3600,
            (absSeconds % 3600) / 60,
            absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionProvider.addSelectionChangedListener(listener);

    }

    @Override
    public ISelection getSelection() {
        return selectionProvider.getSelection();
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionProvider.removeSelectionChangedListener(listener);
    }

    @Override
    public void setSelection(ISelection selection) {
        selectionProvider.setSelection(selection);
    }
    
    @Override
    public void setMenu(Menu menu) {
        super.setMenu(menu);
        table.setMenu(menu);
    }
}
