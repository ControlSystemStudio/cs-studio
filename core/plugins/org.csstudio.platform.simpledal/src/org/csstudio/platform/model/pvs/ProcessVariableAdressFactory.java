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
package org.csstudio.platform.model.pvs;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.SimpleDalPluginActivator;
import org.csstudio.platform.internal.model.pvs.AbstractProcessVariableNameParser;
import org.csstudio.platform.internal.model.pvs.DalNameParser;
import org.csstudio.platform.internal.model.pvs.SimpleNameParser;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Factory for process variable adresses.
 *
 * TODO: Extract an interface!!!!
 *
 * @author Sven Wende
 *
 */
public class ProcessVariableAdressFactory {
    public static final String PROP_CONTROL_SYSTEM = "PROP_CONTROL_SYSTEM"; //$NON-NLS-1$

    public static final String PROP_ASK_FOR_CONTROL_SYSTEM = "PROP_ASK_FOR_CONTROL_SYSTEM"; //$NON-NLS-1$

    private static ProcessVariableAdressFactory _instance;

    private static HashMap<ControlSystemEnum, AbstractProcessVariableNameParser> _parserMapping;

    static {
        _parserMapping = new HashMap<ControlSystemEnum, AbstractProcessVariableNameParser>();

        for (final ControlSystemEnum cs : ControlSystemEnum.values()) {
            if ((cs == ControlSystemEnum.UNKNOWN) || (cs == ControlSystemEnum.SDS_SIMULATOR)) {
                _parserMapping.put(cs, new SimpleNameParser(cs));
            } else {
                _parserMapping.put(cs, new DalNameParser(cs));
            }
        }

        // check, that there is a parser for each control system
        for (final ControlSystemEnum controlSystem : ControlSystemEnum.values()) {
            assert _parserMapping.containsKey(controlSystem);
        }
    }

    /**
     * Hidden Constructor.
     */
    private ProcessVariableAdressFactory() {
        // Empty.
    }

    public static synchronized ProcessVariableAdressFactory getInstance() {
        if (_instance == null) {
            _instance = new ProcessVariableAdressFactory();
        }
        return _instance;
    }

    public IProcessVariableAddress createProcessVariableAdress(final String rawName,
            final ControlSystemEnum controlSystem) {
        // determine name parser
        final AbstractProcessVariableNameParser nameParser = _parserMapping.get(controlSystem);

        // parse raw name
        final IProcessVariableAddress result = nameParser.parseRawName(rawName);

        return result;
    }

    public IProcessVariableAddress createProcessVariableAdress(final String rawName) {
        // determine control system
        ControlSystemEnum controlSystem = getControlSystem(rawName, getDefaultControlSystem());

        if (controlSystem == null) {
            controlSystem = getDefaultControlSystem();
        }

        return createProcessVariableAdress(rawName, controlSystem);
    }

    public boolean hasValidControlSystemPrefix(final String rawName) {
        final ControlSystemEnum cs = getControlSystem(rawName, null);
        return ((cs != null) && (cs != ControlSystemEnum.UNKNOWN));
    }

    public ControlSystemEnum getDefaultControlSystem() {
        ControlSystemEnum controlSystem = ControlSystemEnum.LOCAL;
        final IPreferencesService prefService = Platform.getPreferencesService();

        if (prefService != null) {
            final String defaultCs = 
                Platform.getPreferencesService().getString(SimpleDalPluginActivator.ID,
                                                           PROP_CONTROL_SYSTEM, 
                                                           ControlSystemEnum.LOCAL.name(), //$NON-NLS-1$
                                                           null);
            controlSystem = ControlSystemEnum.valueOf(defaultCs);
        }

        assert controlSystem != null;
        return controlSystem;
    }

    public boolean askForControlSystem() {
        final boolean result = Platform.getPreferencesService().getBoolean(SimpleDalPluginActivator.ID,
                PROP_ASK_FOR_CONTROL_SYSTEM, true, //$NON-NLS-1$
                null);

        return result;
    }

    /**
     * @param controlSystemEnum not supported yet
     * @param device  not supported yet
     * @param property not supported yet
     * @param characteristics not supported yet
     */
    public IProcessVariableAddress createProcessVariableAdress(final ControlSystemEnum controlSystemEnum,
            final String device, final String property, final String characteristics) {
        throw new RuntimeException("not supported yet");
    }

    /**
     * @param defaultControlSystem  not supported yet
     */
    private ControlSystemEnum getControlSystem(final String rawName,
            final ControlSystemEnum defaultControlSystem) {
        ControlSystemEnum controlSystem = null;
        // compile a regex pattern and parse the String
        final Pattern p = Pattern.compile("^([^:]*)://");

        final Matcher m = p.matcher(rawName);

        if (m.find()) {
            final String s = m.group(1);
            if ((s != null) && (s.length() > 0)) {
                controlSystem = ControlSystemEnum.findByPrefix(s.toUpperCase());
            }
        }

        return controlSystem;
    }
}
