package org.csstudio.dct.metamodel.persistence;

import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.eclipse.core.runtime.IPath;

public interface IMetaModelService {
	IDatabaseDefinition read(String path);
}
