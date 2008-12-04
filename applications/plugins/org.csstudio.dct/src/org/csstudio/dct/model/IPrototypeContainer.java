package org.csstudio.dct.model;

public interface IPrototypeContainer {
	void addPrototype(IPrototype prototype);
	void addPrototype(IPrototype prototype, int index);
	void removePrototype(IPrototype prototype);
	void removePrototype(int index);
}
