package dev.erencalbay.merge;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@SpringBootApplication
@RestController
public class MergeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MergeApplication.class, args);
		//mongoDbConnection();
	}
	private static void mongoDbConnection() {
		ArrayList<String> words = new ArrayList<String>();
		words.add("araba");
		words.add("tır");
		String finalpres = "araba"+"tır";
		MongoClient client = MongoClients.create("mongodb+srv://erencalbay:05kWvvz45Ohjx8E2@javaweb.jyy216v.mongodb.net/test");
		MongoDatabase db = client.getDatabase("sampleDB");
		MongoCollection col = db.getCollection("sampleCollection");
		Document sampleDoc = new Document();
		int ct = 0;
		for (String string : words) {
			sampleDoc.append("Metin " + ct, string);
			ct++;
		}
		sampleDoc.append("Final", finalpres);
		col.insertOne(sampleDoc);
	}
	@RequestMapping(value = "/index")
	public String index() {
		return "index";
	}
	@RequestMapping(value = "/savetext", method = RequestMethod.POST)
	public ModelAndView save(@ModelAttribute User user){
		System.out.println(user.toString());
		System.out.println(user.getTexts());
		for (String us: user.getTexts()) {
			System.out.println(us);
		}

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("final");
		modelAndView.addObject("user", user);
		return modelAndView;
	}

}

