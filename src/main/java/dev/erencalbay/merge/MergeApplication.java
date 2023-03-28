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
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@RestController
public class MergeApplication {
	public static String finalWord;
	public static ArrayList<String> wordsList;
	public static double lastDuration;
	public static int temphmany;
	public static int counterOfMerge;
	public static void main(String[] args) {
		SpringApplication.run(MergeApplication.class, args);
	}
	@RequestMapping(value = "/index")
	public String index() {
		return "index";
	}
	@RequestMapping(value = "/savetext", method = RequestMethod.POST)
	public ModelAndView save(@ModelAttribute User user){
		System.out.println(user.toString());
		ArrayList<String> tmpList = new ArrayList<>();
		for (String us: user.getTexts()) {
			System.out.println(us);
			tmpList.add(us);
		}
		temphmany = tmpList.size();
		finalWord = mainAlgs(tmpList);
		System.out.println("son kelime " +finalWord);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("final");
		modelAndView.addObject("message", finalWord);
		return modelAndView;
	}
	@RequestMapping(value="/godbsave")
	public ModelAndView godbsave(@ModelAttribute User user) {
		System.out.println("Success");
		mongoDbConnection();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("godbsave");
		modelAndView.addObject("message", finalWord);
		return modelAndView;
	}
	public static String mainAlgs(ArrayList<String> tmpList) {
		wordsList = (ArrayList<String>) tmpList.clone();
		int hmanyText = tmpList.size();
		int i = 0;
		long startTime = System.nanoTime();
		boolean isContain = false;
		while(hmanyText-1!=i)
		{
			ArrayList<String> txtCheck1 = new ArrayList<>();
			ArrayList<String> txtCheck2 = new ArrayList<>();
			String[] tmpString1;
			String[] tmpString2;
			tmpString1 = tmpList.get(i).split(" ");
			tmpString2 = tmpList.get(i+1).split(" ");
			Collections.addAll(txtCheck1, tmpString1);
			Collections.addAll(txtCheck2, tmpString2);
			if(txtCheck1.size() == 1 && txtCheck2.size() == 1) {
				String txt1 = txtCheck1.get(0);
				String txt2 = txtCheck2.get(0);
				boolean isSame = checkFull(txt1,txt2);
				if(isSame) {
					finalWord = onewordControl(txt1,txt2);
					return finalWord;
				}
				else {
					finalWord = "Eşleştirme olmadı - Birleştirme yapılamadı.";
				}
			}
			else {
				isContain = checkContains(txtCheck1, txtCheck2);
			}
			System.out.println(isContain);
			i++;
		}
		if(isContain) {
			firstqueueControl(tmpList);
			finalWord = mainMergeFunc(wordsList);
			finalWord = duplicateControl(finalWord);
		}
		else {
			finalWord = "Eşleştirme olmadı - Birleştirme yapılamadı.";
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); // hesaplama
		double durationtoSecond = (double) duration / 1_000_000;
		lastDuration = durationtoSecond;
		System.out.println(durationtoSecond+" milisaniye.");
		return finalWord;
	}
	private static String onewordControl(String txt1, String txt2) {
		int i =0;
		String jointChars = "";
		int txt1length = txt1.length();
		int txt2length = txt2.length();
		int shortest = 0;
		if(txt1length>txt2length) {
			shortest = txt2length;
		}
		else {
			shortest = txt1length;
		}
		while (i<shortest) {
			if(txt1.charAt(i)==txt2.charAt(i)) {
				jointChars+=txt1.charAt(i);
				i++;
			}
		}
		int jclen = jointChars.length();
		int firstjclen = jclen;
		if(txt1length>txt2length) {
			if(txt1length-txt2length>2) {
				while(txt1length-jclen-1!=0) {
					jointChars+=txt1.charAt(jclen);
					jclen++;
				}
				return jointChars;
			}
			else {
				while(txt1length-jclen!=0) {
					jointChars+=txt1.charAt(jclen);
					jclen++;
				}
				if(txt2length != firstjclen) {
					while (txt2length - firstjclen - 1 != 0) {
						jointChars += txt2.charAt(firstjclen);
						firstjclen++;
					}
				}
				return jointChars;
			}
		}
		else {
			if(txt2length-txt1length>2) {
				while(txt2length-jclen-1!=0) {
					jointChars+=txt2.charAt(jclen);
					jclen++;
				}
				return jointChars;
			}
			else {
				while(txt2length-jclen!=0) {
					jointChars+=txt2.charAt(jclen);
					jclen++;
				}
				if(txt1length != firstjclen) {
				while(txt1length-firstjclen-1!=0) {
					jointChars+=txt1.charAt(firstjclen);
					firstjclen++;
				}
				}
				return jointChars;
			}
		}
	}
	private static String mainMergeFunc(ArrayList<String> tmpList) {
		int hmanyText = tmpList.size();
		int i = 0;
		finalWord = "";
		while(hmanyText-1!=i)
		{
			ArrayList<String> txtCheck1 = new ArrayList<>();
			ArrayList<String> txtCheck2 = new ArrayList<>();
			String[] tmpString1;
			String[] tmpString2;
			tmpString1 = tmpList.get(i).split(" ");
			tmpString2 = tmpList.get(i+1).split(" ");
			Collections.addAll(txtCheck1, tmpString1);
			Collections.addAll(txtCheck2, tmpString2);

			finalWord = findSameSub(txtCheck1,txtCheck2);
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
	private static String findSameSub(ArrayList<String> txtCheck1, ArrayList<String> txtCheck2) {
		ArrayList<String> tmpList1;
		ArrayList<String> tmpList2;
		tmpList1 = (ArrayList<String>) txtCheck1.clone();
		tmpList2 = (ArrayList<String>) txtCheck2.clone();
		ArrayList<String> tmpListforcontrol1 = (ArrayList<String>) txtCheck1.clone();
		ArrayList<String> tmpListforcontrol2 = (ArrayList<String>) txtCheck2.clone();
		for (String txt1:txtCheck1) {
			for (String txt2:txtCheck2) {
				boolean isMean = checkFull(txt1, txt2);
				if((txt1.contains(txt2) || txt2.contains(txt1)) && isMean){
					int word1index = tmpListforcontrol1.indexOf(txt1);
					int word2index = tmpListforcontrol2.indexOf(txt2);
					if(word1index == word2index && word1index==0) {
						tmpList2.remove(txt2);
					}
					else {
						tmpList1.remove(txt1);
					}
				}
			}
		}
		for (String str:tmpList1) {
			if(finalWord.isEmpty()) {
				finalWord += str;
			} else
				finalWord += " " + str;
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
	private static String duplicateControl(String finalWord) {
		String[] finalWordList = finalWord.split(" ");
		ArrayList<String> finalWordListArray = new ArrayList<>(Arrays.asList(finalWordList));
		int dupcontrolsize = finalWordListArray.size();
		while(dupcontrolsize-1!=0) {
			String word1 = finalWordListArray.get(dupcontrolsize-1);
			String word2 = finalWordListArray.get(dupcontrolsize-2);
			if(word1.contains(word2) || word2.contains(word1)) {
				boolean isFull = checkFull(word1,word2);
				if(isFull) {
					finalWordListArray.remove(dupcontrolsize-1);
				}
			}
			dupcontrolsize--;
		}
		String lastFinalWord =null;
		for (String str:finalWordListArray) {
			if(lastFinalWord==null) {
				lastFinalWord = "";
				lastFinalWord += str;
			} else
				lastFinalWord += " " + str;
		}
		return lastFinalWord;
	}
	private static void firstqueueControl(ArrayList<String> tmpList) {
		int i;
		int j = 0;
		ArrayList<String> txtCheck1 = new ArrayList<>();
		ArrayList<String> txtCheck2 = new ArrayList<>();
		String[] tmpString1;
		String[] tmpString2;
		int listSize = tmpList.size();
		i=0;
		while(i!=listSize)
		{
			tmpString1 = tmpList.get(i).split(" ");
			while (j!=listSize)
			{
				tmpString2 = tmpList.get(j).split(" ");
				if(i!=j)
				{
					Collections.addAll(txtCheck1, tmpString1);
					Collections.addAll(txtCheck2, tmpString2);
					int boolif = queueIndexControl(txtCheck1, txtCheck2);
					if(boolif == 1) {
						String tmpStr1 = tmpList.get(i);
						String tmpStr2 = tmpList.get(j);
						System.out.println("ilk " +wordsList);
						wordsList.set(i, tmpStr2);
						wordsList.set(j, tmpStr1);
						System.out.println("son " +wordsList);
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
	private static int queueIndexControl(ArrayList<String> txtCheck1, ArrayList<String> txtCheck2) {
		for (String txt1:txtCheck1) {
			for (String txt2:txtCheck2) {
				if(txt1.contains(txt2) || txt2.contains(txt1)) {
					boolean isFull= checkFull(txt1, txt2);
					if(isFull) {
						int txt1index = txtCheck1.indexOf(txt1);
						int txt2index = txtCheck2.indexOf(txt2);
						if(txt1index>txt2index) {
							return 0;
						}
						else {
							return 1;
						}
					}
				}
			}
		}
		return 0;
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
		int lengthShortestWord;
		int lengthTallestWord;
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

		double similarityControlDouble = similarityControl;
		double lengthTallestWordDouble = lengthTallestWord;
		double equality = similarityControlDouble/lengthTallestWordDouble;
		if(equality >= 3 / 17.) {
			return false;
		}
		return true;
	}
}

