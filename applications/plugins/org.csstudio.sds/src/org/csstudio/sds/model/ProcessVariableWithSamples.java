/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.sds.model;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.data.values.IValue;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 06.06.2007
 */
public class ProcessVariableWithSamples extends ProcessVariable {
    /**
     * The list of Samples.
     */
    private IValue[] _samplesList = null;
    /**
     * @param name PV Name
     * @param samples of PV
     */
    public ProcessVariableWithSamples(final String name, final IValue[] samples) {
    	super(name);
		_samplesList= samples;
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.model.IProcessVariableWithSamples#getSample(int)
     */
    /**
     * @param index of Sample.
     * @return Sample whit index
     */
    public final IValue getSample(final int index) {
        return _samplesList[index];
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.model.IProcessVariableWithSamples#size()
     */
    /**
     * @return the Number of Samples
     */
    public final int size() {
        return _samplesList.length;
    }
}
