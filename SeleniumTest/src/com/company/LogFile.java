package com.company;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogFile {
    public static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void setup(String logPath){
        try{
            FileHandler fh = new FileHandler(logPath, true);
            LOGGER.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
        }catch(SecurityException | IOException e){
            e.printStackTrace();
        }
    }

}
