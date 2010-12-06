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
package org.csstudio.archive.service.mysqlimpl.sample;

import org.csstudio.domain.desy.data.CumulativeAverageCache;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.ICssValueType;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 25.11.2010
 */
public class SampleAggregator<V, A extends ICssValueType<V>> {
        Double _aggregateVal;
        Double _lastVal;
        Double _minVal;
        Double _maxVal;
        TimeInstant _lastWritetime;
        IComparableAlarm<EpicsAlarm> _alarm;
        IComparableAlarm<EpicsAlarm> _minAlarm;
        int _n = 1;

        CumulativeAverageCache<Double, A> _avg;

        /**
         * Constructor.
         */
        public SampleAggregator(final V firstVal,
                           final IComparableAlarm<EpicsAlarm> newAlarm,
                           final TimeInstant timestamp) {
            Double val;
            if (firstVal instanceof Integer) {
                val = Double.valueOf((Integer) firstVal);
            } else {
                val = (Double) firstVal;
            }
            _aggregateVal = _minVal = _maxVal = _lastVal = val;
            _alarm = newAlarm;
            _minAlarm = newAlarm;
            _lastWritetime = timestamp;
        }

        void aggregateNewVal(final V newVal,
                             final EpicsAlarm alarm) {
            Double val;
            if (newVal instanceof Integer) {
                val = Double.valueOf((Integer) newVal);
            } else {
                val = (Double) newVal;
            }
            _aggregateVal += val;
            _maxVal = Math.max(_maxVal, val);
            _minVal = Math.min(_minVal, val);
            _alarm = _alarm.compareAlarmTo(alarm) < 0 ? alarm : _alarm;
            _n++;
        }

        void reset(final TimeInstant threshold) {
            final double avg = getAvg();
            _aggregateVal = _lastVal = avg;
            _minVal = avg;
            _maxVal = avg;
            _n = 1;
            _alarm = _minAlarm;
            _lastWritetime = threshold;
        }

        Double getAvg() {
            return _aggregateVal / _n;
        }
        Double getMin() {
            return _minVal;
        }
        Double getMax() {
            return _maxVal;
        }
        Double getLastVal() {
            return _lastVal;
        }

        public TimeInstant getLastWriteTime() {
            return _lastWritetime;
        }

}
