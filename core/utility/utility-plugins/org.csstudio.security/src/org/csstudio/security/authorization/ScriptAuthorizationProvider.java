/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.authorization;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import org.csstudio.security.SecurityPreferences;
import org.csstudio.security.SecuritySupport;

/** AuthorizationProvider that invokes external command (script) to determine authorizations.
 * 
 *  <p>See <code>id_auth.sh</code> for example.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScriptAuthorizationProvider implements AuthorizationProvider
{
    final private String script;
    
    /** Thread that reads stream into buffer */
    static class StreamReaderThread extends Thread
    {
        final private InputStream stream;
        final private StringBuilder text = new StringBuilder();

        /** @param stream Stream to read */
        public StreamReaderThread(final InputStream stream)
        {
            this.stream = stream;
        }

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            try
            {
                final InputStreamReader reader = new InputStreamReader(stream);
                final char[] buf = new char[512];
                int chars;
                while ((chars = reader.read(buf)) >= 0)
                    text.append(buf, 0, chars);
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        /** @return Text read from stream */
        @Override
        public String toString()
        {
            return text.toString();
        }
    };

    /** Initialize from preferences
     *  @throws Exception on error
     */
    public ScriptAuthorizationProvider() throws Exception
    {
        this(SecurityPreferences.getAuthorizationScript());
    }
    
    /** Initialize
     *  @param config_file_path Path to authentication file
     *  @throws Exception on error
     */
    public ScriptAuthorizationProvider(final String script_path) throws Exception
    {
        this.script = script_path;
    }

    /** {@inheritDoc} */
    @Override
    public Authorizations getAuthorizations(final Subject user) throws Exception
    {
        final Set<String> authorizations = new HashSet<>();
        
        // Execute script
        final Process process = Runtime.getRuntime().exec(
            new String[]
            {
                script,
                SecuritySupport.getSubjectName(user)
            });
        
        // Read output
        final StreamReaderThread result_reader =
            new StreamReaderThread(process.getInputStream());
        final StreamReaderThread error_reader =
            new StreamReaderThread(process.getErrorStream());
        result_reader.start();
        error_reader.start();
        
        // Wait for script to finish
        result_reader.join();
        error_reader.join();
        final String result = result_reader.toString();
        final String error = error_reader.toString();
        
        // Error output or error code?
        if (!error.isEmpty())
            throw new Exception(error);
        if (process.exitValue() != 0)
            throw new Exception(script + " exited with " + process.exitValue());

        // Treat each space-separated text as an authorization
        for (String authorization : result.split(" +"))
            authorizations.add(authorization);
        
        return new Authorizations(authorizations);
    }
}
