package org.csstudio.opibuilder.scriptUtil;


import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ImplementationLoader;

/**Single source helper for Script Utils. The IMPL is allowed to be NULL.
 * @author Xihui Chen
 *
 */
public abstract class ScriptUtilSSHelper {
	
	private static final ScriptUtilSSHelper IMPL =
			(ScriptUtilSSHelper)ImplementationLoader.newInstance(
				ScriptUtilSSHelper.class, false);
	
	
	public static ScriptUtilSSHelper getIMPL(){
		return IMPL;
	}

	public abstract void writeTextFile(String filePath, boolean inWorkspace, 
			AbstractBaseEditPart widget, String text, 
			boolean append) throws Exception;
	
	public abstract String openFileDialog(boolean inWorkspace);
	
	public abstract void makeElogEntry(final String filePath);
	
}
