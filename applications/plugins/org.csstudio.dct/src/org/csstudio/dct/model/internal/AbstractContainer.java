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
	private IFolder folder;

	/**
	 * All containers (instances or prototypes) that inherit from this
	 * container.
	 */
	private Set<IContainer> dependentContainers = new HashSet<IContainer>();

	/**
	 * Contained instances.
	 */
	private List<IInstance> instances = new ArrayList<IInstance>();

	/**
	 * Contained records.
	 */
	private List<IRecord> records = new ArrayList<IRecord>();

	public AbstractContainer(String name, IContainer parent, UUID id) {
		super(name, id);
		assert (parent != null) || (parent instanceof IPrototype) || (parent instanceof IFolder) : "Each instance must have a parent. Only prototypes have no parent.";
		this.parent = parent;
	}

	public IContainer getContainer() {
		return container;
	}

	public void setContainer(IContainer container) {
		this.container = container;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IContainer getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IInstance> getInstances() {
		return instances;
	}

	/**
	 * {@inheritDoc}
	 */
	public IInstance getInstance(int index) {
		return instances.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IContainer> getDependentContainers() {
		return dependentContainers;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDependentContainer(IContainer container) {
		assert container != null;
		assert container.getParent() == this : "Container must inherit from here.";
		dependentContainers.add(container);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDependentContainer(IContainer container) {
		assert container != null;
		assert container.getParent() == this : "Container must inherit from here.";
		dependentContainers.remove(container);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addInstance(IInstance instance) {
		assert instance.getParent() != null : "Instance must have a hierarchical parent.";
		assert instance.getContainer() == null : "Instance must not be in a container yet.";

		instances.add(instance);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInstance(int index, IInstance instance) {
		assert instance.getParent() != null : "Instance must have a hierarchical parent.";
		assert instance.getContainer() == null : "Instance must not be in a container yet.";

		// .. fill with nulls
		while(index>=instances.size()) {
			instances.add(null);
		}
		
		instances.set(index, instance);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addInstance(int index, IInstance instance) {
		assert instance.getParent() != null : "Instance must have a hierarchical parent.";
		assert instance.getContainer() == null : "Instance must not be in a container yet.";

		instances.add(index, instance);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeInstance(IInstance instance) {
		assert instance.getParent() != null;
		assert instance.getContainer() == this : "The physical container must equal this.";
		instances.remove(instance);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRecord> getRecords() {
		return records;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRecord(IRecord record) {
		assert record.getContainer() == null : "Record must not be part of another container.";

		records.add(record);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setRecord(int index, IRecord record) {
		assert record.getContainer() == null : "Record must not be part of another container.";
		
		// .. fill with nulls
		while(index>=records.size()) {
			records.add(null);
		}
		
		records.set(index, record);
	}

	public void addRecord(int index, IRecord record) {
		assert record.getContainer() == null : "Record must not be part of another container.";
		records.add(index, record);
	}

	public void removeRecord(IRecord record) {
		assert record.getContainer() == this : "Record must not be part of this container.";

		records.remove(record);
	}

	/**
	 * {@inheritDoc}
	 */
	public IFolder getParentFolder() {
		return folder;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParentFolder(IFolder folder) {
		this.folder = folder;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getFinalParameterValues() {
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
	public Map<String, String> getFinalProperties() {
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
	protected Stack<IContainer> getParentStack() {
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
	public List<IRecordContainer> getDependentRecordContainers() {
		return new ArrayList<IRecordContainer>(dependentContainers);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof AbstractContainer) {
			AbstractContainer container = (AbstractContainer) obj;

			// .. super
			if (super.equals(obj)) {
				// .. instances
				if (getInstances().equals(container.getInstances())) {
					// .. records
					if (getRecords().equals(container.getRecords())) {
						// .. parent (we check the id only, to prevent stack overflows)
						if (CompareUtil.idsEqual(getParent(), container.getParent())) {
							result = true;
						}
					}
				}
			}
		}

		return result;
	}

}
