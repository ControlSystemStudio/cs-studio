package de.desy.language.snl.configurationservice;

/**
 * Constants used in the preference pages of SNL-development tools.
 * 
 * All constants are post-fixes to be a suffix of the plug-in Id.
 */
public enum PreferenceConstants {
	
	/**
	 * Post-fix for the bold-style constant to be added to the type id.
	 */
	BOLD_POST_FIX(".bold"),

	/**
	 * Post-fix for a color constant to be added to the type id.
	 */
	COLOR_POST_FIX(".color"),

	/**
	 * Post-fix of preference id for epics base location.
	 */
	EPICS_BASE_LOCATION_POST_FIX(".epics.base-location"),
	
	/**
	 * Post-fix of preference id for epics seq location.
	 */
	EPICS_SEQ_LOCATION_POST_FIX(".epics.seq-location"),

	/**
	 * Post-fix for the italic-style constant to be added to the type id.
	 */
	ITALIC_POST_FIX(".italic"),

	/**
	 * SNC location / directory.
	 */
	SNC_LOCATION_POST_FIX(".compiler.location"),
	
	/**
	 * C Compiler location / directory.
	 */
	C_COMPILER_LOCATION_POST_FIX(".c_compiler.location"),
	
	/**
	 * Pre Compiler location / directory.
	 */
	PRE_COMPILER_LOCATION_POST_FIX(".pre_compiler.location"),
	
	/**
	 * Application Compiler location / directory.
	 */
	APPLICATION_COMPILER_POST_FIX(".application_compiler.location"),

	/**
	 * Post-fix for the strike-through-style constant to be added to the type
	 * id.
	 */
	STRIKETHROUGH_POST_FIX(".strikethrough"),

	/**
	 * Post-fix for the underline-style constant to be added to the type id.
	 */
	UNDERLINE_POST_FIX(".underline"),
	
	/**
	 * Post-fix for the target platform constant to be added to the type id.
	 */
	TARGET_PLATFORM(".targetplatform"),
	
	/**
	 * Post-fix for the option enable compilation after save.
	 */
	SAVE_AND_COMPILE_POST_FIX(".saveAndCompile"),
	
	/**
	 * Post-fix for the option to keep generated files.
	 */
	KEEP_GENERATED_FILES_POST_FIX(".keepGeneratedFiles");

	/**
	 * Id used in the preference store.
	 */
	private final String preferenceStoreId;

	/**
	 * Initializer of enum elements.
	 * 
	 * @param preferenceStoreId
	 *            The id to be used in the preference store for this element.
	 */
	PreferenceConstants(String preferenceStoreId) {
		this.preferenceStoreId = preferenceStoreId;

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
