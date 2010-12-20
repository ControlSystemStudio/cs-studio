package org.csstudio.archivereader.aapi;

import org.csstudio.archivereader.ArchiveInfo;
import org.csstudio.archivereader.ArchiveReader;
import org.csstudio.archivereader.UnknownChannelException;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.logging.CentralLogger;

import de.desy.aapi.AapiClient;

/**
 * Access to aapi server.
 * 
 * @author jhatje
 * @author $Author: jhatje $
 * @version $Revision: 1.7 $
 * @since 17.12.2010
 */
public class AapiArchiveReader implements ArchiveReader {

	private String _url;

	public AapiArchiveReader(String url) throws Exception {
		_url = url;
		String[] urlParts = url.split("//");
		String[] hostAndPort = urlParts[1].split(":");
		String host = hostAndPort[0];
		Integer port = null;
		try {
			port = Integer.parseInt(hostAndPort[1]);
		} catch (NumberFormatException e) {
			CentralLogger.getInstance().getLogger(this).error("invalid url format");
		}
		AapiClient _aapiClient = new AapiClient(host, port);
		String[] channelList = _aapiClient.getChannelList();
		System.out.println();
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String getServerName() {
		return "Aapi Server";
	}

	@Override
	public String getURL() {
		return _url;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArchiveInfo[] getArchiveInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNamesByPattern(int key, String glob_pattern)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueIterator getRawValues(int key, String name, ITimestamp start,
			ITimestamp end) throws UnknownChannelException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name,
			ITimestamp start, ITimestamp end, int count)
			throws UnknownChannelException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
