/**
 * 
 */
package org.csstudio.sds.cosyrules.color;

import java.util.Map;

import org.csstudio.sds.model.IRule;
import org.eclipse.core.runtime.IPath;

/**
 * @author hrickens
 *
 */
public class MaintenanceRule implements IRule {

    private static final IPath DEFAULT_PATH = null;
    private Map<String, IPath> _rTypUrlMap;

    /**
     * 
     */
    public MaintenanceRule() {
        init();
    }
    
    /**
     * 
     */
    private void init() {
        _rTypUrlMap = MaintenanceRulePreference.getRTypUrlMap();
    }

    /* (non-Javadoc)
     * @see org.csstudio.sds.model.IRule#evaluate(java.lang.Object[])
     */
    @Override
    public Object evaluate(Object[] arguments) {
        IPath iPath =  DEFAULT_PATH;
        if(arguments.length>0) {
            Object key = arguments[0];
            if(_rTypUrlMap.containsKey(key)) {
                iPath = _rTypUrlMap.get(key);
            }
        }
        return iPath;
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.sds.model.IRule#getDescription()
     */
    @Override
    public String getDescription() {
        return "Gibt das Maintenance Display für den übergebenen RTYP wieder";
    }
    
}
