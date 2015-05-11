package org.csstudio.sds.model;

import java.util.Collections;
import java.util.Map;

import org.csstudio.sds.internal.runmode.RunModeBoxInput;
import org.eclipse.core.runtime.IPath;
import org.csstudio.dal.simple.SimpleDALBroker;

/**
 * Collects runtime information for a display.
 *
 * When a display is opened in run mode, we bind this runtime context to the
 * {@link DisplayModel} to be able to access that information via widget
 * controllers, e.g. to close the current shell on a menu button click.
 *
 * @author Sven Wende
 *
 */
public class RuntimeContext {
    private IPath _displayFilePath;
    private Map<String, String> _aliases;
    private RunModeBoxInput _runModeBoxInput;
    private SimpleDALBroker _broker;

    /**
     * Constructor.
     *
     * @param windowHandle
     *            a runtime window handle
     *
     * @param displayFilePath
     *            the path of the opened file at runtime
     *
     * @param aliases
     *            the runtime aliases
     */
    public RuntimeContext(IPath displayFilePath, Map<String, String> aliases) {
        _displayFilePath = displayFilePath;
        _aliases = aliases;
    }

    public IPath getDisplayFilePath() {
        return _displayFilePath;
    }

    public void setDisplayFilePath(IPath displayFilePath) {
        _displayFilePath = displayFilePath;
    }

    public Map<String, String> getAliases() {
        return Collections.unmodifiableMap(_aliases);
    }

    public void setAliases(Map<String, String> aliases) {
        _aliases = aliases;
    }

    public RunModeBoxInput getRunModeBoxInput() {
        return _runModeBoxInput;
    }

    public void setRunModeBoxInput(RunModeBoxInput runModeBoxInput) {
        _runModeBoxInput = runModeBoxInput;
    }

    public void setBroker(SimpleDALBroker broker) {
        _broker = broker;
    }

    public SimpleDALBroker getBroker() {
        return _broker;
    }
}
