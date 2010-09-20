package org.remotercp.ecf.session;

import java.util.List;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.im.IChatManager;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterManager;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.ecf.ECFConnector;

public interface ISessionService {

	public ConnectionDetails getConnectionDetails();

	public void setConnectionDetails(ConnectionDetails connectionDetails);

	public void setContainer(ECFConnector container);

	/**
	 * Returns whether a container has been set on this session service. This is
	 * essentially a workaround for the fact the the SessionService is always
	 * registered, even when there is no session yet. Clients should call this
	 * method to verify that the SessionService is connected before calling the
	 * service's methods.
	 *
	 * @return <code>true</code> if a container has been set, <code>false</code>
	 *         otherwise.
	 */
	public boolean hasContainer();

	public IRosterManager getRosterManager();

	public IRoster getRoster();

	public IChatManager getChatManager();

	public IContainer getContainer();

	/**
	 * Registers a service as remote service for OSGi over ECF
	 *
	 * @param impl
	 *            The object implementing the service.
	 * @param serviceName
	 *            The name of the service interface.
	 * @deprecated To make a service available as a remote service, register it
	 *             as an OSGi service with the property
	 *             <code>org.csstudio.management.remoteservice</code> set to
	 *             <code>Boolean.TRUE</code>.
	 */
	@Deprecated
	public void registerRemoteService(String serviceName, Object impl);

	/**
     * Unget a remote service. This operation should actually be called if a
     * client disconnects. Ask ECF devs if this happens. If yes, delete this
     * method.
     *
     * @param idFilter
     *            The user id array for which the service should be unget
     * @param service
     *            The service interface class
     * @param filter
     * @throws ECFException
     * @throws InvalidSyntaxException
     */
	public void ungetRemoteService(ID[] idFilter,
	                               String serviceName,
	                               String filter) throws ECFException, InvalidSyntaxException;

	/**
	 * Returns a list of remote service proxies for services of the specified
	 * class. Use <code>filterIDs</code> to specify the remote containers from
	 * which to get the services.
	 *
	 * @param <T>
	 *            The service type.
	 * @param clazz
	 *            The class of the remote service.
	 * @param filterIDs
	 *            The IDs of the containers from which to get the remote
	 *            services. If this is <code>null</code>, get services from all
	 *            remote containers.
	 * @param filter
	 *            Additional filter which checks if the service properties do
	 *            match the given filer. May be null if all services should be
	 *            found.
	 * @return A list of remote service proxies.
	 * @throws InvalidSyntaxException
	 *             if <code>filter</code> contains an invalid filter string that
	 *             cannot be parsed.
	 * @deprecated use {@link #getRemoteServiceProxies(Class, ID[], String)}
	 *             instead.
	 */
	@Deprecated
	public <T> List<T> getRemoteService(Class<T> clazz, ID[] filterIDs,
			String filter) throws ECFException, InvalidSyntaxException;

	/**
	 * Returns the remote services that implement the specified class. Use
	 * <code>filterIDs</code> to specify the remote containers from which to get
	 * the services.
	 *
	 * @param clazz
	 *            the class of the remote services.
	 * @param filterIDs
	 *            The IDs of the containers from which to get the remote
	 *            services. If this is <code>null</code>, get services from all
	 *            remote containers.
	 * @param filter
	 *            Additional filter which checks if the service properties do
	 *            match the given filer. May be null if all services should be
	 *            found.
	 * @return an array of remote services.
	 * @throws InvalidSyntaxException
	 *             if <code>filter</code> contains an invalid filter string that
	 *             cannot be parsed.
	 * @deprecated use {@link #getRemoteServices(Class, ID[], String)} instead.
	 */
	@Deprecated
	public IRemoteService[] getRemoteServiceReference(Class<?> clazz,
			ID[] filterIDs, String filter) throws InvalidSyntaxException;

	/**
	 * Returns the remote services that implement the specified class. Use
	 * <code>filterIDs</code> to specify the remote containers from which to get
	 * the services.
	 *
	 * @param clazz
	 *            the class of the remote services.
	 * @param filterIDs
	 *            The IDs of the containers from which to get the remote
	 *            services. If this is <code>null</code>, get services from all
	 *            remote containers.
	 * @param filter
	 *            Additional filter which checks if the service properties do
	 *            match the given filer. May be null if all services should be
	 *            found.
	 * @return a list of remote services.
	 * @throws InvalidSyntaxException
	 *             if <code>filter</code> contains an invalid filter string that
	 *             cannot be parsed.
	 * @see #getRemoteServices(Class, ID[])
	 */
	public List<IRemoteService> getRemoteServices(Class<?> clazz,
			ID[] filterIDs, String filter) throws InvalidSyntaxException;

	/**
	 * <p>
	 * Returns the remote services that implement the specified class. Use
	 * <code>filterIDs</code> to specify the remote containers from which to get
	 * the services.
	 * </p>
	 * <p>
	 * Calling this method will return the same result as calling
	 * <code>getRemoteServices(clazz, filterIDs, null)</code>, but this method
	 * does not throw an <code>InvalidSyntaxException</code>, so it is more
	 * convenient to use by callers that don't need the additional filter
	 * string.
	 * </p>
	 *
	 * @param clazz
	 *            the class of the remote services.
	 * @param filterIDs
	 *            The IDs of the containers from which to get the remote
	 *            services. If this is <code>null</code>, get services from all
	 *            remote containers.
	 * @return a list of remote services.
	 * @see #getRemoteServices(Class, ID[], String)
	 */
	public List<IRemoteService> getRemoteServices(Class<?> clazz, ID[] filterIDs);

	/**
	 * Returns a list of remote service proxies for services of the specified
	 * class. Use <code>filterIDs</code> to specify the remote containers from
	 * which to get the services.
	 *
	 * @param <T>
	 *            The service type.
	 * @param clazz
	 *            The class of the remote service.
	 * @param filterIDs
	 *            The IDs of the containers from which to get the remote
	 *            services. If this is <code>null</code>, get services from all
	 *            remote containers.
	 * @param filter
	 *            Additional filter which checks if the service properties do
	 *            match the given filer. May be null if all services should be
	 *            found.
	 * @return A list of remote service proxies.
	 * @throws InvalidSyntaxException
	 *             if <code>filter</code> contains an invalid filter string that
	 *             cannot be parsed.
	 * @see #getRemoteServiceProxies(Class, ID[])
	 */
	public <T> List<T> getRemoteServiceProxies(Class<T> clazz, ID[] filterIDs,
			String filter) throws InvalidSyntaxException;

	/**
	 * <p>
	 * Returns a list of remote service proxies for services of the specified
	 * class. Use <code>filterIDs</code> to specify the remote containers from
	 * which to get the services.
	 * </p>
	 * <p>
	 * Calling this method will return the same result as calling
	 * <code>getRemoteServiceProxies(clazz, filterIDs, null)</code>, but this
	 * method does not throw an <code>InvalidSyntaxException</code>, so it is
	 * more convenient to use by callers that don't need the additional filter
	 * string.
	 * </p>
	 *
	 * @param <T>
	 *            The service type.
	 * @param clazz
	 *            The class of the remote service.
	 * @param filterIDs
	 *            The IDs of the containers from which to get the remote
	 *            services. If this is <code>null</code>, get services from all
	 *            remote containers.
	 * @return A list of remote service proxies.
	 * @see #getRemoteServiceProxies(Class, ID[], String)
	 */
	public <T> List<T> getRemoteServiceProxies(Class<T> clazz, ID[] filterIDs);
}
