package org.csstudio.platform.model.rfc;


public class CommonNameParser extends
		AbstractProcessVariableNameParser {
	private ControlSystemEnum _controlSystem;
	
	public CommonNameParser(ControlSystemEnum controlSystem) {
		assert controlSystem != null;
		_controlSystem = controlSystem;
	}

	@Override
	protected IProcessVariableAdress doParse(String nameWithoutPrefix, String rawName) {
		IProcessVariableAdress result = new ProcessVariable(rawName,
				_controlSystem, null, nameWithoutPrefix, null);
		return result;
	}

}
