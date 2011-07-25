/**
 * 
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;

/**
 * @author hrickens
 *
 */
public class ParsedGsdFileModel extends AbstractGsdPropertyModel implements Serializable  {
    
    private static final long serialVersionUID = 1L;
    
    private final String _name;
    private final Map<Integer, PrmText> _prmTextMap;
    private final Map<Integer, GsdModuleModel2> _gsdModuleModelMap;
    private final Map<Integer, ExtUserPrmData> _gsdExtUserPrmData;
    private final GSDFileDBO _gsdFileDBO;
    
    /**
     * Constructor.
     * @param gsdFileDBO
     */
    public ParsedGsdFileModel(@Nonnull GSDFileDBO gsdFileDBO) {
        _gsdFileDBO = gsdFileDBO;
        _name = gsdFileDBO.getName();
        _prmTextMap = new TreeMap<Integer, PrmText>();
        _gsdModuleModelMap = new TreeMap<Integer, GsdModuleModel2>();
        _gsdExtUserPrmData = new HashMap<Integer, ExtUserPrmData>();
    }
    
    @Nonnull
    public String getName() {
        return _name;
    }
    
    @Nonnull
    public Map<Integer, PrmText> getPrmTextMap() {
        return _prmTextMap;
    }
    
    /**
     * @param prmText
     */
    public void putPrmText(@Nonnull PrmText prmText) {
        getPrmTextMap().put(prmText.getIndex(), prmText);
    }
    
    /**
     * @param gsdModuleModel
     */
    public void setModule(@Nonnull GsdModuleModel2 gsdModuleModel) {
        GsdModuleModel2 put = _gsdModuleModelMap.put(gsdModuleModel.getModuleNumber(),
                                                     gsdModuleModel);
        gsdModuleModel.setParent(this);
        if (put != null) {
            throw new IllegalArgumentException();
        }
    }
    
    public boolean hasModule() {
        return _gsdModuleModelMap!=null&&!_gsdModuleModelMap.isEmpty();
    }
    
    public void setExtUserPrmData(@Nonnull ExtUserPrmData extUserPrmData) {
        _gsdExtUserPrmData.put(extUserPrmData.getIndex(), extUserPrmData);
    }
    
    @Override
    @CheckForNull
    public ExtUserPrmData getExtUserPrmData(@Nonnull Integer index) {
        return _gsdExtUserPrmData.get(index);
    }
    
    @CheckForNull
    public GsdModuleModel2 getModule(@Nonnull Integer moduleNumber) {
        return _gsdModuleModelMap.get(moduleNumber);
    }

    @Nonnull
    public Map<Integer, GsdModuleModel2> getModuleMap() {
        return _gsdModuleModelMap;
    }
    
    /**
     * @return
     */
    @Nonnull
    public Integer getIdentNumber() {
        Integer identNumber = getIntValue("Ident_Number");
        if(identNumber==null) {
            identNumber=-1;
        }
        return identNumber;
    }

    /**
     * @return
     */
    @Nonnull
    public Integer getMaxModule() {
        Integer maxModule = getIntValue("Max_Module");
        if(maxModule==null) {
            maxModule=0;
        }
        return maxModule;
    }

    public boolean isSalve() {
        Integer intProperty = getIntProperty("Station_Type");
        // Station_Type == 0 => Slave
        return intProperty==null?false:intProperty==0;
    }

    public boolean isMaster() {
        Integer intProperty = getIntProperty("Station_Type");
        // Station_Type == 1 => Master
        return intProperty==null?false:intProperty==1;
    }

    /**
     * 
     */
    public boolean isModularStation() {
        Integer intProperty = getIntProperty("Modular_Station");
        return intProperty==null?false:intProperty!=0;
    }

    @Nonnull
    public GSDFileDBO getGsdFileDBO() {
        return _gsdFileDBO;
    }
}
