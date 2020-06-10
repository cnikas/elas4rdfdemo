package gr.forth.ics.isl.elas4rdfdemo;

import edu.stanford.nlp.pipeline.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import gr.forth.ics.isl.elas4rdfdemo.qa.AnswerExtraction;
import gr.forth.ics.isl.elas4rdfdemo.qa.QuestionAnalysis;

public class Main {

    public static Properties props;
    public static StanfordCoreNLP simple_pipeline;
    public static StanfordCoreNLP syntax_pipeline;

    //for running qa on the command line
    public static void main(String[] args){

        initializeTools();

        QuestionAnalysis qa = new QuestionAnalysis();
        AnswerExtraction ae = new AnswerExtraction();

        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.println("Enter Question:");
            String line = scanner.nextLine();
            if(line.equals("exit")){
                break;
            } else{
                System.out.println(ae.extractAnswerJson(qa.analyzeQuestion(line)).toString());
            }
        }

    }

    //this is called after the application has started
    public static void initializeTools(){

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