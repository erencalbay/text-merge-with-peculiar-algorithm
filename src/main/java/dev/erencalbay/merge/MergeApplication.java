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
	public static String finalWord;
	public static ArrayList<String> wordsList;
	public static double lastDuration;
	public static int temphmany;
	public static void main(String[] args) {
		SpringApplication.run(MergeApplication.class, args);
		//mongoDbConnection();
	}
	@RequestMapping(value = "/index")
	public String index() {
		return "index";
	}
	@RequestMapping(value = "/savetext", method = RequestMethod.POST)
	public ModelAndView save(@ModelAttribute User user){
		System.out.println(user.toString());
		ArrayList<String> tmpList = new ArrayList<>();
		ArrayList<String> finalList = new ArrayList<>();
		for (String us: user.getTexts()) {
			System.out.println(us);
			tmpList.add(us);
		}
		temphmany = tmpList.size();
		finalWord = mainAlgs(tmpList);
		System.out.println("son kelime " +finalWord);
		mongoDbConnection();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("final");
		modelAndView.addObject("user", user);
		return modelAndView;
	}
	public static String mainAlgs(ArrayList<String> tmpList) {
		wordsList = (ArrayList<String>) tmpList.clone();
		int hmanyText = tmpList.size();
		int i = 0;
		long startTime = System.nanoTime();
		ArrayList<String> finalList = new ArrayList<>();
		boolean isContain = false;
		while(hmanyText-1!=i)
		{
			ArrayList<String> txtCheck1 = new ArrayList<>();
			ArrayList<String> txtCheck2 = new ArrayList<>();
			String[] tmpString1;
			String[] tmpString2;
			tmpString1 = tmpList.get(i).split(" ");
			tmpString2 = tmpList.get(i+1).split(" ");
			for (String tmps:tmpString1) {
				txtCheck1.add(tmps);
			}
			for (String tmps:tmpString2) {
				txtCheck2.add(tmps);
			}
			isContain = checkContains(txtCheck1, txtCheck2);
			System.out.println(isContain);
			i++;
		}
		if(isContain) {
			//firstqueueControl(tmpList);
			finalWord = mainMergeFunc(tmpList);
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); // hesaplama
		double durationtoSecond = (double) duration / 1_000_000;
		lastDuration = durationtoSecond;
		System.out.println(durationtoSecond+" milisaniye.");
		return finalWord;
	}
	private static String mainMergeFunc(ArrayList<String> tmpList) {
		int hmanyText = tmpList.size();
		int i = 0;
		long startTime = System.nanoTime();
		ArrayList<String> finalList = new ArrayList<>();
		boolean isContain = false;
		finalWord = "";
		while(hmanyText-1!=i)
		{
			ArrayList<String> txtCheck1 = new ArrayList<>();
			ArrayList<String> txtCheck2 = new ArrayList<>();
			String[] tmpString1;
			String[] tmpString2;
			tmpString1 = tmpList.get(i).split(" ");
			tmpString2 = tmpList.get(i+1).split(" ");
			for (String tmps:tmpString1) {
				txtCheck1.add(tmps);
			}
			for (String tmps:tmpString2) {
				txtCheck2.add(tmps);
			}

			finalWord = findSameSub(txtCheck1,txtCheck2, hmanyText);
			i++;
		}
		return finalWord;
	}
	private static void mongoDbConnection() {
		MongoClient client = MongoClients.create("mongodb+srv://erencalbay:05kWvvz45Ohjx8E2@javaweb.jyy216v.mongodb.net/test");
		MongoDatabase db = client.getDatabase("testDB");
		MongoCollection col = db.getCollection("testCollection");
		Document sampleDoc = new Document();
		int ct = 0;
		for (String word : wordsList) {
			sampleDoc.append("Metin " + ct, word);
			ct++;
		}
		sampleDoc.append("Final", finalWord);
		sampleDoc.append("Duration(ms)",lastDuration);
		col.insertOne(sampleDoc);
	}
	private static String findSameSub(ArrayList<String> txtCheck1, ArrayList<String> txtCheck2, int hmanyText) {
		ArrayList<String> tmpList1;
		ArrayList<String> tmpList2;
		tmpList1 = (ArrayList<String>) txtCheck1.clone();
		tmpList2 = (ArrayList<String>) txtCheck2.clone();
		for (String txt1:txtCheck1) {
			for (String txt2:txtCheck2) {
				boolean isMean = checkFull(txt1, txt2);
				if(txt1.contains(txt2) && isMean==true){
					tmpList1.remove(txt1);
				}
			}
		}
		for (String str:tmpList1) {
			if(finalWord.isEmpty())
			{
				finalWord+=str;

			}
			else
			{
				finalWord+=" "+str;
			}

		}

		temphmany--;
		if(temphmany-1==0)
		{
			for (String str:tmpList2) {
				finalWord+=" "+str;
			}
		}
		return finalWord;
	}
	private static void firstqueueControl(ArrayList<String> tmpList) {
		int i = 0;
		int j = 0;
		ArrayList<String> txtCheck1 = new ArrayList<>();
		ArrayList<String> txtCheck2 = new ArrayList<>();
		String[] tmpString1;
		String[] tmpString2;
		int listSize = tmpList.size();
		ArrayList<Integer> position = new ArrayList<>();
		while(i!=listSize)
		{
			position.add(0);
			i++;
		}
		i=0;
		while(i!=listSize)
		{
			tmpString1 = tmpList.get(i).split(" ");
			while (j!=listSize)
			{
				tmpString2 = tmpList.get(j).split(" ");
				if(i!=j)
				{
					for (String tmps:tmpString1) {
						txtCheck1.add(tmps);
					}
					for (String tmps:tmpString2) {
						txtCheck2.add(tmps);
					}
					int txt1half = txtCheck1.size()/2;
					int txt2half = txtCheck2.size()/2;
					System.out.println(txt1half);
					System.out.println(txt2half);
				}
				j++;
			}
			i++;
		}
	}
	private static boolean checkContains(ArrayList<String> txtCheck1, ArrayList<String> txtCheck2) {
		for (String txtParse1:txtCheck2) {
			for (String txtParse2:txtCheck1) {
				if(txtParse2.contains(txtParse1) || txtParse1.contains(txtParse2))
				{
					boolean control = checkFull(txtParse2, txtParse1);
					if(control)
						return true;
				}
			}
		}
		return false;
	}
	private static boolean checkFull(String txtParse2, String txtParse1) {
		int similarityControl = 0;
		int lengthShortestWord = 0;
		int lengthTallestWord = 0;
		int index = 0;
		if(txtParse2.length()>txtParse1.length())
		{
			lengthShortestWord = txtParse1.length();
			lengthTallestWord = txtParse2.length();
		}
		else
		{
			lengthShortestWord = txtParse2.length();
			lengthTallestWord = txtParse1.length();
		}
		if(index==0 && txtParse2.charAt(index)!=txtParse1.charAt(index))
		{
			return false;
		}
		while(lengthShortestWord!=index)
		{

			if(txtParse2.charAt(index)!=txtParse1.charAt(index)) {
				similarityControl++;
			}
			index++;
		}
		int extra = lengthTallestWord - lengthShortestWord;
		similarityControl+=extra;

		double similarityControlDouble = Double.valueOf(similarityControl);
		double lengthTallestWordDouble = Double.valueOf(lengthTallestWord);
		double equality = similarityControlDouble/lengthTallestWordDouble;
		if (equality >= 3/13.) {
			return false;
		}
		return true;
	}
}

