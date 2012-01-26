/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmServiceJMSImpl.java,v 1.2
 * 2010/04/26 09:35:21 jpenning Exp $
 */
package org.csstudio.alarm.service.internal;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.dal.CssApplicationContext;
import org.csstudio.dal.simple.SimpleDALBroker;

/**
 * DAL based implementation of the AlarmService.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class AlarmServiceDALImpl implements IAlarmService {

    /**
     * The configuration service
     */
    private final IAlarmConfigurationService _alarmConfigService;

    /**
     * Constructor.
     *
     * @param alarmConfigService .
     */
    public AlarmServiceDALImpl(@Nonnull final IAlarmConfigurationService alarmConfigService) {
        _alarmConfigService = alarmConfigService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IAlarmConnection newAlarmConnection() {
        return new AlarmConnectionDALImpl(_alarmConfigService, SimpleDALBroker.newInstance(new CssApplicationContext("CSS")));
    }


    @Override
    public void retrieveInitialState(@Nonnull final List<IAlarmInitItem> initItems) {
        // There is nothing to do in the DAL implementation. The usual listeners will take care of the state changes.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IAlarmResource createAlarmResource(@CheckForNull final List<String> topics,
                                                    @CheckForNull final String filepath) {
        return new AlarmResource(topics, filepath);
    }

}
