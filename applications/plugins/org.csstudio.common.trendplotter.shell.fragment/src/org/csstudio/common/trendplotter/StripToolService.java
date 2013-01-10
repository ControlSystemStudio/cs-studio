package org.csstudio.common.trendplotter;


import org.csstudio.desy.startuphelper.IStripTool;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;

public class StripToolService implements IStripTool {

	    /* (non-Javadoc)
	     * @see org.csstudio.platform.ui.trends.IStripTool#openEditor(org.eclipse.core.resources.IFile)
	     */
	    public EditorPart openEditor(IFile file) {
//	        IEditorInput input = new FileEditorInput(file);
	        return null;
	    }

	    /* (non-Javadoc)
	     * @see org.csstudio.platform.ui.trends.IStripTool#openView(org.eclipse.core.resources.IFile)
	     */
	    public ViewPart openView(IFile file) {
			DB2Shell dbshell = new DB2Shell(file);
			dbshell.openShell();
	        return null;
	    }
}
