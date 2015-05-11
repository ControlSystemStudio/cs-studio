/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.platform.internal.simpledal.local;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.ExecutionService;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;

/**
 * Represents a local channel.
 *
 * @author Sven Wende
 *
 * @version $Revision$
 */
public final class LocalChannel {
    private ScheduledFuture _scheduledFuture;

    private AbstractDataGenerator _dataGenerator;

    /**
     * The current value.
     */
    private Object _currentValue;

    private List<ILocalChannelListener> _listeners;

    public LocalChannel(IProcessVariableAddress pv) {
        assert pv != null;
        _currentValue = null;
        _listeners = new ArrayList<ILocalChannelListener>();

        // find data generator using regular expressions
        boolean found = false;
        for (DataGeneratorInfos dgInfo : DataGeneratorInfos.values()) {
            if (!found) {
                Pattern p = dgInfo.getPattern();
                Matcher m = p.matcher(pv.getProperty());

                if (m.find()) {
                    found = true; // we apply only the first data generator
                    // that fits

                    // get the options that are encoded in the name of the
                    // process variable
                    final String[] options = new String[m.groupCount()];

                    for (int i = 0; i < m.groupCount(); i++) {
                        options[i] = m.group(i+1);
                    }

                    // create and init the generator
                    _dataGenerator = dgInfo.getDataGeneratorFactory()
                            .createGenerator(this, 1000, options);

                    // init the current value
                    _currentValue = _dataGenerator.generateNextValue();

                    schedule();
                }
            }
        }
    }

    public Object getValue() {
        return _currentValue;
    }

    public void setValue(Object value) {
        _currentValue = value;
        fireValueChangeEvent();
    }

    public void addListener(ILocalChannelListener listener) {
        if (!_listeners.contains(listener)) {
            _listeners.add(listener);
            schedule();
        }
    }

    public void removeListener(ILocalChannelListener listener) {
        boolean removed = _listeners.remove(listener);
        if (removed) {
            schedule();
        }
    }

    private void fireValueChangeEvent() {
        for (ILocalChannelListener listener : _listeners) {
            listener.valueChanged(_currentValue);
        }
    }

    private void schedule() {
        // we need a data generator
        if (_dataGenerator != null) {
            // if nobody listens, we do not need to generate any random data
            if (_listeners.size() == 0) {
                // we cancel all scheduled jobs
                if (_scheduledFuture != null) {
                    boolean stopped = _scheduledFuture.cancel(false);
                    _scheduledFuture = null;
                }
            } else {
                // we schedule the job if necessary
                if (_scheduledFuture == null) {
                    _scheduledFuture = ExecutionService.getInstance()
                            .getScheduledExecutorService().scheduleAtFixedRate(
                                    _dataGenerator, 1000,
                                    _dataGenerator.getPeriod(),
                                    TimeUnit.MILLISECONDS);
                }
            }
        }
    }

}
