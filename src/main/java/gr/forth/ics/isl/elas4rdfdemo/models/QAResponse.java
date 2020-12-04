package gr.forth.ics.isl.elas4rdfdemo.models;

import com.google.gson.Gson;

public class QAResponse {
    private String category;

    private String types;

    public class QAAnswer{
        private String answer;
        private String entity;
        private double score;
        private String text;

        public String getAnswer() {
            return answer;
        }

        public String getEntity() {
            return entity;
        }

        public double getScore() {
            return score;
        }

        public String getText() { return text; }
    }

    private QAAnswer[] answers;

    public String getCategory() {
        return category;
    }

    public String getTypes() {
        return types;
    }

    public QAAnswer[] getAnswers() {
        return answers;
    }

    public static void main(String[] args){
        String text = "{\n" +
                "    \"answers\": [\n" +
                "        {\n" +
                "            \"answer\": \"Atlanta Dream\",\n" +
                "            \"entity\": \"http://dbpedia.org/resource/Michael_Cooper\",\n" +
                "            \"score\": 0.5769169926643372\n" +
                "        },\n" +
                "        {\n" +
                "            \"answer\": \"East Carolina University.\",\n" +
                "            \"entity\": \"http://dbpedia.org/resource/Michael_Perry_(basketball)\",\n" +
                "            \"score\": 0.38970625400543213\n" +
                "        },\n" +
                "        {\n" +
                "            \"answer\": \"Michael McBride (Football Coach)\",\n" +
                "            \"entity\": \"http://dbpedia.org/resource/Michael_McBride_(football_coach)\",\n" +
                "            \"score\": 0.04166274890303612\n" +
                "        },\n" +
                "        {\n" +
                "            \"answer\": \"Michael D'Asaro\",\n" +
                "            \"entity\": \"http://dbpedia.org/resource/Michael_D'Asaro,_Jr.\",\n" +
                "            \"score\": 0.020448679104447365\n" +
                "        },\n" +
                "        {\n" +
                "            \"answer\": \"Colbert Report in 2009.\",\n" +
                "            \"entity\": \"http://dbpedia.org/resource/List_of_The_Colbert_Report_episodes_(2009)\",\n" +
                "            \"score\": 1.6584803233854473e-05\n" +
                "        },\n" +
                "        {\n" +
                "            \"answer\": \"[]\",\n" +
                "            \"entity\": \"http://dbpedia.org/resource/Michael_Phelps_Push_the_Limit\",\n" +
                "            \"score\": 3.9106254234866356e-07\n" +
                "        },\n" +
                "        {\n" +
                "            \"answer\": \"[]\",\n" +
                "            \"entity\": \"http://dbpedia.org/resource/Michael_Phelps_Push_The_Limit\",\n" +
                "            \"score\": 3.9106254234866356e-07\n" +
                "        }\n" +
                "    ],\n" +
                "    \"category\": \"resource\",\n" +
                "    \"entities\": [\n" +
                "        {\n" +
                "            \"rdfs_comment\": \"[]\",\n" +
                "            \"uri\": \"http://dbpedia.org/resource/Michael_Phelps_Push_the_Limit\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"rdfs_comment\": \"[]\",\n" +
                "            \"uri\": \"http://dbpedia.org/resource/Michael_Phelps_Push_The_Limit\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"rdfs_comment\": \"[  This is a list of episodes for The Colbert Report in 2009. @en]\",\n" +
                "            \"uri\": \"http://dbpedia.org/resource/List_of_The_Colbert_Report_episodes_(2009)\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"rdfs_comment\": \"[  Michael D'Asaro Jr. is an American sabre fencer.  His father Michael D'Asaro, Sr. coached the US fencing team at the 1976 Summer Olympics.The younger D'Asaro is a coach at the California Institute of Technology. @en]\",\n" +
                "            \"uri\": \"http://dbpedia.org/resource/Michael_D'Asaro,_Jr.\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"rdfs_comment\": \"[  Michael McBride (Football Coach) is an Australian youth football (soccer) coach who presently holds the position of Assistant Coach at FFV NTC. He is part of a new generation of youth coaches in Australia that focus on developing the relationship between technique and intelligence. @en]\",\n" +
                "            \"uri\": \"http://dbpedia.org/resource/Michael_McBride_(football_coach)\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"rdfs_comment\": \"[  Michael Jerome Cooper (born April 15, 1956) is an American basketball coach, currently serving as head coach of the Atlanta Dream of the WNBA,and retired professional player. Prior to joining Atlanta, he coached the USC Women of Troy college basketball team.  He is a former player in the National Basketball Association (NBA) who spent his entire career with the Los Angeles Lakers, and has coached in the NBA, WNBA, and the NBA DL. @en]\",\n" +
                "            \"uri\": \"http://dbpedia.org/resource/Michael_Cooper\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"rdfs_comment\": \"[  Michael Perry is an American college basketball coach and a current assistant men's basketball coach at East Carolina University. @en]\",\n" +
                "            \"uri\": \"http://dbpedia.org/resource/Michael_Perry_(basketball)\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"types\": [\n" +
                "        \"Athlete\",\n" +
                "        \"Person\",\n" +
                "        \"Agent\",\n" +
                "        \"MusicalArtist\",\n" +
                "        \"OfficeHolder\",\n" +
                "        \"Organisation\",\n" +
                "        \"Artist\",\n" +
                "        \"University\",\n" +
                "        \"SportsTeam\",\n" +
                "        \"SoccerClub\"\n" +
                "    ]\n" +
                "}";

        Gson gson = new Gson();
        QAResponse r = gson.fromJson(text, QAResponse.class);
        System.out.println(gson.toJson(r));
    }
}
