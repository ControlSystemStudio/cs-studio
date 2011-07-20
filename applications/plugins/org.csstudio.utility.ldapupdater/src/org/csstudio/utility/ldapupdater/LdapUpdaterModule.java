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
package org.csstudio.utility.ldapupdater;

import javax.annotation.Nonnull;

import org.csstudio.utility.ldapupdater.service.ILdapFacade;
import org.csstudio.utility.ldapupdater.service.ILdapServiceProvider;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterFileService;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapupdater.service.impl.LdapFacadeImpl;
import org.csstudio.utility.ldapupdater.service.impl.LdapUpdaterFileServiceImpl;
import org.csstudio.utility.ldapupdater.service.impl.LdapUpdaterServiceImpl;

import com.google.inject.AbstractModule;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 27.04.2011
 */
public class LdapUpdaterModule extends AbstractModule {
    private final ILdapServiceProvider _serviceProvider;

    /**
     * Constructor.
     */
    public LdapUpdaterModule(@Nonnull final ILdapServiceProvider provider) {
        _serviceProvider = provider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(ILdapServiceProvider.class).toInstance(_serviceProvider);
        bind(ILdapFacade.class).to(LdapFacadeImpl.class);
        bind(ILdapUpdaterService.class).to(LdapUpdaterServiceImpl.class);
        bind(ILdapUpdaterFileService.class).to(LdapUpdaterFileServiceImpl.class);
    }

}
