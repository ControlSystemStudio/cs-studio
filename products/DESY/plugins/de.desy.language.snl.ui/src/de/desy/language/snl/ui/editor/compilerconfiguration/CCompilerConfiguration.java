package de.desy.language.snl.ui.editor.compilerconfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.desy.language.snl.ui.SNLEditorConstants;
import de.desy.language.snl.ui.preferences.ICompilerOptionsService;

public class CCompilerConfiguration extends AbstractCompilerConfiguration {
	
	public CCompilerConfiguration(ICompilerOptionsService service) {
		super(service);
	}
	
	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getCompilerOptions()
	 */
	public List<String> getCompilerParameter(String sourceFile, String targetFile) {
		List<String> result = new ArrayList<String>();
		result.add(getCompilerPath());
		result.add("-c");
		result.add("-o");
		result.add(targetFile);
		result.add("-D_POSIX_C_SOURCE=199506L");
		result.add("-D_POSIX_THREADS");
		result.add("-D_XOPEN_SOURCE=500");
		result.add("-D_X86_");
		result.add("-DUNIX");
		result.add("-D_BSD_SOURCE");
		result.add("-Dlinux");
		result.add("-D_REENTRANT");
		result.add("-ansi");
		result.add("-O3");
		result.add("-Wall");
		result.add("-m32");
		result.add("-g");
		result.add("-fPIC");
		result.add("-I" + getCompilerOptionService().getSeqFolder() + "/include");
		result.add("-I" + getCompilerOptionService().getEpicsFolder() + "/include/os/Linux");
		result.add("-I" + getCompilerOptionService().getEpicsFolder() + "/include");
		result.add(sourceFile);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getErrorPattern()
	 */
	public Pattern getErrorPattern() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getCompilerPath()
	 */
	public String getCompilerPath() {
		return getCompilerOptionService().getCCompilerPath();
	}
	
	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getSourceFolder()
	 */
	public String getSourceFolder() {
		return SNLEditorConstants.GENERATED_FOLDER.getValue();
	}
	
	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getTargetFolder()
	 */
	public String getTargetFolder() {
		return SNLEditorConstants.BIN_FOLDER.getValue();
	}
	
	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getSourceFileExtension()
	 */
	public String getSourceFileExtension() {
		return SNLEditorConstants.C_FILE_EXTENSION.getValue();
	}
	
	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getTargetFileExtension()
	 */
	public String getTargetFileExtension() {
		return SNLEditorConstants.O_FILE_EXTENSION.getValue();
	}

}
