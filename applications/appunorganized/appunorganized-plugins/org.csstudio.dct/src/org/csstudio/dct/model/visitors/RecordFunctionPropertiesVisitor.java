package org.csstudio.dct.model.visitors;

import java.util.Map;

import org.csstudio.dct.IRecordFunction;
import org.csstudio.dct.model.AbstractVisitor;
import org.csstudio.dct.model.IRecord;

/**
 * Visitor that equips all abstract records of a project with properties that
 * belong to {@link IRecordFunction} extensions.
 *
 * @author Sven Wende
 *
 */
public final class RecordFunctionPropertiesVisitor extends AbstractVisitor {
    private Map<String, String> properties;

    public RecordFunctionPropertiesVisitor( Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void visit(IRecord record) {
        if (record.isAbstract()) {
            for (String key : properties.keySet()) {
                if (!record.getProperties().containsKey(key)) {
                    record.addProperty(key, properties.get(key));
                }
            }
        }
    }
}
