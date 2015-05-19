package de.desy.language.snl.configuration.linux.configurations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.desy.language.snl.SNLConstants;
import de.desy.language.snl.compilerconfiguration.AbstractCompilerConfiguration;
import de.desy.language.snl.configurationservice.ICompilerOptionsService;

public class SNCompilerConfiguration extends AbstractCompilerConfiguration {

    public SNCompilerConfiguration(ICompilerOptionsService service) {
        super(service);
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getCompilerOptions()
     */
    public List<String> getCompilerParameters(String sourceFile, String targetFile) {
        List<String> result = new ArrayList<String>();
        result.add(getCompilerPath());
        result.addAll(getCompilerOptionService().getCCompilerOptions());
        result.add("-o");
        result.add(targetFile);
        result.add(sourceFile);
        return result;
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getErrorPattern()
     */
    public Pattern getErrorPattern() {
        return Pattern.compile("(syntax error: line no. )([\\d]*)([^\\n]*)(\\n)([\\S\\s]*)");
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getCompilerPath()
     */
    protected String getCompilerPath() {
        return getCompilerOptionService().getSNCompilerPath();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getSourceFolder()
     */
    public String getSourceFolder() {
        return SNLConstants.GENERATED_FOLDER.getValue();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getTargetFolder()
     */
    public String getTargetFolder() {
        return SNLConstants.GENERATED_FOLDER.getValue();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getSourceFileExtension()
     */
    public String getSourceFileExtension() {
        return SNLConstants.I_FILE_EXTENSION.getValue();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getTargetFileExtension()
     */
    public String getTargetFileExtension() {
        return SNLConstants.C_FILE_EXTENSION.getValue();
    }

}
