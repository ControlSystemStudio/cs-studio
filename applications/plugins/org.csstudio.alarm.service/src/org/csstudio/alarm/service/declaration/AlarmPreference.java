/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.alarm.service.declaration;

import java.io.IOException;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.AlarmServiceActivator;
import org.csstudio.domain.desy.preferences.AbstractPreference;
import org.csstudio.remote.jms.command.ClientGroup;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constant definitions for alarm service preferences (mimicked enum with inheritance).
 *
 * @param <T> the type of the preference. It must match the type of the default value.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010
 */
public final class AlarmPreference<T> extends AbstractPreference<T> {
    
    private static final Logger LOG = LoggerFactory.getLogger(AlarmPreference.class);
    
    public static final String STRING_LIST_SEPARATOR = ";";
    
    // if true, the dal implementation of the alarm service is used, otherwise the jms implementation
    // the dal implementation is useful for the css island version where no server environment is present.
    // it is also useful for the dal2jms server which forwards alarms from old-style iocs to jms when no ics may be applied.
    public static final AlarmPreference<Boolean> ALARMSERVICE_IS_DAL_IMPL =
        new AlarmPreference<Boolean>("isDalImpl", false);
    
    // if true, an ldap server is used for the retrieval of the alarm tree configuration
    // if false, the configuration is expected to be in an xml file
    public static final AlarmPreference<Boolean> ALARMSERVICE_CONFIG_VIA_LDAP =
        new AlarmPreference<Boolean>("configViaLdap", true);
    
    // if the alarm tree configuration is taken from a file the file name is provided here
    public static final AlarmPreference<String> ALARMSERVICE_CONFIG_FILENAME =
        new AlarmPreference<String>("configFileName", "res/alarmServiceConfig.xml");
    
    // if the alarm table and the alarm tree are listening to a jms server (not via dal) usually the user provides a list of jms topics
    // if this is not feasible (e.g. for a headless application) the set of jms topics is taken from this preference
    public static final AlarmPreference<String> ALARMSERVICE_TOPICS =
        new AlarmPreference<String>("topics", "MKK_ALARM;ACK;");
       // new AlarmPreference<String>("topics", "SMOKETEST;ACK;");
    
    // css clients as well as headless applications belong to one party (e.g. a department at desy)
    // this is used for distribution of commands to css clients as well as headless applications
    // see enum ClientGroup for further information
    public static final AlarmPreference<String> ALARMSERVICE_CLIENT_GROUP =
        new AlarmPreference<String>("clientGroup", "DESY_MKS2");
    
    // the alarm data is archived.
    // to be able to distinguish between alarm messages for different parties (e.g. departments at desy) a tag may be given.
    // see enum AlarmGroup for further information, esp. the string mapping
    public static final AlarmPreference<String> ALARMSERVICE_ALARM_GROUP =
        new AlarmPreference<String>("alarmGroup", "MKS2");
    
    // if true, the alarm service runs the server implementation providing the remote acknowledge service
    // and listening to the remote command service with the given client group.
    public static final AlarmPreference<Boolean> ALARMSERVICE_RUNS_AS_SERVER =
        new AlarmPreference<Boolean>("runsAsServer", false);
    
    // dns name of the rmi registry server
    public static final AlarmPreference<String> ALARMSERVICE_RMI_REGISTRY_SERVER =
        new AlarmPreference<String>("rmiRegistryServer", "localhost");
    
    // port of the rmi registry server
    public static final AlarmPreference<Integer> ALARMSERVICE_RMI_REGISTRY_PORT =
        new AlarmPreference<Integer>("rmiRegistryPort", 1100);
    
    // if true, the alarm service listens to the remote acknowledge service providing for persistent acknowledge state of pvs  
    // the alarm service then also listens to the remote command service with the given client group
    public static final AlarmPreference<Boolean> ALARMSERVICE_LISTENS_TO_ALARMSERVER =
        new AlarmPreference<Boolean>("listensToAlarmServer", false);
    
    // the given facility names determine the top level nodes visible in the alarm tree
    // these nodes usually represent the facilities, thus the name
    // see getFacilityNames()
    public static final AlarmPreference<String> ALARMSERVICE_FACILITIES =
        new AlarmPreference<String>("facilities", "Test;");
    
    // to prevent overload when the initial alarm state is retrieved this is done in chunks with the given size
    public static final AlarmPreference<Integer> ALARMSERVICE_PV_CHUNK_SIZE =
        new AlarmPreference<Integer>("pvChunkSize", 100);
    
    // to prevent overload when the initial alarm state is retrieved after each chunk this time is waited for
    public static final AlarmPreference<Integer> ALARMSERVICE_PV_CHUNK_WAIT_MSEC =
        new AlarmPreference<Integer>("pvChunkWaitMsec", 500);
    
    // to prevent overload when the initial alarm state is retrieved wait before the next registration takes place
    // currently unused
    public static final AlarmPreference<Integer> ALARMSERVICE_PV_REGISTER_WAIT_MSEC =
        new AlarmPreference<Integer>("pvRegisterWaitMsec", 2000);
    
    /**
     * Constructor.
     * @param keyAsString
     * @param defaultValue
     */
    private AlarmPreference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }
    
    @Override
    @Nonnull
    public String getPluginID() {
        return AlarmServiceActivator.PLUGIN_ID;
    }
    
    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) AlarmPreference.class;
    }
    
    /**
     * The topic names are defined in a string like this: "ALARM;ACK;", i.e. separated without blanks,
     * they are separated properly here.
     *
     * @return an unmodifiable list with the topic names
     */
    @Nonnull
    public static List<String> getTopicNames() {
        final String resultString = ALARMSERVICE_TOPICS.getValue();
        final String[] result = resultString.split(STRING_LIST_SEPARATOR);
        return Arrays.asList(result);
    }
    
    /**
     * The facility names are defined in a string like this: "CMTB;Flash;Test;", i.e. separated without blanks,
     * they are separated properly here.
     *
     * If the preferences contain no facility names, the name of a default entry (typically 'Test') will be returned.
     *
     * @return an unmodifiable list with the facility names
     */
    @Nonnull
    public static List<String> getFacilityNames() {
        final String resultString = ALARMSERVICE_FACILITIES.getValue();
        
        String[] result = resultString.split(STRING_LIST_SEPARATOR);
        if (hasNoFacilityNames(result)) {
            LOG.debug("No facility names found in preferences, using default.");
            result = ALARMSERVICE_FACILITIES.getDefaultValue().split(STRING_LIST_SEPARATOR);
        }
        LOG.debug("getFacilityNames: " + Arrays.asList(result));
        return Arrays.asList(result);
    }
    
    private static boolean hasNoFacilityNames(@Nonnull final String[] result) {
        return result.length == 0 || result.length == 1 && result[0].isEmpty();
    }
    
    /**
     * The default file is located in the plugin.
     * Via preference page a file from the local file system may be set.
     * The result is not null, but may not point to a valid file.  
     *
     * @return the full pathname
     */
    @Nonnull
    public static String getConfigFilename() {
        return getStringFromPath(ALARMSERVICE_CONFIG_FILENAME.getValue());
    }
    
    @Nonnull
    private static String getStringFromPath(@Nonnull final String pathAsString) {
        String result = null;
        Path path = new Path(pathAsString);
        if (path.isAbsolute()) {
            result = path.toOSString();
        } else {
            URL url = FileLocator.find(AlarmServiceActivator.getDefault().getBundle(), path, null);
            try {
                result = FileLocator.toFileURL(url).getPath();
            } catch (final IOException e) {
                result = ALARMSERVICE_CONFIG_FILENAME.getValue(); // visible in log file
                LOG.error("Error determining filename from value "
                        + ALARMSERVICE_CONFIG_FILENAME.getValue() + ".");
            }
        }
        return result;
    }
    
    @Nonnull
    public static AlarmGroup getAlarmGroup() {
        String alarmGroupAsString = AlarmPreference.ALARMSERVICE_ALARM_GROUP.getValue();
        AlarmGroup result = AlarmGroup.MKS2;
        
        try {
            result = AlarmGroup.valueOf(alarmGroupAsString);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to create preference AlarmGroup from " + alarmGroupAsString);
        } catch (NullPointerException e) {
            LOG.debug("Failed to create preference AlarmGroup from " + alarmGroupAsString);
        }
        return result;
    }

    @Nonnull
    public static ClientGroup getClientGroup() {
        String clientGroupAsString = AlarmPreference.ALARMSERVICE_CLIENT_GROUP.getValue();
        ClientGroup result = ClientGroup.UNDEFINED;
        
        try {
            result = ClientGroup.valueOf(clientGroupAsString);
        } catch (IllegalArgumentException e) {
            LOG.debug("Failed to create preference ClientGroup from " + clientGroupAsString);
        } catch (NullPointerException e) {
            LOG.debug("Failed to create preference ClientGroup from " + clientGroupAsString);
        }
        return result;
    }
}
