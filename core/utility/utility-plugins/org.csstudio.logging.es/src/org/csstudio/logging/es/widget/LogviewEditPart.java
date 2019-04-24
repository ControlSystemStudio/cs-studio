package org.csstudio.logging.es.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.logging.JMSLogMessage;
import org.csstudio.logging.es.Helpers;
import org.csstudio.logging.es.Preferences;
import org.csstudio.logging.es.archivedjmslog.MergedModel;
import org.csstudio.logging.es.archivedjmslog.MergedModelListener;
import org.csstudio.logging.es.archivedjmslog.PropertyFilter;
import org.csstudio.logging.es.archivedjmslog.StringPropertyFilter;
import org.csstudio.logging.es.model.EventLogMessage;
import org.csstudio.logging.es.model.LogArchiveModel;
import org.csstudio.logging.es.model.MessageSeverityPropertyFilter;
import org.csstudio.logging.es.util.ThrottledExecutor;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

public class LogviewEditPart extends AbstractWidgetEditPart
        implements MergedModelListener<EventLogMessage>
{
    LogArchiveModel msg_model;

    String filterByApplication = null;
    String filterByClass = null;
    String filterByHost = null;
    Integer filterMinSeverity = null;
    String startTime = Preferences.getDefaultStart();
    /** Executor used to throttle redraws on new messages. */
    ThrottledExecutor updateTimer;

    private PropertyFilter[] filters;

    public LogviewEditPart() throws Exception
    {
        this.msg_model = null;
        this.updateTimer = new ThrottledExecutor(() -> {
            ((LogviewFigure) getFigure()).setData(this.msg_model);
        }, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void deactivate()
    {
        super.deactivate();
        this.msg_model.removeListener(this);
    }

    @Override
    protected void doActivate()
    {
        super.doActivate();
        if (null != this.msg_model)
        {
            this.msg_model.addListener(this);
        }
    }

    @Override
    protected IFigure doCreateFigure()
    {
        LogviewFigure f = new LogviewFigure(this);
        LogviewModel model = getWidgetModel();
        this.filterByApplication = model.getFilterApplication();
        this.filterByClass = model.getFilterClass();
        this.filterByHost = model.getFilterHost();
        this.filterMinSeverity = model.getFilterMinSeverity();
        this.startTime = model.getStartTime();
        updateFilters();
        return f;
    }

    @Override
    protected void doDeActivate()
    {
        super.doDeActivate();
        // TODO
    }

    public String getEndTime()
    {
        return RelativeTime.NOW;
    }

    public PropertyFilter[] getFilters()
    {
        return this.filters;
    }

    public String getStartTime()
    {
        return this.startTime;
    }

    @Override
    public LogviewModel getWidgetModel()
    {
        return (LogviewModel) getModel();
    }

    @Override
    public void onChange(MergedModel<EventLogMessage> model)
    {
        this.updateTimer.schedule();
    }

    @Override
    public void onError(MergedModel<EventLogMessage> model, String error)
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void registerPropertyChangeHandlers()
    {
        setPropertyChangeHandler(LogviewModel.PROP_FILTER_APPLICATION,
                new IWidgetPropertyChangeHandler()
                {
                    @Override
                    public boolean handleChange(Object oldValue,
                            Object newValue, IFigure refreshableFigure)
                    {
                        if (!(newValue instanceof String)) return false;
                        LogviewEditPart.this.filterByApplication = (String) newValue;
                        LogviewEditPart.this.updateFilters();
                        return true;
                    }
                });
        setPropertyChangeHandler(LogviewModel.PROP_FILTER_CLASS,
                new IWidgetPropertyChangeHandler()
                {
                    @Override
                    public boolean handleChange(Object oldValue,
                            Object newValue, IFigure refreshableFigure)
                    {
                        if (!(newValue instanceof String)) return false;
                        LogviewEditPart.this.filterByClass = (String) newValue;
                        LogviewEditPart.this.updateFilters();
                        return true;
                    }
                });
        setPropertyChangeHandler(LogviewModel.PROP_FILTER_HOST,
                new IWidgetPropertyChangeHandler()
                {
                    @Override
                    public boolean handleChange(Object oldValue,
                            Object newValue, IFigure refreshableFigure)
                    {
                        if (!(newValue instanceof String)) return false;
                        LogviewEditPart.this.filterByHost = (String) newValue;
                        LogviewEditPart.this.updateFilters();
                        return true;
                    }
                });
        setPropertyChangeHandler(LogviewModel.PROP_FILTER_MINSEVERITY,
                new IWidgetPropertyChangeHandler()
                {
                    @Override
                    public boolean handleChange(Object oldValue,
                            Object newValue, IFigure refreshableFigure)
                    {
                        if (!(newValue instanceof Integer)) return false;
                        LogviewEditPart.this.filterMinSeverity = (Integer) newValue;
                        LogviewEditPart.this.updateFilters();
                        return true;
                    }
                });
        setPropertyChangeHandler(LogviewModel.PROP_START_TIME,
                new IWidgetPropertyChangeHandler()
                {

                    @Override
                    public boolean handleChange(Object oldValue,
                            Object newValue, IFigure f)
                    {
                        if (!(newValue instanceof String)) return false;
                        LogviewEditPart.this.startTime = (String) newValue;
                        LogviewEditPart.this.updateFilters();
                        return true;
                    }
                });
    }

    @Override
    public void setExecutionMode(ExecutionMode executionMode)
    {
        if (executionMode.equals(ExecutionMode.RUN_MODE)
                && (null == this.msg_model))
        {
            this.msg_model = Helpers.createModel(null /* TODO: shell */);
            updateFilters();
        }
        super.setExecutionMode(executionMode);

    }

    protected void updateFilters()
    {
        List<PropertyFilter> f = new LinkedList<>();
        if ((null != this.filterByApplication)
                && !this.filterByApplication.isEmpty())
        {
            f.add(new StringPropertyFilter(JMSLogMessage.APPLICATION_ID,
                    this.filterByApplication, false));
        }
        if ((null != this.filterByClass) && !this.filterByClass.isEmpty())
        {
            f.add(new StringPropertyFilter(JMSLogMessage.CLASS,
                    this.filterByClass, false));
        }
        if ((null != this.filterByHost) && !this.filterByHost.isEmpty())
        {
            f.add(new StringPropertyFilter(JMSLogMessage.HOST,
                    this.filterByHost, false));
        }
        if ((null != this.filterMinSeverity) && this.filterMinSeverity > 0)
        {
            f.add(new MessageSeverityPropertyFilter(this.filterMinSeverity));
        }
        try
        {
            this.filters = f.toArray(new PropertyFilter[f.size()]);
            this.msg_model.setFilters(getFilters());
            this.msg_model.setTimerange(getStartTime(), getEndTime());
        }
        catch (Exception ex)
        {
            // TODO
            System.err.println(ex.getMessage());
        }
    }
}
