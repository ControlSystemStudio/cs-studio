package org.csstudio.dct.model.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.util.CompareUtil;

/**
 * Standard implementation for {@link IContainer}. Base class for
 * {@link Instance} and {@link Prototype}.
 *
 * @author Sven Wende
 */
public abstract class AbstractContainer extends AbstractPropertyContainer implements IContainer, IFolderMember {

    private IContainer container;

    /**
     * The parent in the inheritance hierarchy.
     */
    private IContainer parent;

    /**
     * The folder, this contain resides in. May be null.
     */
    private transient IFolder folder;

    /**
     * All containers (instances or prototypes) that inherit from this
     * container.
     */
    private transient Set<IContainer> dependentContainers = new HashSet<IContainer>();

    /**
     * Contained instances.
     */
    private List<IInstance> instances = new ArrayList<IInstance>();

    /**
     * Contained records.
     */
    private List<IRecord> records = new ArrayList<IRecord>();

    public AbstractContainer() {
        dependentContainers = new HashSet<IContainer>();
        instances = new ArrayList<IInstance>();
        records = new ArrayList<IRecord>();
    }

    /**
     * Constructor.
     *
     * @param name
     *            the name
     * @param parent
     *            the parent container
     * @param id
     *            the id
     */
    public AbstractContainer(String name, IContainer parent, UUID id) {
        super(name, id);
        assert (parent != null) || (this instanceof IPrototype) || (this instanceof IFolder) : "Each instance must have a parent. Only prototypes and folders have no parent.";
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IContainer getContainer() {
        return container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setContainer(IContainer container) {
        this.container = container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IContainer getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<IInstance> getInstances() {
        return instances;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IInstance getInstance(int index) {
        return instances.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<IContainer> getDependentContainers() {
        return dependentContainers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addDependentContainer(IContainer container) {
        assert container != null;
        assert container.getParent() == this : "Container must inherit from here.";
        dependentContainers.add(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeDependentContainer(IContainer container) {
        assert container != null;
        assert container.getParent() == this : "Container must inherit from here.";
        dependentContainers.remove(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addInstance(IInstance instance) {
        assert instance.getParent() != null : "Instance must have a hierarchical parent.";
        assert instance.getContainer() == null : "Instance must not be in a container yet.";

        instances.add(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setInstance(int index, IInstance instance) {
        assert instance.getParent() != null : "Instance must have a hierarchical parent.";
        assert instance.getContainer() == null : "Instance must not be in a container yet.";

        // .. fill with nulls
        while (index >= instances.size()) {
            instances.add(null);
        }

        instances.set(index, instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addInstance(int index, IInstance instance) {
        assert instance.getParent() != null : "Instance must have a hierarchical parent.";
        assert instance.getContainer() == null : "Instance must not be in a container yet.";

        instances.add(index, instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeInstance(IInstance instance) {
        assert instance.getParent() != null;

        if (instance.getContainer() != null && instance.getContainer() != this) {
            assert instance.getContainer() == this : "The physical container must equal this.";
        }
        instances.remove(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<IRecord> getRecords() {
        return records;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addRecord(IRecord record) {
        assert record.getContainer() == null : "Record must not be part of another container.";

        records.add(record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setRecord(int index, IRecord record) {
        assert record.getContainer() == null : "Record must not be part of another container.";

        // .. fill with nulls
        while (index >= records.size()) {
            records.add(null);
        }

        records.set(index, record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addRecord(int index, IRecord record) {
        assert record.getContainer() == null : "Record must not be part of another container.";
        records.add(index, record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeRecord(IRecord record) {
        assert record.getContainer() == this : "Record must not be part of this container.";

        records.remove(record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IFolder getParentFolder() {
        return folder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IProject getProject() {
        IFolder f;

        if (folder != null) {
            f = folder;

            while (f != null && f.getParentFolder() != null) {
                f = f.getParentFolder();
            }
            assert f != null;
            assert f instanceof IProject;
            return (IProject) f;
        } else {
            return parent.getProject();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setParentFolder(IFolder folder) {
        this.folder = folder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Map<String, String> getFinalParameterValues() {
        Map<String, String> result = new HashMap<String, String>();

        Stack<IContainer> stack = getParentStack();

        while (!stack.isEmpty()) {
            IContainer top = stack.pop();
            result.putAll(top.getParameterValues());
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Map<String, String> getFinalProperties() {
        Map<String, String> result = new HashMap<String, String>();

        Stack<IContainer> stack = getParentStack();

        while (!stack.isEmpty()) {
            IContainer top = stack.pop();
            result.putAll(top.getProperties());
        }

        return result;
    }

    /**
     * Collect all parent containers in a stack. On top of the returned stack is
     * the parent that resides at the top of the hierarchy.
     *
     * @return all parent containers, including this
     */
    protected final Stack<IContainer> getParentStack() {
        Stack<IContainer> stack = new Stack<IContainer>();

        IContainer c = this;

        while (c != null) {
            stack.add(c);
            c = c.getParent();
        }
        return stack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<IRecordContainer> getDependentRecordContainers() {
        return new ArrayList<IRecordContainer>(dependentContainers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof AbstractContainer) {
            AbstractContainer c = (AbstractContainer) obj;

            // .. super
            if (super.equals(obj)) {
                // .. instances
                if (getInstances().equals(c.getInstances())) {
                    // .. records
                    if (getRecords().equals(c.getRecords())) {
                        // .. parent (we check the id only, to prevent stack
                        // overflows)
                        if (CompareUtil.idsEqual(getParent(), c.getParent())) {
                            result = true;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
