package de.desy.language.snl.compilerconfiguration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.desy.language.snl.configurationservice.ICompilerOptionsService;

/**
 * The abstract superclass of all compiler configurations.
 * 
 * @author Kai Meyer (C1 WPS)
 * 
 */
public abstract class AbstractCompilerConfiguration {

	/**
	 * The {@link ICompilerOptionsService} used to get the preferences.
	 */
	private final ICompilerOptionsService _service;

	/**
	 * Constructor.
	 * 
	 * @param service
	 *            The {@link ICompilerOptionsService} to use.
	 */
	public AbstractCompilerConfiguration(ICompilerOptionsService service) {
		_service = service;
	}

	/**
	 * Returns all needed parameters to invoke the external compiler. The
	 * Parameters have to include the path to the compiler itself. If the
	 * returned list is empty then the compilation finished successfully. The
	 * list shouldn't be null
	 * 
	 * @param sourceFile
	 *            The path to the file to be compiled
	 * @param targetFile
	 *            The path to the result of the compilation
	 * @return A list of error messages occurred during compilation
	 */
	public abstract List<String> getCompilerParameters(String sourceFile,
			String targetFile);

	/**
	 * Specifies the {@link Pattern} used to extract details (the line number
	 * and the message) out of the output of the {@link Process}. If the pattern
	 * is null, the complete output will be used as error message.
	 * 
	 * @return The {@link Pattern} used for by {@link Matcher}. Can be null
	 */
	public abstract Pattern getErrorPattern();

	/**
	 * Returns the path to the compiler.
	 * 
	 * @return The path to the compiler
	 */
	protected abstract String getCompilerPath();

	/**
	 * Return the path to the folder containing the file to compile.
	 * 
	 * @return The path to the folder containing the file to compile
	 */
	public abstract String getSourceFolder();

	/**
	 * Return the path to the folder containing the result of the compilation.
	 * 
	 * @return The path to the folder containing the result of the compilation
	 */
	public abstract String getTargetFolder();

	/**
	 * Returns the file extension of the file to compile.
	 * 
	 * @return The file extension of the file to compile
	 */
	public abstract String getSourceFileExtension();

	/**
	 * Returns the file extension of the result of the compilation.
	 * 
	 * @return The file extension of the result of the compilation
	 */
	public abstract String getTargetFileExtension();

	/**
	 * Returns the {@link ICompilerOptionsService} used for to get the
	 * preferences.
	 * 
	 * @return The {@link ICompilerOptionsService} to use.
	 */
	protected final ICompilerOptionsService getCompilerOptionService() {
		return _service;
	}

}