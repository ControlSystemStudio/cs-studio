package org.csstudio.opibuilder.editor;

import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

public class OPIEditorMatcher implements IEditorMatchingStrategy {

    /*
     * @see org.eclipse.ui.IEditorMatchingStrategy#matches(org.eclipse.ui.
     * IEditorReference, org.eclipse.ui.IEditorInput)
     */
    @Override
    public boolean matches(IEditorReference editorRef, IEditorInput input) {
        try {
            IEditorInput editorInput = editorRef.getEditorInput();
            IPath editorInputPath = ResourceUtil.getPathInEditor(editorInput);
            IPath inputPath = ResourceUtil.getPathInEditor(input);
            return editorInputPath.equals(inputPath);
        } catch (PartInitException e) {
            return false;
        }

    }

}
