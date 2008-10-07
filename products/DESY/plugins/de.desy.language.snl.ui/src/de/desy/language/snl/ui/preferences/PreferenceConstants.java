package de.desy.language.snl.ui.preferences;

/**
 * Constants used in the preference pages of SNL-development tools.
 * 
 * All constants are post-fixes to be a suffix of the plug-in Id.
 */
enum PreferenceConstants {

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
	 * Post-fix for the italic-style constant to be added to the type id.
	 */
	ITALIC_POST_FIX(".italic"),

	/**
	 * SNC location / directory.
	 */
	SNC_LOCATION_POST_FIX(".compiler.location"),

	/**
	 * SNC compiler option +a - do asynchronous pvGet.
	 */
	SNC_OPTIONS_ASYNCHRONOUS_PVGET_POST_FIX(".compiler.options.plus-a"),

	/**
	 * SNC compiler option -i - don't register commands/programs
	 */
	SNC_OPTIONS_DONT_REGISTER_COMMANDS_OR_PROGRAM_POST_FIX(
			".compiler.options.minus-i"),

	/**
	 * SNC compiler option -e - don't use new event flag mode
	 */
	SNC_OPTIONS_DONT_USE_NEW_EVENT_FLAG_MODE_POST_FIX(
			".compiler.options.minus-e"),

	/**
	 * SNC compiler option -c - don't wait for all connects
	 */
	SNC_OPTIONS_DONT_WAIT_FOR_CONNECTIONS_POST_FIX(".compiler.options.minus-c"),

	/**
	 * SNC compiler option +m - generate main program
	 */
	SNC_OPTIONS_GENERATE_MAIN_PROGRAM_POST_FIX(".compiler.options.plus-m"),

	/**
	 * SNC compiler option +r - make reentrant at run-time
	 */
	SNC_OPTIONS_MAKE_REENTRANT_AT_RUN_TIME_POST_FIX(".compiler.options.plus-r"),

	/**
	 * SNC compiler option -w - suppress compiler warnings
	 */
	SNC_OPTIONS_SUPRESS_COMPILER_WARNINGS_POST_FIX(".compiler.options.minus-w"),

	/**
	 * SNC compiler option -l - suppress line numbering
	 */
	SNC_OPTIONS_SUPRESS_LINE_NUMBERING_POST_FIX(".compiler.options.minus-l"),

	/**
	 * SNC compiler option +d - turn on debug run-time option
	 */
	SNC_OPTIONS_TURN_ON_DEBUG_RUNTIME_OPTION_POST_FIX(
			".compiler.options.plus-d"),

	/**
	 * Post-fix for the strike-through-style constant to be added to the type
	 * id.
	 */
	STRIKETHROUGH_POST_FIX(".strikethrough"),

	/**
	 * Post-fix for the underline-style constant to be added to the type id.
	 */
	UNDERLINE_POST_FIX(".underline");

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
