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

/**
 * Information about an archive data source for a process variable.
 * <p>
 * To remain generic, this includes
 * <ul>
 * <li>A URL:<br>
 * All history data sources should have some sort of URL. Not necessarily a URL
 * that a web browser understands, but something that the archive data retrieval
 * library can handle.
 * <li>Name, key:<br>
 * Some archive data sources might provide several sub-archives, one example
 * being the ChannelArchiver's network data server. The numeric key and the
 * user-readable name (inherited from the IControlSystemItem interface) might be
 * redundant, but both are provided for generality.
 * </ul>
 *  *
 * <p>
 * This interface is not intended to be implemented by clients. Instances of
 * archive datasources can be created via the @see {@link CentralItemFactory} factory.
 * 
 * @author Kay Kasemir, Sven Wende
 */
public interface IArchiveDataSource extends IControlSystemItem {
	/**
	 * The global type id.
	 */
	String TYPE_ID = "css:archiveDataSource"; //$NON-NLS-1$

	/** @return The url of the archive data server. */
	String getUrl();

	/** @return The key of the archive under the url. */
	int getKey();
}
