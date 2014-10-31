package de.desy.language.snl.parser.nodes;

/**
 * The outline node of an SNL variable.
 * 
 * @author C1 WPS / KM
 */
public class AllDefineStatementsNode extends AbstractSNLNode {

	public AllDefineStatementsNode() {
	}

	@Override
	public String humanReadableRepresentation() {
		return "Defines";
	}

	@Override
	public String getNodeTypeName() {
		return "Defines";
	}

	@Override
	protected String doGetSourceIdentifier() {
		return "Defines";
	}

}
