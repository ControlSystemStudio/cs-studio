package org.csstudio.config.kryonamebrowser.model.entry;

/**
 * 
 * TODO (jhatje) : 
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 13.04.2010
 */
@Deprecated 
public class KryoProcessEntry {

    private String name;
    private String id;
    private String explanation;

    
    public KryoProcessEntry(String name, String id, String explanation) {
        this.name = name;
        this.id = id;
        this.explanation = explanation;
    }

    public KryoProcessEntry(KryoProcessEntry kryoProcessEntry) {
    	  this.name = kryoProcessEntry.name;
          this.id = kryoProcessEntry.id;
          this.explanation = kryoProcessEntry.explanation;
	}

	public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getExplanation() {
        return explanation;
    }
}
