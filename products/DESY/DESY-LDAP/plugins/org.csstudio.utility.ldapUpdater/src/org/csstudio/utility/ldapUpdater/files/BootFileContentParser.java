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
package org.csstudio.utility.ldapUpdater.files;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.file.AbstractLineBasedFileContentParser;
import org.csstudio.domain.desy.net.IpAddress;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.service.util.LdapFieldsAndAttributes;
import org.csstudio.utility.ldapUpdater.UpdaterLdapConstants;

import com.google.common.collect.Maps;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 28.04.2011
 */
public class BootFileContentParser extends AbstractLineBasedFileContentParser {

    private IOC _currentIOC;
    private boolean _ipAddressFound = false;
    private final Map<String, IOC> _iocMap;


    /**
     * Constructor.
     * @throws IOException
     * @throws ParseException
     */
    public BootFileContentParser(@Nonnull final File directory,
                                 @Nonnull final Collection<IOC> iocs) throws IOException, ParseException {
        _iocMap = Maps.newHashMapWithExpectedSize(iocs.size());
        for (final IOC ioc : iocs) {
            _ipAddressFound = false;

            setFieldsToCurrentlyProcessedIOC(ioc);
            processIocFile(directory, ioc);

            if (!_ipAddressFound) {
                throw new ParseException("No IpAddress could be identified for IOC " + ioc.getName() +
                                         " in directory " + directory.getAbsolutePath() , 0);
            }
        }
    }

    private void processIocFile(@Nonnull final File directory,
                                @Nonnull final IOC ioc) throws IOException,
                                                               ParseException {
        final File filePath = new File(directory, ioc.getName() + UpdaterLdapConstants.BOOT_FILE_SUFFIX);

        parseFile(filePath);
    }

    private void setFieldsToCurrentlyProcessedIOC(@Nonnull final IOC ioc) {
        _currentIOC = ioc;
        _iocMap.put(ioc.getName(), _currentIOC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processLine(@Nonnull final String line) {
        if (isNotACommment(line)) {

            final Pattern pattern =
                Pattern.compile("^\\s*" + LdapFieldsAndAttributes.ATTR_VAL_IOC_IP_ADDRESS + "\\s*=\\s*(" + IpAddress.IP_ADDRESS_REGEX +")\\s*$");

            final Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                _currentIOC.setIpAddress(new IpAddress(matcher.group(1)));
                _ipAddressFound = true;
            }
        }

    }

    private boolean isNotACommment(@Nonnull final String line) {
        return !Pattern.matches("^/s*#.*", line);
    }

    @Nonnull
    public Map<String, IOC> getIocMap() {
        return _iocMap;
    }

}
