package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.caching.SimpleAnswerRepository;
import gr.forth.ics.isl.elas4rdfdemo.caching.SimpleEntityRepository;
import gr.forth.ics.isl.elas4rdfdemo.caching.SimpleTripleRepository;
import gr.forth.ics.isl.elas4rdfdemo.models.Answer;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultEntity;
import gr.forth.ics.isl.elas4rdfdemo.models.ResultTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Controller
@EnableCaching(proxyTargetClass=true)
public class Elas4rdfDemoApplication {

	@Autowired
    private SimpleAnswerRepository sar;
	@Autowired
	private SimpleTripleRepository str;
	@Autowired
	private SimpleEntityRepository ser;

	public static void main(String[] args) {
		SpringApplication.run(Elas4rdfDemoApplication.class, args);

	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		Main.initializeTools();
	}

	@GetMapping("/results/triples")
	public String handleTriples(@RequestParam(name="query") String query, @RequestParam(name="page", required = true, defaultValue="1") int page, Model model) {

		int maxPages = 0;
		int endIndex = 0;
		int startIndex = (page-1)*10;

		ArrayList<ResultTriple> results = str.searchTriples(query);
		maxPages = results.size()/10;
		if(page==maxPages+1){
			endIndex = results.size();
		} else {
			endIndex = startIndex+10;
		}
		model.addAttribute("triples", results.subList(startIndex,endIndex));

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
		model.addAttribute("type","triples");
		model.addAttribute("page",page);
		return "results";
	}

	@GetMapping("/results/entities")
	public String handleEntities(@RequestParam(name="query") String query, @RequestParam(name="page", required = true, defaultValue="1") int page, Model model) {

		int maxPages = 0;
		int endIndex = 0;
		int startIndex = (page-1)*10;

		ArrayList<ResultEntity> results = ser.searchEntities(query);
		maxPages = results.size()/10;
		if(page==maxPages+1){
			endIndex = results.size();
		} else {
			endIndex = startIndex+10;
		}
		List<ResultEntity> subResults = results.subList(startIndex,endIndex);
		for(ResultEntity res : subResults){
			res.imageUrl = res.findImageUrl();
		}

		model.addAttribute("entities", subResults);

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
		model.addAttribute("type","entities");
		model.addAttribute("page",page);
		return "results";
	}

	@GetMapping("/results/qa")
	public String handleQa(@RequestParam(name="query") String query, @RequestParam(name="page", required = true, defaultValue="1") int page, Model model) {

		ArrayList<Answer> answers;
		answers = sar.getAnswers(query);


		int maxPages = answers.size()/10;
		int endIndex = 0;
		int startIndex = (page-1)*10;

		if(page==maxPages+1){
			endIndex = answers.size();
		} else {
			endIndex = startIndex+10;
		}
		model.addAttribute("answers",answers.subList(startIndex,endIndex));

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

		model.addAttribute("pages", pageList);
		model.addAttribute("query",query);
		model.addAttribute("type","qa");
		model.addAttribute("page",page);
		return "results";
	}

    @GetMapping("/results/graph")
    public String handleGraph(@RequestParam(name="query") String query, Model model) {


		ArrayList<Answer> answers;
		answers = sar.getAnswers(query);


		model.addAttribute("query",query);
        model.addAttribute("type","graph");
		model.addAttribute("page",1);
        model.addAttribute("jsonGraph",AnswerExploration.createModel(answers).toString());
        return "graph";
    }

}
