package org.csstudio.service.jdbcsample;

import java.util.concurrent.Executors;

import org.diirt.service.jdbc.JDBCService;
import org.diirt.service.jdbc.JDBCServiceDescription;
import org.diirt.service.jdbc.JDBCServiceMethodDescription;
import org.diirt.service.jdbc.SimpleDataSource;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VString;

public class JDBCSampleService extends JDBCService {

    public JDBCSampleService() {
        super(new JDBCServiceDescription("jdbcSample", "A test service")
                .dataSource(new SimpleDataSource("jdbc:mysql://localhost/test?user=root&password=root"))
                .executorService(Executors.newSingleThreadExecutor(org.diirt.datasource.util.Executors.namedPool("jdbcSample")))
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
