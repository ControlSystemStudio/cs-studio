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
    @Override
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
    @Override
    public Pattern getErrorPattern() {
        return Pattern.compile("(syntax error: line no. )([\\d]*)([^\\n]*)(\\n)([\\S\\s]*)");
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getCompilerPath()
     */
    @Override
    protected String getCompilerPath() {
        return getCompilerOptionService().getSNCompilerPath();
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
        return SNLConstants.GENERATED_FOLDER.getValue();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getSourceFileExtension()
     */
    @Override
    public String getSourceFileExtension() {
        return SNLConstants.I_FILE_EXTENSION.getValue();
    }

    /* (non-Javadoc)
     * @see de.desy.language.snl.ui.editor.compilerconfiguration.ICompilerConfiguration#getTargetFileExtension()
     */
    @Override
    public String getTargetFileExtension() {
        return SNLConstants.C_FILE_EXTENSION.getValue();
    }

}
