package gr.forth.ics.isl.elas4rdfdemo.models;

import edu.stanford.nlp.pipeline.CoreDocument;
import gr.forth.ics.isl.elas4rdfdemo.Main;
import gr.forth.ics.isl.elas4rdfdemo.Wordnet;

import java.util.ArrayList;
import java.util.HashSet;

public class Keyword {
    private String word;
    private String lemma;
    private HashSet<String> multiWordPhrases;
    private HashSet<String> derivations;
    private String wordTermForQuery;

    public String getWordTermForQuery() {
        return wordTermForQuery;
    }

    public void setWordTermForQuery(String wordTermForQuery) {
        this.wordTermForQuery = wordTermForQuery;
    }

    public Keyword() {
        this.word = "";
        this.lemma = "";
        this.multiWordPhrases = new HashSet<>();
        this.derivations = new HashSet<>();

    }

    public Keyword(String word) {
        this.word = word;
        this.lemma = "";
        this.multiWordPhrases = new HashSet<>();
        this.derivations = new HashSet<>();
    }

    public void addToMultiWordPhrases(ArrayList<String> phrases) {
        this.multiWordPhrases.addAll(phrases);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public HashSet<String> getMultiWordPhrases() {
        return multiWordPhrases;
    }

    public void setMultiWordPhrases(HashSet<String> multiWordPhrases) {
        this.multiWordPhrases = multiWordPhrases;
    }

    public HashSet<String> getDerivations() {
        return derivations;
    }

    public void setDerivations(HashSet<String> derivations) {
        this.derivations = derivations;
    }

    public HashSet<String> setOfAllWords(){
        HashSet<String> joined = new HashSet<>();
            joined.add(this.getWord().toLowerCase());
            joined.add(this.getLemma().toLowerCase());
            for(String mwe : this.getMultiWordPhrases()){
                joined.add(mwe.toLowerCase());
            }
        return joined;
    }

    public HashSet<String> setOfAllWordsExtended(){
        HashSet<String> joined = this.setOfAllWords();
        for(String der : this.getDerivations()){
            joined.add(der.toLowerCase());
        }
        return joined;
    }

    public String generateQueryTerm(){
        ArrayList<String> parsedMwes = new ArrayList<>();
        for(String word : this.setOfAllWords()){
            if(word.split(" ").length > 1){
                parsedMwes.add("("+word+")");
            } else {
                parsedMwes.add(word);
            }
        }
        return "("+String.join(" OR ",parsedMwes)+")";
    }

    @Override
    public String toString() {
        return "Keyword{" +
                "word='" + word + '\'' +
                ", lemma='" + lemma + '\'' +
                ", multiWordPhrases=" + multiWordPhrases +
                ", derivations=" + derivations +
                '}';
    }
}
