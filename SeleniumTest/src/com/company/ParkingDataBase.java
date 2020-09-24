package com.company;

import java.sql.*;
import java.util.List;

public class ParkingDataBase {
    private final Connection con;

    //Inputs: connection string, username, password
    public ParkingDataBase(String dbPath, String user, String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(dbPath, user, password);
    }

    //insertIntoParkingTickets
    //Inputs: List<String> of size 5 with values in order that are pt_caseno, pt_date, pt_time, pt_address, pt_reason
    //Output: Number of rows affected (Should be 1 if successful, 0 if unsuccessful)
    public int insertIntoParkingTickets(List<String> values){
        if (values.size() != 5)
            return 0;
        int result = 0;
        String query = "INSERT INTO parking_tickets (pt_caseno, pt_date, pt_time, pt_address, pt_reason) VALUES ( '" +
                values.get(0) + "', '" + values.get(1) + "', '" + values.get(2) + "', '" + values.get(3) + "', '" +
                values.get(4) + "');";
        try{
            Statement statement = con.createStatement();
            result = statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    //Closes SQL connection
    public void closeConnection() throws SQLException {
        if(con != null)
            con.close();
    }

    //Returns the last case number inserted into parking database, -1 if exception occurs
    public int lastCaseNo(){
        int result = -1;
        try{
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT MAX(pt_caseno) FROM parking_tickets");
            rs.next();
            String caseNo = rs.getString(1);
            result = Integer.parseInt(caseNo.substring(1, caseNo.length()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
