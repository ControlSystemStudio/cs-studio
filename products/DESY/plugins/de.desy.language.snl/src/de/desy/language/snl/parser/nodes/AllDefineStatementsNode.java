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
		return "Define";
	}

	@Override
	public String getNodeTypeName() {
		return "Define";
	}

	@Override
	protected String doGetSourceIdentifier() {
		return "Define";
	}

}
