/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.preferences;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.csstudio.domain.desy.net.HostAddress;
import org.csstudio.domain.desy.typesupport.AbstractTypeSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.epics.pvmanager.TypeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preferences type support pattern for type safe retrieval of eclipse preferences.
 *
 * @author bknerr
 * @since 08.08.2011
 * @param <T> the type of the preference
 * CHECKSTYLE OFF : AbstractClassName
 */
public abstract class PreferencesTypeSupport<T> extends AbstractTypeSupport<T> {
    // CHECKSTYLE ON : AbstractClassName

    private static final Logger LOG = LoggerFactory.getLogger(PreferencesTypeSupport.class);

    private static boolean INSTALLED;

    /**
     * Constructor.
     */
    public PreferencesTypeSupport(@Nonnull final Class<T> typeClass) {
        super(typeClass, PreferencesTypeSupport.class);
    }

    // CHECKSTYLE OFF : MethodLength
    public static void install() {
        if (INSTALLED) {
            return;
        }
        INSTALLED = true;

        TypeSupport.addTypeSupport(new PreferencesTypeSupport<Integer>(Integer.class) {
            @Override
            @Nonnull
            protected Integer getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                       @Nonnull final String context,
                                                       @Nonnull final String key,
                                                       @Nonnull final Integer defaultValue) {
                return prefs.getInt(context, key, defaultValue, null);
            }
        });

        TypeSupport.addTypeSupport(new PreferencesTypeSupport<Long>(Long.class) {
            @Override
            @Nonnull
            protected Long getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                    @Nonnull final String context,
                                                    @Nonnull final String key,
                                                    @Nonnull final Long defaultValue) {
                return prefs.getLong(context, key, defaultValue, null);
            }
        });
        TypeSupport.addTypeSupport(new PreferencesTypeSupport<Float>(Float.class) {
            @Override
            @Nonnull
            protected Float getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                     @Nonnull final String context,
                                                     @Nonnull final String key,
                                                     @Nonnull final Float defaultValue) {
                return prefs.getFloat(context, key, defaultValue, null);
            }
        });
        TypeSupport.addTypeSupport(new PreferencesTypeSupport<Double>(Double.class) {
            @Override
            @Nonnull
            protected Double getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                      @Nonnull final String context,
                                                      @Nonnull final String key,
                                                      @Nonnull final Double defaultValue) {
                return prefs.getDouble(context, key, defaultValue, null);
            }
        });
        TypeSupport.addTypeSupport(new PreferencesTypeSupport<Boolean>(Boolean.class) {
            @Override
            @Nonnull
            protected Boolean getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                       @Nonnull final String context,
                                                       @Nonnull final String key,
                                                       @Nonnull final Boolean defaultValue) {
                return prefs.getBoolean(context, key, defaultValue, null);
            }
        });
        TypeSupport.addTypeSupport(new PreferencesTypeSupport<String>(String.class) {
            @Override
            @Nonnull
            protected String getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                      @Nonnull final String context,
                                                      @Nonnull final String key,
                                                      @Nonnull final String defaultValue) {
                return prefs.getString(context, key, defaultValue, null);
            }
        });
        TypeSupport.addTypeSupport(new PreferencesTypeSupport<URL>(URL.class) {
            @Override
            @Nonnull
            protected URL getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                   @Nonnull final String context,
                                                   @Nonnull final String key,
                                                   @Nonnull final URL defaultValue) {
                try {
                    return new URL(prefs.getString(context, key, defaultValue.toString(), null));
                } catch (final MalformedURLException e) {
                    LoggerFactory.getLogger(AbstractPreference.class).error("URL preference is not well formed.", e);
                    throw new IllegalArgumentException("URL preference not well-formed. "
                            + "That is not supposed to happen, since the defaultValue is by definition of type URL.");
                }
            }
        });
        TypeSupport.addTypeSupport(new PreferencesTypeSupport<HostAddress>(HostAddress.class) {
            @Override
            @Nonnull
            protected HostAddress getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                           @Nonnull final String context,
                                                           @Nonnull final String key,
                                                           @Nonnull final HostAddress defaultValue) {
                return new HostAddress(prefs.getString(context,
                                                       key,
                                                       defaultValue.getHostAddress(),
                                                       null));
            }
        });
        TypeSupport.addTypeSupport(new PreferencesTypeSupport<InternetAddress>(InternetAddress.class) {
            @Override
            @Nonnull
            protected InternetAddress getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                               @Nonnull final String context,
                                                               @Nonnull final String key,
                                                               @Nonnull final InternetAddress defaultValue) {
                try {
                    return new InternetAddress(prefs.getString(context,
                                                               key,
                                                               defaultValue.getAddress(),
                                                               null));
                } catch (@Nonnull final AddressException e) {
                    LoggerFactory.getLogger(AbstractPreference.class).error("InternetAddress preference is not well formed.", e);
                    throw new IllegalArgumentException("Preference is not well-formed. "
                                                       + "That is not supposed to happen, since the defaultValue is by definition of type " + InternetAddress.class.getSimpleName());
                }
            }
        });
        TypeSupport.addTypeSupport(new PreferencesTypeSupport<File>(File.class) {
            @Override
            @Nonnull
            protected File getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                    @Nonnull final String context,
                                                    @Nonnull final String key,
                                                    @Nonnull final File defaultValue) {
                return new File(prefs.getString(context, key, defaultValue.toString(), null));
            }
        });
        TypeSupport.addTypeSupport(new PreferencesTypeSupport<IPath>(IPath.class) {
            @Override
            @Nonnull
            public IPath getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                  @Nonnull final String context,
                                                  @Nonnull final String key,
                                                  @Nonnull final IPath defaultValue) {
                final String value = prefs.getString(context, key, defaultValue.toString(), null);

                final IWorkspace workspace = ResourcesPlugin.getWorkspace();
                final IResource findMember = workspace.getRoot().findMember(value);
                return findMember == null ? new Path(value) :
                                            findMember.getFullPath();
            }
        });
    }
 // CHECKSTYLE ON : MethodLength

    @Nonnull
    public static <T> T getPreference(@Nonnull final String context,
                                      @Nonnull final String key,
                                      @Nonnull final T defaultValue) throws TypeSupportException {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) {
            LOG.warn("Preference service unavailable, fall back to default preference.");
            return defaultValue;
        }

        @SuppressWarnings("unchecked")
        final Class<T> typeClass = (Class<T>) defaultValue.getClass();
        final PreferencesTypeSupport<T> support =
            (PreferencesTypeSupport<T>) findTypeSupportForOrThrowTSE(PreferencesTypeSupport.class, typeClass);

        return support.getResultFromPreferences(prefs, context, key, defaultValue);
    }

    @Nonnull
    protected abstract T getResultFromPreferences(@Nonnull final IPreferencesService prefs,
                                                  @Nonnull final String context,
                                                  @Nonnull final String key,
                                                  @Nonnull final T defaultValue);
}
