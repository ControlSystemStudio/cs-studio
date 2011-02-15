/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.platform;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Extend this class to implement your preferences.
 *
 * A preference must be defined as a constant in the subclass. This class helps you in retrieving typed values and default values.
 *
 * The constant defines the default value and implicitly the type (it is derived from the default value).
 * The constant also defines the string which is used for accessing the value via the preferences api.
 *
 * @param <T> type of the Preference, must be given in the constant declaration
 *
 * Example:
 * {@code}static final Preference<Integer> TIME = new Preference<Integer>("Time", 3600);
 * 
 * Supported explicit types:<br/>
 * <li> java.util.String </li>
 * <li> java.util.Integer </li>
 * <li> java.util.Long </li>
 * <li> java.util.Float </li>
 * <li> java.util.Double </li>
 * <li> java.util.Boolean </li>
 * <li> java.net.URL </li>
 *
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010
 */
public abstract class AbstractPreference<T> {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(AbstractPreference.class);

    private final String _keyAsString;
    private final T _defaultValue;
    private final Class<?> _type;

    /**
     * Constructor.
     * @param keyAsString the string used to define the preference in initializers
     * @param defaultValue the value used if none is defined in initializers. The type is derived from this value and must match T.
     */
    protected AbstractPreference(final String keyAsString, final T defaultValue) {
        assert keyAsString != null : "keyAsString must not be null";
        assert defaultValue != null : "defaultValue must not be null";

        _keyAsString = keyAsString;
        _defaultValue = defaultValue;
        _type = defaultValue.getClass();
    }

    public final String getKeyAsString() {
        assert _keyAsString != null : "_keyAsString must not be null";

        return _keyAsString;
    }

    /**
     * @return the correctly typed value
     */
    @SuppressWarnings("unchecked")
    public final T getValue() {
        final IPreferencesService prefs = Platform.getPreferencesService();

        Object result = null;

        if (_type.equals(String.class)) {
            result = prefs.getString(getPluginID(), getKeyAsString(), (String) _defaultValue, null);
        } else if (_type.equals(Integer.class)) {
            result = prefs.getInt(getPluginID(), getKeyAsString(), (Integer) _defaultValue, null);
        } else if (_type.equals(Long.class)) {
            result = prefs.getLong(getPluginID(), getKeyAsString(), (Long) _defaultValue, null);
        } else if (_type.equals(Float.class)) {
            result = prefs.getFloat(getPluginID(), getKeyAsString(), (Float) _defaultValue, null);
        } else if (_type.equals(Double.class)) {
            result = prefs.getDouble(getPluginID(), getKeyAsString(), (Double) _defaultValue, null);
        } else if (_type.equals(Boolean.class)) {
            result = prefs.getBoolean(getPluginID(), getKeyAsString(), (Boolean) _defaultValue, null);
        } else if (_type.equals(URL.class)) {
            try {
                result = new URL(prefs.getString(getPluginID(), getKeyAsString(), _defaultValue.toString(), null));
            } catch (final MalformedURLException e) {
                LOG.error("URL preference is not well formed.", e);
            }
        }

        assert result != null : "result must not be null";
        return (T) result;
    }

    public final String getDefaultAsString() {
        assert _defaultValue != null : "_defaultValue must not be null";

        return _defaultValue.toString();
    }

    public final T getDefaultValue() {
        assert _defaultValue != null : "_defaultValue must not be null";

        return _defaultValue;
    }

    /**
     * Collects all preferences that are defined in the derived class.
     * Accessible via any preference field in this derived class (cannot be static unfortunately).
     *
     * @return a list of all preferences
     */
    @SuppressWarnings("unchecked")
    public List<AbstractPreference<?>> getAllPreferences() {

        final Class<? extends AbstractPreference> clazz = getClassType();

        final Field[] fields = clazz.getFields();

        final List<AbstractPreference<?>> list = new ArrayList<AbstractPreference<?>>();
            for (final Field field : fields) {
                if (field.getType().equals(clazz)) {
                    try {
                        final Object pref = field.get(null); // for static fields param is ignored
                        list.add((AbstractPreference<?>) pref);
                    } catch (final IllegalAccessException e) {
                        LOG.error("One of the preferences constants in class " + clazz.getName() + " is not accessible.", e);
                    }
                }
            }
        return list;
    }

    /**
     * Returns the runtime (sub-)class of this object.
     * @return the runtime (sub-)class of this object
     */
    protected abstract Class<? extends AbstractPreference<T>> getClassType();

    /**
     * @return the subclass has to define the plugin ID, this is used as the qualifier for preference retrieval.
     */
    protected abstract String getPluginID();

}
