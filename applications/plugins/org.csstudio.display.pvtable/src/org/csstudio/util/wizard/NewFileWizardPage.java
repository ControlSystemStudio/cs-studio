package org.csstudio.util.wizard;

import java.io.InputStream;

import org.csstudio.platform.ui.wizards.WizardNewFileCreationPage;
import org.eclipse.jface.viewers.IStructuredSelection;

/** The "New" wizard page allows setting the container for the new file as well
 *  as the file name. The page will only accept file name without the extension
 *  OR with the extension that matches the expected one (xml).
 *  
 *  @author Kay Kasemir
 */
public class NewFileWizardPage extends WizardNewFileCreationPage
{
    private final String default_content;
    
    public NewFileWizardPage(String title, String default_content,
                    IStructuredSelection selection)
    {
        super(title, selection);
        this.default_content = default_content;
        setDescription(Messages.CreateNew___ + title + Messages.___TypeFile);
    }
    
    protected InputStream getInitialContents()
    {
        return new StringInputStream(default_content);
    }
    
    public String getFileExtension()
    {
        return "xml"; //$NON-NLS-1$
    }
}
