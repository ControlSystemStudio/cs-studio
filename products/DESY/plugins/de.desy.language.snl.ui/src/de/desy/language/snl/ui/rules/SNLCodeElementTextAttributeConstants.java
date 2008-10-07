package de.desy.language.snl.ui.rules;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import de.desy.language.libraries.utils.contract.Contract;

/**
 * Identifier of colors used for coloring SNL code elements.
 */
public enum SNLCodeElementTextAttributeConstants {
	/** The color key for braces. */
	SNL_BRACES("snl_braces", new RGB(0, 0, 0), "Braces"), //$NON-NLS-1$

	/**
	 * The color key for predefined constants of SNL.
	 */
	SNL_CONSTANTS("snl_constants", new RGB(0, 0, 0), SWT.ITALIC, "Constants"), //$NON-NLS-1$

	/**
	 * The color key for anything in SNL code for which no other color is
	 * specified.
	 */
	SNL_DEFAULT("snl_default", new RGB(0, 0, 0), "Other characters"), //$NON-NLS-1$

	/** The color key for embedded c statements. */
	SNL_EMBEDDED_C(
			"snl_embedded_c", new RGB(255, 120, 120), SWT.NORMAL, "Embedded C code"), //$NON-NLS-1$

	/** The color key for keywords in SNL code. */
	SNL_KEYWORD("snl_keyword", new RGB(127, 0, 85), SWT.BOLD, "Keywords"), //$NON-NLS-1$

	/** The color key for multi-line comments in SNL code. */
	SNL_MULTI_LINE_COMMENT(
			"snl_multi_line_comment", new RGB(63, 127, 95), "Multi line comment"), //$NON-NLS-1$

	/** The color key for multi-line documentation comments in SNL code. */
	SNL_MULTI_LINE_DOC_COMMENT(
			"snl_multi_line_doc_comment", new RGB(63, 127, 95), "Multi line documentation"), //$NON-NLS-1$

	/** The color key for numbers. */
	SNL_NUMBER("snl_numbers", new RGB(255, 0, 0), "Numbers"), //$NON-NLS-1$

	/** The color key for operators. */
	SNL_OPERATOR("snl_operators", new RGB(0, 0, 0), "Operators"), //$NON-NLS-1$

	/** The color key for predefined method names. */
	SNL_PREDEFINED_METHOD(
			"snl_method_pattern", new RGB(0, 0, 255), "Predefined methods"), //$NON-NLS-1$

	/** The color key for single-line comments in SNL code. */
	SNL_SINGLE_LINE_COMMENT(
			"snl_single_line_comment", new RGB(63, 127, 95), "Single line comment"), //$NON-NLS-1$

	/** The color key for embedded c statements. */
	SNL_SINGLE_LINE_EMBEDDED_C(
			"snl_single_line_embedded_c", new RGB(255, 120, 120), SWT.NORMAL, "Embedded C code (a single line)"), //$NON-NLS-1$

	/** The color key for string and character literals in SNL code. */
	SNL_STRING("snl_string", new RGB(0, 128, 0), "Strings"),

	/** The color key for built-in types and keywords in SNL code. */
	SNL_TYPE("snl_type", new RGB(210, 40, 40), SWT.BOLD, "Types"), //$NON-NLS-1$

	/**
	 * The color key for task tags in SNL comments (value
	 * <code>"c_comment_task_tag"</code>).
	 */
	TASK_TAG("snl_comment_task_tag", new RGB(0, 128, 0), "Task tags"); //$NON-NLS-1$

	/**
	 * Returns the CodeElementTextAttributeConstant of given id-string, if
	 * existing.
	 * 
	 * @param idString
	 *            The id string, may not be null.
	 * @return The found constant or null if no constant found for given id.
	 */
	public static SNLCodeElementTextAttributeConstants findCodeElementOfId(
			final String idString) {
		Contract.requireNotNull("idString", idString);

		for (final SNLCodeElementTextAttributeConstants constant : SNLCodeElementTextAttributeConstants
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
	SNLCodeElementTextAttributeConstants(@Deprecated
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
	SNLCodeElementTextAttributeConstants(final String constantId,
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
