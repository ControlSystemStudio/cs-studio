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
package org.csstudio.platform.model;

import org.csstudio.data.values.IValue;
import org.csstudio.platform.internal.model.ArchiveDataSource;
import org.csstudio.platform.internal.model.ControlSystemItemFactoriesRegistry;
import org.csstudio.platform.internal.model.ProcessVariable;
import org.csstudio.platform.internal.model.ProcessVariableWithArchive;
import org.csstudio.platform.internal.model.ProcessVariableWithSamples;
import org.csstudio.platform.util.ControlSystemItemPath;

/**
 * A factory for control system items. Central control system items (e.g.
 * IProcessVariables), can be obtained by static creation methods.
 *
 * @author Sven Wende
 *
 */
public final class CentralItemFactory {

	/**
	 * Hidden constructor.
	 * Don't instantiate.
	 */
	private CentralItemFactory() {
	    // Empty
	}

	/**
	 * Creates a process variable.
	 *
	 * @param name
	 *            the name of the process variable
	 * @return a process variable
	 */
	public static IProcessVariable createProcessVariable(final String name) {
		return new ProcessVariable(name);
	}

	/**
	 * Creates a archive datasource.
	 *
	 * @param url
	 *            an url
	 * @param key
	 *            a key
	 * @param name
	 *            the name
	 * @return an archive datasource
	 */
	public static IArchiveDataSource createArchiveDataSource(final String url,
			final int key, final String name) {
		assert url != null;
		assert name != null;
		return new ArchiveDataSource(url, key, name);
	}

	/**
	 * Creates a process variable with an archive data source.
	 *
	 * @param pvName
	 *            the name of the process variable
	 * @param url
	 *            an url
	 * @param key
	 *            a key
	 * @param archiveName
	 *            the archive name
	 * @return A process variable with an archive data source.
	 */
	public static IProcessVariableWithArchive createProcessVariableWithArchive(
			final String pvName, final String url, final int key,
			final String archiveName) {
		return new ProcessVariableWithArchive(pvName, url, key, archiveName);
	}

    /**
     *
     * @param name of PV
     * @param samples of PV
     * @return PV with Samples
     */
    public static IProcessVariableWithSamples createProcessVariableWithSamples(final String name,final IValue[] samples) {
        return new ProcessVariableWithSamples(name, samples);
    }

	/**
	 * Create a control system item from the specified path.
	 *
	 * @param path
	 *            the path
	 * @return a control system item or null, if none was identified by the path
	 */
	public static IControlSystemItem createControlSystemItem(
			final ControlSystemItemPath path) {
		assert path != null;
		IControlSystemItem result = null;

		final AbstractControlSystemItemFactory<IControlSystemItem> factory = ControlSystemItemFactoriesRegistry
				.getInstance().getControlSystemItemFactory(path.getTypeId());

		if (factory != null) {
			result = factory.createControlSystemItem(path);
		}

		return result;
	}

	/**
	 * Creates a path for the specified control system item.
	 *
	 * @param item
	 *            the control system item
	 * @return a path
	 */
	public static ControlSystemItemPath createControlSystemItemPath(
			final IControlSystemItem item) {
		assert item != null;
		ControlSystemItemPath result = null;

		final AbstractControlSystemItemFactory<IControlSystemItem> factory = ControlSystemItemFactoriesRegistry
				.getInstance().getControlSystemItemFactory(item.getTypeId());

		if (factory != null) {
			result = factory.getTransportablePath(item);
		}

		return result;
	}

}
