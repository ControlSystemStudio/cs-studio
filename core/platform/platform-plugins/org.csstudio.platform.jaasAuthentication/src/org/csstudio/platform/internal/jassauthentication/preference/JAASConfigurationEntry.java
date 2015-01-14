package org.csstudio.platform.internal.jassauthentication.preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

/**
 * The login configuration entry
 * @author Xihui Chen
 *
 */
public class JAASConfigurationEntry {

	private String loginModuleName;
	private String moduleControlFlag;
	private List<String[]> moduleOptionsList; 	
	
	
	/**
	 * Constructor
	 */
	public JAASConfigurationEntry() {}
	
	/**
	 * Constructor. moduleOptionList will be initialized with a new ArrayList<String[]>().
	 * @param loginModuleName
	 * @param moduleControlFlag
	 */
	public JAASConfigurationEntry(String loginModuleName, 
			String moduleControlFlag) {
		this.loginModuleName = loginModuleName;
		this.moduleControlFlag = moduleControlFlag;
		this.moduleOptionsList = new ArrayList<String[]>();
	}
	/**
	 * @return the loginModuleName
	 */
	public String getLoginModuleName() {
		return loginModuleName;
	}

	/**
	 * @param loginModuleName the loginModuleName to set
	 */
	public void setLoginModuleName(String loginModuleName) {
		this.loginModuleName = loginModuleName;
	}



	/**
	 * @return the moduleControlFlag
	 */
	public String getModuleControlFlag() {
		return moduleControlFlag;
	}

	/**
	 * @return the moduleControlFlag index in JAASPreferenceModel.FLAGS. 
	 * -1 if the moduleControlFlag is not in JAASPreferenceModel.FLAGS. 
	 */
	public int getModuleControlFlagIndex() {
		if(moduleControlFlag.equals(JAASPreferenceModel.FLAG_REQUIRED))
			return 0;
		else if (moduleControlFlag.equals(JAASPreferenceModel.FLAG_REQUISITE))
			return 1;
		else if (moduleControlFlag.equals(JAASPreferenceModel.FLAG_SUFFICIENT))
			return 2;
		else if (moduleControlFlag.equals(JAASPreferenceModel.FLAG_OPTIONAL))
			return 3;
		return -1;
	}

	/**
	 * @param moduleControlFlag the moduleControlFlag to set
	 */
	public void setModuleControlFlag(String moduleControlFlag) {
		this.moduleControlFlag = moduleControlFlag;
	}



	/**
	 * @return the moduleOptionsList
	 */
	public List<String[]> getModuleOptionsList() {
		return moduleOptionsList;
	}



	/**
	 * @param moduleOptionsList the moduleOptionsList to set
	 */
	public void setModuleOptionsList(List<String[]> moduleOptionsList) {
		this.moduleOptionsList = moduleOptionsList;
	}

	
	public LoginModuleControlFlag getLoginModuleControlFlag() {
		LoginModuleControlFlag flag = null;
		if(moduleControlFlag.equals(JAASPreferenceModel.FLAG_REQUIRED))
			flag = LoginModuleControlFlag.REQUIRED;
		else if (moduleControlFlag.equals(JAASPreferenceModel.FLAG_REQUISITE))
			flag = LoginModuleControlFlag.REQUISITE;
		else if (moduleControlFlag.equals(JAASPreferenceModel.FLAG_SUFFICIENT))
			flag = LoginModuleControlFlag.SUFFICIENT;
		else if (moduleControlFlag.equals(JAASPreferenceModel.FLAG_OPTIONAL))
			flag = LoginModuleControlFlag.OPTIONAL;
		return flag;
	}
	
	public Map<String, String> getModuleOptionsMap() {
		Map<String, String> moduleOptionsMap =  new HashMap<String, String>();
		for(String[] optionTuple : moduleOptionsList) {
			moduleOptionsMap.put(optionTuple[0], optionTuple[1]);
		}
		return moduleOptionsMap;
	}

}
