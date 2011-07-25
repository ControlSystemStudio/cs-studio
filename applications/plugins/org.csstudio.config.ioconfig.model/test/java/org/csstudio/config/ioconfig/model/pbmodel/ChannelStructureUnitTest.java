package org.csstudio.config.ioconfig.model.pbmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 17.04.2009
 */
public class ChannelStructureUnitTest {
    private ProfibusSubnetDBO _profibusSubnet;
    private MasterDBO _master;
    private SlaveDBO _slave;


    @Before
    public void setUp() throws PersistenceException {
        Repository.injectIRepository(new DummyRepository());
        _profibusSubnet = new ProfibusSubnetDBO(new IocDBO());
        _master = new MasterDBO(_profibusSubnet);
        _slave = new SlaveDBO(_master);
    }
    
    @Test
    public void testModule() throws PersistenceException {
        ChannelStructureDBO out = new ChannelStructureDBO();
        assertNull(out.getModule());
        assertNull(out.getParent());
        
        ModuleDBO module = new ModuleDBO(_slave);
        module.addChild(out);
        
        assertNotNull(out.getModule());
        assertNotNull(out.getParent());
        
        assertEquals(out.getModule(), module);
        assertEquals(out.getModule(), out.getParent());
        
    }

    @Ignore("not change the code yet")
    @Test
    public void testChannelStructs() throws PersistenceException {
        ModuleDBO module = new ModuleDBO(_slave);
        ChannelStructureDBO out = new ChannelStructureDBO();
        module.addChild(out);
        assertTrue(out.getChildren().size()==0);

        ChannelDBO structChannel1 = new ChannelDBO();
        structChannel1.setId(111);
        structChannel1.moveSortIndex((short) 11);
        structChannel1.setName("structChannel 1");
        ChannelDBO structChannel2 = new ChannelDBO();
        structChannel2.setId(121);
        structChannel2.setName("structChannel 2");
        structChannel2.moveSortIndex((short) 12);
        out.addChild(structChannel1);
        out.addChild(structChannel2);
        
        assertTrue(out.getChildren().size()==2);
        
        structChannel1 = new ChannelDBO();
        structChannel1.setId(211);
        structChannel1.moveSortIndex((short) 21);
        structChannel1.setName("structChannel 21");
        structChannel2 = new ChannelDBO();
        structChannel2.setId(221);
        structChannel2.setName("structChannel 22");
        structChannel2.moveSortIndex((short) 22);

        out.addChild(structChannel1);
        out.addChild(structChannel2);
        
        
        assertTrue(out.getChildren().size()==2);
    }

}
