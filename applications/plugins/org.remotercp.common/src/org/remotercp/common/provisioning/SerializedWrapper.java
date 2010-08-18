package org.remotercp.common.provisioning;

import java.io.Serializable;

public interface SerializedWrapper<T> extends Comparable<T>, Serializable {

	public String getLabel();

	public void setLabel(String label);

	public String getVersion();

	public void setVersion(String version);

}
