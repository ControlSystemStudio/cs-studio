package org.csstudio.logging.es.widget;

import org.csstudio.logging.es.Helpers;
import org.csstudio.logging.es.Preferences;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class LogviewModel extends AbstractWidgetModel
{
    public static final String PROP_FILTER_APPLICATION = "FilterApplication"; //$NON-NLS-1$
    public static final String PROP_FILTER_CLASS = "FilterClass"; //$NON-NLS-1$
    public static final String PROP_FILTER_HOST = "FilterHost"; //$NON-NLS-1$
    public static final String PROP_FILTER_MINSEVERITY = "FilterMinSeverity"; //$NON-NLS-1$
    public static final String PROP_START_TIME = "StartTime"; //$NON-NLS-1$

    public LogviewModel()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void configureProperties()
    {
        addProperty(new StringProperty(PROP_FILTER_APPLICATION,
                Messages.LogviewModel_FilterApplication,
                WidgetPropertyCategory.Misc, "")); //$NON-NLS-1$
        addProperty(new StringProperty(PROP_FILTER_CLASS,
                Messages.LogviewModel_FilterClass, WidgetPropertyCategory.Misc,
                "")); //$NON-NLS-1$
        addProperty(new StringProperty(PROP_FILTER_HOST,
                Messages.LogviewModel_FilterHost, WidgetPropertyCategory.Misc,
                "")); //$NON-NLS-1$
        addProperty(new ComboProperty(PROP_FILTER_MINSEVERITY,
                Messages.LogviewModel_MinSeverity, WidgetPropertyCategory.Misc,
                Helpers.LOG_LEVELS, 0));
        addProperty(new StringProperty(PROP_START_TIME,
                Messages.LogviewModel_StartTime, WidgetPropertyCategory.Misc,
                Preferences.getDefaultStart()));
    }

    public String getFilterApplication()
    {
        return getCastedPropertyValue(PROP_FILTER_APPLICATION);
    }

    public String getFilterClass()
    {
        return getCastedPropertyValue(PROP_FILTER_CLASS);
    }

    public String getFilterHost()
    {
        return getCastedPropertyValue(PROP_FILTER_HOST);
    }

    public int getFilterMinSeverity()
    {
        return (Integer) getCastedPropertyValue(PROP_FILTER_MINSEVERITY);
    }

    public String getStartTime()
    {
        return getCastedPropertyValue(PROP_START_TIME);
    }

    @Override
    public String getTypeID()
    {
        return "org.csstudio.logging.es.widget"; //$NON-NLS-1$
    }
}
