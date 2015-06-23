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
 /**
 *
 */
package org.csstudio.platform.model.pvs;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * An enumeration for all available control system prefixes.
 *
 * TODO (bknerr, all) : define control system vs control system's communication protocol
 * questions: <br/>
 * <li> DOOCS vs TINE, control system or communication protocol
 * <li> distinguish system's by type AND id? (think of more than one system with the same type?)
 * <li> isn't the 'protocol' prefix of a control system rather the protocol's id than the control system's id <br/>
 *      (what if a control system offers more than one communication protocol?)
 * <li> versions of control systems? different or parameterized types?
 * <li> technical: to allow for general use, the types shouldn't implement IAdaptable originally
 *
 * @author Sven Wende
 *
 */
public enum ControlSystemEnum implements IAdaptable {
    SDS_SIMULATOR("local", null, false, "icons/cs-local.png"),

    DAL_SIMULATOR("simulator", "Simulator", true, "icons/controlsystem-local"),
    /**
     * @Deprecated replaced by EPICS.
     */
    @Deprecated
    DAL_EPICS("dal-epics", "EPICS", true, "icons/controlsystem-epics"),
    /**
     * @Deprecated replaced by TINE.
     */
    @Deprecated
    DAL_TINE("dal-tine", "TINE", true, "icons/controlsystem-tine"),
    /**
     * @Deprecated replaced by TANGO.
     */
    @Deprecated
    DAL_TANGO("dal-tango", null, false, "icons/controlsystem-tango"),

    TINE("tine", "TINE", true, "icons/controlsystem-epics"),

    EPICS("epics", "EPICS", true, "icons/controlsystem-epics"),

    TANGO("tango", "Tango", true, "icons/controlsystem-epics"),

    LOCAL("local", null, false, "icons/controlsystem-local"),

    UNKNOWN("", null, false, "icons/controlsystem-unknown");

    private String _prefix;

    private String _dalName;

    private boolean _supportedByDAL;

    private String _icon;

    ControlSystemEnum(String prefix, String dalName, boolean supportedByDAL, String icon) {
        assert prefix != null;
        assert icon != null;
        _prefix = prefix;
        _dalName = dalName;
        _supportedByDAL = supportedByDAL;
        _icon = icon;
    }

    public String getIconPrefix() {
        return _icon;
    }

    public String getPrefix() {
        return _prefix;
    }

    public String getResponsibleDalPlugId() {
        return _dalName;
    }

    public boolean isSupportedByDAL() {
        return _supportedByDAL;
    }

    /**
     * Get only the ControlSystem that are shown or selectable.
     * @return the visible ControlSystem.
     */
    public static ControlSystemEnum[] valuesShown() {
        ArrayList<ControlSystemEnum> list = new ArrayList<ControlSystemEnum>();
        for (ControlSystemEnum cs : values()) {
            switch(cs) {
                case DAL_EPICS:
                case DAL_TANGO:
                case DAL_TINE:
                    break;
                default:
                    list.add(cs);
            }
        }
        return list.toArray(new ControlSystemEnum[0]);
    }

    @Override
    public String toString() {
        return name();
    }

    public static ControlSystemEnum findByPrefix(String prefix) {
        ControlSystemEnum result = UNKNOWN;
        for (ControlSystemEnum e : values()) {
            if (e.getPrefix().equalsIgnoreCase(prefix)) {
                result = e;
            }
        }

        return result;
    }

    @Override
    public Object getAdapter(Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }
}