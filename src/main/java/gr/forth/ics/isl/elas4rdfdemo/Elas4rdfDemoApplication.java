package gr.forth.ics.isl.elas4rdfdemo;

import gr.forth.ics.isl.elas4rdfdemo.caching.SimpleAnswerRepository;
import gr.forth.ics.isl.elas4rdfdemo.caching.SimpleEntityRepository;
import gr.forth.ics.isl.elas4rdfdemo.caching.SimpleTripleRepository;
import gr.forth.ics.isl.elas4rdfdemo.models.*;
import gr.forth.ics.isl.elas4rdfdemo.qa.models.AnswersContainer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

@SpringBootApplication
@Controller
public class Elas4rdfDemoApplication {

	@Autowired
    private SimpleAnswerRepository sar;
	@Autowired
	private SimpleTripleRepository str;
	@Autowired
	private SimpleTripleRepository strWithoutAnnotations;
	@Autowired
	private SimpleEntityRepository ser;

	public TriplesContainer triplesContainer;
	public EntitiesContainer entitiesContainer;
	public AnswersContainer answersContainer;

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

		triplesContainer = str.searchTriples(query);

		maxPages = triplesContainer.getTriples().size()/10;
		if(page==maxPages+1){
			endIndex = triplesContainer.getTriples().size();
		} else {
			endIndex = startIndex+10;
		}
		model.addAttribute("triples", triplesContainer.getTriples().subList(startIndex,endIndex));

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
		model.addAttribute("maxPages", maxPages);
		model.addAttribute("query",query);
		model.addAttribute("type","triples");
		model.addAttribute("page",page);
		model.addAttribute("maxSize",triplesContainer.getMaxSize());
		return "results";
	}

	@GetMapping("/results/entities")
	public String handleEntities(@RequestParam(name="query") String query, @RequestParam(name="page", required = true, defaultValue="1") int page,  @RequestParam(name="size", required = true, defaultValue="1000") int size, Model model) {

		int maxPages = 0;
		int endIndex = 0;
		int startIndex = (page-1)*10;

		entitiesContainer = ser.searchEntities(query,size);

		maxPages = entitiesContainer.getEntities().size()/10;
		if(page==maxPages+1){
			endIndex = entitiesContainer.getEntities().size();
		} else {
			endIndex = startIndex+10;
		}
		List<ResultEntity> subResults = entitiesContainer.getEntities().subList(startIndex,endIndex);

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
		model.addAttribute("maxPages", maxPages);
		model.addAttribute("size",size);
		model.addAttribute("query",query);
		model.addAttribute("type","entities");
		model.addAttribute("page",page);
		model.addAttribute("maxSize",entitiesContainer.getMaxSize());
		return "results";
	}

	@GetMapping("/results/qa")
	public String handleQa(@RequestParam(name="query") String query, @RequestParam(name="page", required = true, defaultValue="1") int page, Model model) {

		answersContainer = sar.getAnswers(query);

		int maxPages = answersContainer.getAnswers().size()/10;
		int endIndex = 0;
		int startIndex = (page-1)*10;

		if(page==maxPages+1){
			endIndex = answersContainer.getAnswers().size();
		} else {
			endIndex = startIndex+10;
		}

		if(!answersContainer.isList()){
			model.addAttribute("topAnswer",answersContainer.getTopAnswer());
		}

		model.addAttribute("answers",answersContainer.getAnswers().subList(startIndex,endIndex));
		model.addAttribute("qType",answersContainer.getType());

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
		model.addAttribute("maxPages",maxPages);
		model.addAttribute("type","qa");
		model.addAttribute("page",page);
		return "qa";
	}

    @GetMapping("/results/graph")
    public String handleGraph(@RequestParam(name="query") String query, @RequestParam(name="size",  defaultValue="25") int size, Model model) {

		triplesContainer = strWithoutAnnotations.searchTriples(query);
		AnswerExploration ae = new AnswerExploration(triplesContainer.getTriples(),size);

		String jsonGraph = ae.createModelFromTriples();

		model.addAttribute("query",query);
        model.addAttribute("type","graph");
		model.addAttribute("size",size);
        model.addAttribute("jsonGraph",jsonGraph);
        return "graph";
    }

	@GetMapping("/loadimage")
	@ResponseBody
	public String loadImage(@RequestParam(name="id") String id) {
		return findImageUrl(id);
	}

	public String findImageUrl(String id){
		String url="";
		String baseURL = "https://en.wikipedia.org/w/api.php?action=query&titles="+id+"&prop=pageimages&format=json&pithumbsize=200";
		try {
			//HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
			HttpClient client = HttpClientBuilder.create().build();
			URIBuilder builder = new URIBuilder(baseURL);

			HttpGet request = new HttpGet(builder.build());
			request.addHeader(CONTENT_TYPE, "application/json");

			HttpResponse response = client.execute(request);

			String json_string = EntityUtils.toString(response.getEntity());
			JSONObject responseObject = new JSONObject(json_string);
			if(responseObject != null){
				Iterator<String> ijo = responseObject.getJSONObject("query").getJSONObject("pages").keys();
				if(ijo.hasNext()){
					JSONObject jo = responseObject.getJSONObject("query").getJSONObject("pages").getJSONObject(ijo.next());
					if(jo.has("thumbnail")){
						url = jo.getJSONObject("thumbnail").getString("source");
					}
				}
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return url;
	}

	@GetMapping("/file")
	public void returnFile(@RequestParam(name="query") String query, @RequestParam(name="size",  defaultValue="100") int size, @RequestParam(name="type",  defaultValue="turtle") String type, HttpServletResponse response) throws IOException {
		triplesContainer = strWithoutAnnotations.searchTriples(query);
		AnswerExploration ae = new AnswerExploration(triplesContainer.getTriples(),triplesContainer.getTriples().size());
		String myString = ae.createFile(type);
		String extension = ".ttl";
		if(type.equals("ntriples")){
			extension = ".nt";
		} else if(type.equals("jsonld")){
			extension = ".jsonld";
		}
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Content-Disposition","attachment;filename=triples"+extension);
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		out.println(myString);
		out.flush();
		out.close();
	}

	@GetMapping("/about")
	public String aboutPage(){
		return "about";
	}

}
