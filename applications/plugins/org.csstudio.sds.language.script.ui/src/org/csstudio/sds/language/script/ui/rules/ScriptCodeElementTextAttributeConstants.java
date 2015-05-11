package org.csstudio.sds.language.script.ui.rules;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import de.desy.language.libraries.utils.contract.Contract;

/**
 * Identifier of colors used for coloring SNL code elements.
 */
public enum ScriptCodeElementTextAttributeConstants {
    /** The color key for braces. */
    SCRIPT_BRACES("script_braces", new RGB(0, 0, 0), "Braces"), //$NON-NLS-1$

    /**
     * The color key for anything in SCRIPT code for which no other color is
     * specified.
     */
    SCRIPT_DEFAULT("script_default", new RGB(0, 0, 0), "Other characters"), //$NON-NLS-1

    /** The color key for keywords in SCRIPT code. */
    SCRIPT_KEYWORD("script_keyword", new RGB(127, 0, 85), SWT.BOLD, "Keywords"), //$NON-NLS-1$

    /** The color key for numbers. */
    SCRIPT_NUMBER("script_numbers", new RGB(255, 0, 0), "Numbers"), //$NON-NLS-1$

    /** The color key for operators. */
    SCRIPT_OPERATOR("script_operators", new RGB(0, 0, 0), "Operators"), //$NON-NLS-1$

    /** The color key for predefined method names. */
    SCRIPT_PREDEFINED_METHOD(
            "script_method_pattern", new RGB(0, 0, 200), "Predefined methods"), //$NON-NLS-1$

    /** The color key for predefined method names. */
    SCRIPT_PREDEFINED_VARIABLES(
            "script_method_pattern", new RGB(0, 0, 255), "Predefined methods"),//$NON-NLS-1$

    /** The color key for string and character literals in SCRIPT code. */
    SCRIPT_STRING("script_string", new RGB(42, 0, 255), "Strings"),//$NON-NLS-1$

    /** The color key for comments in SCRIPT code. */
    SCRIPT_MULTI_LINE_COMMENT("script_comment", new RGB(63, 127, 95), "Comment"); //$NON-NLS-1$

    /**
     * Returns the CodeElementTextAttributeConstant of given id-string, if
     * existing.
     *
     * @param idString
     *            The id string, may not be null.
     * @return The found constant or null if no constant found for given id.
     */
    public static ScriptCodeElementTextAttributeConstants findCodeElementOfId(
            final String idString) {
        Contract.requireNotNull("idString", idString);

        for (final ScriptCodeElementTextAttributeConstants constant : ScriptCodeElementTextAttributeConstants
                .values()) {
            if (constant.asStringId().equals(idString)) {
                return constant;
            }
        }

        return null;
    }

    private RGB colorValue;

    private String constantId;

    private String shortDescription;

    private int swtFontStyleCode;

    /**
     * Initialize a enum constant of this type.
     *
     * @param constantId
     *            A senseless string-id of the color.
     * @param colorValue
     *            The value of the color as SWT-RGB-code.
     * @param swtFontStyleCode
     *            The SWT-font-style-code (Please refer the {@link SWT}-documentation).
     */
    ScriptCodeElementTextAttributeConstants(@Deprecated
    final String constantId, final RGB colorValue, final int swtFontStyleCode,
            final String shortDescription) {
        this.constantId = constantId;
        this.colorValue = colorValue;
        this.swtFontStyleCode = swtFontStyleCode;
        this.shortDescription = shortDescription;
    }

    /**
     * Initialize a enum constant of this type with the {@link SWT#NORMAL}-font-style.
     *
     * @param constantId
     *            A senseless string-id of the color.
     * @param colorValue
     *            The value of the color as SWT-RGB-code.
     */
    ScriptCodeElementTextAttributeConstants(final String constantId,
            final RGB colorValue, final String shortDescription) {
        this(constantId, colorValue, SWT.NORMAL, shortDescription);
    }

    /**
     * Returns a String color-id for this element-type.
     */
    public String asStringId() {
        return this.constantId;
    }

    /**
     * Returns the RGB-values of the color of this element-type.
     */
    public RGB getRGB() {
        return this.colorValue;
    }

    /**
     * Returns the human readable short description.
     *
     * @return The short description
     */
    public String getShortDescription() {
        return this.shortDescription;
    }

    /**
     * Returns the SWT-font-style-constant of this element-type.
     */
    public int getSwtFontStyleCode() {
        return this.swtFontStyleCode;
    }

    /**
     * Replaces default rgb with given rgb color value.
     *
     * @param color
     *            The new rgb value, may not be null.
     */
    public void setRGBValue(final RGB color) {
        Contract.requireNotNull("color", color);

        this.colorValue = color;
    }

    public void setSwtFontStyleCode(final int swtFontStyleCode) {
        this.swtFontStyleCode = swtFontStyleCode;
    }
}
