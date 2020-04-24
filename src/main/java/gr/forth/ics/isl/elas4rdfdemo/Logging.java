package gr.forth.ics.isl.elas4rdfdemo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logging {
    public static void logRequest(String remoteAddr,String type,String query,int page,int maxSize,int triplesUsed){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();

        try{
            Writer output;
            output = new BufferedWriter(new FileWriter("request_log.tsv", true));
            output.write(dtf.format(now)+"\t"+remoteAddr+"\t"+type+"\t"+query+"\t"+maxSize+"\t"+page+"\t"+triplesUsed+"\n");
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}