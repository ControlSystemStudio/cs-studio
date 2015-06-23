/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.security.internal;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.equinox.security.storage.provider.PasswordProvider;

/** Eclipse password provider based on install location.
 *
 *  <p>Eclipse {@link SecurePreferencesFactory} requires
 *  a master password.
 *
 *  <p>On Windows and OS X, OS-specific plugins (fragments)
 *  <code>org.eclipse.equinox.security.*</code>
 *  provide such a password based on the current user.
 *  On Linux, there is no such implementation,
 *  so the <code>DefaultPasswordProvider</code>
 *  from <code>org.eclipse.equinox.security.ui</code>
 *  will prompt the user.
 *
 *  <p>This password provider should be registered with priority 4.
 *  That's over the priority 2 used by the <code>DefaultPasswordProvider</code>,
 *  so it will suppress the prompt.
 *  The Windows resp. Mac OS X password providers use
 *  priority 5, i.e. on those platforms this password provider
 *  will be ignored.
 *
 *  @author Kay Kasemir
 *  @author Xihui Chen - Original SNSPasswordProvider
 */
@SuppressWarnings("nls")
public class InstallLocationPasswordProvider extends PasswordProvider
{
    @Override
    public PBEKeySpec getPassword(final IPreferencesContainer container,
            final int passwordType)
    {
        // The master password must not include spaces
        final String installLoc = Platform.getInstallLocation()
                .getURL().toString().replaceAll("\\s", "");
        return new PBEKeySpec(installLoc.toCharArray());
    }
}
