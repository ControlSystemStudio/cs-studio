package de.desy.language.snl.ui.preferences;

import java.util.List;

public interface ICompilerOptionsService {

	/**
	 * Returns the compiler path (directory) stored in the preference store if
	 * exists, null otherwise.
	 * 
	 * @return The Path as a File-instance or null if no valid path is avail.
	 */
	public abstract String getSNCompilerPath();

	public abstract String getCCompilerPath();
	
	public abstract String getEpicsFolder();

	public abstract String getSeqFolder();
	
	public abstract List<String> getCCompilerOptions();

}