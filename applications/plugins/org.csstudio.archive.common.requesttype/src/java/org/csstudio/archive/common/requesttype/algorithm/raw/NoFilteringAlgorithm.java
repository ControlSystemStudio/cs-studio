
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */

package org.csstudio.archive.common.requesttype.algorithm.raw;

import java.util.Collection;
import java.util.regex.Pattern;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.requesttypes.IArchiveRequestType;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.Limits;
import com.google.common.collect.ImmutableSet;

/**
 * TODO (Markus Moeller) : 
 * 
 * @author Markus Moeller
 * @since 01.04.2011
 */
public class NoFilteringAlgorithm implements IArchiveReaderFacade {

	@Override
	public ImmutableSet<IArchiveRequestType> getRequestTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V, T extends IAlarmSystemVariable<V>> Collection<IArchiveSample<V, T>> readSamples(
			String channelName, TimeInstant start, TimeInstant end)
			throws ArchiveServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V, T extends ISystemVariable<V>> Collection<IArchiveSample<V, T>> readSamples(
			String channelName, TimeInstant start, TimeInstant end,
			IArchiveRequestType type) throws ArchiveServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V, T extends ISystemVariable<V>> IArchiveSample<V, T> readLastSampleBefore(
			String channelName, TimeInstant time)
			throws ArchiveServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IArchiveChannel getChannelByName(String name)
			throws ArchiveServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Limits<?> readDisplayLimits(String channelName)
			throws ArchiveServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getChannelsByNamePattern(Pattern pattern)
			throws ArchiveServiceException {
		// TODO Auto-generated method stub
		return null;
	}
}
