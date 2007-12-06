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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.internal.simpledal.converters.ConverterUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ValueType;

/**
 * Represents a local channel.
 * 
 * @author Sven Wende
 * 
 * @version $Revision$
 */
public final class LocalChannel {

	private IProcessVariableAddress _processVariableAddress;

	/**
	 * The type of values this channel represents.
	 */
	private ValueType _valueType;

	/**
	 * The current value.
	 */
	private Object _currentValue;

	private List<ILocalChannelListener> _listeners;

	public LocalChannel(IProcessVariableAddress pv, ValueType valueType) {
		assert pv != null;
		assert valueType != null;
		_processVariableAddress = pv;
		_valueType = valueType;
		_currentValue = null;
		_listeners = new ArrayList<ILocalChannelListener>();

		for (GeneratedData gd : GeneratedData.values()) {
			Pattern p = gd.getPattern();

			Matcher m = p.matcher(pv.getProperty());

			if (m.find()) {
				final String[] options = new String[m.groupCount()];

				for (int i = 0; i < m.groupCount(); i++) {
					options[i] = m.group(i+1);
				}
				
				final AbstractDataGenerator generator = gd.getDataGeneratorFactory().createGenerator(this, 1000, options);

				// init the current value
				_currentValue = generator.generateNextValue();
				
				// start automatic value generators
				// FIXME: Use a thread pool instead of a single thread for each value!!!!
				Thread t = new Thread(generator);
				t.start();
			}
		}
	}

	public Object getValue(ValueType valueType) {
		return ConverterUtil.convert(_currentValue, valueType);
	}

	public void setValue(Object value) {
		_currentValue = ConverterUtil.convert(value, _valueType);
		fireValueChangeEvent();
	}

	public void addListener(ILocalChannelListener listener) {
		if (!_listeners.contains(listener)) {
			_listeners.add(listener);
		}
	}

	public void removeListener(ILocalChannelListener listener) {
		if (_listeners.contains(listener)) {
			_listeners.remove(listener);
		}
	}

	private void fireValueChangeEvent() {
		for (ILocalChannelListener listener : _listeners) {
			listener.valueChanged(ConverterUtil.convert(_currentValue, listener
					.getExpectedValueType()));
		}
	}
}
