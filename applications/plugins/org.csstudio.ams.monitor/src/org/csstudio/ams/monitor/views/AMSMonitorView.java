
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.ams.monitor.views;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.csstudio.ams.Log;
import org.csstudio.ams.MyRunnable;
import org.csstudio.ams.Utils;
import org.csstudio.ams.dbAccess.configdb.HistoryDAO;
import org.csstudio.ams.dbAccess.configdb.HistoryTObject;
import org.csstudio.ams.gui.CommandButton;
import org.csstudio.ams.gui.VerifyInput;
import org.csstudio.ams.monitor.Messages;
import org.csstudio.ams.monitor.MonitorPlugin;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class AMSMonitorView extends ViewPart implements SelectionListener {
    public static final String ID = AMSMonitorView.class.getName();

    private final ArrayList<HistoryTObject> historyList = new ArrayList<HistoryTObject>();
    private final HistoryLoader autoLoader = new HistoryLoader();

    private final int[] intervals = new int[] { -1, 1000, 2000, 5000, 10000 };

    private int autoLoadCount = 200;
    private int autoLoadInterval = -1;

    private Object lock = new Object();

    public AMSMonitorView() {
        // Nothing to do
    }

    /**
     * Release resources
     */
    @Override
    public void dispose() {
        autoLoader.running = false;
        // Tell the label provider to release its ressources
        tblMonitorViewer.getLabelProvider().dispose();
    }

    @Override
    public void showBusy(boolean busy) {
        if (busy) {
            Cursor busyCursor = mainComposite.getParent().getDisplay()
                    .getSystemCursor(SWT.CURSOR_APPSTARTING);
            mainComposite.getParent().setCursor(busyCursor);
            mainComposite.setEnabled(false);
        } else {
            mainComposite.setEnabled(true);
            mainComposite.getParent().setCursor(null);
        }
    }

    /**
     * Sent when selection occurs in the control.
     * <p>
     * For example, selection occurs in a List when the user selects an item or
     * items with the keyboard or mouse. On some platforms, the event occurs
     * when a mouse button or key is pressed. On others, it happens when the
     * mouse or key is released. The exact key or mouse gesture that causes this
     * event is platform specific.
     * </p>
     * 
     * @param e
     *            an event containing information about the selection
     */
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == cmdClear) {
            BusyIndicator.showWhile(mainComposite.getDisplay(), new Runnable() {
                public void run() {
                    synchronized (historyList) {
                        historyList.removeAll(historyList);
                        autoLoader.lastHistoryID = 0;
                        tblMonitorViewer.refresh();
                    }
                }
            });
            return;
        } else if (e.getSource() == cmdRefresh) {
            if (!optLast.getSelection()) {
                Date periodStart = null, periodEnd = null;

                try {
                    periodStart = getDate(txtPeriodStart);
                    periodEnd = getDate(txtPeriodEnd);
                } catch (Exception ex) {
                    MessageBox msg = new MessageBox(mainComposite.getShell(),
                            SWT.ICON_ERROR);
                    msg.setMessage(NLS.bind(
                            Messages.AMSMonitorView_Error_InputFormatTime_Msg,
                            maskTime));
                    msg
                            .setText(Messages.AMSMonitorView_Error_InputFormattime_Title);
                    msg.open();
                    return;
                }

                final Date periodStartfinal = periodStart;
                final Date periodEndfinal = periodEnd;

                showBusy(true);
                new Thread(new MyRunnable() {
                    public void run() {
                        try {
                            synchronized (historyList) {
                                final List<HistoryTObject> list = HistoryDAO
                                        .selectList(MonitorPlugin
                                                .getConnection(),
                                                periodStartfinal,
                                                periodEndfinal, 2000);

                                mainComposite.getDisplay().syncExec(
                                        new Runnable() {
                                            public void run() {
                                                historyList
                                                        .removeAll(historyList);
                                                historyList.addAll(list);
                                                tblMonitorViewer.refresh();
                                                showBusy(false);
                                            }
                                        });
                            }
                        } catch (Exception ex) {
                            Log.log(Log.FATAL, ex);
                        }
                    }
                }).start();
            }
            return;
        }

        synchronized (lock) {
            if (e.getSource() == optLast)
                autoLoader.lastHistoryID = -1;

            if (cboInterval.getSelectionIndex() > -1) {
                autoLoadInterval = intervals[cboInterval.getSelectionIndex()];
                lock.notify();
            } else
                autoLoadInterval = -1;

            autoLoader.isAutoLoading = optLast.getSelection()
                    && autoLoadInterval > -1;
            cmdRefresh.setEnabled(!autoLoader.isAutoLoading);
        }

    }

    /**
     * Sent when default selection occurs in the control.
     * <p>
     * For example, on some platforms default selection occurs in a List when
     * the user double-clicks an item or types return in a Text. On some
     * platforms, the event occurs when a mouse button or key is pressed. On
     * others, it happens when the mouse or key is released. The exact key or
     * mouse gesture that causes this event is platform specific.
     * </p>
     * 
     * @param e
     *            an event containing information about the default selection
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        // Nothing to do
    }

    public boolean isEmpty(Text txt) {
        String text = txt.getText();
        if (text == null || text.trim().length() == 0)
            return true;
        return false;
    }

    private DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
            DateFormat.SHORT);
    private String maskTime = ((SimpleDateFormat) df).toLocalizedPattern();

    private Date getDate(Text text) throws Exception {
        if (isEmpty(text))
            return null;

        return df.parse(text.getText());
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    @Override
    public void createPartControl(Composite parent) {
        ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL
                | SWT.V_SCROLL);

        mainComposite = new Composite(sc, SWT.BORDER);
        mainComposite.setLayout(new GridLayout(1, false));

        /*
         * // Set the absolute size of the child child.setSize(400, 400);
         */
        // Set the child as the scrolled content of the ScrolledComposite
        sc.setContent(mainComposite);

        // Set the minimum size
        sc.setMinSize(600, 300);

        // Expand both horizontally and vertically
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        filterComposite = new Group(mainComposite, SWT.NONE);
        filterComposite.setLayout(new GridLayout(7, false));
        filterComposite.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.FILL,
                SWT.FILL, true, false));

        optLast = new Button(filterComposite, SWT.RADIO);
        optLast.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.BEGINNING,
                SWT.CENTER, false, false));
        lblLastCount = new Label(filterComposite, SWT.NONE);
        lblLastCount.setLayoutData(Utils.getGridData(-1, -1, 1, 1,
                SWT.BEGINNING, SWT.CENTER, false, false));
        txtLastCount = new Text(filterComposite, SWT.BORDER | SWT.SINGLE);
        txtLastCount.setLayoutData(Utils.getGridData(100, -1, 1, 1,
                SWT.BEGINNING, SWT.CENTER, false, false));
        new Label(filterComposite, SWT.NONE); // dummy
        cboInterval = new Combo(filterComposite, SWT.READ_ONLY);
        cboInterval.setLayoutData(Utils.getGridData(80, -1, 1, 1,
                SWT.BEGINNING, SWT.CENTER, false, false));
        excelExport = new Button(filterComposite, SWT.NONE);
        excelExport.setLayoutData(Utils.getGridData(-1, -1, 2, 1,
                SWT.BEGINNING, SWT.CENTER, false, false));

        optPeriod = new Button(filterComposite, SWT.RADIO);
        optPeriod.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.BEGINNING,
                SWT.CENTER, false, false));
        lblPeriodStart = new Label(filterComposite, SWT.NONE);
        lblPeriodStart.setLayoutData(Utils.getGridData(-1, -1, 1, 1,
                SWT.BEGINNING, SWT.CENTER, false, false));
        txtPeriodStart = new Text(filterComposite, SWT.BORDER | SWT.SINGLE);
        txtPeriodStart.setLayoutData(Utils.getGridData(100, -1, 1, 1,
                SWT.BEGINNING, SWT.CENTER, false, false));
        lblPeriodEnd = new Label(filterComposite, SWT.NONE);
        lblPeriodEnd.setLayoutData(Utils.getGridData(-1, -1, 1, 1,
                SWT.BEGINNING, SWT.CENTER, false, false));
        txtPeriodEnd = new Text(filterComposite, SWT.BORDER | SWT.SINGLE);
        txtPeriodEnd.setLayoutData(Utils.getGridData(100, -1, 1, 1,
                SWT.BEGINNING, SWT.CENTER, false, false));
        cmdClear = new CommandButton(filterComposite, CommandButton.CLEAR);
        cmdClear.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.END,
                SWT.CENTER, true, false));
        cmdRefresh = new CommandButton(filterComposite, CommandButton.REFRESH);
        cmdRefresh.setLayoutData(Utils.getGridData(-1, -1, 1, 1, SWT.BEGINNING,
                SWT.CENTER, false, false));

        optLast.addSelectionListener(this);
        cboInterval.addSelectionListener(this);
        cmdClear.addSelectionListener(this);
        cmdRefresh.addSelectionListener(this);

        excelExport.addSelectionListener(new SelectionAdapter() {

            // Opens a dialog to set a file for export and starts the job for
            // the excel export.
            @Override
            public void widgetSelected(final SelectionEvent e) {
                // File standard dialog
                FileDialog fileDialog = new FileDialog(Display.getDefault()
                        .getActiveShell(), SWT.SAVE);

                // Set the text
                fileDialog.setText("Save File");
                // Set filter on .txt files
                fileDialog.setFilterExtensions(new String[] { "*.xls" });
                // Put in a readable name for the filter
                fileDialog
                        .setFilterNames(new String[] { "Excel 97-2003 format(*.xls)" });

                // Open Dialog and save result of selection
                String selected = fileDialog.open();

                File path = new File(selected);

                ExcelExporter exporter = new ExcelExporter(path);
                synchronized (historyList) {
                    exporter.setData(historyList, tblMonitor.getColumns());
                }
                exporter.addJobChangeListener(new JobChangeAdapter() {

                    @Override
                    public void done(IJobChangeEvent e) {
                        final IJobChangeEvent event = e;
                        Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                if (event.getResult().isOK()) {
                                    MessageBox messageBox = new MessageBox(
                                            Display.getDefault()
                                                    .getActiveShell(), SWT.OK
                                                    | SWT.ICON_INFORMATION);
                                    messageBox.setText("Excel Export");
                                    messageBox
                                            .setMessage("Excel export file is created");
                                    messageBox.open();
                                } else {
                                    MessageBox messageBox2 = new MessageBox(
                                            Display.getDefault()
                                                    .getActiveShell(),
                                            SWT.ERROR | SWT.ICON_ERROR);
                                    messageBox2.setText("Excel Export");
                                    messageBox2
                                            .setMessage("IO error, excel file could not created");
                                    messageBox2.open();
                                }
                            }
                        });
                    }
                });

                exporter.schedule();
            }
        });

        String[] items = new String[intervals.length];

        items[0] = Messages.AMSMonitorView_txtNoUpdate;
        for (int i = 1; i < items.length; i++) {
            if (intervals[i] / 1000 > 0)
                items[i] = (intervals[i] / 1000) + " s";
            else
                items[i] = intervals[i] + " ms";
        }
        cboInterval.setItems(items);
        cboInterval.select(0);

        optPeriod.addSelectionListener(this);
        optLast.setSelection(true);

        txtLastCount.setText("" + autoLoadCount);
        txtLastCount.addVerifyListener(new VerifyInput("1234567890"));
        txtLastCount.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    if (!isEmpty(txtLastCount)) {
                        int value = Integer.parseInt(txtLastCount.getText());
                        if (value > 0)
                            autoLoadCount = value;
                    }
                    txtLastCount.setText("" + autoLoadCount);
                    BusyIndicator.showWhile(mainComposite.getDisplay(),
                            new Runnable() {
                                public void run() {
                                    synchronized (historyList) {
                                        historyList.removeAll(historyList);
                                        autoLoader.lastHistoryID = 0;
                                        tblMonitorViewer.refresh();
                                    }
                                }
                            });
                }
            }
        });

        txtLastCount.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (!isEmpty(txtLastCount)) {
                    int value = Integer.parseInt(txtLastCount.getText());
                    if (value > 0)
                        autoLoadCount = value;
                }

            }
        });

        createTable();
        createTableViewer();
        initText();

        autoLoader.start();
        // new HistoryMaker().start();
    }

    private void createTable() {
        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.FULL_SELECTION;

        tblMonitor = new Table(mainComposite, style);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        tblMonitor.setLayoutData(gridData);

        tblMonitor.setLinesVisible(true);
        tblMonitor.setHeaderVisible(true);

        int idx = 0;
        TableColumn column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(50);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.HISTORYID));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(120);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.TIMENEW));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(75);
        column.addSelectionListener(new SortSelectionAdapter(TableSorter.TYPE));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(100);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.MSGHOST));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(100);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.MSGNAME));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(120);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.MSGEVENTTIME));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(450);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.DESCRIPTION));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(80);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.ACTIONTYPE));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(60);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.USERREF));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(110);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.USERNAME));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(70);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.DESTTYPE));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(110);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.DESTADRESS));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(60);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.GROUPREF));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(80);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.GROUPNAME));

        column = new TableColumn(tblMonitor, SWT.LEFT, idx++);
        column.setText(Messages.getString("MonitorTable_tbl_Colum" + idx));
        column.setWidth(80);
        column.addSelectionListener(new SortSelectionAdapter(
                TableSorter.RECIEVERPOS));
    }

    public void createTableViewer() {
        tblMonitorViewer = new TableViewer(tblMonitor);
        tblMonitorViewer.setUseHashlookup(true);

        tblMonitorViewer.setContentProvider(new ArrayContentProvider());
        tblMonitorViewer.setLabelProvider(new HistoryLabelProvider());
        // The input for the table viewer
        tblMonitorViewer.setInput(historyList);
    }

    private void initText() {
        filterComposite.setText(Messages.AMSMonitorView_filterComposite);

        optLast.setText(Messages.AMSMonitorView_optLast);
        lblLastCount.setText(Messages.AMSMonitorView_lblLastCount);
        cboInterval.setText(Messages.AMSMonitorView_cboInterval);
        excelExport.setText(Messages.AMSMonitorView_excelExport);
        optPeriod.setText(Messages.AMSMonitorView_optPeriod);
        lblPeriodStart.setText(Messages.AMSMonitorView_lblPeriodStart);
        lblPeriodEnd.setText(Messages.AMSMonitorView_lblPeriodEnd);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        mainComposite.setFocus();
    }

    private Composite mainComposite = null;
    private Group filterComposite = null;
    private Button optLast = null;
    private Label lblLastCount = null;
    private Text txtLastCount = null;
    private Combo cboInterval = null;
    private Button optPeriod = null;
    private Label lblPeriodStart = null;
    private Text txtPeriodStart = null;
    private Label lblPeriodEnd = null;
    private Text txtPeriodEnd = null;
    private Button excelExport = null;

    private CommandButton cmdClear = null;
    private CommandButton cmdRefresh = null;

    private Table tblMonitor = null;
    private TableViewer tblMonitorViewer = null;

    private class HistoryLoader extends Thread {
        private boolean running = true;
        private boolean isAutoLoading = false;

        private int lastHistoryID = 0;
        private boolean bAutoscroll = false;

        @Override
        public void run() {
            while (running) {
                try {
                    synchronized (lock) {
                        Log.log(Log.INFO, "AMSMonitor.AutoLoading Wait...");

                        if (autoLoadInterval < 0)
                            lock.wait();
                        else
                            lock.wait(autoLoadInterval);

                        Log.log(Log.INFO, "AMSMonitor.AutoLoading Wait...done");
                    }

                    if (!isAutoLoading)
                        continue;
                    Log.log(Log.INFO,
                            "AMSMonitor.AutoLoading Check for new data...");

                    synchronized (historyList) {
                        List<HistoryTObject> newData = getHistory(autoLoadCount);

                        if (newData.size() > 0)
                            bAutoscroll = true;
                        else
                            bAutoscroll = false;

                        Log.log(Log.INFO,
                                "AMSMonitor.AutoLoading Check for new data...Found "
                                        + newData.size());

                        historyList.addAll(0, newData);

                        for (int i = historyList.size(); i > autoLoadCount; i--)
                            historyList.remove(i - 1);
                    }

                    if (running) {
                        mainComposite.getDisplay().syncExec(new Runnable() {
                            public void run() {
                                if (running) {
                                    tblMonitorViewer.refresh();
                                    if (bAutoscroll)
                                        tblMonitor.setSelection(0); // for
                                    // autoscrolling,
                                    // if new
                                    // history
                                    // entries
                                }
                            }
                        });
                    }
                } catch (Exception ex) {
                    Log.log(Log.ERROR, ex);
                }
            }
        }

        // TODO: Die erste Abfrage liefert immer alle Eintraege!!!!
        //       Besser waere eine Abfrage, die wirklich nur die definierte Anzahl liefert.
        public List<HistoryTObject> getHistory(int maxCount) {
            List<HistoryTObject> array = new ArrayList<HistoryTObject>();
            try {
                if (lastHistoryID < 0)
                    lastHistoryID = HistoryDAO.getLastHistoryID(MonitorPlugin.getConnection());

                array = HistoryDAO.selectList(MonitorPlugin.getConnection(),
                        autoLoadCount, lastHistoryID);

                if (!array.isEmpty())
                    lastHistoryID = array.get(0).getHistoryID();

                System.out.println("lastHistoryID=" + lastHistoryID);
            } catch (Exception ex) {
                Log.log(Log.FATAL, ex);
            }

            return array;
        }
    }

    public class HistoryLabelProvider extends LabelProvider implements
            ITableLabelProvider {
        DateFormat df = DateFormat.getDateTimeInstance();

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
         *      int)
         */
        public String getColumnText(Object element, int columnIndex) {
            HistoryTObject item = (HistoryTObject) element;
            switch (columnIndex) {
            case 0:
                return Integer.toString(item.getHistoryID());
            case 1:
                return df.format(item.getTimeNew());
            case 2:
                return item.getType();
            case 3:
                return item.getMsgHost();
            case 4:
                return item.getMsgName();
            case 5:
                return item.getMsgEventtime();
            case 6:
                return item.getDescription();
            case 7:
                if (item.getActionType() == null
                        || item.getActionType().length() == 0)
                    return "";

                return item.getActionType();

            case 8:
                if ((item.getActionType() == null || item.getActionType()
                        .length() == 0)
                        && (!item.getType().equals("Announce")))
                    return "";

                return Integer.toString(item.getUserRef());
            case 9:
                return item.getUserName();
            case 10:
                return item.getDestType();
            case 11:
                return item.getDestAdress();

            case 12:
                if ((item.getActionType() == null || item.getActionType()
                        .length() == 0)
                        && (!item.getType().equals("Announce")))
                    return "";

                if (item.getGroupRef() == 0)
                    return "-";

                return Integer.toString(item.getGroupRef());
            case 13:
                if ((item.getActionType() == null || item.getActionType()
                        .length() == 0)
                        && (!item.getType().equals("Announce")))
                    return "";

                if (item.getGroupRef() == 0)
                    return "-";

                return item.getGroupName();
            case 14:
                if ((item.getActionType() == null || item.getActionType()
                        .length() == 0)
                        && (!item.getType().equals("Announce")))
                    return "";

                if (item.getReceiverPos() == 0)
                    return "-";

                return Integer.toString(item.getReceiverPos());
            default:
                return "?";
            }
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
         *      int)
         */
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    private class SortSelectionAdapter extends SelectionAdapter {
        private int fieldID;

        public SortSelectionAdapter(int fieldID) {
            this.fieldID = fieldID;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            TableSorter sorter = (TableSorter) tblMonitorViewer.getSorter();
            int sortDirection = 1;

            if (sorter != null && sorter.fieldID == fieldID)
                sortDirection = sorter.getNextSortDirection();

            tblMonitorViewer.setSorter(new TableSorter(fieldID,
                    fieldID == TableSorter.HISTORYID ? -1 : sortDirection));
        }
    }

    public class TableSorter extends ViewerSorter {
        public final static int HISTORYID = 1;
        public final static int TIMENEW = 2;
        public final static int TYPE = 3;
        public final static int MSGHOST = 4;
        public final static int MSGNAME = 5;
        public final static int MSGEVENTTIME = 6;
        public final static int DESCRIPTION = 7;
        public final static int ACTIONTYPE = 8;
        public final static int GROUPREF = 9;
        public final static int GROUPNAME = 10;
        public final static int RECIEVERPOS = 11;
        public final static int USERREF = 12;
        public final static int USERNAME = 13;
        public final static int DESTTYPE = 14;
        public final static int DESTADRESS = 15;

        private int fieldID;
        private int sortDirection = 1;

        public TableSorter(int fieldID, int sortDirection) {
            super();
            this.fieldID = fieldID;
            this.sortDirection = sortDirection;
        }

        public int getNextSortDirection() {
            return sortDirection == -1 ? 1 : -1;
        }

        /*
         * (non-Javadoc) Method declared on ViewerSorter.
         */
        @Override
        public int compare(Viewer viewer, Object o1, Object o2) {

            HistoryTObject val1 = (HistoryTObject) o1;
            HistoryTObject val2 = (HistoryTObject) o2;

            switch (fieldID) {
            case HISTORYID:
                return compareNumber(val1.getHistoryID(), val2.getHistoryID());
            case TIMENEW:
                return compareDateTime(val1.getTimeNew(), val2.getTimeNew());
            case TYPE:
                return compareString(val1.getType(), val2.getType());
            case MSGHOST:
                return compareString(val1.getMsgHost(), val2.getMsgHost());
            case MSGNAME:
                return compareString(val1.getMsgName(), val2.getMsgName());
            case MSGEVENTTIME:
                return compareString(val1.getMsgEventtime(), val2
                        .getMsgEventtime()); // Format noch nicht bekannt
            case DESCRIPTION:
                return compareString(val1.getDescription(), val2
                        .getDescription());
            case ACTIONTYPE:
                return compareString(val1.getActionType(), val2.getActionType());
            case GROUPREF:
                return compareNumber(val1.getGroupRef(), val2.getGroupRef());
            case GROUPNAME:
                return compareString(val1.getGroupName(), val2.getGroupName());
            case RECIEVERPOS:
                return compareNumber(val1.getReceiverPos(), val2
                        .getReceiverPos());
            case USERREF:
                return compareNumber(val1.getUserRef(), val2.getUserRef());
            case USERNAME:
                return compareString(val1.getUserName(), val2.getUserName());
            case DESTTYPE:
                return compareString(val1.getDestType(), val2.getDestType());
            case DESTADRESS:
                return compareString(val1.getDestAdress(), val2.getDestAdress());
            default:
                return 0;
            }
        }

        /**
         * Returns a number reflecting the collation order of the given tasks
         * based on the percent completed.
         * 
         * @param task1
         * @param task2
         * @return a negative number if the first element is less than the
         *         second element; the value <code>0</code> if the first element
         *         is equal to the second element; and a positive number if the
         *         first element is greater than the second element
         */
        private int compareString(String str1, String str2) {
            if (str1 == null)
                str1 = "";
            if (str2 == null)
                str2 = "";

            return (sortDirection) * collator.compare(str1, str2);
        }

        /**
         * Returns a number reflecting the collation order of the given tasks
         * based on the description.
         * 
         * @param task1
         *            the first task element to be ordered
         * @param resource2
         *            the second task element to be ordered
         * @return a negative number if the first element is less than the
         *         second element; the value <code>0</code> if the first element
         *         is equal to the second element; and a positive number if the
         *         first element is greater than the second element
         */
        protected int compareNumber(int int1, int int2) {
            return (sortDirection) * (int1 - int2);
        }

        /**
         * Returns a number reflecting the collation order of the given tasks
         * based on their owner.
         * 
         * @param resource1
         *            the first resource element to be ordered
         * @param resource2
         *            the second resource element to be ordered
         * @return a negative number if the first element is less than the
         *         second element; the value <code>0</code> if the first element
         *         is equal to the second element; and a positive number if the
         *         first element is greater than the second element
         */
        protected int compareDateTime(Date t1, Date t2) {
            if (t1 == null)
                t1 = new Date(0);
            if (t2 == null)
                t2 = new Date(0);

            return (sortDirection) * t1.compareTo(t2);
        }
    }
}