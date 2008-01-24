package org.csstudio.sds.ui.internal.runmode;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

public class RunModeBoxInput implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6449572208586410269L;

	private transient IPath _filePath;

	private transient Map<String, String> _aliases;

	private RunModeType _type;

	public RunModeBoxInput(IPath filePath, Map<String, String> aliases,
			RunModeType type) {
		_filePath = filePath;
		_aliases = aliases;
		_type = type;
	}

	public void setFilePath(IPath filePath) {
		_filePath = filePath;
	}

	public void setAliases(Map<String, String> aliases) {
		_aliases = aliases;
	}

	public void setType(RunModeType type) {
		_type = type;
	}

	public IPath getFilePath() {
		return _filePath;
	}

	public Map<String, String> getAliases() {
		return _aliases;
	}

	public RunModeType getType() {
		return _type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_aliases == null) ? 0 : _aliases.hashCode());
		result = prime * result
				+ ((_filePath == null) ? 0 : _filePath.hashCode());
		result = prime * result + ((_type == null) ? 0 : _type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof RunModeBoxInput) {
			RunModeBoxInput input = (RunModeBoxInput) obj;

			if (_type == RunModeType.SHELL) {
				result = _filePath.equals(input.getFilePath());
			} else if (_type == RunModeType.VIEW) {
				// 
				result = false;
			}
		}
		return result;
	}

}
