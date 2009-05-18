package de.desy.language.snl.ui.preferences;

import java.io.File;
import java.util.List;

public interface ICompilerOptionsService {

	/**
	 * Returns the compiler path (directory) stored in the preference store if
	 * exists, null otherwise.
	 * 
	 * @return The Path as a File-instance or null if no valid path is avail.
	 */
	public abstract File getSNCompilerPath();

	public abstract File getCCompilerPath();
	
	public abstract File getEpicsFolder();

	public abstract File getSeqFolder();
	
	public abstract List<String> getCompilerOptions();

}