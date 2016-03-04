package org.csstudio.dct.model.visitors;

import java.util.ArrayList;
import java.util.List;
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
 * Visitor implementation that can be used to find instances using their
 * prototype´s id as search criteria.
 *
 * @author swende
 *
 */
public final class SearchInstancesVisitor implements IVisitor {
    private UUID prototypeId;
    private List<IInstance> result;

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(Project project) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IFolder folder) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IPrototype prototype) {
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IRecord record) {

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IInstance instance) {
        if(prototypeId.equals(instance.getPrototype().getId())) {
            result.add(instance);
        }
    }


    /**
     * Deep search for instances in a project.
     *
     * @param project
     *            the project
     * @param prototypeId
     *            the id of a prototype
     * @return all instances of the specified prototype
     */
    public List<IInstance> search(IProject project, UUID prototypeId) {
        assert project != null;
        assert prototypeId != null;

        this.prototypeId = prototypeId;
        this.result = new ArrayList<IInstance>();

        project.accept(this);

        return result;
    }


}
