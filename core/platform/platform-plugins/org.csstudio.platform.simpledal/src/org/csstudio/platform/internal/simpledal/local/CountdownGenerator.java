/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

/**
 * Generator for random double values.
 *
 * @author swende
 *
 */
public class CountdownGenerator extends AbstractDataGenerator<Double> {
    private double _distance;
    private double _from;
    private double _to;
    private long _countdownPeriod;

    private long _startMs=-1;

    /**
     * Constructor.
     * @param localChannel the local channel
     * @param defaultPeriod the default period
     * @param options
     */
    public CountdownGenerator(LocalChannel localChannel, int defaultPeriod,
            String[] options) {
        super(localChannel, defaultPeriod, options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init(String[] options) {
        try {
            _from = Double.parseDouble(options[0]);
        } catch (NumberFormatException nfe) {
            _from = 0;
        }

        try {
            _to = Double.parseDouble(options[1]);
        } catch (NumberFormatException nfe) {
            _to = 1;
        }

        try {
            _countdownPeriod = Long.parseLong(options[2]);
        } catch (NumberFormatException nfe) {
            _to = 1000;
        }


        try {
            int period = Integer.parseInt(options[3]);
            setPeriod(period);
        } catch (NumberFormatException nfe) {
            // ignore
        }

        if (_from < _to) {
            double tmp = _from;
            _from = _to;
            _to = tmp;
        }

        _distance = _from - _to;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Double generateNextValue() {
        double result = -1;

        if(_startMs < 0) {
            _startMs = System.currentTimeMillis();
        }

        long now = System.currentTimeMillis();
        long diff = now-_startMs;


        if(diff>=_countdownPeriod) {
            _startMs = -1;
            result = _from;
        } else {
            double percent = (double) diff/_countdownPeriod;
            result = _from - (_distance * percent);
        }


        return result;
    }

}
