package org.csstudio.dct.model;

import org.csstudio.dct.model.internal.Project;

/**
 * Visitor is an interface that has a visit() method for each element class of
 * the hierarchical DCT model. The accept() method of an element class calls
 * back the visit() method for its class. Separate concrete visitor classes can
 * then be written that perform some particular operations, by implementing
 * these operations in their respective visit() methods.
 *
 * @author Sven Wende
 *
 */
public interface IVisitor {
    /**
     * Call back method for {@link Project} nodes.
     *
     * @param project
     *            a project
     */
    void visit(Project project);

    /**
     * Call back method for {@link IFolder} nodes.
     * @param folder a folder
     */
    void visit(IFolder folder);

    /**
     * Call back method for {@link IPrototype} nodes.
     * @param prototype a prototype
     */
    void visit(IPrototype prototype);

    /**
     * Call back method for {@link IInstance} nodes.
     * @param instance a instance
     */
    void visit(IInstance instance);

    /**
     * Call back method for {@link IRecord} nodes.
     * @param record a record
     */
    void visit(IRecord record);
}
