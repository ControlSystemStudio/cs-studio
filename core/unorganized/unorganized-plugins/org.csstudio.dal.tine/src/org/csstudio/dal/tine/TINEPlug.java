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

package org.csstudio.dal.tine;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.csstudio.dal.RemoteException;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.impl.PropertyUtilities;
import org.csstudio.dal.proxy.AbstractPlug;
import org.csstudio.dal.proxy.DeviceProxy;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.simple.RemoteInfo;

import de.desy.tine.definitions.TFormat;


/**
 * Implementation of DAL plugin for TINE.
 *
 * @author Jaka Bobnar, Cosylab
 *
 */
public class TINEPlug extends AbstractPlug {

    /** Plug type string */
    public static final String PLUG_TYPE = "TINE";

    /** Default authority */
    public static final String DEFAULT_AUTHORITY = "DEFAULT";

    /** Singleton instance of plug */
    private static TINEPlug instance;

    private static Map<String, Map<String,Object>> characteristicsMap;

    /**
     * Returns the instance of this class with null properties
     * @return
     */
    public static final TINEPlug getInstance()
    {
        return TINEPlug.getInstance((Properties)null);
    }

    /**
     * Returns the instance of this plug with specified configuration
     * @param conf configuration properties
     * @return
     */
    public static final synchronized TINEPlug getInstance(Properties conf)
    {
        if (TINEPlug.instance == null) {
            TINEPlug.instance = new TINEPlug(conf);
            TINEPlug.characteristicsMap = new HashMap<String, Map<String,Object>>();
        }

        return TINEPlug.instance;
    }

    /**
     * Returns the instance of this plug with specified configuration
     * @param conf configuration properties
     * @return
     */
    public static final synchronized TINEPlug getInstance(AbstractApplicationContext ctx)
    {
        return new TINEPlug(ctx);
    }

    /**
     * Singleton object.
     *
     * @param configuration
     */
    private TINEPlug(Properties configuration) {
        super(configuration);
        initialize();
    }

    /**
     * Singleton object.
     *
     * @param configuration
     */
    private TINEPlug(AbstractApplicationContext ctx) {
        super(ctx);
        initialize();
    }

    private void initialize() {
        getConfiguration().putAll(System.getProperties());
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#getPlugType()
     */
    @Override
    public String getPlugType() {
        return TINEPlug.PLUG_TYPE;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#createNewDeviceProxy(java.lang.String, java.lang.Class)
     */
    @Override
    protected <T extends DeviceProxy<?>> T createNewDeviceProxy(String uniqueName, Class<T> type) {
        throw new UnsupportedOperationException("Devices not supported");
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#createNewDirectoryProxy(java.lang.String)
     */
    @Override
    protected DirectoryProxy<?> createNewDirectoryProxy(String uniqueName) {
        // directory is already added to cache in createNewPropertyProxy method
        throw new RuntimeException("Error in factory implementation, PropertyProxy must be created first.");
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#createNewPropertyProxy(java.lang.String, java.lang.Class)
     */
    @Override
    protected <T extends PropertyProxy<?,?>> T createNewPropertyProxy(String uniqueName, Class<T> type) throws ConnectionException {
        try {
            PropertyProxy<?,?> p = type.getConstructor(String.class).newInstance(uniqueName);
            // adding to directory cache as well
            if (p instanceof DirectoryProxy) {
                putDirectoryProxyToCache((DirectoryProxy<?>)p);
            }

            return type.cast(p);
        } catch (Exception e) {
            throw new ConnectionException(this,"Failed to instantitate property proxy '"+uniqueName+"' for type '"+type.getName()+"'.",e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#getDeviceImplementationClass(java.lang.String)
     */
    @Override
    protected Class<? extends AbstractDevice> getDeviceImplementationClass(String uniqueDeviceName) {
        throw new UnsupportedOperationException("Devices not supported");
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#getDeviceProxyImplementationClass(java.lang.String)
     */
    @Override
    protected Class<? extends DeviceProxy<?>> getDeviceProxyImplementationClass(String uniqueDeviceName) {
        throw new UnsupportedOperationException("Devices not supported");
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.String)
     */
    @Override
    protected Class<? extends SimpleProperty<?>> getPropertyImplementationClass(String uniquePropertyName) {
        return PropertyProxyUtilities.getPropertyImplementationClass(uniquePropertyName);
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#getPropertyProxyImplementationClass(java.lang.String)
     */
    @Override
    protected Class<? extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(String uniquePropertyName) {
        return PropertyProxyUtilities.getPropertyProxyImplementationClass(uniquePropertyName);
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#getPropertyImplementationClass(java.lang.Class)
     */
    @Override
    public Class<?extends SimpleProperty<?>> getPropertyImplementationClass(
                Class<?extends SimpleProperty<?>> type, String propertyName) {
        if (type != null) {
            return PropertyUtilities.getImplementationClass(type);
        } else {
            return getPropertyImplementationClass(propertyName);
        }
    }

    @Override
    public Class<?extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(
            Class<?extends SimpleProperty<?>> type, Class<?extends SimpleProperty<?>> implementationType, String uniquePropertyName) throws RemoteException
    {
        Class<? extends PropertyProxy<?,?>> impl = PropertyProxyUtilities.getPropertyProxyImplementationClass(uniquePropertyName, type);

        if (impl == null) {
            impl = super.getPropertyProxyImplementationClass(type,implementationType, uniquePropertyName);
        }
        if (impl == null) {
            return PropertyProxyUtilities.getProxyImplementationClass(type);
        } else {
            return impl;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.AbstractPlug#releaseInstance()
     */
    @Override
    public void releaseInstance() throws Exception {
        //
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.context.PlugContext#createRemoteInfo(java.lang.String)
     */
    public RemoteInfo createRemoteInfo(String uniqueName) throws NamingException {
        return new RemoteInfo(TINEPlug.PLUG_TYPE, uniqueName, null, null);
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.context.PlugContext#getDefaultDirectory()
     */
    public DirContext getDefaultDirectory() {
        return null;
    }

    /**
     * Checks if the plug already contains characteristics for the given property name.
     * @param name the full name of device and property
     * @return
     */
    public boolean containsName(String name) {
        return TINEPlug.characteristicsMap.containsKey(name);
    }

    /**
     * Returns the characteristics for the given property name.
     *
     * @param uniqueName the name of the property
     * @return map of all characteristics
     * @throws ConnectionFailed
     */
    public Map<String,Object> getCharacteristics(String uniqueName) throws ConnectionFailed {
        return getCharacteristics(uniqueName, Double.class);
    }

    /**
     * Returns the characteristics for the given property name. The numberType parameter
     * specifies the type of numeric characteristics (eg. if numberType is Long.class
     * numeric characteristics will be returned as longs).
     *
     * @param uniqueName the name of the property
     * @param numberType type of numeric properties
     * @return map of all characteristics
     * @throws ConnectionFailed
     */
    public Map<String,Object> getCharacteristics(String uniqueName, Class numberType) throws ConnectionFailed {
        if (TINEPlug.characteristicsMap.containsKey(uniqueName)) {
            return TINEPlug.characteristicsMap.get(uniqueName);
        }
        Map<String,Object> entry = PropertyProxyUtilities.getCharacteristics(new PropertyNameDissector(uniqueName), numberType);
        TINEPlug.characteristicsMap.put(uniqueName, entry);
        return entry;

    }

    /**
     * Returns the TFormat characteristic of the given property.
     *
     * @param uniqueName the name of the property
     * @return
     */
    public TFormat getTFormat(String uniqueName) {
        if (TINEPlug.characteristicsMap.containsKey(uniqueName)) {
            return (TFormat) TINEPlug.characteristicsMap.get(uniqueName).get("dataFormat");
        }
        return null;
    }

    /**
     * Returns the TFormat for the given property dissector.
     *
     * @param dissector
     * @return
     */
    public TFormat getTFormat(PropertyNameDissector dissector) {
        return getTFormat(dissector.getRemoteName());
    }

    /**
     * Returns the sequence length of the given property.
     *
     * @param uniqueName the name of the property
     * @return
     */
    public int getSequenceLength(String uniqueName) {
        if (TINEPlug.characteristicsMap.containsKey(uniqueName)) {
            Object result = TINEPlug.characteristicsMap.get(uniqueName).get("sequenceLength");
            return ((Integer)result).intValue();
        }
        return 0;
    }

    /**
     * Returns the sequence length of the given property dissector.
     *
     * @param dissector
     * @return
     */
    public int getSequenceLength(PropertyNameDissector dissector) {
        return getSequenceLength(dissector.getRemoteName());
    }

}
