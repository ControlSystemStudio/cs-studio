/**
 * <p>
 * Management Command Service package. The Management Command Service can be
 * used to define commands that can be used by administrative tools to manage
 * a running CSS application.
 * </p>
 *
 * <p>
 * To provide a management command, clients must implement the
 * {@link IManagementCommand} interface and register the management command as
 * an extension using the <code>managementCommands</code> extension point.
 * Paramaters which the command expects can be specified declaratively as part
 * of the extension declaration.
 * </p>
 *
 * <p>
 * To call a mangement command, the {@link IManagementCommandService} can be
 * used. The platform publishes an implementation of this interface as an OSGi
 * service. This service can be used to call the management commands provided
 * by the application itself (i.e., it can be used to locally call management
 * commands). Other plug-ins can use this service to make those management
 * commands available for remote access by remote management tools.
 * </p>
 *
 * <p>
 * Management commands can return results. Results consist of a type, which
 * indicates how the result should be handled, and a type-specific serializable
 * object. Management tools should use a {@link ResultDispatcher} to dispatch
 * the result to an appropriate {@link IResultReceiver}. Receivers can be
 * contributed using the <code>managementCommandResultReceivers</code>
 * extension point.
 * </p>
 */
package org.csstudio.remote.management;
