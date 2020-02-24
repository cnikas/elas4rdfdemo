package gr.forth.ics.isl.elas4rdfdemo;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.*;
import net.sf.extjwnl.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Wordnet {

    public Dictionary dictionary;

    public Wordnet(){
        try {
            this.dictionary = Dictionary.getDefaultResourceInstance();
        } catch (JWNLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){

    }

    //Get the wordnet POS based on coreNLP POS
    public static POS getWordNetPos(String pos) {
        if (pos.startsWith("J")) {
            return POS.ADJECTIVE;
        } else if (pos.startsWith("R")) {
            return POS.ADVERB;
        } else if (pos.startsWith("N")) {
            return POS.NOUN;
        } else if (pos.startsWith("V")) {
            return POS.VERB;
        }
        return null;
    }

    public HashSet<String> getDerivations(String word){

        HashSet<String> mms = new HashSet<>();
        try {
            IndexWordSet idxWordSet = this.dictionary.lookupAllIndexWords(word);
            for(IndexWord iw : idxWordSet.getIndexWordCollection()){
                POS pos = iw.getPOS();
                String lemma = iw.getLemma();
                List<Pointer> pointers = iw.getSenses().get(0).getPointers(PointerType.DERIVATION);
                for(Pointer p : pointers) {
                    for(Word w : p.getTargetSynset().getWords()){
                        if(w.getPOS().equals(POS.NOUN)){
                            System.out.println(w.getLemma()+"   ---   "+p.getType());
                            mms.add(w.getLemma());
                        }
                    }
                }
            }

        } catch (JWNLException e) {
            e.printStackTrace();
        }
        System.out.println(mms);
        return mms;
    }

    /**
     * Returns the derived adjective with the same word form for the most common
     * sense of the given noun if exists.
     *
     * @param verb the verb
     */
    public ArrayList<String> getDerivedNouns(String verb) {
        try {
            IndexWord verbIW = this.dictionary.lookupIndexWord(POS.VERB, verb);

            List<Synset> senses = verbIW.getSenses();

            Synset mainSense = senses.get(0);

            List<Pointer> pointers = mainSense.getPointers(PointerType.DERIVATION);

            ArrayList<String> wordExt = new ArrayList<>();

            for (Pointer pointer : pointers) {
                Synset derivedSynset = pointer.getTargetSynset();

                if (derivedSynset.getPOS() == POS.NOUN) {
                    List<Word> words = derivedSynset.getWords();
                    /*or (int i = 0; i < words.size(); i++) {
                        String tmpWord = words.get(i).getLemma(); // get only i=0 for less results (most common sense)
                        if (!wordExt.contains(tmpWord)) {
                            wordExt.add(tmpWord);
                        }
                    }*/
                    String tmpWord = words.get(0).getLemma(); // get only i=0 for less results (most common sense)
                    if (!wordExt.contains(tmpWord)) {
                        wordExt.add(tmpWord);
                    }
                }
            }

            return wordExt;
        } catch (JWNLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
