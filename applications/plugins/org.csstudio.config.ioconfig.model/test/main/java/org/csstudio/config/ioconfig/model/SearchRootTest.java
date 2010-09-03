package org.csstudio.config.ioconfig.model;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;


public class SearchRootTest {
    
    @Test
    public void testGetRootPath() throws Exception {
        try {
            @SuppressWarnings("unused")
            List<Integer> rootPath = Repository.getRootPath(9327);
            
        }catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        assertTrue(true);
    }
    
}
