package org.csstudio.archive.desy.aapi;


/** Generic interface used by all ...Request classes.
 *  @author Albert Kagarmanov
 */
interface ClientRequest
{
	/** Perform the request, i.e. read from archive data server */
	//public void read(XmlRpcClient xmlrpc) throws Exception;
	public void read();
}
