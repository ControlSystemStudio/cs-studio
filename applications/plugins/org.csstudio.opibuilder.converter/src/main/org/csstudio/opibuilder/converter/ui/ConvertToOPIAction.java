package org.csstudio.opibuilder.converter.ui;

import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.writer.OpiWriter;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**Convert an EDM file to an OPI file.
 * @author Xihui Chen
 *
 */
public class ConvertToOPIAction implements IObjectActionDelegate {

	private static final String colorDefFile = "src/test/resources/colors.list";

	private IResource selectedFile;
	
	private IPath convertedFile;
	
	public ConvertToOPIAction() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}

	public void run(IAction action) {
		try {
			setEnvironment();
			OpiWriter writer = OpiWriter.getInstance();		
			convertedFile = selectedFile.getLocation().removeFileExtension().addFileExtension("opi");
			writer.writeDisplayFile(
					selectedFile.getLocation().toOSString(), 
					convertedFile.toOSString());
			//writer.writeColorDef(System.getProperty("edm2xml.colorsOutput"));			
			
			selectedFile.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
				
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection && !((IStructuredSelection)selection).isEmpty())
			selectedFile = ((IResource)((IStructuredSelection)selection).getFirstElement());
	}

	private void setEnvironment() {
		IFile file = ResourceUtil.getIFileFromIPath(new Path("/CSS/colors.list"));
		if(file != null)
			System.setProperty("edm2xml.colorsFile", file.getLocation().toOSString());
		System.setProperty("edm2xml.colorsOutput",
				file.getLocation().removeFileExtension().addFileExtension("def").toOSString());
		/**
		 * Enable fail-fast mode for stricter tests.
		 * Set this to true for the partial conversion in case of exceptions.
		 */
		System.setProperty("edm2xml.robustParsing", "true");
	}
	
}
