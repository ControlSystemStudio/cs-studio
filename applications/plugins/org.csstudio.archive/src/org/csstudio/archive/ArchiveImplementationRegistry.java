package org.csstudio.archive;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/** 'Client' code uses this registry to obtain an ArchiveServer for a URL.
 *  @see #getServer(String)
 *  @author Jan Hatje
 *  @author Albert Kagarmanov
 *  @author Kay Kasemir
 */
public class ArchiveImplementationRegistry
{
    /** ID of the extension point that implementing plugins use. */
    private static final String EXTENSION_ID =
        "org.csstudio.archive.ArchiveImplementation"; //$NON-NLS-1$

    /** Default prefix for URLs that have none. */
    private static final String DEFAULT_PREFIX = "aapi"; //$NON-NLS-1$

    /** The singleton instance. */
    private static ArchiveImplementationRegistry _instance;

    /** Contains the AAL implementation descriptors.
     *  <p>
     *  Maps the URL prefix to an implementation.
     */
    final private HashMap<String, ArchiveImplementationDescriptor> _AALImpl
        = new HashMap<String, ArchiveImplementationDescriptor>();

    /** Contains the URL Lists of all ArchiveImplementation providers. */
    final private ArrayList<String> _UrlList = new ArrayList<String>();

    /** Private constructor.
     *  @see #getInstance()
     */
    private ArchiveImplementationRegistry()
    {   lookup();  }

    /** @return The singleton instance. */
    public static ArchiveImplementationRegistry getInstance()
    {
        if (_instance == null)
            _instance = new ArchiveImplementationRegistry();
        return _instance;
    }

    /** Internal lookup of the Eclipse extension registry.
     *  <p>
     *  Searches for all implementors of the ArchiveImplementation extension
     *  point, and puts them into the hash map.
     */
    @SuppressWarnings("nls")
    private void lookup()
    {
        final IExtensionRegistry extReg = Platform.getExtensionRegistry();
        final IConfigurationElement[] confElements = extReg
                        .getConfigurationElementsFor(EXTENSION_ID);

        for (IConfigurationElement element : confElements)
        {
            try
            {
                final IArchiveImplementation aalImpl = (IArchiveImplementation)
                    element.createExecutableExtension("class");
                final String urls[] = aalImpl.getURLList();
                if (urls != null)
                    for (String url : urls)
                        _UrlList.add(url);
            }
            catch (CoreException e)
            {
                Activator.getLogger().error(
                       "Error while creating '" + element.getName() + "'", e); 
            }

            final String prefix = element.getAttribute("prefix");
            if (_AALImpl.containsKey(prefix))
                throw new IllegalArgumentException("URL prefix >>" + prefix
                                + "<< is in use"); 
            Activator.getLogger().debug(
                            "Registering handler for URL prefix '"
                            + prefix + "'");
            _AALImpl.put(prefix, new ArchiveImplementationDescriptor(element));
        }
    }

    /** @return A list of all default URLs that implementations provided. */
    public String[] getUrlList()
    {
        final String urls[] = new String[_UrlList.size()];
        return _UrlList.toArray(urls);
    }

    /** Locate an ArchiveServer based on a URL.
     *  <p>
     *  Searches all implementors of the ArchiveImplementation
     *  extension point for the URL prefix, and obtains
     *  an ArchiveServer instance from one that matches the prefix.
     *  @param url The URL to resolve.
     *  @return The ArchiveServer for the URL or <code>null</code>.
     */
    @SuppressWarnings("nls")
    public ArchiveServer getServer(String url)
    {
        String prefix;
        int i = url.indexOf(':');
        if (i < 0)
            prefix = DEFAULT_PREFIX;
        else
            prefix = url.substring(0, i);
        final ArchiveImplementationDescriptor aid = _AALImpl.get(prefix);
        if (aid == null)
        {
            Activator.getLogger().error(
                            "Unknown prefix in URL '" + url + "'");
            return null;
        }
        try
        {
            return aid.getArchiveImplementation().getServerInstance(url);
        }
        catch (Exception e)
        {
            Activator.getLogger().error(
                            "Cannot resolve URL '" + url + "'", e);
            return null;
        }
    }
}
