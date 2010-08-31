package org.csstudio.config.ioconfig.model.pbmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Iterator;
import java.util.Set;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.Ioc;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CopyNodeTest {
    
    private static final boolean OUTPUT = false;
    private static final boolean INPUT = true;
    private ProfibusSubnet _profibusSubnet;
    private Master _master;
    private Slave _slave;


    @Test
    public void testCopyModule() throws Exception {
        assertEquals("--- Precondition wrong!","@Subnet:1234", _slave.getEpicsAdressString());
        
        // Copy a Module to the same Slave
        Module module = new Module(_slave);
        module.setName("Module");
        ChannelStructure sco = ChannelStructure.makeChannelStructure(module,OUTPUT,DataType.INT8,"SCO ");
        Channel pci = new Channel(sco,INPUT,false);
        pci.setChannelType(DataType.INT16);
        pci.setName("PCI");
        module.localSave();
        
        assertEquals(1, _slave.getChildren().size());
        
        NamedDBClass node = module.copyThisTo(_slave);
        // - Test Slave
        assertEquals(2, _slave.getChildren().size());
        
        // - Test Module 
        assertTrue(node instanceof Module);
        Module copyModule = (Module) node;
        assertEquals(_slave, copyModule.getParent());
        assertEquals("Copy of Module", copyModule.getName());
        assertEquals(0, copyModule.getId());
        
        // TODO: Test Simple and Struct Channle
        // -- Test Children
        assertEquals(module.getChildren().size(), copyModule.getChildren().size());
        assertEquals(module.getPureChannels().size(), copyModule.getPureChannels().size());
        assertEquals(module.getChannelStructs().size(), copyModule.getChannelStructs().size());

        // -- Test PCO Children
        Set<Channel> pureChannels = module.getPureChannels();
        Set<Channel> copyPureChannels = copyModule.getPureChannels();
        assertEquals(pureChannels, copyPureChannels);
        assertEquals(pureChannels.isEmpty(), copyPureChannels.isEmpty());
        assertEquals(pureChannels.size(), copyPureChannels.size());
        Iterator<Channel> iterator = pureChannels.iterator();
        Iterator<Channel> copyIterator = copyPureChannels.iterator();
        while(iterator.hasNext()&&copyIterator.hasNext()) {
            Channel channel = iterator.next();
            Channel copy = copyIterator.next();
            assertEquals(channel, copy);
            assertEquals(channel.isDigital(), copy.isDigital());
            assertEquals(channel.isInput(), copy.isInput());
            assertEquals(channel.getChannelNumber(), copy.getChannelNumber());
            assertEquals(channel.getChannelStructure(), copy.getChannelStructure());
            assertEquals(channel.getChannelType(), copy.getChannelType());
            assertEquals(channel.getChSize(), copy.getChSize());
            assertEquals(channel, copy);
            assertEquals(channel, copy);
        }
        // -- Test PCO Children
        
        Set<ChannelStructure> channelStructs = module.getChannelStructs();
        Set<ChannelStructure> copyChannelStructs = copyModule.getChannelStructs();
        assertEquals(channelStructs.isEmpty(), copyChannelStructs.isEmpty());
        assertEquals(channelStructs.size(), copyChannelStructs.size());
        
        while(iterator.hasNext()&&copyIterator.hasNext()) {
            Channel channel = iterator.next();
            Channel copy = copyIterator.next();
            assertEquals(channel, copy);
            assertEquals(channel.isDigital(), copy.isDigital());
            assertEquals(channel.isInput(), copy.isInput());
            assertEquals(channel.getChannelNumber(), copy.getChannelNumber());
            assertEquals(channel.getChannelStructure(), copy.getChannelStructure());
            assertEquals(channel.getChannelType(), copy.getChannelType());
            assertEquals(channel.getChSize(), copy.getChSize());
            assertEquals(channel.getCurrenUserParamDataIndex(), copy.getCurrenUserParamDataIndex());
            assertEquals(channel.getDescription(), copy.getDescription());
            assertEquals(channel.getGSDFile(), copy.getGSDFile());
            assertEquals(channel.getIoName(), copy.getIoName());
            assertEquals(channel.getModule(), copy.getModule());
            assertEquals(channel.getName(), copy.getName());
            assertEquals(channel.getParent(), copy.getParent());
            assertEquals(channel.getStruct(), copy.getStruct());
            assertEquals(channel.getVersion(), copy.getVersion());
        }
        
        // Paste the Module to are other Slave
    }
    
    @Before
    public void setUp() throws PersistenceException {
        Repository.injectIRepository(new DummyRepository());
        _profibusSubnet = new ProfibusSubnet(new Ioc());
        _profibusSubnet.setName("Subnet");
        _profibusSubnet.localSave();
        _master = new Master(_profibusSubnet);
        _master.setName("Master");
        _master.localSave();
        _slave = new Slave(_master);
        _slave.setName("Slave");
        _slave.moveSortIndex((short)1234);
        _slave.localSave();
    }
    
    @After
    public void setDown() {
        _slave = null;
        _master = null;
        _profibusSubnet = null;
        Repository.injectIRepository(null);
    }
    
}
