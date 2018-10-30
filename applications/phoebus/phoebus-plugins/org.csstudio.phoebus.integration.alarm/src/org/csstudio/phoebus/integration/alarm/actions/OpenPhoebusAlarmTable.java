package org.csstudio.phoebus.integration.alarm.actions;

import static org.csstudio.phoebus.integration.alarm.Activator.log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.csstudio.phoebus.integration.PhoebusLauncherService;
import org.csstudio.phoebus.integration.alarm.Activator;
import org.csstudio.phoebus.integration.alarm.PreferenceConstants;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Command handler for opening the alarm table
 *
 * @author carcassi, Kay Kasemir, Kunal Shroff
 */
public class OpenPhoebusAlarmTable extends AbstractHandler implements IHandler {
    static final IPreferencesService prefs = Platform.getPreferencesService();

    private static final String schema = "alarm";
    private static final String app = "alarm_table";

    private static String serverHost = prefs.getString(Activator.PLUGIN_ID, PreferenceConstants.ServerHost, "localhost", null);
    private static int serverPort = prefs.getInt(Activator.PLUGIN_ID, PreferenceConstants.ServerPort, 9092, null);

    private static List<String> topics = Arrays
            .asList(prefs.getString(Activator.PLUGIN_ID, PreferenceConstants.Topics, "Accelerator", null).split(","))
            .stream().map(String::trim).collect(Collectors.toList());

    /** {@inheritDoc} */
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final Shell shell = HandlerUtil.getActiveShell(event);

        if(topics != null && topics.isEmpty()) {
            PhoebusLauncherService.launchApplication(Messages.AlarmTable);
        }

        if(topics.size() > 5 && !MessageDialog.openConfirm(shell, Messages.MultipleInstancesTitle,
                NLS.bind(Messages.MultipleInstancesFmt, topics.size(), app))) {
            return null;
        }
        String query = "app="+app;
        topics.forEach(topic -> {
            URI uri;
            try {
                uri = new URI(schema, null, serverHost, serverPort, "/" + topic, query, null);
                PhoebusLauncherService.launchResource(uri.toString());
            } catch (URISyntaxException e) {
                log.log(Level.WARNING, "Failed to launch the alarm table for topic " + topic, e);
            }
        });
        return null;
    }

}
