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

package org.csstudio.dal.impl;

import org.csstudio.dal.DataAccess;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.SimpleProperty;

import com.cosylab.util.ListenerList;

public abstract class AbstractDataAccessWrapper<T> implements DataAccess<T>
{
    protected static final int UNKNOWN = -1;
    protected DataAccess sourceDA;
    protected Class<T> valClass;
    private ListenerList dvListeners;
    //protected T lastValue;
    protected int conversion = UNKNOWN;

    public class DADynamicValueListener implements DynamicValueListener
    {
        private void fireEvent(DynamicValueEvent event, int id)
        {
            if (!hasDynamicValueListeners()) {
                return;
            }
            //this event violates generics usage - Property is of different type than
            //value
            DynamicValueEvent newEvent =
                new DynamicValueEvent(
                    AbstractDataAccessWrapper.this,
                    event.getProperty(),
                    convertFromOriginal(event.getValue(),
                        (DataAccess)event.getSource()), event.getCondition(),
                    event.getTimestamp(), event.getMessage(), event.getError(),
                    event.getEventID());

            DynamicValueListener<T, SimpleProperty<T>>[] listeners = (DynamicValueListener<T, SimpleProperty<T>>[])getDvListeners()
                .toArray();

            for (int i = 0; i < listeners.length; i++) {
                switch (id) {
                case 0:
                    listeners[i].valueUpdated(newEvent);
                    //lastValue = (T)newEvent.getValue();

                    break;

                case 1:
                    listeners[i].valueChanged(newEvent);
                    //lastValue = (T)newEvent.getValue();

                    break;

                case 2:
                    listeners[i].timeoutStarts(newEvent);

                    break;

                case 3:
                    listeners[i].timeoutStops(newEvent);

                    break;

                case 4:
                    listeners[i].timelagStarts(newEvent);

                    break;

                case 5:
                    listeners[i].timelagStops(newEvent);

                    break;

                case 6:
                    listeners[i].errorResponse(newEvent);

                    break;

                case 7:
                    listeners[i].conditionChange(newEvent);

                    break;
                }
            }
        }

        public void valueUpdated(DynamicValueEvent event)
        {
            fireEvent(event, 0);
        }

        public void valueChanged(DynamicValueEvent event)
        {
            fireEvent(event, 1);
        }

        public void timeoutStarts(DynamicValueEvent event)
        {
            fireEvent(event, 2);
        }

        public void timeoutStops(DynamicValueEvent event)
        {
            fireEvent(event, 3);
        }

        public void timelagStarts(DynamicValueEvent event)
        {
            fireEvent(event, 4);
        }

        public void timelagStops(DynamicValueEvent event)
        {
            fireEvent(event, 5);
        }

        public void errorResponse(DynamicValueEvent event)
        {
            fireEvent(event, 6);
        }

        public void conditionChange(DynamicValueEvent event)
        {
            fireEvent(event, 7);
        }
    }

    public AbstractDataAccessWrapper(Class<T> valClass, DataAccess sourceDA)
    {
        this.sourceDA = sourceDA;
        this.valClass = valClass;
        conversion = getConversion();

        if (conversion == UNKNOWN) {
            throw new IllegalArgumentException();
        }

        sourceDA.addDynamicValueListener(new DADynamicValueListener());
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#addDynamicValueListener(org.csstudio.dal.DynamicValueListener)
     */
    public <P extends SimpleProperty<T>> void addDynamicValueListener(DynamicValueListener<T, P> l)
    {
        getDvListeners().add(l);
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#removeDynamicValueListener(org.csstudio.dal.DynamicValueListener)
     */
    public <P extends SimpleProperty<T>> void removeDynamicValueListener(DynamicValueListener<T, P> l)
    {
        getDvListeners().remove(l);
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#getDynamicValueListeners()
     */
    public DynamicValueListener<T, SimpleProperty<T>>[] getDynamicValueListeners()
    {
        if (hasDynamicValueListeners()) {
            return (DynamicValueListener<T, SimpleProperty<T>>[])
            getDvListeners().toArray(
                    new DynamicValueListener[getDvListeners().size()]);
        }
        return new DynamicValueListener[0];
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#getDataType()
     */
    public Class<T> getDataType()
    {
        return valClass;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#isSettable()
     */
    public boolean isSettable()
    {
        return sourceDA.isSettable();
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#setValue(java.lang.Object)
     */
    public void setValue(T value) throws DataExchangeException
    {
        Object newVal = convertToOriginal(value, sourceDA);

        if (newVal != null) {
            sourceDA.setValue(newVal);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#getValue()
     */
    public T getValue() throws DataExchangeException
    {
        T value = convertFromOriginal(sourceDA.getValue(), sourceDA);

        if (value != null) {
            return value;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.DataAccess#getLatestReceivedValue()
     */
    public T getLatestReceivedValue()
    {
        T value = convertFromOriginal(sourceDA.getLatestReceivedValue(), sourceDA);

        if (value != null) {
            return value;
        }

        return null;
    }

    protected abstract int getConversion();

    protected abstract Object convertToOriginal(T value,
        DataAccess dataAccess);

    protected abstract T convertFromOriginal(Object value,
        DataAccess dataAccess);

    protected ListenerList getDvListeners() {
        if (dvListeners==null) {
            synchronized (this) {
                if (dvListeners==null) {
                     dvListeners= new ListenerList(DynamicValueListener.class);
                }
            }
        }
        return dvListeners;
    }

    public boolean hasDynamicValueListeners() {
        return dvListeners!=null && dvListeners.size()>0;
    }
}

/* __oOo__ */
