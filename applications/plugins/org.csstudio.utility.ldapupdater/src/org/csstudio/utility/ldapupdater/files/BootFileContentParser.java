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
package org.csstudio.utility.ldapupdater.files;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.file.AbstractLineBasedFileContentParser;
import org.csstudio.domain.desy.net.IpAddress;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.csstudio.utility.ldapupdater.UpdaterLdapConstants;
import org.csstudio.utility.ldapupdater.model.IOC;
import org.csstudio.utility.ldapupdater.model.Record;

import com.google.common.collect.Maps;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 28.04.2011
 */
public class BootFileContentParser extends AbstractLineBasedFileContentParser {

    private IpAddress _ipAddress;
    private boolean _ipAddressFound = false;
    private final Map<String, IOC> _iocMap;


    /**
     * Constructor.
     * @throws IOException
     * @throws ParseException
     */
    public BootFileContentParser(@Nonnull final File directory,
                                 @Nonnull final Collection<String> iocNames) throws IOException, ParseException {
        _iocMap = Maps.newHashMapWithExpectedSize(iocNames.size());
        for (final String iocName : iocNames) {

            final SortedSet<Record> records = getBootRecordsFromFile(directory, iocName);

            final IpAddress ipAddress = extractIpAddressFromBootFile(directory, iocName);

            final TimeInstant timestamp = extractLastModificationTimeFromBootFile(directory, iocName);

            _iocMap.put(iocName, new IOC(iocName, timestamp, ipAddress, records));
        }
    }

    @Nonnull
    public Map<String, IOC> getIocMap() {
        return _iocMap;
    }

    @Nonnull
    private TimeInstant extractLastModificationTimeFromBootFile(@Nonnull final File directory,
                                                                @Nonnull final String iocName) {
        final File bootFile = new File(directory, iocName + UpdaterLdapConstants.BOOT_FILE_SUFFIX);
        return TimeInstantBuilder.fromMillis(bootFile.lastModified());
    }

    @Nonnull
    private IpAddress extractIpAddressFromBootFile(@Nonnull final File directory,
                                                   @Nonnull final String iocName) throws IOException, ParseException {
        _ipAddressFound = false;
        final File filePath = new File(directory, iocName + UpdaterLdapConstants.BOOT_FILE_SUFFIX);
        parseFile(filePath);
        if (!_ipAddressFound) {
            throw new ParseException("No IpAddress could be identified for IOC " + iocName +
                                     " in directory " + directory.getAbsolutePath() , 0);
        }
        return _ipAddress;
    }

    @Nonnull
    private SortedSet<Record> getBootRecordsFromFile(@Nonnull final File directory,
                                                     @Nonnull final String iocName) throws IOException {
        final RecordsFileContentParser parser = new RecordsFileContentParser();
        parser.parseFile(new File(directory, iocName + UpdaterLdapConstants.RECORDS_FILE_SUFFIX));
        return parser.getRecords();
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
                _ipAddress = new IpAddress(matcher.group(1));
                _ipAddressFound = true;
            }
        }

    }

    private boolean isNotACommment(@Nonnull final String line) {
        return !Pattern.matches("^/s*#.*", line);
    }
}
