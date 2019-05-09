package org.csstudio.logging.es.widget;

import org.csstudio.logging.JMSLogMessage;
import org.csstudio.logging.es.MessageHistoryView;
import org.csstudio.logging.es.archivedjmslog.MergedModel;
import org.csstudio.logging.es.model.EventLogMessage;
import org.csstudio.logging.es.util.MessageContentProvider;
import org.csstudio.logging.es.util.PropertyLabelProvider;
import org.csstudio.logging.es.util.SeverityLabelProvider;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.ui.util.MinSizeTableColumnLayout;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class LogviewFigure extends AbstractSWTWidgetFigure<Composite>
{
    TableViewer table_viewer;

    LogviewFigure(AbstractBaseEditPart editpart)
    {
        super(editpart);
    }

    @Override
    protected Composite createSWTWidget(Composite parent, int style)
    {
        Composite table_parent = new Composite(parent, 0);

        this.table_viewer = new TableViewer(table_parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
        this.table_viewer.setContentProvider(new MessageContentProvider());
        Table table = this.table_viewer.getTable();
        table.setHeaderVisible(false);
        table.setLinesVisible(true);
        ColumnViewerToolTipSupport.enableFor(this.table_viewer);
        TableColumnLayout table_layout = new MinSizeTableColumnLayout(10);
        table_parent.setLayout(table_layout);

        TableViewerColumn view_col1 = new TableViewerColumn(this.table_viewer,
                0);
        TableColumn table_col1 = view_col1.getColumn();
        table_col1.setText(Messages.Column_Time);
        table_col1.setMoveable(true);
        view_col1.setLabelProvider(
                new PropertyLabelProvider(JMSLogMessage.CREATETIME));
        table_layout.setColumnData(table_col1, new ColumnWeightData(2, 80));

        try
        {
            TableViewerColumn view_col2 = new TableViewerColumn(
                    this.table_viewer, 0);
            TableColumn table_col2 = view_col2.getColumn();
            table_col2.setText(Messages.Column_Severity);
            table_col2.setMoveable(true);
            view_col2.setLabelProvider(
                    new SeverityLabelProvider(JMSLogMessage.SEVERITY, parent));
            table_layout.setColumnData(table_col2, new ColumnWeightData(1, 35));
        }
        catch (Exception e)
        {

        }

        TableViewerColumn view_col3 = new TableViewerColumn(this.table_viewer,
                0);
        TableColumn table_col3 = view_col3.getColumn();
        table_col3.setText(Messages.Column_Text);
        table_col3.setMoveable(true);
        view_col3.setLabelProvider(
                new PropertyLabelProvider(JMSLogMessage.TEXT));
        table_layout.setColumnData(table_col3, new ColumnWeightData(5, 200));

        Menu popupMenu = new Menu(table);
        MenuItem itemOpenView = new MenuItem(popupMenu, SWT.NONE);
        itemOpenView.addListener(SWT.Selection, new Listener()
        {
            @Override
            public void handleEvent(Event event)
            {
                IWorkbench workbench = PlatformUI.getWorkbench();
                IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                try
                {
                    IWorkbenchPage page = window.getActivePage();
                    MessageHistoryView view = (MessageHistoryView) page
                            .showView(MessageHistoryView.ID);
                    @SuppressWarnings("synthetic-access")
                    LogviewEditPart ep = (LogviewEditPart) LogviewFigure.this.editPart;
                    view.setFilters(ep.getFilters());
                    view.setTimeRange(ep.getStartTime(), ep.getEndTime());
                }
                catch (Exception ex)
                {
                    ExceptionDetailsErrorDialog.openError(parent.getShell(),
                            Messages.Error,
                            String.format(Messages.LogviewFigure_ErrorOpening,
                                    MessageHistoryView.ID),
                            ex);
                }
            }
        });
        itemOpenView.setText(Messages.LogviewFigure_OpenInView);
        table.setMenu(popupMenu);

        return table_parent;
    }

    public void setData(final MergedModel<EventLogMessage> model)
    {
        Display.getDefault().asyncExec(() -> {
            if ((LogviewFigure.this.table_viewer.getControl() != null)
                    && !LogviewFigure.this.table_viewer.getControl()
                            .isDisposed())
            {
                LogviewFigure.this.table_viewer.setInput(model);
            }
        });
    }
}
