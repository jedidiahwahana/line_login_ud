package com.linecorp.example.testing.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONArray;

public class PostgresHelper {
    
    private Connection conn;
    private String url;
    private String dbName;
    private String user;
    private String pass;
    
    //we don't like this constructor
    protected PostgresHelper() {}
    
    public PostgresHelper(String url) {
        this.url = url;
//        this.dbName = dbName;
//        this.user = user;
//        this.pass = pass;
    }
    
    public boolean connect() throws SQLException, ClassNotFoundException {
        if (url.isEmpty()) {
            throw new SQLException("Database credentials missing");
        }
        
        Class.forName("org.postgresql.Driver");
        this.conn = DriverManager.getConnection(this.url);
        return true;
    }
    
    public ResultSet execQuery(String query) throws SQLException {
        return this.conn.createStatement().executeQuery(query);
    }
    
    public int insert(String table, Map<String,Object> values) throws SQLException {
        
        StringBuilder columns = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        
        for (String col : values.keySet()) {
            columns.append(col).append(",");
            
            if (values.get(col) instanceof String) {
                vals.append("'").append(values.get(col)).append("', ");
            }
            else vals.append(values.get(col)).append(",");
        }
        
        columns.setLength(columns.length()-1);
        vals.setLength(vals.length()-2);
        
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", table,
                                     columns.toString(), vals.toString());
        System.out.println("SQL: " + query);
        
        return this.conn.createStatement().executeUpdate(query);
    }
    
    public boolean checkTable(String table, String insertedMID) throws SQLException {
        
        String query = String.format("SELECT EXISTS(SELECT * FROM %s WHERE MID = '%s')", table, insertedMID);
        System.out.println("SQL: " + query);
        ResultSet rs = this.conn.createStatement().executeQuery(query);
        rs.next();
        boolean existsData = rs.getBoolean(1);
        
        return existsData;
    }
    
    public JSONArray getUserFiles(String table, String insertedMID) throws SQLException{
        
        String query = String.format("SELECT array_to_json(array_agg(files)) FROM %s WHERE MID = '%s'", table, insertedMID);
        System.out.println("SQL: " + query);
        ResultSet rs = this.conn.createStatement().executeQuery(query);
        rs.next();
        String result = rs.getString(1);
        JSONArray existsData = new JSONArray(result);
        System.out.println("Result: " + rs.getString(1));
        
//        JSONArray existsData = rs.getJSONArray(1);
        
        return existsData;
    }
}
