package gr.forth.ics.isl.elas4rdfdemo;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import gr.forth.ics.isl.elas4rdfdemo.models.Keyword;
import gr.forth.ics.isl.elas4rdfdemo.models.ParsedQuestion;
import gr.forth.ics.isl.elas4rdfdemo.models.ParsedSyntax;
import gr.forth.ics.isl.elas4rdfdemo.utilities.StringUtilsSimple;
import static gr.forth.ics.isl.elas4rdfdemo.Main.simple_pipeline;
import static gr.forth.ics.isl.elas4rdfdemo.Main.syntax_pipeline;

import java.util.*;

/*
* This class contains methods to analyze a question and create a ParsedQuestion
 */
public class QuestionAnalysis {

    public QuestionAnalysis() {
        StringUtilsSimple.generateStopLists();
    }

    /**
     * Function responsible to analyze the question and create an object representing the parsed information.
     * @param question
     * @return
     */
    public ParsedQuestion analyzeQuestion(String question){
        System.out.println("Analyzing question: "+question);

        ParsedQuestion q = new ParsedQuestion();

        if(question.endsWith("?") || question.endsWith(".")){
            question = question.substring(0,question.length()-1);
        }

        //plain question string
        q.setQuestion(question);
        CoreDocument simple_document = new CoreDocument(question);
        simple_pipeline.annotate(simple_document);

        //question type
        String qType = identifyQuestionType(simple_document);
        q.setqType(qType);

        //expected answer type
        if(qType.equals("factoid")){
            q.setaType(expectedAnswerType(simple_document));
        } else if(qType.equals("confirmation")){
            q.setaType("boolean");
        } else if(qType.equals("definition")){
            q.setaType("definition");
        } else {
            q.setaType("unknown");
        }

        CoreDocument syntax_document = new CoreDocument(cleanQuestionForDependencyParse(question));
        syntax_pipeline.annotate(syntax_document);

        ParsedSyntax ps = getSyntax(syntax_document);

        q.setSyntax(ps);

        //true for list questions
        q.setList(isListQuestion(simple_document,ps.getSubject()));

        HashSet<String> noStopWords = removeStopwords(question);
        List<Keyword> keyWords = new ArrayList<>();
        for(String nsw : noStopWords){
            keyWords.add(new Keyword(nsw));
        }

        for(Keyword kw : keyWords){
            kw.addToMultiWordPhrases(findMultiWordExpressions(syntax_document,kw.getWord()));
            kw.addToMultiWordPhrases(findNounPhrases(syntax_document,kw.getWord()));
            kw.setLemma(findLemma(syntax_document,kw.getWord()));
            kw.setDerivations(getDerivedNouns(kw.getWord()));
        }

        q.setKeyWords(keyWords);

        System.out.println("Set of keywords: ");
        System.out.println(q.joinAllTerms());

        return q;
    }

    /**
     * Function responsible to identify the question type. This is achieved
     * based on simple heuristics i.e. indicative starting words
     *
     * The question type can be: (1) factoid, (2) confirmation, (3) definition,
     * (4) none (i.e. unrecognized type)
     *
     * @param document
     * @return
     */
    public String identifyQuestionType(CoreDocument document) {

        String question = document.text().toLowerCase();

        ArrayList<String> factoid_words = new ArrayList<>(Arrays.asList("when", "who", "where", "what", "which",
                "in which", "to which", "on which", "how many", "how much", "show me", "give me", "show", "how", "whom", "in what",
                "of what", "name a", "for which"));

        for (String f_word : factoid_words) {
            if (question.startsWith(f_word)) {
                return "factoid";
            }
        }

        ArrayList<String> confirmation_words = new ArrayList<>(Arrays.asList("are", "did", "is ", "was", "does", "were", "do "));

        for (String c_word : confirmation_words) {
            if (question.startsWith(c_word)) {
                return "confirmation";
            }
        }

        return "factoid";//default
    }

    /**
     * Function which performs the following:
     * tokenization, stop-word removal
     *
     * @param document
     * @return
     */
    public Set<String> getCleanTokens(CoreDocument document) {

        List<CoreLabel> tokens = document.tokens();

        Set<String> final_tokens = new HashSet<>();

        String tmp_token = "";
        for (CoreLabel tok : tokens) {
            tmp_token = tok.word().toLowerCase().trim();
            if (!tmp_token.isEmpty() && !StringUtilsSimple.isStopWord(tmp_token)) {
                final_tokens.add(tmp_token);
            }
        }

        return final_tokens;

    }

    /**
     * Function which detects if a question is a list question using heuristic rules
     *
     * @param document
     * @return
     */
    public boolean isListQuestion(CoreDocument document, String subject){

        List<CoreLabel> tokens = document.tokens();
        String qString = document.text();

        ArrayList<String> list_words = new ArrayList<>(Arrays.asList("all", "list"));
        ArrayList<String> starting_list_words = new ArrayList<>(Arrays.asList("what", "which", "list"));

        for (CoreLabel tok : tokens) {
            if (list_words.contains(tok.word().toLowerCase())) {
                return true;
            }

        }

        for (String slw : starting_list_words) {
            if (qString.toLowerCase().startsWith(slw)) {
                if(isPlural(subject)) return true;
            }
        }

        return false;
    }

    /**
     * Function which attempts to detect if a word is plural by comparing the lemma of the word with the original word
     *
     * @param s
     * @return
     */
    public boolean isPlural(String s){

        if(s != null && !s.isEmpty()){
            CoreDocument doc = new CoreDocument(s);
            simple_pipeline.annotate(doc);
            CoreLabel token = doc.tokens().get(0);

            //only works for nouns
            if(doc.sentences().get(0).posTags().get(0).startsWith("N")) {
                String wordLemma = token.lemma().toLowerCase();

                String word = token.word().toLowerCase();

                if (!wordLemma.equals(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Function which attempts to detect expected answer type based on words starting with "wh"
     *
     * @param document
     * @return
     */
    public String expectedAnswerType(CoreDocument document){

        List<CoreLabel> tokens = document.tokens();

        ArrayList<String> tokenStrings = new ArrayList<String>();

        String tmp_token = "";
        for(CoreLabel tok : tokens){
            tmp_token = tok.word().toLowerCase().trim();
            if (!tmp_token.isEmpty()) {
                tokenStrings.add(tmp_token);
            }
        }

        if(tokenStrings.contains("where")){
            return "Place";
        } else if(tokenStrings.contains("who")){
            return "Person";
        } else {
            return "";
        }

    }

    /**
     * Function which finds named entities in the question
     *
     * @param document
     * @return
     */
    public HashMap<String,String> getNamedEntities(CoreDocument document){

        HashMap<String,String> detected_entities = new HashMap<String,String>();

        for (CoreEntityMention em : document.entityMentions())
            detected_entities.put(em.text(),em.entityType());

        return detected_entities;
    }

    /**
     * Function which attempts to find subject, predicate and object of the question using dependencies parsed by Stanford CoreNLP.
     *
     * @param document
     * @return
     */
    public ParsedSyntax getSyntax(CoreDocument document){

        SemanticGraph sg = document.sentences().get(0).dependencyParse();

        ParsedSyntax ps = new ParsedSyntax();
        IndexedWord root = sg.getFirstRoot();
        ps.setPredicate(root.word());
        List<SemanticGraphEdge> edges = sg.getOutEdgesSorted(root);

        for (SemanticGraphEdge sge : edges){
            if(sge.getRelation().toString().contains("subj")){
                ps.setSubject(sge.getDependent().word());
            } else if(sge.getRelation().toString().contains("obj")){
                ps.setObject(sge.getDependent().word());
            }
        }

        return ps;
    }

    public ArrayList<String> findMultiWordExpressions(CoreDocument document, String targetWord) {

        ArrayList<String> expressions = new ArrayList<>();

        SemanticGraph sg = document.sentences().get(0).dependencyParse();

        List<IndexedWord> nodes = sg.getAllNodesByWordPattern(targetWord);

        for(IndexedWord node : nodes){
            expressions.add(findMultiWordExpression(node,sg));
        }

        return expressions;
    }

    /**
     * Function which creates multi word expressions from words using "fixed", "flat", and "compound" dependencies.
     *
     * @param id
     * @param sg
     * @return
     */
    public String findMultiWordExpression(IndexedWord id, SemanticGraph sg){
        List<SemanticGraphEdge> edges = sg.getOutEdgesSorted(id);

        String mwe = id.word();
        ArrayList<String> mwe_dependencies = new ArrayList<>(Arrays.asList("fixed", "flat", "compound"));
        for (SemanticGraphEdge sge : edges){
            if(mwe_dependencies.contains(sge.getRelation().toString())) {
                mwe = sge.getDependent().word().toLowerCase() + " " + mwe;
            }
        }

        return mwe.trim();
    }

    public HashSet<String> getDerivedNouns(String word){
        HashSet<String> dn = new HashSet<>();
        if(word.split(" ").length <= 1){
            CoreDocument doc = new CoreDocument(word);
            Main.simple_pipeline.annotate(doc);
            if(doc.sentences().get(0).posTags().get(0).startsWith("V")){
                Wordnet wn = new Wordnet();
                dn.addAll(wn.getDerivedNouns(word));
            }
        }
        return dn;
    }

    public String cleanQuestionForDependencyParse(String question){
        String clean_question = "";

        ArrayList<String> wordsToRemove = new ArrayList<String>(Arrays.asList("give","me","all","list","who","what","which","where","when"));

        for(String token : question.split(" ")){
            if(!wordsToRemove.contains(token.toLowerCase())){
                clean_question = clean_question + " " + token;
            }
        }
        // Remove any quotes
        clean_question = clean_question.replaceAll("\"", "");
        // Trim the final question text
        clean_question = clean_question.trim();
        return clean_question;
    }

    public HashSet<String> removeStopwords(String question){
        question.replaceAll("\"", "").trim();
        ArrayList<String> wordsToRemove = new ArrayList<String>(Arrays.asList("give","me","all","list","who","what","which","where","when"));
        HashSet<String> clean_words = new HashSet<>();

        for(String token : question.split(" ")){
            if(!StringUtilsSimple.isStopWord(token) && !wordsToRemove.contains(token.toLowerCase())){
                clean_words.add(token.toLowerCase());
            }
        }

        return clean_words;
    }

    public ArrayList<String> findNounPhrases(CoreDocument document, String phrasePart){
        ArrayList<String> nounPhrases = new ArrayList<String>();

        Tree constituencyTreeRoot = document.sentences().get(0).constituencyParse();
        List<Tree> constituencyTreeLeaves = constituencyTreeRoot.getLeaves();

        for(Tree constituencyTreeLeaf : constituencyTreeLeaves){
            if(constituencyTreeLeaf.value().equalsIgnoreCase(phrasePart)){
                List<Tree> pathFromRoot = constituencyTreeRoot.dominationPath(constituencyTreeLeaf);
                for(int i = pathFromRoot.size()-1; i >= 0; i--) {
                    if(pathFromRoot.get(i).label().toString().equals("NP")) {
                        String newTerm = "";
                        for (Tree leaf : pathFromRoot.get(i).getLeaves()) {
                            newTerm = newTerm + " " + leaf.value().toLowerCase();
                        }
                        nounPhrases.add(newTerm.trim());
                        break;
                    }
                }
            }
        }
        return nounPhrases;
    }

    public HashSet<String> getSingleWordTerms(HashSet<String> cleanWords, HashSet<String> multiWordTerms){
        HashSet<String> swts = new HashSet<String>();
        String mwesConcatenated = String.join(" ",multiWordTerms);
        for(String word: cleanWords){
            if(!mwesConcatenated.contains(word))
                swts.add(word);
        }
        return swts;
    }

    public String findLemma(CoreDocument document, String targetWord){
        String lemma = "";
        for(CoreLabel token : document.tokens()){
            if(token.word().toLowerCase().equals(targetWord.toLowerCase())){
                lemma = token.lemma();
            }
        }
        return lemma;
    }

}
