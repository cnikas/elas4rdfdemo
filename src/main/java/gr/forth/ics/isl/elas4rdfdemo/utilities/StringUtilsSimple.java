package gr.forth.ics.isl.elas4rdfdemo.utilities;

import edu.stanford.nlp.util.Sets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class StringUtilsSimple {

    //The paths for the stopWords Files.
    public static String filePath_en = "src/main/resources/stoplists/stopwordsEn.txt";
    public static String filePath_gr = "src/main/resources/stoplists/stopwordsGr.txt";
    public static HashMap<String, HashSet<String>> stopLists = new HashMap<>();

    /**
     * Generates a HashMap with languages as keys and stopLists as values.
     *
     */
    public static void generateStopLists() {
        HashSet<String> stopWordsEn = new HashSet<String>();
        HashSet<String> stopWordsGr = new HashSet<String>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath_en))) {
            stream.forEach(word -> stopWordsEn.add(word.toLowerCase()));
        } catch (IOException ex) {
            Logger.getLogger(StringUtilsSimple.class.getName()).log(Level.SEVERE, null, ex);
        }
        stopLists.put("en", stopWordsEn);
        try (Stream<String> stream = Files.lines(Paths.get(filePath_gr))) {
            stream.forEach(word -> stopWordsEn.add(word.toLowerCase()));
        } catch (IOException ex) {
            Logger.getLogger(StringUtilsSimple.class.getName()).log(Level.SEVERE, null, ex);
        }
        stopLists.put("gr", stopWordsGr);
    }

    /**
     * This function takes as input a string and return as value true if the
     * string is stopword, false otherwise
     *
     * @param word
     * @return
     */
    public static boolean isStopWord(String word) {

        if (word.trim().isEmpty()) {
            return true;
        }

        for (Map.Entry<String, HashSet<String>> entry : stopLists.entrySet()) {
            if (entry.getValue().contains(word.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    //Function which calculates the Jaccard Similarity between to Arrays of Strings
    public static double JaccardSim(String[] sentenceTerms, String[] quesTerms) {
        Set<String> sentenceTermsAsSet = new HashSet<>(Arrays.asList(sentenceTerms));
        Set<String> quesTermsAsSet = new HashSet<>(Arrays.asList(quesTerms));
        Set<String> union = Sets.union(sentenceTermsAsSet, quesTermsAsSet);
        Set<String> intersection = Sets.intersection(sentenceTermsAsSet, quesTermsAsSet);
        double jaccardSim = ((double) intersection.size()) / union.size();
        return jaccardSim;
    }
}
