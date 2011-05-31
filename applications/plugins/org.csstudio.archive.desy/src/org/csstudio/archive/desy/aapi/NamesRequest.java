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

import org.csstudio.archive.ArchiveAccessException;
import org.csstudio.archive.NameInfo;
import org.csstudio.data.values.TimestampFactory;

import AAPI.AAPI;
import AAPI.AnswerChannelInfo;


/** Handles the "archiver.names" request and its results.
 *  @author Albert Kagarmanov
 */
public class NamesRequest implements ClientRequest
{
	private final boolean debug=false;
	private final boolean debugOut=false;
	private final int key;
	private final String pattern;
	private NameInfo names[];
	public void read(){};
	/** Create a name lookup.
	 *   @param pattern Regular expression pattern for the name.
	 */
	public NamesRequest(final int key, final String pattern)
	{
		this.key = key;
		this.pattern = pattern;
	}

	/**
	 * Read info from data server
	 * @param aapi
	 * @throws ArchiveAccessException
	 */
	public void read(final AAPI aapi) throws ArchiveAccessException
	{


		//	{ string name,
		//    int32 start_sec,  int32 start_nano,
		//	  int32 end_sec,    int32 end_nano
		//   }[] = archiver.names(int32 key,  string pattern)

		//public String[] getRegExpChannelList(String regExp) {

		String[] result;
		int size;
		try {
			result = aapi.getRegExpChannelList(pattern);
			if (result == null) {
				System.out.println("getRegExpChannelList return null" );
				size = 0;
			} else {
                size = result.length;
            }
		} catch (final RuntimeException e) {
			e.printStackTrace();
			throw new ArchiveAccessException("AAPI getRegExpChannelList call failed");
		}
		names = new NameInfo[size];
		int from=0,u_from=0, to=0, u_to=0;
		String PV;
		for (int i=0; i<size; i++)
		{
			PV=result[i];
			if (debugOut) {
                System.out.println("\t\t PV="+ PV +";" );
            }
			final AnswerChannelInfo chInfo=aapi.getChannelInfo(PV);
			if (chInfo != null) {
				from  =chInfo.getFromTime();
				u_from=chInfo.getFromUTime();
				to    =chInfo.getToTime();
				u_to  =chInfo.getToUTime();
			} else {
				System.out.println("getChannelInfo for "+ PV +"returns null" );

			}

			names[i] = new NameInfo(PV,
                            TimestampFactory.createTimestamp(from,u_from),
                            TimestampFactory.createTimestamp(to, u_to));
		}
	}

	/** @return Returns the name infos that were found. */
	public final NameInfo[] getNameInfos()
	{
		return names;
	}

	/** @return Returns a more or less useful string. */
	@Override public String toString()
	{
		final StringBuffer result = new StringBuffer();
		result.append(String.format("Names with key %d matching '%s':\n",
				key, pattern));
		for (int i=0; i<names.length; ++i)
		{
			if (i>0) {
                result.append(", ");
            }
			result.append('\'');
			result.append(names[i].getName());
			result.append('\'');
		}
		return result.toString();
	}
}
