package de.desy.language.snl.configurationservice;

import java.util.List;

/**
 * Interface of a service to get the compiler options from the preferences.
 * 
 * @author Kai Meyer (C1 WPS)
 * 
 */
public interface ICompilerOptionsService {

	/**
	 * Returns the SN compiler path (directory) stored in the preference store
	 * if exists, null otherwise.
	 * 
	 * @return The Path as String or null if no valid path is avail.
	 */
	public abstract String getSNCompilerPath();

	/**
	 * Returns the c compiler path (directory) stored in the preference store if
	 * exists, null otherwise.
	 * 
	 * @return The Path as String or null if no valid path is avail.
	 */
	public abstract String getCCompilerPath();

	/**
	 * Returns the pre-compiler path (directory) stored in the preference store
	 * if exists, null otherwise.
	 * 
	 * @return The Path as String or null if no valid path is avail.
	 */
	public abstract String getPreCompilerPath();
	
	/**
	 * Returns the application compiler path (directory) stored in the preference store
	 * if exists, null otherwise.
	 * 
	 * @return The Path as String or null if no valid path is avail.
	 */
	public abstract String getApplicationCompilerPath();

	/**
	 * Returns the path (directory) to the EPICS environment (the BASE
	 * directory) stored in the preference store if exists, null otherwise.
	 * 
	 * @return The Path as String or null if no valid path is avail.
	 */
	public abstract String getEpicsFolder();

	/**
	 * Returns the path (directory) to an additional folder (the SEQ folder) for
	 * included files stored in the preference store if exists, null otherwise.
	 * 
	 * @return The Path as String or null if no valid path is avail.
	 */
	public abstract String getSeqFolder();

	/**
	 * Returns a list of compiler options for the SNC represented as Strings stored in the preference store.
	 * The list can be empty, but won't be null.
	 * 
	 * @return The list of compiler options.
	 */
	public abstract List<String> getCCompilerOptions();
	
	/**
	 * Returns if the generated files should be kept or not.
	 * 
	 * @return <code>true</code> if the generated files should be kept, <code>false</code> otherwise
	 */
	public abstract boolean getKeepGeneratedFiles();
	
	/**
	 * Returns if the file should be compiled after saving or not.
	 * 
	 * @return <code>true</code> if the file should be compiled after saving, <code>false</code> otherwise
	 */
	public abstract boolean getSaveAndCompile();

}