/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.authorization;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import org.csstudio.security.SecurityPreferences;

/** AuthorizationProvider that reads configuration from a file
 *
 *  <p>See <code>authorization.conf</code> for example.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FileBasedAuthorizationProvider implements AuthorizationProvider
{
    final private String config_file_path;

    /** Initialize from preferences
     *  @throws Exception on error
     */
    public FileBasedAuthorizationProvider() throws Exception
    {
        this(SecurityPreferences.getAuthorizationFile());
    }

    /** Initialize
     *  @param config_file_path Path to authentication file
     *  @throws Exception on error
     */
    public FileBasedAuthorizationProvider(final String config_file_path) throws Exception
    {
        this.config_file_path = config_file_path;
    }

    /** Read authentication file
     *  @return Map from authorization to patterns of user names who hold that authorization
     *  @throws Exception on error
     */
    private Map<String, List<Pattern>> readConfigurationFile() throws Exception
    {
        final InputStream config_stream = getInputStream(config_file_path);

        final Logger logger = Logger.getLogger(getClass().getName());
        final Properties settings = new Properties();
        settings.load(config_stream);

        final Map<String, List<Pattern>> rules = new HashMap<>();
        for (String authorization : settings.stringPropertyNames())
        {
            final String auth_setting_cfg = settings.getProperty(authorization);
            final String[] auth_setting = auth_setting_cfg.split("\\s*,\\s*");
            logger.fine("Authorization '" + authorization + "' : Name Patterns " + Arrays.toString(auth_setting));
            final List<Pattern> patterns = new ArrayList<>(auth_setting.length);
            for (String setting : auth_setting)
                patterns.add(Pattern.compile(setting));
            rules.put(authorization, patterns);
        }
        return rules;
    }

    /** @param path Plain file or "platform:.." path
     *  @return {@link InputStream}
     *  @throws Exception on error
     */
    private static InputStream getInputStream(final String path) throws Exception
    {
        try
        {   // Try URL, which would handle "platform:" but also "http:" etc.
            return new URL(path).openStream();
        }
        catch (Exception ex)
        {   // Fall back to plain file
            return new FileInputStream(path);
        }
    }

    /** Check if user matches a pattern
     *  @param user {@link Subject} that describes user
     *  @param patterns User name {@link Pattern}s
     *  @return <code>true</code> if user matches one of the patterns
     */
    private static boolean userMatchesPattern(final Subject user, final List<Pattern> patterns)
    {
        for (Pattern pattern : patterns)
        {   // Check each of the user's principal
            for (Principal principal : user.getPrincipals())
            {
                final String name = principal.getName();
                if (pattern.matcher(name).matches())
                    return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Authorizations getAuthorizations(final Subject user) throws Exception
    {
        final Map<String, List<Pattern>> rules = readConfigurationFile();
        final Set<String> authorizations = new HashSet<>();
        for (Entry<String, List<Pattern>> rule : rules.entrySet())
        {
            final String permission = rule.getKey();
            final List<Pattern> patterns = rule.getValue();
            if (userMatchesPattern(user, patterns))
                authorizations.add(permission);
        }
        return new Authorizations(authorizations);
    }
}
