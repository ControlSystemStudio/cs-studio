package org.csstudio.archive;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/** Helper: References an implementor of IArchiveImplementation,
 *  and instantiates it lazily.
 *  @author Jan Hatje
 *  @author Albert Kagarmanov
 *  @author Kay Kasemir
 */
class ArchiveImplementationDescriptor
{
	/** Reference to a configuration element of the Eclipse plugin registry. */
	private IConfigurationElement _configurationElement;

	/** An IArchiveImplementation, which is instantiated lazy. */
	private IArchiveImplementation _archive_implementation;

	/** Constructs a descriptor, which is based on the specified
	 *  configuration element.
	 * 
	 *  @param configurationElement
	 *            the configuration element
	 */
	public ArchiveImplementationDescriptor(
			final IConfigurationElement configurationElement) 
    {
		_configurationElement = configurationElement;
	}

	/** @return The IArchiveImplementation. */
	@SuppressWarnings("nls")
    public IArchiveImplementation getArchiveImplementation()
	{
		if (_archive_implementation == null)
        {
			try
            {
				_archive_implementation = (IArchiveImplementation) _configurationElement
						.createExecutableExtension("class"); //$NON-NLS-1$
			}
            catch (CoreException e)
            {
                CentralLogger.getInstance().error(this,
                                "Cannot create instance of '" +
                                _configurationElement.getName() + "'", e);
			}
		}

		return _archive_implementation;
	}
}
	

