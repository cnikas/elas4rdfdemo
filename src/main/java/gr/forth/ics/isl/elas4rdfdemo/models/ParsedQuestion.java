package gr.forth.ics.isl.elas4rdfdemo.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ParsedQuestion {
    private String question;
    private boolean isList;
    private String qType;
    private String aType;
    private ParsedSyntax syntax;
    private List<Keyword> keyWords;
    private HashMap<String, String> namedEntities;

    public List<Keyword> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(List<Keyword> keyWords) {
        this.keyWords = keyWords;
    }

    public ParsedQuestion() {
        this.question = "";
        this.isList = false;
        this.qType = "";
        this.aType = "";
        this.syntax = new ParsedSyntax();
        this.namedEntities = new HashMap<>();
    }

    public ParsedSyntax getSyntax() {
        return syntax;
    }

    public void setSyntax(ParsedSyntax syntax) {
        this.syntax = syntax;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getaType() {
        return aType;
    }

    public void setaType(String aType) {
        this.aType = aType;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }

    public String getqType() {
        return qType;
    }

    public void setqType(String qType) {
        this.qType = qType;
    }

    public HashMap<String, String> getNamedEntities() {
        return namedEntities;
    }

    public void setNamedEntities(HashMap<String, String> namedEntities) {
        this.namedEntities = namedEntities;
    }

    public HashSet<String> joinAllTerms(){
        HashSet<String> joined = new HashSet<>();
        for(Keyword kw : this.getKeyWords()){
            joined.addAll(kw.setOfAllWords());
        }
        return joined;
    }
    public HashSet<String> joinAllTermsExtended(){
        HashSet<String> joined = new HashSet<>();
        for(Keyword kw : this.getKeyWords()){
            joined.addAll(kw.setOfAllWordsExtended());
        }
        return joined;
    }
}
