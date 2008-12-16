package org.csstudio.dct.model;

import org.csstudio.dct.model.internal.Project;

public interface IVisitor {
	void visit(Project project);
	void visit(IFolder folder);
	void visit(IPrototype prototype);
	void visit(IInstance instance);
	void visit(IRecord record);
}
