package org.csstudio.dct.model.internal;

import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.eclipse.ui.internal.Model;

public class MyVisitor implements IVisitor {
	private UUID id;
	private IElement result;

	
	/**
	 *{@inheritDoc}
	 */
	public void visit(Project project) {
		doVisit(project);
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IFolder folder) {
		doVisit(folder);
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IPrototype prototype) {
		doVisit(prototype);
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IInstance instance) {
		doVisit(instance);
	}

	/**
	 *{@inheritDoc}
	 */
	public void visit(IRecord record) {
		doVisit(record);
	}
	
	public IElement search(Project project, UUID id) {
		this.id = id;
		this.result = null;
		project.accept(this);
		
		return result;
	}

	private void doVisit(IElement element) {
		if (id.equals(element.getId())) {
			result = element;
		}
	}
}
