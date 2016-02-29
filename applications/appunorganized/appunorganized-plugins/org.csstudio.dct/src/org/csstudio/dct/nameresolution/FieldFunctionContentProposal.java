/**
 *
 */
package org.csstudio.dct.nameresolution;

import org.eclipse.jface.fieldassist.IContentProposal;

public final class FieldFunctionContentProposal implements IContentProposal {

    private String content;
    private String label;
    private String description;
    private int cursorPosition;

    public FieldFunctionContentProposal(String content, String label, String description, int cursorPosition) {
        this.content = content;
        this.label = label;
        this.description = description;
        this.cursorPosition = cursorPosition;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public int getCursorPosition() {
        return cursorPosition;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getLabel() {
        return label;
    }

}