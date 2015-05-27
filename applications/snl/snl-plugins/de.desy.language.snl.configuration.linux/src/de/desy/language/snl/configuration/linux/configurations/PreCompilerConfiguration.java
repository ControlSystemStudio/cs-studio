package de.desy.language.snl.configuration.linux.configurations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.desy.language.snl.SNLConstants;
import de.desy.language.snl.compilerconfiguration.AbstractCompilerConfiguration;
import de.desy.language.snl.configurationservice.ICompilerOptionsService;
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
    public List<String> getCompilerParameters(String sourceFile,
            String targetFile) {
        boolean arch64 = "amd64".equals(System.getProperty("os.arch"));
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
        result.add(arch64 ? "-D_X86_64_" : "-D_X86_");
        result.add("-DUNIX");
        result.add("-D_BSD_SOURCE");
        result.add("-Dlinux");
        result.add("-D_REENTRANT");
        result.add("-I" + getCompilerOptionService().getSeqFolder() + "/include");
        result.add("-I" + getCompilerOptionService().getEpicsFolder() + "/include/os/Linux");
        String s = getCompilerOptionService().getEpicsFolder() + "/include/compiler/gcc";
        if (new File(s).exists()) {
            result.add("-I" + s);
        }
        result.add("-I" + getCompilerOptionService().getEpicsFolder() + "/include");
        result.add(sourceFile);
        return result;
    }

    @Override
    protected String getCompilerPath() {
        return getCompilerOptionService().getPreCompilerPath();
    }

    @Override
    public Pattern getErrorPattern() {
        return null;
    }

    @Override
    public String getSourceFileExtension() {
        return SNLConstants.ST_FILE_EXTENSION.getValue();
    }

    @Override
    public String getSourceFolder() {
        return SNLConstants.SOURCE_FOLDER.getValue();
    }

    @Override
    public String getTargetFileExtension() {
        return SNLConstants.I_FILE_EXTENSION.getValue();
    }

    @Override
    public String getTargetFolder() {
        return SNLConstants.GENERATED_FOLDER.getValue();
    }

}
