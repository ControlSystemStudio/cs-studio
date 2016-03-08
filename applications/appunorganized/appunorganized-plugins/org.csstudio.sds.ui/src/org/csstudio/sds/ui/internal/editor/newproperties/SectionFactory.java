package org.csstudio.sds.ui.internal.editor.newproperties;

import org.csstudio.sds.model.PropertyTypesEnum;
import org.eclipse.ui.views.properties.tabbed.ISection;

/**
 * Default implementation of {@link ISectionFactory}.
 *
 * @author Sven Wende
 *
 */
public class SectionFactory implements ISectionFactory {
    /**
     *{@inheritDoc}
     */
    @Override
    public ISection createSection(String propertyId, PropertyTypesEnum type) {
        ISection section = null;
        switch (type) {
        case COLOR:
            section = new ColorSection(propertyId);
            break;
        case INTEGER:
            section = new IntegerSection(propertyId);
            break;
        case DOUBLE:
            section = new DoubleSection(propertyId);
            break;
        case STRING:
            section = new TextSection(propertyId);
            break;
        case BOOLEAN:
            section = new BooleanSection(propertyId);
            break;
        case OPTION:
            section = new OptionSection(propertyId);
            break;
        case ARRAYOPTION:
            section = new ArrayOptionSection(propertyId);
            break;
        case PARAMSTRING:
            section = new TooltipSection(propertyId);
            break;
        case MAP:
            section = new StringMapSection(propertyId);
            break;
        case DOUBLEARRAY:
            section = new DoubleArraySection(propertyId);
            break;
        case FONT:
            section = new FontSection(propertyId);
            break;
        case BEHAVIOR:
            section = new BehaviorSection(propertyId);
            break;
        case ACTION:
            section = new ActionDataSection(propertyId);
            break;
        case RESOURCE:
            section = new RessourceSection(propertyId);
            break;
        case POINT_LIST:
            section = new PointListSection(propertyId);
            break;
        default:
            section = new MissingImplementationSection(propertyId);
            break;
        }

        assert section!=null;

        return section;
    }
}
