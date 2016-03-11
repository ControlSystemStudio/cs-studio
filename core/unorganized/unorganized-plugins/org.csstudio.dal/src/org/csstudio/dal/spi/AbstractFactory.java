package org.csstudio.dal.spi;

import javax.naming.directory.DirContext;

import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.proxy.AbstractPlug;

/**
 * This is interface with methods, which are common to <code>PropertyFacctory</code>
 * and <code>DeviceFactory</code>.
 *
 *
 * @author ikriznar
 *
 */
public interface AbstractFactory {

    /**
     * If property with this name and value false is provided trough configuration of application
     * context, then plug will use own instance of plug for this factory.
     * @see AbstractFactory#isPlugShared()
     */
    String SHARE_PLUG = "AbstractFactory.plug_shared";

    /**
     * If property withi this name and value true is provided through
     * the configuration to the AbstractFactorySupport, the factory will
     * use a synchronized family to store the properties/devices. If the
     * value is false, the factory will use nonsynchronized version which
     * will have to be synchronized from the outside when the structure of
     * the family is changed (elements added or removed).
     */
    String SYNCHRONIZE_FAMILY = "AbstractFactory.synchronizeFamily";
    /**
     * Returns link policy code. This policy affects how
     * <code>getProperty</code> methods are behaving when new property is
     * created. It can be immediately synchronously or asynchronously linked
     * or left as it tis without linking.
     *
     * @return link policy code, valid values are <code>NO_LINK_POLICY</code>,
     *         <code>SYNC_LINK_POLICY</code> and
     *         <code>ASYNC_LINK_POLICY</code>.
     */
    public LinkPolicy getLinkPolicy();

    /**
     * Return application context which initialized this factory.
     * @return application context which initialized this factory
     */
    public AbstractApplicationContext getApplicationContext();

    /**
     * Must be called by DeviceFactoryService before factory is used. Can be called only once.
     * @param policy
     */
    public void initialize(AbstractApplicationContext ctx,
            LinkPolicy policy);

    /**
     * Return plug type which is used for connection. If this factory serves as facade for multiple plugs,
     * than default type must be returned. This is convenience method,type is obtained from plug.
     *
     * @return plug type which is used for connection
     */
    public String getPlugType();

    /**
     * Returns a default Directory Context. This is convenience method, directory is obtained from PlugContext.
     *
     * @return default directory from PlugContext
     */
    public DirContext getDefaultDirectory();

    /**
     * Return plug which is used for connection. If this factory serves as facade for multiple plugs,
     * than default plug must be returned.
     *
     * @return plug which is used for connection
     */
    public AbstractPlug getPlug();

    /**
     * Return <code>true</code> if this factory is sharing plug instance with other factories.
     * Default DAL implementation is using shared plug instance if possible. This way
     * all connections are shared among different factories and applications within same JVM.
     * @return <code>true</code> if this factory is sharing plug instance with other factories
     */
    public boolean isPlugShared();

}