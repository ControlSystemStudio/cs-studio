package de.desy.language.editor.ui.editor;


/**
 * A listener to be used by the {@link LanguageEditor} to offer adapted classes ({@link LanguageEditor#getAdapter(Class)})
 * a way to highlight text ranges.
 *
 * @author C1 WPS / KM, MZ
 */
public abstract class HighlightingListener {
    public void highlightRegion(final int startOffset, final int endOffset,
            final boolean activateEditor) {
        this.doHighlightRegion(startOffset, endOffset, activateEditor);
    }

    protected abstract void doHighlightRegion(int startOffset, int endOffset,
            boolean activateEditor);
}