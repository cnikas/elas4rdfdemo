package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@SpringBootApplication
@Controller
public class Elas4rdfDemoApplication {

	QuestionAnalysis qa;
	AnswerExtraction ae;
	KeywordSearch ks;

	public static void main(String[] args) {
		SpringApplication.run(Elas4rdfDemoApplication.class, args);

	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		//Main.initializeTools();
		//qa = new QuestionAnalysis();
		//ae =  new AnswerExtraction();
		ks = new KeywordSearch();
	}

	@GetMapping("/results")
	public String hello(@RequestParam(name="query") String query, @RequestParam(name="type", required = true, defaultValue="triples") String type, @RequestParam(name="page", required = true, defaultValue="1") int page, Model model) {

		int maxPages = 0;
		int endIndex = 0;
		int startIndex = (page-1)*10;

		if(type.equals("triples")){
			ArrayList<ResultTriple> results = ks.searchTriples(query);
			maxPages = results.size()/10;
			if(page==maxPages+1){
				endIndex = results.size();
			} else {
				endIndex = startIndex+10;
			}
			model.addAttribute("triples", results.subList(startIndex,endIndex));
		} else if(type.equals("entities")){
			ArrayList<ResultEntity> results = ks.searchEntities(query);
			maxPages = results.size()/10;
			if(page==maxPages+1){
				endIndex = results.size();
			} else {
				endIndex = startIndex+10;
			}
			model.addAttribute("entities", results.subList(startIndex,endIndex));
		}

		ArrayList<Integer> pageList = new ArrayList<>();
		if(maxPages == 0){
			pageList.add(0,1);
		} else if(maxPages==1){
			pageList.add(0,1);
			pageList.add(1,2);
		} else {
			if(page == 1){
				pageList.add(0,1);
				pageList.add(1,2);
				pageList.add(2,3);
			} else if(page == maxPages){
				pageList.add(0,page-2);
				pageList.add(1,page-1);
				pageList.add(2,page);
			} else {
				pageList.add(0,page-1);
				pageList.add(1,page);
				pageList.add(2,page+1);
			}
		}

		//String jsonAnswer = ae.extractAnswerJson(qa.analyzeQuestion(query)).toString();
		model.addAttribute("pages", pageList);
		model.addAttribute("query",query);
		model.addAttribute("type",type);
		model.addAttribute("page",page);
		return "results";
	}

}
