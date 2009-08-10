package org.csstudio.opibuilder.properties.support;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.properties.ScriptProperty;

/**The value type definition for {@link ScriptProperty}, which describes the input
 * for a Script Property.
 * @author Xihui Chen
 *
 */
public class ScriptsInput {

	private List<ScriptData> scriptList;
	
	public ScriptsInput(List<ScriptData> scriptDataList) {
		scriptList = scriptDataList;
	}
	
	public ScriptsInput() {
		scriptList = new ArrayList<ScriptData>();
	}

	/**
	 * @return the scriptList
	 */
	public List<ScriptData> getScriptList() {
		return scriptList;
	}
	
	/**
	 * @return a total contents copy of this ScriptsInput.
	 */
	public ScriptsInput getCopy(){
		ScriptsInput copy = new ScriptsInput();
		for(ScriptData data : scriptList){
			copy.getScriptList().add(data.getCopy());
		}
		return copy;
	}
	
	
}
