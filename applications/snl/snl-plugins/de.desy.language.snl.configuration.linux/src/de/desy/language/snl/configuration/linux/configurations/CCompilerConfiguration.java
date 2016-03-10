package de.desy.language.snl.configuration.linux.configurations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.desy.language.snl.SNLConstants;
import de.desy.language.snl.compilerconfiguration.AbstractCompilerConfiguration;
import de.desy.language.snl.configurationservice.ICompilerOptionsService;

public class CCompilerConfiguration extends AbstractCompilerConfiguration {

    public CCompilerConfiguration(ICompilerOptionsService service) {
        super(service);
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getCompilerOptions()
     */
    @Override
    public List<String> getCompilerParameters(String sourceFile, String targetFile) {
        boolean arch64 = "amd64".equals(System.getProperty("os.arch"));
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
        result.add(arch64 ? "-m64" : "-m32");
        result.add("-g");
        result.add("-fPIC");
        result.add("-I" + getCompilerOptionService().getSeqFolder() + "/include");
        result.add("-I" + getCompilerOptionService().getEpicsFolder() + "/include/os/Linux");
        String scratchFolder = getCompilerOptionService().getScratchFolder();
        if (scratchFolder != null && !scratchFolder.isEmpty()) {
            result.add("-I" + getCompilerOptionService().getScratchFolder() + "/include");
        }
        String s = getCompilerOptionService().getEpicsFolder() + "/include/compiler/gcc";
        if (new File(s).exists()) {
            result.add("-I" + s);
        }
        result.add("-I" + getCompilerOptionService().getEpicsFolder() + "/include");
        result.add(sourceFile);
        return result;
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getErrorPattern()
     */
    @Override
    public Pattern getErrorPattern() {
        return null;
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getCompilerPath()
     */
    @Override
    protected String getCompilerPath() {
        return getCompilerOptionService().getCCompilerPath();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getSourceFolder()
     */
    @Override
    public String getSourceFolder() {
        return SNLConstants.GENERATED_FOLDER.getValue();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getTargetFolder()
     */
    @Override
    public String getTargetFolder() {
        return SNLConstants.BIN_FOLDER.getValue();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getSourceFileExtension()
     */
    @Override
    public String getSourceFileExtension() {
        return SNLConstants.C_FILE_EXTENSION.getValue();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getTargetFileExtension()
     */
    @Override
    public String getTargetFileExtension() {
        return SNLConstants.O_FILE_EXTENSION.getValue();
    }

}
