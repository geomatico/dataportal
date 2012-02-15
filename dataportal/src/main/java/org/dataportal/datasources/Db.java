package org.dataportal.datasources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dataportal.Config;

public class Db {
    
    public Connection getConnection() throws SQLException {
        String driverClassName = Config.get("jdbc.driver");
        String url = Config.get("jdbc.connection");
        String user = Config.get("jdbc.user");
        String password = Config.get("jdbc.password");
        
        Connection conn = null;
        
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, user, password);
        } catch(SQLException e) {
            if(conn != null) conn.close();
            e.printStackTrace();
            throw(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            SQLException ne = new SQLException(e);
            throw(ne);
        }
        return conn;
    }
}
