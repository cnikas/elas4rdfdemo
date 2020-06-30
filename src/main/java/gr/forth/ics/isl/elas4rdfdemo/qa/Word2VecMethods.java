package gr.forth.ics.isl.elas4rdfdemo.qa;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class is used to initialize Word2Vec and create vectors from text sequences
 */
public class Word2VecMethods {

    private Word2Vec vec;
    private static StanfordCoreNLP posPipeline;

    public Word2VecMethods() {
        File gModel = new File("src/main/resources/qa/model.txt");
        this.vec = WordVectorSerializer.readWord2VecModel(gModel);
        Properties posProps = new Properties();
        posProps.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        this.posPipeline = new StanfordCoreNLP(posProps);
    }

    public INDArray averageVectorForSentence(ArrayList<String> keys) {

        INDArray sumVector = Nd4j.zeros(new int[]{1, 300});
        for (String key : keys) {
            INDArray currVector = vec.getWordVectorMatrix(key);
            if (currVector != null) {
                sumVector = sumVector.add(currVector);
            }
        }

        return sumVector;

    }

    public INDArray sentenceVector(String sentence) {
        CoreDocument doc = new CoreDocument(sentence);
        posPipeline.annotate(doc);

        ArrayList<String> keys = new ArrayList<String>();

        List<String> tags = doc.sentences().get(0).posTags();
        List<CoreLabel> tokens = doc.sentences().get(0).tokens();
        for (int i = 0; i < tokens.size(); i++) {
            String tag = convertPosTag(tags.get(i));
            if (!"".equals(tag)) {
                String key = tokens.get(i).lemma().toLowerCase() + "_" + tag;
                keys.add(key);
            }
        }

        return averageVectorForSentence(keys);
    }

    /*
     * https://universaldependencies.org/tagset-conversion/en-penn-uposf.html
     */
    public String convertPosTag(String s) {
        if (s.startsWith("NN"))
            return "NOUN";
        else if (s.startsWith("VB"))
            return "VERB";
        else if (s.startsWith("J") || s.equals("AFX"))
            return "ADJ";
        else if (s.startsWith("R") || s.equals("WRB"))
            return "ADV";
        else
            return "";
    }

}
