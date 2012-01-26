/**
 * 
 */
package org.csstudio.dal.simple;

/**
 * Objects with parameters, which specifies to DAL 
 * how to connect to remote data entity.
 * 
 * @author ikriznar
 *
 */
public class ConnectionParameters {

	private RemoteInfo remoteInfo;
	private DataFlavor connectionType;
	private DataFlavor dataType;
	private int hashCode;
	
	/**
	 * Creates new instance of connection parameters.
	 * 
	 * @param remoteInfo the connection name for remote object, can describe device, property or characteristic.
	 * @param connectionType 
	 * 		hint for DAL how to create connection to remote object if DAL needs to establish connection type when connecting.
	 *  	If <code>null</code> then DAL decides which type to use.
	 * @param dataType 
	 * 		hint for DAL for which data type to retrieve remote data. Note that connection type can be different from data type.
	 * 		If <code>null</code> then DAL decides which type to use.
	 */
	public ConnectionParameters(RemoteInfo remoteInfo,
			DataFlavor connectionType, DataFlavor dataType) {
		super();
		this.remoteInfo = remoteInfo;
		this.connectionType = connectionType;
		this.dataType = dataType;
	}
	
	/**
	 * Creates new instance of connection parameters.
	 * 
	 * @param remoteInfo the connection name for remote object, can describe device, property or characteristic.
	 * @param javaType hint for DAL for which data type to retrieve remote data. Note that connection type can be 
	 * different from data type, in this case it will be determined from data type.
	 */
	public ConnectionParameters(RemoteInfo remoteInfo,
			Class<?> javaType) {
		this(remoteInfo,DataFlavor.fromJavaType(javaType),DataFlavor.fromJavaType(javaType));
	}

	/**
	 * Creates new instance of connection parameters. This connection parameters object will have
	 * <code>null</code> for connection and data type, which means that it will be left to DAL
	 * to use appropriate types.
	 * 
	 * @param rinfo the connection name for remote object, can describe device, property or characteristic.
	 */
	public ConnectionParameters(RemoteInfo rinfo) {
		this(rinfo,null,null);
	}

	/**
	 * The connection name for remote object, can describe device, property or characteristic.
	 * @return remote info
	 */
	public RemoteInfo getRemoteInfo() {
		return remoteInfo;
	}

	/**
	 * Hint for DAL how to create connection to remote object if DAL needs to establish connection type when connecting.
	 * @return type of connection
	 */
	public DataFlavor getConnectionType() {
		return connectionType;
	}

	/**
	 * Hint for DAL for which data type to retrieve remote data. 
	 * Note that connection type can be different from data type.
	 * @return type of data retrieval
	 */
	public DataFlavor getDataType() {
		return dataType;
	}
	
	
	@Override
	public String toString() {
		// TODO should we make this string in a way, that can be sued for serialization/deserialization?
		
		StringBuilder sb= new StringBuilder();
		sb.append("ConnParam={rinfo=");
		sb.append(remoteInfo.toString());
		if (connectionType!=null) {
			sb.append(",connT=");
			sb.append(connectionType.toString());
		}
		if (dataType!=null) {
			sb.append(",dataT=");
			sb.append(dataType.toString());
		}
		sb.append("}");
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		if (hashCode==0) {
			hashCode=toString().hashCode();
		}
		return hashCode;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConnectionParameters)) return false;
		return obj.toString().equals(this.toString());
	}
	
}
