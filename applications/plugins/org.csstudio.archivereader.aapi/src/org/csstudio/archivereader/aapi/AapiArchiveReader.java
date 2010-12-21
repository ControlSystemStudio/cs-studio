package org.csstudio.archivereader.aapi;

import org.csstudio.archivereader.ArchiveInfo;
import org.csstudio.archivereader.ArchiveReader;
import org.csstudio.archivereader.UnknownChannelException;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.logging.CentralLogger;

import de.desy.aapi.AapiClient;
import de.desy.aapi.AapiReductionMethod;
import de.desy.aapi.AnswerData;
import de.desy.aapi.RequestData;

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

	private AapiClient createAapiClient(String url) {
		String[] urlParts = url.split("//");
		String[] hostAndPort = urlParts[1].split(":");
		_host = hostAndPort[0];
		_port = null;
		try {
			_port = Integer.parseInt(hostAndPort[1]);
		} catch (NumberFormatException e) {
			CentralLogger.getInstance().getLogger(this).error("invalid url format");
		}
		return new AapiClient(_host, _port);
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String getServerName() {
		System.out.println();
		return "Aapi Server";
	}

	@Override
	public String getURL() {
		System.out.println();
		return _url;
	}

	@Override
	public String getDescription() {
		System.out.println();
		System.out.println();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getVersion() {
		System.out.println();
		// TODO Auto-generated method stub
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
		System.out.println();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNamesByRegExp(int key, String reg_exp) throws Exception {
		System.out.println();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueIterator getRawValues(int key, String name, ITimestamp start,
			ITimestamp end) throws UnknownChannelException, Exception {
		return null;
	}

	@Override
	public ValueIterator getOptimizedValues(int key, String name,
			ITimestamp start, ITimestamp end, int count)
			throws UnknownChannelException, Exception {
		RequestData requestData = new RequestData();
		requestData.setFromTime((int) start.seconds());
		requestData.setToTime((int) end.seconds());
		requestData.setNum(500);
		requestData.setPV(new String[] {name});
		requestData.setConversParam(AapiReductionMethod.MIN_MAX_AVERAGE_METHOD.getMethodNumber());
		AnswerData data = _aapiClient.getData(requestData);
			System.out.println();
		return null;
	}

	@Override
	public void cancel() {
	}

	@Override
	public void close() {
	}

}
