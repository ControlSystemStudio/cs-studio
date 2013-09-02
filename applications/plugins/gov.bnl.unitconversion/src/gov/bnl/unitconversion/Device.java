/**
 * 
 */
package gov.bnl.unitconversion;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author shroffk
 * 
 */
@XmlRootElement
public class Device {

    public static class DeviceBuilder {

	private String name;
	private String system;
	private int installId;
	private int inventoryId;
	private String componentTypeName;
	private String typeDescription;
	private String vendor;
	private int serialNumber;
	private Map<String, Map<String, Conversion>> conversionInfo;

	/**
	 * 
	 */
	private DeviceBuilder() {

	}

	/**
	 * create a builder for {@link Device}
	 * 
	 * @param name
	 *            - name of device
	 * @return DeviceBuilder
	 */
	public static DeviceBuilder device(String name) {
	    DeviceBuilder deviceBuilder = new DeviceBuilder();
	    deviceBuilder.name = name;
	    return deviceBuilder;
	}

	public DeviceBuilder system(String system) {
	    this.system = system;
	    return this;
	}

	public DeviceBuilder installId(int installId) {
	    this.installId = installId;
	    return this;
	}

	public DeviceBuilder inventoryId(int inventoryId) {
	    this.inventoryId = inventoryId;
	    return this;
	}

	public DeviceBuilder componentTypeName(String componentTypeName) {
	    this.componentTypeName = componentTypeName;
	    return this;
	}

	public DeviceBuilder typeDescription(String typeDescription) {
	    this.typeDescription = typeDescription;
	    return this;
	}

	public DeviceBuilder vendor(String vendor) {
	    this.vendor = vendor;
	    return this;
	}

	public DeviceBuilder serialNumber(int serialNumber) {
	    this.serialNumber = serialNumber;
	    return this;
	}

	public DeviceBuilder conversionInfo(
		Map<String, Map<String, Conversion>> conversionInfo) {
	    this.conversionInfo = conversionInfo;
	    return this;
	}

	public Device build() {
	    return new Device(name, system, installId, inventoryId, componentTypeName,
		    typeDescription, vendor, serialNumber, conversionInfo);
	}

    }

    private String name;
    private String system;
    private int installId;
    private int inventoryId;
    private int serialNumber;
    private String componentType;
    private String typeDescription;
    private String vendor;

    private Map<String, Map<String, Conversion>> conversionInfo;

    private Device() {
    }

    /**
     * @param name
     * @param system
     * @param installId
     * @param componentTypeName
     * @param typeDescription
     * @param vendor
     */
    private Device(String name, String system, int installId, int inventoryId,
	    String componentTypeName, String typeDescription, String vendor,
	    int serialNumber,
	    Map<String, Map<String, Conversion>> conversionInfo) {
	this.name = name;
	this.system = system;
	this.installId = installId;
	this.inventoryId = inventoryId;
	this.componentType = componentTypeName;
	this.typeDescription = typeDescription;
	this.vendor = vendor;
	this.serialNumber = serialNumber;
	this.conversionInfo = conversionInfo;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @return the system
     */
    public String getSystem() {
	return system;
    }

    /**
     * @return the installId
     */
    public int getInstallId() {
	return installId;
    }

    /**
     * @return the inventoryId
     */
    public int getInventoryId() {
        return inventoryId;
    }

    /**
     * @return the serialNumber
     */
    public int getSerialNumber() {
	return serialNumber;
    }

    /**
     * @return the componentTypeName
     */
    public String getComponentType() {
	return componentType;
    }

    /**
     * @return the typeDescription
     */
    public String getTypeDescription() {
	return typeDescription;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
	return vendor;
    }

    /**
     * @return the conversionInfo
     */
    public Map<String, Map<String, Conversion>> getConversionInfo() {
        return conversionInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime
		* result
		+ ((componentType == null) ? 0 : componentType
			.hashCode());
	result = prime * result
		+ ((conversionInfo == null) ? 0 : conversionInfo.hashCode());
	result = prime * result + installId;
	result = prime * result + inventoryId;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + serialNumber;
	result = prime * result + ((system == null) ? 0 : system.hashCode());
	result = prime * result
		+ ((typeDescription == null) ? 0 : typeDescription.hashCode());
	result = prime * result + ((vendor == null) ? 0 : vendor.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Device other = (Device) obj;
	if (componentType == null) {
	    if (other.componentType != null)
		return false;
	} else if (!componentType.equals(other.componentType))
	    return false;
	if (conversionInfo == null) {
	    if (other.conversionInfo != null)
		return false;
	} else if (!conversionInfo.equals(other.conversionInfo))
	    return false;
	if (installId != other.installId)
	    return false;
	if (inventoryId != other.inventoryId)
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (serialNumber != other.serialNumber)
	    return false;
	if (system == null) {
	    if (other.system != null)
		return false;
	} else if (!system.equals(other.system))
	    return false;
	if (typeDescription == null) {
	    if (other.typeDescription != null)
		return false;
	} else if (!typeDescription.equals(other.typeDescription))
	    return false;
	if (vendor == null) {
	    if (other.vendor != null)
		return false;
	} else if (!vendor.equals(other.vendor))
	    return false;
	return true;
    }

}
