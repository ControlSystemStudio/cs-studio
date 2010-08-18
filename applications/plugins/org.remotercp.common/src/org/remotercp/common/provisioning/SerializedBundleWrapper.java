package org.remotercp.common.provisioning;

/**
 * This class is no longer used as this tool only handles features and not
 * bundles. But this tool can be extended to handle bundles as well.
 * 
 * @author Eugen Reiswich
 * 
 * @see use instead {@link SerializedFeatureWrapper}
 */
@Deprecated
public class SerializedBundleWrapper implements
		SerializedWrapper<SerializedBundleWrapper> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8234041937066066069L;

	private long bundleId;

	private String identifier;

	private int state;

	private String bundleVersion;

	public long getBundleId() {
		return bundleId;
	}

	public void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}

	public String getLabel() {
		return identifier;
	}

	public void setLabel(String label) {
		this.identifier = label;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public boolean equals(Object bundle) {
		if (bundle instanceof SerializedBundleWrapper) {
			SerializedBundleWrapper bundleWrapper = (SerializedBundleWrapper) bundle;
			return this.getLabel().equals(bundleWrapper.getLabel());
		}
		return super.equals(bundle);
	}

	public int compareTo(SerializedBundleWrapper bundle) {
		return this.getLabel().compareTo(bundle.getLabel());
	}

	public String getVersion() {
		return bundleVersion;
	}

	public void setVersion(String version) {
		this.bundleVersion = version;
	}
}
