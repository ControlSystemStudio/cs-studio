package org.csstudio.archive.reader.aapi;

import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.UnknownChannelException;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ITimestamp;

import de.desy.aapi.AapiClient;
import de.desy.aapi.AapiException;

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
	private AapiClient _aapiClient;
	private String _host;
	private Integer _port;

	public AapiArchiveReader(String url) throws Exception {
		_url = url;
		_aapiClient = createAapiClient(url);
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
		return _aapiClient.getDescription();
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public ArchiveInfo[] getArchiveInfos() {
        return new ArchiveInfo[] {
               new ArchiveInfo("aapi", _host + ":" + Integer.toString(_port), 1)};
	}

	@Override
	public String[] getNamesByPattern(int key, String glob_pattern)
			throws Exception {
		//SDDS as long term archive does not support a name service, use channelarchiver instead.
		return null;
	}

	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
		//SDDS as long term archive does not support a name service, use channelarchiver instead.
		return null;
	}

	@Override
	public ValueIterator getRawValues(int key, String name, ITimestamp start,
			ITimestamp end) throws UnknownChannelException, Exception {
		AapiValueIterator valueIterator = new RawAapiValueIterator(_aapiClient, key, name,
				start, end);
		valueIterator.getData();
		return valueIterator;
	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name,
			ITimestamp start, ITimestamp end, int count)
			throws UnknownChannelException, Exception {
		AapiValueIterator valueIterator = new MinMaxAapiValueIterator(_aapiClient, key, name,
				start, end, count);
		valueIterator.getData();
		return valueIterator;
	}

	@Override
	public void cancel() {
	}

	@Override
	public void close() {
		//TODO (jhatje): Add cancel option for aapi request.
		cancel();
		_aapiClient = null;
	}

	/**
	 * Create the Aapi server for the url
	 * 
	 * TODO (jhatje) aapiClient in factory erzeugen.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private AapiClient createAapiClient(String url) throws Exception {
		String[] urlParts = url.split("//");
		if (!urlParts[0].equalsIgnoreCase("aapi:")) {
			throw new AapiException("Invalid prefix for aapi server");
		}
		String[] hostAndPort = urlParts[1].split(":");
		_host = hostAndPort[0];
		_port = Integer.parseInt(hostAndPort[1]);
		return new AapiClient(_host, _port);
	}
}
