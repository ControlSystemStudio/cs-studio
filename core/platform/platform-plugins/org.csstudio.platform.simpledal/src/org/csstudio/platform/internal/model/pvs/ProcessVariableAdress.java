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
 /**
 *
 */
package org.csstudio.platform.internal.model.pvs;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.dal.simple.RemoteInfo;

/**
 * {@link IProcessVariableAddress} implementation.
 *
 * @author Sven Wende
 *
 */
final class ProcessVariableAdress implements IProcessVariableAddress {
    /**
     * The control system.
     */
    private ControlSystemEnum _controlSystem;

    /**
     * The device.
     */
    private String _device;

    /**
     * The property.
     */
    private String _property;

    /**
     * The property.
     */
    private String _characteristic;

    /**
     * The raw name.
     */
    private String _rawName;

    private ValueType _valueTypeHint;

    /**
     * Constructs a process variable address using the provided information
     * pieces.
     *
     * @param rawName
     *            the raw address (mandatory)
     * @param controlSystem
     *            the control system (mandatory)
     * @param device
     *            a device name (optional, provide null to leave it out)
     * @param property
     *            the property part of the address (mandatory)
     * @param characteristic
     *            the characteristics part of the address (optional, provide
     *            null to leave it out)
     */
    public ProcessVariableAdress(final String rawName,
            final ControlSystemEnum controlSystem, final String device,
            final String property, final String characteristic) {
        assert controlSystem != null;
        assert rawName != null;
        assert property != null;
        _rawName = rawName;
        _controlSystem = controlSystem;
        _device = device;
        _property = property;
        _characteristic = characteristic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueType getValueTypeHint() {
        return _valueTypeHint;
    }

    public void setValueTypeHint(ValueType valueTypeHint) {
        _valueTypeHint = valueTypeHint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCharacteristic() {
        return _characteristic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDevice() {
        return _device;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProperty() {
        return _property;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteInfo toDalRemoteInfo() {
        assert _controlSystem != null;
        assert _property != null;

        RemoteInfo remoteInfo = null;

        if (_controlSystem.isSupportedByDAL()) {
            remoteInfo = new RemoteInfo(
                    _controlSystem.getResponsibleDalPlugId()
                    ,_property
                    ,null
                    ,null);
        }
        return remoteInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullName() {
        assert _controlSystem != null;
        assert _property != null;
        StringBuffer sb = new StringBuffer();

        if (_controlSystem != ControlSystemEnum.UNKNOWN) {
            sb.append(_controlSystem.getPrefix());
            sb.append("://");
        }

        sb.append(_property);

        if (_characteristic != null) {
            sb.append("[");
            sb.append(_characteristic);
            sb.append("]");
        }

        if (_valueTypeHint != null) {
            sb.append(", ");
            sb.append(_valueTypeHint.toPortableString());
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControlSystemEnum getControlSystem() {
        return _controlSystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRawName() {
        return _rawName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCharacteristic() {
        return (_characteristic != null && _characteristic.length() > 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ProcessVariableAdress) {
            ProcessVariableAdress that = (ProcessVariableAdress) obj;
            return this.getFullName().equals(that.getFullName());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getFullName().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getFullName();
    }

    /**
     * {@inheritDoc}
     */
    public String toString2() {
        StringBuffer sb = new StringBuffer();
        sb.append("Raw Name: " + _rawName);
        sb.append("\n");
        sb.append("Control-System: " + _controlSystem);
        sb.append("\n");
        sb.append("Property-Part: " + _property);
        sb.append("\n");
        sb.append("Device-Part: " + _device);
        sb.append("\n");
        sb.append("Characteristic-Part: " + _characteristic);
        sb.append("\n");
        sb.append("RemoteInfo: " + toDalRemoteInfo());
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public IProcessVariableAddress deriveNoCharacteristicPart() {
        // TODO: is it OK to keep raw name?
        return new ProcessVariableAdress(_rawName,_controlSystem,_device,_property,null);
    }

    @Override
    public IProcessVariableAddress deriveCharacteristic(String characteristic) {
        // TODO: is it OK to keep raw name?
        return new ProcessVariableAdress(_rawName,_controlSystem,_device,_property,characteristic);
    }
}