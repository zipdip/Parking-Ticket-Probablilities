package com.company;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchThroughThread implements Runnable{
    private final int startCase;
    private final int increment;
    private final ParkingDataBase db;

    //Inputs: starting case to use, amount to increment each loop, ParkingDatabase connection
    public SearchThroughThread(int startCase, int increment, ParkingDataBase db){
        this.startCase = startCase;
        this.increment = increment;
        this.db = db;
    }

    //Input: Time in format "hh:mm AM/PM"
    //Output: Time in 24 hr format
    public String formatTime(String time){
        if(time.substring(time.length()-2, time.length()).equals("AM")) {
            if (!time.split(":")[0].equals("12"))
                return time.substring(0, time.length() - 3);
            return "00" + time.substring(2, time.length()-3);
        }
        if (!time.split(":")[0].equals("12")) {
            String hh = time.split(":")[0];
            String newHH = Integer.toString(Integer.parseInt(hh) + 12);
            return newHH + time.substring(2, time.length()-3);
        }
        return (time.substring(0, time.length()-3));
    }

    @Override
    public void run() {
        //Create Variables
        WebBrowser browser = new WebBrowser("https://www.austintexas.gov/AmcPublicInquiry/search/vclsearch.aspx");
        String prefix = "W";
        SimpleDateFormat dts = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat std = new SimpleDateFormat("MM/dd/yyy");

        //Start looping through website form
        int zeroCount = 0;
        int currCase = startCase;
        while (zeroCount < 3) {
            String caseNo = prefix + String.format("%06d", currCase+increment);
            List<String> results = new ArrayList<>();
            try {
                results = browser.searchByCase(caseNo);
            }catch (Exception e){
                e.printStackTrace();
                LogFile.LOGGER.warning("Could Not find caseNo info from website for " + caseNo + ": Attempting " + (3 - zeroCount) + " more times.");
                browser.resetDriver();
                zeroCount ++;
                continue;
            }
            if (results.size() != 4){
                currCase += increment;
                zeroCount++;
                continue;
            }
            results.add(0, caseNo);

            //Remove "PARKING - " from reason and double the apostrophes to make it SQL safe
            String reason = results.get(4);
            reason = reason.replace("PARKING - ", "");
            reason = reason.replace("'", "''");
            results.set(4, reason);

            //Format Date and Time correctly
            Date date = null;
            try {
                date = std.parse(results.get(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            results.set(1, dts.format(date));
            results.set(2, formatTime(results.get(2)));

            //Insert into database
            LogFile.LOGGER.info("Attempting Input for " + results.toString());
            try {
                db.insertIntoParkingTickets(results);
            } catch (Exception e) {
                e.printStackTrace();
                LogFile.LOGGER.warning("Could Not input Data for " + caseNo);
            }

            currCase += increment;
        }
        browser.closeBrowser();
    }
}
