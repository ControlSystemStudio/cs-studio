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
package org.csstudio.domain.desy.preferences;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <li> org.csstudio.domain.desy.net.HostAddress </li>
 * <li> javax.mail,InternetAddress </li>
 *
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010
 */
public abstract class AbstractPreference<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPreference.class);

    private final String _keyAsString;
    private final T _defaultValue;
    private final Class<T> _type;
    private IPreferenceValidator<T> _validator;

    /**
     * Constructor.
     */
    protected AbstractPreference(@Nonnull final String keyAsString,
                                 @Nonnull final T defaultValue,
                                 @Nonnull final Class<T> type) {
        assert keyAsString != null : "keyAsString must not be null";
        assert defaultValue != null : "defaultValue must not be null";

        _keyAsString = keyAsString;
        _defaultValue = defaultValue;
        _type = type;

        PreferencesTypeSupport.install();
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unchecked")
    protected AbstractPreference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        this(keyAsString, defaultValue, (Class<T>) defaultValue.getClass());
    }

    @Nonnull
    protected AbstractPreference<T> addValidator(@Nonnull final IPreferenceValidator<T> validator) {
        _validator = validator;
        if(!_validator.validate(_defaultValue)) {
            throw new IllegalArgumentException("Default value is not valid with this validator.");
        }
        return this;
    }

    @Nonnull
    public final String getKeyAsString() {
        assert _keyAsString != null : "_keyAsString must not be null";

        return _keyAsString;
    }

    /**
     * @return the correctly typed value
     */
    @Nonnull
    public final T getValue() {
        T pref;
        try {
            pref = PreferencesTypeSupport.getPreference(getPluginID(),
                                                        getKeyAsString(),
                                                        _defaultValue);
        } catch (final TypeSupportException e) {
            LOG.warn("Preferences for {} could not be transformed into type {}. Use default value",
                     getKeyAsString(), _type.getSimpleName());
            return _defaultValue;
        }
        return validatedResult(pref, _validator, _defaultValue);
    }

    @Nonnull
    private T validatedResult(@Nonnull final T result,
                              @Nonnull final IPreferenceValidator<T> validator,
                              @Nonnull final T defaultValue) {
        if(validator == null || validator.validate(result)) {
            return result;
        }
        LOG.warn("Preference is not valid for: " + getKeyAsString()
                + "Fall back to default value: " + defaultValue);
        return defaultValue;
    }

    @Nonnull
    public final String getDefaultAsString() {
        assert _defaultValue != null : "_defaultValue must not be null";

        return _defaultValue.toString();
    }

    @Nonnull
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
    @Nonnull
    public List<AbstractPreference<?>> getAllPreferences() {

        final Class<? extends AbstractPreference<?>> clazz = getClassType();

        final Field[] fields = clazz.getFields();

        final List<AbstractPreference<?>> list = new ArrayList<AbstractPreference<?>>();
        for (final Field field : fields) {
            if(field.getType().equals(clazz)) {
                try {
                    final Object pref = field.get(null); // for static fields param is ignored
                    list.add((AbstractPreference<?>) pref);
                } catch (final IllegalAccessException e) {
                    LOG.error("One of the preferences constants in class " + clazz.getName()
                            + " is not accessible.", e);
                }
            }
        }
        return list;
    }

    /**
     * Returns the runtime (sub-)class of this object.
     * @return the runtime (sub-)class of this object
     */
    @Nonnull
    protected abstract Class<? extends AbstractPreference<T>> getClassType();

    /**
     * @return the subclass has to define the plugin ID, this is used as the qualifier for preference retrieval.
     */
    @Nonnull
    public abstract String getPluginID();

}
