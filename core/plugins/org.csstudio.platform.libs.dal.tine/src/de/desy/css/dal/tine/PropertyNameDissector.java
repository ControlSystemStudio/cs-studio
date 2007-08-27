package de.desy.css.dal.tine;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class PropertyNameDissector
{
 
  private String accessProtocol;
  private String deviceContext;
  private String deviceGroup;
  private String deviceName;
  private String deviceProperty;

  /**
   * Creates new instance of ConnectionParameters.
   * 
   * @param remoteName
   * @param accessMode
   * @param accessRate
   */
  public PropertyNameDissector(String remoteName)
  {
    super();
    String[] s = remoteName.split("/");
    
    this.accessProtocol = "TINE"; //$NON-NLS-1$
    this.deviceContext = (s.length>0 && s[0]!=null) ? s[0] : "";
    this.deviceGroup = (s.length>1 && s[1]!=null) ? s[1] : "";
    this.deviceProperty = (s.length>3 && s[s.length-1]!=null) ? s[s.length-1] : "";
    
    StringBuilder sb= new StringBuilder(128);

    if (s.length>2 && s[2]!=null) {
    	sb.append(s[2]);
    }
    for (int i = 3; i < s.length-1; i++) {
		sb.append('/');
		if (s[i]!=null) sb.append(s[i]);
	}
    
    this.deviceName = sb.toString();
  }
  
  /**
   * Returns the accessProtocol.
   * 
   * @return Returns the accessProtocol.
   */
  public String getAccessProtocol()
  {
    return accessProtocol;
  }
  /**
   * Returns the deviceContext.
   * 
   * @return Returns the deviceContext.
   */
  public String getDeviceContext()
  {
    return deviceContext;
  }
  /**
   * Returns the deviceGroup.
   * 
   * @return Returns the deviceGroup.
   */
  public String getDeviceGroup()
  {
    return deviceGroup;
  }
  /**
   * Returns the deviceName.
   * 
   * @return Returns the deviceName.
   */
  public String getDeviceName()
  {
    return deviceName;
  }
  /**
   * Returns the deviceProperty.
   * 
   * @return Returns the deviceProperty.
   */
  public String getDeviceProperty()
  {
    return deviceProperty;
  }
  
  /**
   * Return URI ready name for remote TINE property.
   * 
   * @return remote TINE name delimeted with / characters
   */
  public String getRemoteName()
  {
    return accessProtocol + "/" + deviceContext + "/" + deviceGroup + "/" + deviceName + "/" + deviceProperty;
  }


}
