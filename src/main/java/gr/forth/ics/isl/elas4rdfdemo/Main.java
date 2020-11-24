package gr.forth.ics.isl.elas4rdfdemo;

import java.io.*;
import java.util.*;

public class Main {

    public static Properties props;

    //this is called after the application has started
    public static void initializeTools() {

        props = new Properties();
        try {
            props.load(new FileInputStream("./application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create log file if it does not exist
        File file = new File("request_log.tsv");
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}