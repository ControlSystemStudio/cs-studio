package org.csstudio.service.jdbcsample;

import java.util.concurrent.Executors;

import org.epics.pvmanager.jdbc.JDBCService;
import org.epics.pvmanager.jdbc.JDBCServiceDescription;
import org.epics.pvmanager.jdbc.JDBCServiceMethodDescription;
import org.epics.pvmanager.jdbc.SimpleDataSource;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;

public class JDBCSampleService extends JDBCService {

    public JDBCSampleService() {
        super(new JDBCServiceDescription("jdbcSample", "A test service")
                .dataSource(new SimpleDataSource("jdbc:mysql://localhost/test?user=root&password=root"))
                .executorService(Executors.newSingleThreadExecutor(org.epics.pvmanager.util.Executors.namedPool("jdbcSample")))
                .addServiceMethod(new JDBCServiceMethodDescription("query", "A test query")
                    .query("SELECT * FROM Data")
                    .queryResult("result", "The query result")
                )
                .addServiceMethod(new JDBCServiceMethodDescription("insert", "A test insertquery")
                    .query("INSERT INTO `test`.`Data` (`Name`, `Index`, `Value`) VALUES (?, ?, ?)")
                    .addArgument("name", "The name", VString.class)
                    .addArgument("index", "The index", VNumber.class)
                    .addArgument("value", "The value", VNumber.class)
                ));
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
