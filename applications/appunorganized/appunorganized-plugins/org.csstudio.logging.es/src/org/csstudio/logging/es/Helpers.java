package org.csstudio.logging.es;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.logging.Level;

import org.csstudio.logging.JMSLogMessage;
import org.csstudio.logging.es.archivedjmslog.ElasticsearchModel;
import org.csstudio.logging.es.archivedjmslog.JMSReceiver;
import org.csstudio.logging.es.archivedjmslog.JMSReceiverPool;
import org.csstudio.logging.es.archivedjmslog.LiveModel;
import org.csstudio.logging.es.model.EventLogMessage;
import org.csstudio.logging.es.model.LogArchiveModel;
import org.csstudio.security.preferences.SecurePreferences;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONException;
import org.json.JSONObject;

public class Helpers
{
    public final static String[] LOG_LEVELS = new String[] {
            Level.FINEST.getLocalizedName(), Level.FINER.getLocalizedName(),
            Level.FINE.getLocalizedName(), Level.CONFIG.getLocalizedName(),
            Level.INFO.getLocalizedName(), Level.WARNING.getLocalizedName(),
            Level.SEVERE.getLocalizedName() };

    public static LogArchiveModel createModel(Shell shell)
    {
        // Read settings from preferences
        final IPreferencesService service = Platform.getPreferencesService();
        final String url = service.getString(Activator.ID, Preferences.ES_URL,
                null, null);
        final String index = SecurePreferences.get(Activator.ID,
                Preferences.ES_INDEX, null);
        final String mapping = SecurePreferences.get(Activator.ID,
                Preferences.ES_MAPPING, null);
        final String jms_url = service.getString(Activator.ID,
                Preferences.JMS_URL, null, null);
        final String jms_user = SecurePreferences.get(Activator.ID,
                Preferences.JMS_USER, null);
        final String jms_password = SecurePreferences.get(Activator.ID,
                Preferences.JMS_PASSWORD, null);
        final String jms_topic = SecurePreferences.get(Activator.ID,
                Preferences.JMS_TOPIC, JMSLogMessage.DEFAULT_TOPIC);

        ElasticsearchModel<EventLogMessage> archive = null;
        if (null != url)
        {
            archive = new ElasticsearchModel<EventLogMessage>(url,
                    index + "/" + mapping, JMSLogMessage.CREATETIME, //$NON-NLS-1$
                    EventLogMessage::fromElasticsearch)
            {
                @SuppressWarnings("nls")
                @Override
                protected JSONObject getTimeQuery(Instant from, Instant to)
                        throws JSONException
                {
                    SimpleDateFormat df = new SimpleDateFormat(
                            JMSLogMessage.DATE_FORMAT);
                    JSONObject timematch = new JSONObject();
                    timematch.put("gte", df.format(from.toEpochMilli()));
                    timematch.put("lte", df.format(to.toEpochMilli()));
                    return new JSONObject().put("range",
                            new JSONObject().put(this.dateField, timematch));
                }

                @Override
                public EventLogMessage[] getMessages()
                {
                    EventLogMessage[] result = super.getMessages();
                    Long lastTime = null;
                    for (EventLogMessage msg : result)
                    {
                        Long thisTime = msg.getTime();
                        if (null != lastTime)
                        {
                            msg.setDelta(thisTime - lastTime);
                        }
                        lastTime = thisTime;
                    }
                    return result;
                }
            };
        }
        else
        {
            Activator.getLogger().config("No archive URL configured."); //$NON-NLS-1$
        }

        LiveModel<EventLogMessage> live = null;
        if (null != jms_url)
        {
            JMSReceiver receiver = JMSReceiverPool.getReceiver(jms_url,
                    jms_user, jms_password, jms_topic);
            live = new LiveModel<>(receiver, jms_topic,
                    JMSLogMessage.CREATETIME, EventLogMessage::fromJMS);
        }
        else
        {
            Activator.getLogger().config("No JMS URL configured."); //$NON-NLS-1$
        }

        return new LogArchiveModel(shell, archive, live);
    }
}
