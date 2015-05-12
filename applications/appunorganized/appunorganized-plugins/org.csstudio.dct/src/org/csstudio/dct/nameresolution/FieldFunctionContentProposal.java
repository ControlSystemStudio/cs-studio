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

    public String getContent() {
        return content;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

}