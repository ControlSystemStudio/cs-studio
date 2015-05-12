package org.csstudio.dct.ui.editor.copyandpaste;

import java.io.Serializable;
import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IProject;
import org.eclipse.gef.commands.Command;

public interface ICopyAndPasteStrategy {

    Command createPasteCommand(List<IElement> copiedElements, IProject project, List<IElement> selectedElements);

    List<Serializable> createCopyElements(List<IElement> selectedElements);

    boolean canCopy(List<IElement> selectedElements);

    boolean canPaste(List<IElement> selectedElements);

    String getContentDescription();
}
