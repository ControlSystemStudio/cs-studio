package org.csstudio.config.ioconfig.model.pbmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.Ioc;
import org.csstudio.config.ioconfig.model.Repository;
import org.junit.After;
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
public class ChannelStructure_Test {
    private ProfibusSubnet _profibusSubnet;
    private Master _master;
    private Slave _slave;


    @Before
    public void setUp() {
        Repository.injectIRepository(new DummyRepository());
        _profibusSubnet = new ProfibusSubnet(new Ioc());
        _master = new Master(_profibusSubnet);
        _slave = new Slave(_master);
    }
    
    @After
    public void setDown() {
        Repository.injectIRepository(null);
    }

    @Test
    public void testModule() {
        ChannelStructure out = new ChannelStructure();
        assertNull(out.getModule());
        assertNull(out.getParent());
        
        Module module = new Module(_slave);
        module.addChild(out);
        
        assertNotNull(out.getModule());
        assertNotNull(out.getParent());
        
        assertEquals(out.getModule(), module);
        assertEquals(out.getModule(), out.getParent());
        
    }

    @Ignore("not change the code yet")
    @Test
    public void testChannelStructs() {
        Module module = new Module(_slave);
        ChannelStructure out = new ChannelStructure();
        module.addChild(out);
        assertTrue(out.getChannels().size()==0);
        assertTrue(out.getChildren().size()==0);

        out.getChannels();
        
        Channel structChannel1 = new Channel();
        structChannel1.setId(111);
        structChannel1.moveSortIndex((short) 11);
        structChannel1.setName("structChannel 1");
        Channel structChannel2 = new Channel();
        structChannel2.setId(121);
        structChannel2.setName("structChannel 2");
        structChannel2.moveSortIndex((short) 12);
        out.addChild(structChannel1);
        out.addChild(structChannel2);
        
        assertTrue(out.getChannels().size()==2);
        assertTrue(out.getChildren().size()==2);
        
        assertTrue(out.getChannels().containsAll(out.getChildren()));
        assertTrue(out.getChildren().containsAll(out.getChannels()));
        
        
        structChannel1 = new Channel();
        structChannel1.setId(211);
        structChannel1.moveSortIndex((short) 21);
        structChannel1.setName("structChannel 21");
        structChannel2 = new Channel();
        structChannel2.setId(221);
        structChannel2.setName("structChannel 22");
        structChannel2.moveSortIndex((short) 22);

        out.addChild(structChannel1);
        out.addChild(structChannel2);
        
        
        assertTrue(out.getChannels().size()==2);
        assertTrue(out.getChildren().size()==2);
        
        assertTrue(out.getChannels().containsAll(out.getChildren()));
        assertTrue(out.getChildren().containsAll(out.getChannels()));
    }

}
