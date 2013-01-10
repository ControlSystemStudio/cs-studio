
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
 *
 */

package org.csstudio.alarm.jms2ora.service;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * TODO (mmoeller) :
 *
 * @author mmoeller
 * @version 1.0
 * @since 18.08.2011
 */
public class ConnectionInfo {

    private String _userName;

    private String _password;

    private String _dbUrl;

    public ConnectionInfo(@Nonnull final String userName,
                          @Nonnull final String password,
                          @Nonnull final String dbUrl) {

        this._userName = userName;
        this._password = password;
        this._dbUrl = dbUrl;
    }

    @CheckForNull
    public final String getUserName() {
        return _userName;
    }

    public final void setUserName(@Nonnull final String userName) {
        this._userName = userName;
    }

    @CheckForNull
    public String getPassword() {
        return _password;
    }

    public final void setPassword(@Nonnull final String password) {
        this._password = password;
    }

    @CheckForNull
    public String getDbUrl() {
        return _dbUrl;
    }

    public final void setDbUrl(@Nonnull final String dbUrl) {
        this._dbUrl = dbUrl;
    }
}
