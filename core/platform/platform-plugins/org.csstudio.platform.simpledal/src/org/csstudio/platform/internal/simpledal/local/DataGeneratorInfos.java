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

import java.util.regex.Pattern;


/**
 * Process variable name patterns for local channels.
 *
 * @author swende
 *
 */
public enum DataGeneratorInfos {


    /**
     * Countdown generator pattern. The pattern reads the following variables in
     * the process variable name
     * <code> local://property COUNTDOWN:{from}:{to}:{period}:{update} </code>,
     * for example <code>local://abc COUNTDOWN:100:0:10000:200</code> will
     * count down from 100 to 0 in 10 seconds and an update event will be fired
     * each 200 ms.
     *
     */
    COUNTDOWN("^.* COUNTDOWN:([0-9]+):([0-9]+):([0-9]+):([0-9]+)$",
            new CountdownGeneratorFactory()),

    /**
     * Random number generator pattern. The pattern reads the following
     * variables in the process variable name
     * <code> local://property RND:{from}:{to}:{period} </code>, for example
     * <code>local://abc RND:1:100:10</code> which creates random numbers
     * between 1 and 100 every 10 milliseconds.
     *
     */
    RANDOM_NUMBER("^.* RND:([0-9]+):([0-9]+):([0-9]+)$",
            new RandomDoubleGeneratorFactory()),

    /**
     * Class method generator pattern. The pattern reads the following variables
     * in the process variable name
     * <code> local://property CLM:{classname}:{methodname}:{period} </code>,
     * for example <code>local://abc CLM:java.lang.String:toString:10</code>
     * which creates ...
     *
     */
    CLASS_METHOD("^.* CLM:(.+):(.+):([0-9]+)$",
            new ClassMethodGeneratorFactory()),

    SYSTEM_INFO("^.*SINFO:([a-zA-Z0-9]+)(:([0-9]+))?$",
            new SystemInfoGeneratorFactory());

    private Pattern _pattern;
    private IDataGeneratorFactory _dataGeneratorFactory;

    private DataGeneratorInfos(String pattern,
            IDataGeneratorFactory dataGeneratorFactory) {
        assert pattern != null;
        assert dataGeneratorFactory != null;
        _pattern = Pattern.compile(pattern);
        _dataGeneratorFactory = dataGeneratorFactory;
    }

    public Pattern getPattern() {
        return _pattern;
    }

    public IDataGeneratorFactory getDataGeneratorFactory() {
        return _dataGeneratorFactory;
    }

}
