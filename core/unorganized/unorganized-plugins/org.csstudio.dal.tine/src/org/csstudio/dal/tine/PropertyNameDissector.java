/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
 */

package org.csstudio.dal.tine;

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
    if (!remoteName.startsWith("TINE/")) {
    	remoteName = "TINE/" + remoteName;
    }
    String[] s = remoteName.split("/");
    
    this.accessProtocol = (s.length>0 && s[0]!=null) ? s[0] : "";
    this.deviceContext = (s.length>1 && s[1]!=null) ? s[1] : "";
    this.deviceGroup = (s.length>2 && s[2]!=null) ? s[2] : "";
    this.deviceProperty = (s.length>4 && s[s.length-1]!=null) ? s[s.length-1] : "";
    
    StringBuilder sb= new StringBuilder(128);

    if (s.length>3 && s[3]!=null) {
    	sb.append(s[3]);
    }
    for (int i = 4; i < s.length-1; i++) {
		sb.append('/');
		if (s[i]!=null) {
			sb.append(s[i]);
		}
	}
    
    this.deviceName = sb.toString();
  }
 
  /**
   * Returns the deviceContext.
   * 
   * @return Returns the deviceContext.
   */
  public String getDeviceContext()
  {
    return this.deviceContext;
  }
  /**
   * Returns the deviceGroup.
   * 
   * @return Returns the deviceGroup.
   */
  public String getDeviceGroup()
  {
    return this.deviceGroup;
  }
  /**
   * Returns the deviceName.
   * 
   * @return Returns the deviceName.
   */
  public String getDeviceName()
  {
    return this.deviceName;
  }
  /**
   * Returns the deviceProperty.
   * 
   * @return Returns the deviceProperty.
   */
  public String getDeviceProperty()
  {
    return this.deviceProperty;
  }
  
  /**
   * Return URI ready name for remote TINE property.
   * 
   * @return remote TINE name delimeted with / characters
   */
  public String getRemoteName()
  {
    return this.deviceContext + "/" + this.deviceGroup + "/" + this.deviceName + "/" + this.deviceProperty;
  }


}
