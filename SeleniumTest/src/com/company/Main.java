package com.company;

import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {
        //Variable declarations
        //TODO: make this come from a config file
        LogFile.setup("C:\\Users\\zachz\\Documents\\logger.txt");
        LogFile.LOGGER.info("Starting Program");
        String AMC = "https://www.austintexas.gov/AmcPublicInquiry/search/vclsearch.aspx";
        String dbPath = "jdbc:mysql://database-1.cjei7qgfamzt.us-east-2.rds.amazonaws.com:3306/citationsDB";
        String dbUser = "admin";
        String dbPassword = "Uwotm8asdf";
        int threads = 2;

        //Create database connection then find the last case number entered
        ParkingDataBase db = new ParkingDataBase(dbPath, dbUser, dbPassword);
        int lastCase = db.lastCaseNo();

        //Start the threads to find details on each new case
        List<Thread> threadList = new ArrayList<>();
        for(int i=0; i<threads; i++){
            Thread object = new Thread(new SearchThroughThread(lastCase+i, threads, db));
            threadList.add(object);
            object.start();
        }

        //Wait for threads to finish, then close db connection
        for(Thread t : threadList)
            t.join();
        db.closeConnection();
    }
}


