package gr.forth.ics.isl.elas4rdfdemo;

import edu.stanford.nlp.pipeline.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import gr.forth.ics.isl.elas4rdfdemo.models.ParsedQuestion;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    public static Properties props;
    public static StanfordCoreNLP simple_pipeline;
    public static StanfordCoreNLP syntax_pipeline;

    public static void main(String[] args){

        initializeTools();

        QuestionAnalysis qa = new QuestionAnalysis();
        AnswerExtraction ae = new AnswerExtraction();

        /*ae.extractAnswer(qa.analyzeQuestion("Sean Parnell is the governor of which U.S. state?"));
        ae.extractAnswer(qa.analyzeQuestion("Who was the successor of John F. Kennedy?"));
        ae.extractAnswer(qa.analyzeQuestion("Give me all professional skateboarders from Sweden."));
        ae.extractAnswer(qa.analyzeQuestion("Which cities does the Weser flow through?"));
        ae.extractAnswer(qa.analyzeQuestion("Who is the mayor of Berlin?"));
        ae.extractAnswer(qa.analyzeQuestion("To which countries does the Himalayan mountain system extend?"));
        ae.extractAnswer(qa.analyzeQuestion("Give me a list of all trumpet players that were bandleaders."));
        ae.extractAnswer(qa.analyzeQuestion("Who is the youngest player in the Premier League?"));
        ae.extractAnswer(qa.analyzeQuestion("Give me all members of Prodigy."));*/

        Scanner scanner = new Scanner(System.in);

        while(true){
            System.out.println("Enter Question:");
            String line = scanner.nextLine();
            if(line.equals("exit")){
                break;
            } else{
                ae.extractAnswerJson(qa.analyzeQuestion(line));
            }
        }

    }

    public static void initializeTools(){

        props = new Properties();
        try {
            props.load(new FileInputStream("./config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set up pipeline properties
        Properties simple_props = new Properties();
        // set the list of annotators to run
        simple_props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        // build pipeline
        simple_pipeline = new StanfordCoreNLP(simple_props);

        // set up pipeline properties
        Properties syntax_props = new Properties();
        // set the list of annotators to run
        syntax_props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse");
        syntax_props.setProperty("parse.maxlen", "100");
        // build pipeline
        syntax_pipeline = new StanfordCoreNLP(syntax_props);
    }

}