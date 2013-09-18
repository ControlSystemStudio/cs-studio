package org.csstudio.autocomplete.parser.engine.expr;

public class ExprPV extends Expr {

	private String name;

	public ExprPV(String name) {
		super(ExprType.PV);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ExprPV))
			return false;

		ExprPV pv = (ExprPV) obj;
		return pv.name.equals(name);
	}

}
