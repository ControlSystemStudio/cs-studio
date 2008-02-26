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
 package org.csstudio.archive.desy.aapi;


import org.csstudio.archive.ArchiveInfo;


/** Handles the "archives" request and its results.
 *  @author Albert Kagarmanov
 */
public class ArchivesRequest implements ClientRequest
{
	private ArchiveInfo archive_infos[];

	/** Read info from data server */
	public void read() //throws Exception
	{
		archive_infos = new ArchiveInfo[1];
		archive_infos[0] =  new org.csstudio.archive.desy.aapi.ArchiveInfoImpl(
		0,"AAPI","DESY_AAPI_SERVER");		
//			{  int32 key,
//		     string name,
//		     string path }[] = archiver.archives()
	}

	/** @return Returns all the archive infos obtained in the request. */
    public ArchiveInfo[] getArchiveInfos()
	{
		return archive_infos;
	}

	/** @return Returns a more or less useful string. */
	@Override public String toString()
	{
		StringBuffer result = new StringBuffer();
        for (int i = 0; i < archive_infos.length; i++)
        	result.append(String.format("Key %4d: '%s' (%s)\n",
                archive_infos[i].getKey(),
                archive_infos[i].getName(),
                archive_infos[i].getDescription()));
		return result.toString();
	}
}
