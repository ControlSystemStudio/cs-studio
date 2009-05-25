package de.desy.language.snl.ui.editor.compilerconfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.desy.language.snl.ui.SNLEditorConstants;
import de.desy.language.snl.ui.preferences.ICompilerOptionsService;
/*
 * Original compiler command:
 * /usr/bin/gcc -x c -E   -D_POSIX_C_SOURCE=199506L -D_POSIX_THREADS -D_XOPEN_SOURCE=500
 * -D_X86_  -DUNIX  -D_BSD_SOURCE -Dlinux  -D_REENTRANT -I. -I../O.Common -I. -I.. -I../../../include/os/Linux -I../../../include  -I/scratch/EpicsR3.14.10/base/../unbundled/seq/include -I/scratch/EpicsR3.14.10/base/include/os/Linux -I/scratch/EpicsR3.14.10/base/include       ../sncProgram.st > sncProgram.i
 */
public class PreCompilerConfiguration extends AbstractCompilerConfiguration {

	public PreCompilerConfiguration(ICompilerOptionsService service) {
		super(service);
	}

	@Override
	public List<String> getCompilerParameter(String sourceFile,
			String targetFile) {
		List<String> result = new ArrayList<String>();
		result.add(getCompilerPath());
		result.add("-x");
		result.add("c");
		result.add("-o");
		result.add(targetFile);
		result.add("-E");
		result.add("-D_POSTFIX_C_SOURCE=199506L");
		result.add("-D_POSTFIX_THREADS");
		result.add("-D_XOPEN_SOURCE=500");
		result.add("-D_X86_");
		result.add("-DUNIX");
		result.add("-D_BSD_SOURCE");
		result.add("-Dlinux");
		result.add("-D_REENTRANT");
		result.add("-I" + getCompilerOptionService().getSeqFolder() + "/include");
		result.add("-I" + getCompilerOptionService().getEpicsFolder() + "/include/os/Linux");
		result.add("-I" + getCompilerOptionService().getEpicsFolder() + "/include");
		result.add(sourceFile);
		return result;
	}

	@Override
	public String getCompilerPath() {
		return getCompilerOptionService().getCCompilerPath();
	}

	@Override
	public Pattern getErrorPattern() {
		return null;
	}

	@Override
	public String getSourceFileExtension() {
		return SNLEditorConstants.ST_FILE_EXTENSION.getValue();
	}

	@Override
	public String getSourceFolder() {
		return SNLEditorConstants.SOURCE_FOLDER.getValue();
	}

	@Override
	public String getTargetFileExtension() {
		return SNLEditorConstants.I_FILE_EXTENSION.getValue();
	}

	@Override
	public String getTargetFolder() {
		return SNLEditorConstants.GENERATED_FOLDER.getValue();
	}

}
