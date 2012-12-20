/**
 * 
 */
package org.csstudio.sds.cosyrules.color;

import static org.csstudio.sds.cosyrules.color.MaintenanceRulePreference.MAINTENANCE_DISPLAY_PATH;
import static org.csstudio.sds.cosyrules.color.MaintenanceRulePreference.MAINTENANCE_PRE_FILE_NAME;
import static org.csstudio.sds.cosyrules.color.MaintenanceRulePreference.MAINTENANCE_UNKNOWN_DISPLAY_PATH;

import org.csstudio.sds.model.IRule;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 *
 */
public class MaintenanceRule implements IRule {
    
    private static final Logger LOG = LoggerFactory.getLogger(MaintenanceRule.class);
    
    private IPath _defaultPath;
    private IPath _dispayPath = null;
    private String _preFileName = null;
    
    //    private Map<String, IPath> _rTypUrlMap;
    
    /**
     * 
     */
    public MaintenanceRule() {
        init();
    }
    
    /**
    * 
    */
    private boolean init() {
        try {
            _defaultPath = MAINTENANCE_UNKNOWN_DISPLAY_PATH.getValue();
            _dispayPath = MAINTENANCE_DISPLAY_PATH.getValue();
            _preFileName = MAINTENANCE_PRE_FILE_NAME.getValue();
            return true;
        } catch (Exception e) {
            LOG.warn("Wrong preferences!", e);
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.sds.model.IRule#evaluate(java.lang.Object[])
     */
    @Override
    public Object evaluate(Object[] arguments) {
        IPath iPath = null;
        if(init()) {
            iPath = _defaultPath;
            if(arguments.length > 0) {
                Object obj = arguments[0];
                if(obj instanceof String) {
                    String rtyp = (String) obj;
                    int indexOf = _preFileName.toLowerCase().indexOf("{rtyp}");
                    if(indexOf >= 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(_preFileName.substring(0, indexOf));
                        sb.append(rtyp);
                        sb.append(_preFileName.substring(indexOf + 6));
                        if(!sb.toString().toLowerCase().endsWith(".css-sds")) {
                            sb.append(".css-sds");
                        }
                        iPath = _dispayPath.append(sb.toString());
                    } else {
                        iPath = _dispayPath.append(_preFileName + rtyp + ".css-sds");
                    }
                    IPath location = Platform.getLocation();
                    IPath append = location.append(iPath);
                    if(!append.toFile().isFile()) {
                        iPath = _defaultPath;
                    }
                }
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
