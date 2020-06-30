package gr.forth.ics.isl.elas4rdfdemo;

import java.io.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import gr.forth.ics.isl.elas4rdfdemo.qa.AnswerExtraction;
import gr.forth.ics.isl.elas4rdfdemo.qa.AnswerTypePrediction;

public class Main {

    public static Properties props;
    public static AnswerTypePrediction atp;
    public static AnswerExtraction answerExtraction;
    private static List<String> stopwords;

    //this is called after the application has started
    public static void initializeTools() {

        props = new Properties();
        try {
            props.load(new FileInputStream("./application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        atp = new AnswerTypePrediction();
        answerExtraction = new AnswerExtraction();

        //create log file if it does not exist
        File file = new File("request_log.tsv");
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}