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
package org.csstudio.domain.common.preferences;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
 * <li> eclipse.core.runtime.IPath </li>
 *
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010
 */
public abstract class AbstractPreference<T> {


    interface PrefStrategy<T> {
        @Nonnull
        T getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final T defaultValue);
    }
    private static Map<Class<?>, PrefStrategy<?>> TYPE_MAP = new HashMap<Class<?>, PrefStrategy<?>>();
    static {
        TYPE_MAP.put(Integer.class, new PrefStrategy<Integer>() {
            @Override
            @Nonnull
            public Integer getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final Integer defaultValue) {
                final IPreferencesService prefs = Platform.getPreferencesService();
                return prefs.getInt(context, key, defaultValue, null);
            }
        });
        TYPE_MAP.put(Long.class, new PrefStrategy<Long>() {
            @Override
            @Nonnull
            public Long getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final Long defaultValue) {
                final IPreferencesService prefs = Platform.getPreferencesService();
                return prefs.getLong(context, key, defaultValue, null);
            }
        });
        TYPE_MAP.put(Float.class, new PrefStrategy<Float>() {
            @Override
            @Nonnull
            public Float getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final Float defaultValue) {
                final IPreferencesService prefs = Platform.getPreferencesService();
                return prefs.getFloat(context, key, defaultValue, null);
            }
        });
        TYPE_MAP.put(Double.class, new PrefStrategy<Double>() {
            @Override
            @Nonnull
            public Double getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final Double defaultValue) {
                final IPreferencesService prefs = Platform.getPreferencesService();
                return prefs.getDouble(context, key, defaultValue, null);
            }
        });
        TYPE_MAP.put(Boolean.class, new PrefStrategy<Boolean>() {
            @Override
            @Nonnull
            public Boolean getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final Boolean defaultValue) {
                final IPreferencesService prefs = Platform.getPreferencesService();
                return prefs.getBoolean(context, key, defaultValue, null);
            }
        });
        TYPE_MAP.put(String.class, new PrefStrategy<String>() {
            @Override
            @Nonnull
            public String getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final String defaultValue) {
                final IPreferencesService prefs = Platform.getPreferencesService();
                return prefs.getString(context, key, defaultValue, null);
            }
        });
        TYPE_MAP.put(URL.class, new PrefStrategy<URL>() {
            @Override
            @Nonnull
            public URL getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final URL defaultValue) {
                final IPreferencesService prefs = Platform.getPreferencesService();
                try {
                    return new URL(prefs.getString(context, key, defaultValue.toString(), null));
                } catch (final MalformedURLException e) {
                    throw new IllegalArgumentException("URL preference not well-formed. " +
                                                       "That is not supposed to happen, since the defaultValue is by definition of type URL.");
                }
            }
         });
        TYPE_MAP.put(File.class, new PrefStrategy<File>() {
            @Override
            @Nonnull
            public File getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final File defaultValue) {
                final IPreferencesService prefs = Platform.getPreferencesService();
                return new File(prefs.getString(context, key, defaultValue.toString(), null));
            }
        });
        TYPE_MAP.put(IPath.class, new PrefStrategy<IPath>() {
        	@Override
        	@Nonnull
        	public IPath getResult(@Nonnull final String context, @Nonnull final String key, @Nonnull final IPath defaultValue) {
        		final IPreferencesService prefs = Platform.getPreferencesService();
        		return new Path(prefs.getString(context, key, defaultValue.toString(), null));
        	}
        });
    }
    
    
    private final String _keyAsString;
    private final T _defaultValue;
    private final Class<T> _type;

    /**
     * Constructor.
     * @param keyAsString the string used to define the preference in initializers
     * @param defaultValue the value used if none is defined in initializers. The type is derived from this value and must match T.
     */
    @SuppressWarnings("unchecked")
    public AbstractPreference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        assert keyAsString != null : "keyAsString must not be null";
        assert defaultValue != null : "defaultValue must not be null";

        _keyAsString = keyAsString;
        _defaultValue = defaultValue;
        _type = (Class<T>) defaultValue.getClass();
    }

    @Nonnull
    public final String getKeyAsString() {
        assert _keyAsString != null : "_keyAsString must not be null";

        return _keyAsString;
    }

    
    
    /**
     * @return the correctly typed value
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public final T getValue() {
        PrefStrategy<T> prefAction = (PrefStrategy<T>) TYPE_MAP.get(_type);
        return prefAction.getResult(getPluginID(), getKeyAsString(), _defaultValue);
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
            if (field.getType().equals(clazz)) {
                try {
                    final Object pref = field.get(null); // for static fields param is ignored
                    list.add((AbstractPreference<?>) pref);
                } catch (final IllegalAccessException e) {
//                    LOG.error("One of the preferences constants in class " + clazz.getName() + " is not accessible.", e);
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
