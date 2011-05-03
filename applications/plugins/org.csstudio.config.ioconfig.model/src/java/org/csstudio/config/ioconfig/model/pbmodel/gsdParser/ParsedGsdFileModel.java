/**
 * 
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * @author hrickens
 *
 */
public class ParsedGsdFileModel extends AbstractGsdPropertyModel {
    
    private final String _name;
    private final Map<Integer, PrmText> _prmTextMap;
    private final Map<Integer, GsdModuleModel2> _gsdModuleModelMap;
    private final Map<Integer, ExtUserPrmData> _gsdExtUserPrmData;
    /**
     * Constructor.
     * @param name
     */
    public ParsedGsdFileModel(@Nonnull String name) {
        _name = name;
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

    /**
     * @return
     */
    public boolean isSalve() {
        //TODO: is Slave auswerten!
        return false;
    }
}
