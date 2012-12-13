package de.desy.language.snl.configurationservice;

public enum CompilerOptionPreferenceConstants {
	
	/**
	 * SNC compiler option +a - do asynchronous pvGet.
	 */
	SNC_OPTIONS_ASYNCHRONOUS_PVGET(".compiler.options.plus-a", "+a", "do asynchronous pvGet"),

	/**
	 * SNC compiler option -i - don't register commands/programs
	 */
	SNC_OPTIONS_DONT_REGISTER_COMMANDS_OR_PROGRAM(
			".compiler.options.minus-i", "-i", "don't register commands/programs"),

	/**
	 * SNC compiler option -e - don't use new event flag mode
	 */
	SNC_OPTIONS_DONT_USE_NEW_EVENT_FLAG_MODE(
			".compiler.options.minus-e", "-e", "don't use new event flag mode"),

	/**
	 * SNC compiler option -c - don't wait for all connects
	 */
	SNC_OPTIONS_DONT_WAIT_FOR_CONNECTIONS(".compiler.options.minus-c", "-c", "don't wait for all connects"),

	/**
	 * SNC compiler option +m - generate main program
	 */
	SNC_OPTIONS_GENERATE_MAIN_PROGRAM(".compiler.options.plus-m", "+m", "generate main program"),

	/**
	 * SNC compiler option +r - make reentrant at run-time
	 */
	SNC_OPTIONS_MAKE_REENTRANT_AT_RUN_TIME(".compiler.options.plus-r", "+r", "make reentrant at run-time"),

	/**
	 * SNC compiler option -w - suppress compiler warnings
	 */
	SNC_OPTIONS_SUPRESS_COMPILER_WARNINGS(".compiler.options.minus-w", "-w", "suppress compiler warnings"),

	/**
	 * SNC compiler option -l - suppress line numbering
	 */
	SNC_OPTIONS_SUPRESS_LINE_NUMBERING(".compiler.options.minus-l", "-l", "suppress line numbering"),

	/**
	 * SNC compiler option +d - turn on debug run-time option
	 */
	SNC_OPTIONS_TURN_ON_DEBUG_RUNTIME_OPTION(
			".compiler.options.plus-d", "+d", "turn on debug run-time option");
	
	private final String _preferenceStoreId;
	private final String _option;
	private final String _description;

	private CompilerOptionPreferenceConstants(String preferenceStoreId, String option, String description) {
		_preferenceStoreId = preferenceStoreId;
		_option = option;
		_description = description;
	}

	public String getPreferenceStoreId() {
		return _preferenceStoreId;
	}

	public String getOption() {
		return _option;
	}

	public String getDescription() {
		return _description;
	}

}
