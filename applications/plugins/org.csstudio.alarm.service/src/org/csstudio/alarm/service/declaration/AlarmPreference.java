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

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.AlarmServiceActivator;
import org.csstudio.platform.AbstractPreference;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Constant definitions for alarm service preferences
 *
 * @param <T> the type of the preference. It must match the type of the default value.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010
 */
public final class AlarmPreference<T> extends AbstractPreference<T> {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(AlarmPreference.class);

    public static final String STRING_LIST_SEPARATOR = ";";

    public static final AlarmPreference<Boolean> ALARMSERVICE_IS_DAL_IMPL =
        new AlarmPreference<Boolean>("isDalImpl", false);

    public static final AlarmPreference<Boolean> ALARMSERVICE_CONFIG_VIA_LDAP =
        new AlarmPreference<Boolean>("configViaLdap", true);

    public static final AlarmPreference<String> ALARMSERVICE_CONFIG_FILENAME =
        new AlarmPreference<String>("configFileName", "resource/alarmServiceConfig.xml");

    public static final AlarmPreference<String> ALARMSERVICE_TOPICS =
        new AlarmPreference<String>("topics", "ALARM;ACK;");

    public static final AlarmPreference<String> ALARMSERVICE_FACILITIES =
        new AlarmPreference<String>("facilities", "Test;");

    public static final AlarmPreference<Integer> ALARMSERVICE_PV_CHUNK_SIZE =
        new AlarmPreference<Integer>("pvChunkSize", 500);

    public static final AlarmPreference<Integer> ALARMSERVICE_PV_CHUNK_WAIT_MSEC =
        new AlarmPreference<Integer>("pvChunkWaitMsec", 100);

    public static final AlarmPreference<Integer> ALARMSERVICE_PV_REGISTER_WAIT_MSEC =
        new AlarmPreference<Integer>("pvRegisterWaitMsec", 1000);



    private AlarmPreference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    @Override
    protected String getPluginID() {
        return AlarmServiceActivator.PLUGIN_ID;
    }

    /**
     * {@inheritDoc}
     */
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
        String resultString = ALARMSERVICE_TOPICS.getValue();
        String[] result = resultString.split(STRING_LIST_SEPARATOR);
        return Arrays.asList(result);
    }

    /**
     * The facility names are defined in a string like this: "CMTB;Flash;Test;", i.e. separated without blanks,
     * they are separated properly here.
     *
     * If the preferences contain no facility names, an entry named 'TEST' will be returned according to specification.
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
        return (result.length == 0) || ((result.length == 1) && (result[0].isEmpty()));
    }

    /**
     * It is assumed that the file is located in the plugin. The file name is thus given relative
     * to the plugin, e.g. "resource/SomeFile.xml".
     *
     * Here the prefix for the plugin is added, so the caller needn't worry.
     *
     * If the filename is misspelled an IOException will occur. It is catched right here, because it is not known
     * if the filename will be used at all. If it is used eventually, an io-error will occur and must be handled anyway.
     *
     * @return the full pathname
     */
    @Nonnull
    public static String getConfigFilename() {
        Bundle bundle = Platform.getBundle(AlarmServiceActivator.PLUGIN_ID);
        Path path = new Path(ALARMSERVICE_CONFIG_FILENAME.getValue());
        URL url = FileLocator.find(bundle, path, null);
        String result = null;
        try {
            result = FileLocator.toFileURL(url).getPath();
        } catch (IOException e) {
            result = ALARMSERVICE_CONFIG_FILENAME.getValue(); // visible in log file
            LOG.error("Error determining filename from value " + ALARMSERVICE_CONFIG_FILENAME.getValue() + ".");
        }
        assert result != null : "result must not be null";
        return result;
    }


}
