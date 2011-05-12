package org.csstudio.config.ioconfig.model.pbmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SlaveTest {
    private ProfibusSubnetDBO _profibusSubnet;
    private MasterDBO _master;

    
    @Test
    public  void createNewSlaves() throws PersistenceException {
        // add first Slave
        assertEquals(0, _master.getChildren().size());
        SlaveDBO out1 = new SlaveDBO(_master);
        out1.localSave();
        
        // Right size?
        assertEquals(1, _master.getChildren().size());
        // Right Slave in?
        assertTrue(_master.getChildren().contains(out1));
        assertTrue(_master.getChildrenAsMap().containsValue(out1));
        
        // add second Slave
        SlaveDBO out2 = new SlaveDBO(_master);
        out2.localSave();
        // Right size?
        assertEquals(2, _master.getChildren().size());
        // Right Slave in?
        assertTrue(_master.getChildren().contains(out2));
        assertTrue(_master.getChildrenAsMap().containsValue(out2));
        
    }
    
    @Before
    public void setUp() throws PersistenceException {
        Repository.injectIRepository(new DummyRepository());
        _profibusSubnet = new ProfibusSubnetDBO(new IocDBO());
        _profibusSubnet.setName("Subnet");
        _profibusSubnet.localSave();
        _master = new MasterDBO(_profibusSubnet);
        _master.setName("Master");
        _master.localSave();
    }
    
    @After
    public void setDown() {
        _master = null;
        _profibusSubnet = null;
        Repository.injectIRepository(null);
    }
    
}
