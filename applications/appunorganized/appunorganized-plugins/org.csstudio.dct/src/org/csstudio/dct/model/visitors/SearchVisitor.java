package org.csstudio.dct.model.visitors;

import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.model.internal.Project;

/**
 * Visitor implementation that can be used to find elements in the hierarchical
 * model by their id.
 *
 * @author swende
 *
 */
public final class SearchVisitor implements IVisitor {
    private UUID id;
    private IElement result;

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(Project project) {
        doVisit(project);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IFolder folder) {
        doVisit(folder);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IPrototype prototype) {
        doVisit(prototype);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IInstance instance) {
        doVisit(instance);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IRecord record) {
        doVisit(record);
    }

    /**
     * Deep search for an element in a project.
     *
     * @param project
     *            the project
     * @param id
     *            the id of the target element
     * @return an element with the specified id or null
     */
    public IElement search(IProject project, UUID id) {
        assert project != null;
        assert id != null;

        this.id = id;
        this.result = null;
        project.accept(this);

        assert result == null || id.equals(result.getId());

        return result;
    }

    /**
     * Checks whether the id of the visited element matches the target id.
     *
     * @param element
     *            the element
     */
    private void doVisit(IElement element) {
        if (id.equals(element.getId())) {
            result = element;
        }
    }
}
