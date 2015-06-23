/**
 *
 */
package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.Comparator;

import org.csstudio.sds.model.WidgetProperty;

final class PropertyComparator implements Comparator<WidgetProperty> {
    public int compare(WidgetProperty w1, WidgetProperty w2){
        return w1.getDescription().compareTo(w2.getDescription());
    }
}