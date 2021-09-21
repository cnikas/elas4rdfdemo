package gr.forth.ics.isl.elas4rdfdemo.utilities;

import java.io.*;
import java.util.*;

public class Utils {

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

    public static String removeStopwords(String original){
        String stopwords[] = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};
        String clean = original.trim().replaceAll("\\s+", " ").replaceAll("[^a-zA-Z0-9 ]","");
        ArrayList<String> words = new ArrayList<String>(Arrays.asList(clean.split(" ")));

        for (int i = 0; i < stopwords.length; i++) {
            if (words.contains(stopwords[i])) {
                words.remove(stopwords[i]);
            }
        }
        return String.join(" ",words);
    }

    public static void main(String[] args){
        System.out.println(removeStopwords("who is the father of marco polo"));
    }

}