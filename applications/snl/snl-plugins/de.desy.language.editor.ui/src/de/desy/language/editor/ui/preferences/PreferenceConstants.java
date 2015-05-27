package de.desy.language.editor.ui.preferences;

public enum PreferenceConstants {

    MATCHING_CHARACTER_ENABLE("LanguageEditor.MATCHING_CHARACTER_ENABLE_KEY"),
    MATCHING_CHARACTER_COLOR("LanguageEditor.MATCHING_CHARACTER_COLOR_KEY"),
    CURSOR_LINE_ENABLE("LanguageEditor.CURSOR_LINE_ENABLE_KEY"),
    CURSOR_LINE_COLOR("LanguageEditor.CURSOR_LINE_COLOR_KEY"),
    MARGIN_PAINTER_ENABLE("LanguageEditor.MARGIN_PAINTER_ENABLE"),
    MARGIN_PAINTER_COLOR("LanguageEditor.MARGIN_PAINTER_COLOR"),
    MARGIN_COLUMNS("LanguageEditor.MARGIN_COLUMNS");

    private String preferenceStoreId;

    private PreferenceConstants(String key) {
        preferenceStoreId = key;
    }

    /**
     * Id used in the preference store.
     *
     * @return A not-null, non-empty string to be used as id in the preference
     *         store.
     */
    public String getPreferenceStoreId() {
        return preferenceStoreId;
    }

    /**
     * Returns the String representation, here the same value as
     * {@link #getPreferenceStoreId()}.
     */
    @Override
    public String toString() {
        return this.getPreferenceStoreId();
    }

}
