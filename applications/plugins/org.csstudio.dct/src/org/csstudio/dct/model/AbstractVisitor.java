package org.csstudio.dct.model;

import org.csstudio.dct.model.internal.Project;

/**
 * Base class for visitors with empty implementations of {@link IVisitor}
 * methods.
 *
 * Subclasses can override the single visit() method as needed.
 *
 * @author Sven Wende
 *
 */
public abstract class AbstractVisitor implements IVisitor {

    /**
     *{@inheritDoc}
     */
    public void visit(Project project) {
    }

    /**
     *{@inheritDoc}
     */
    public void visit(IFolder folder) {
    }

    /**
     *{@inheritDoc}
     */
    public void visit(IPrototype prototype) {

    }

    /**
     *{@inheritDoc}
     */
    public void visit(IInstance instance) {

    }

    /**
     *{@inheritDoc}
     */
    public void visit(IRecord record) {

    }

}
