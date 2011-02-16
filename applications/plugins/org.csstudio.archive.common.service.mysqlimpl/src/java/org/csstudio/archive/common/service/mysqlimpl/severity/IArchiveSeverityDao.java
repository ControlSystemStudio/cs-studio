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
 */
package org.csstudio.archive.common.service.mysqlimpl.severity;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.severity.ArchiveSeverityId;
import org.csstudio.archive.common.service.severity.IArchiveSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;

/**
 * Dao for archive severity.
 *
 * @author bknerr
 * @since 19.11.2010
 */
public interface IArchiveSeverityDao {

    /**
     * @param sev
     * @return
     * @throws ArchiveSeverityDaoException
     */
    @CheckForNull
    ArchiveSeverityId retrieveSeverityId(@Nonnull final EpicsAlarmSeverity sev) throws ArchiveDaoException;

    /**
     *
     * @param sev
     * @return
     * @throws ArchiveSeverityDaoException
     */
    @CheckForNull
    IArchiveSeverity retrieveSeverity(@Nonnull final EpicsAlarmSeverity sev) throws ArchiveDaoException;

    /**
     * @param id
     * @return
     * @throws ArchiveDaoException
     */
    @CheckForNull
    IArchiveSeverity retrieveSeverityById(@Nonnull final ArchiveSeverityId id) throws ArchiveDaoException;

}
