/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
    private final String extension;
    
    public NewFileWizardPage(String title, String default_content,
                    String extension,
                    IStructuredSelection selection)
    {
        super(title, selection);
        this.default_content = default_content;
        this.extension = extension;
        setDescription(Messages.CreateNew___ + title + Messages.___TypeFile);
    }
    
    @Override
    protected InputStream getInitialContents()
    {
        return new StringInputStream(default_content);
    }
    
    @Override
    public String getFileExtension()
    {
        return extension;
    }
}
