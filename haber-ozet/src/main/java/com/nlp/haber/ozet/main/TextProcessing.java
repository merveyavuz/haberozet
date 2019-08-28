package com.nlp.haber.ozet.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import zemberek.tokenization.TurkishSentenceExtractor;
import zemberek.tokenization.TurkishTokenizer;

public class TextProcessing {

	public static HashMap<String, Integer> map = new HashMap<String, Integer>();
	public static HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
	public static TurkishTokenizer tokenizer = TurkishTokenizer.DEFAULT;
	public static HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();
	public int summaryPercent;
	public String title;
	public String text;
	public static String [] paragraphs;
	
	public TextProcessing(String title, String text) {
		this.title=title;
		this.text=text;
		
		setScoreMap();
		splitToParagraphs(text);
		splitToSentences(text);
		addParagraphScore(map);
		addAverageLengthScore();
		setWordFrequencyMap(text);
		setSentenceScores(title, text);
		
	}
	public static void setScoreMap() {
		scoreMap.put("title", 20);
		scoreMap.put("frequency", 10);
		scoreMap.put("entry", 20);
		scoreMap.put("result", 2);
		scoreMap.put("keyword", 8);
		scoreMap.put("uppercase", 3);
		scoreMap.put("positive", 15);
		scoreMap.put("negative", -20);
		scoreMap.put("collocation", 4);
		scoreMap.put("number", 3);
		scoreMap.put("quotationMark", 2);
		scoreMap.put("endingMark", 2);
		scoreMap.put("averageLength", 10);
		scoreMap.put("dayMonth", 5);
	}

	public static void splitToParagraphs(String text) {
		String patternStr = "(?<=(\r\n|\r|\n))([ \\t]*$)+";
		paragraphs = Pattern.compile(patternStr, Pattern.MULTILINE).split(text);
		
		for (int i=0; i<paragraphs.length; i++) {
		    String paragraph = paragraphs[i];
		    System.out.println("Paragraph: "+paragraph);
		}
	}

	public static void splitToSentences(String input) { // title ve input map e yerlestirilir
		// map.put(title, 0);
		TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
		List<String> sentences = extractor.fromParagraph(input);	
		for (String sentence : sentences) {
			map.put(sentence, 0);
		}
	}

	
	public static void addParagraphScore(HashMap<String, Integer> map ) {
			
		map.entrySet().forEach(entry -> {
			if (paragraphs[0].contains(entry.getKey())) {
				map.put(entry.getKey(), scoreMap.get("entry"));
				System.out.println("entryScore: "+ scoreMap.get("entry"));
			}else if (paragraphs[paragraphs.length-1].contains(entry.getKey())) {
				map.put(entry.getKey(), scoreMap.get("result"));
				System.out.println("resultScore: "+ scoreMap.get("result"));
			}	
		});
		
		
			
	}
	
	
	public static void setSentenceScores(String title, String text) {
		splitToSentences(text);

		for (Entry<String, Integer> entry : map.entrySet()) {
			SentenceProcessing processing = new SentenceProcessing(title, entry.getKey(), text);
			map.replace(entry.getKey(), processing.getSentenceScore());
		}

	}

	public static Map<String, Integer> sortMapByValueDesc(Map<String, Integer> map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Collections.reverse(list);
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static void setWordFrequencyMap(String text) {
		TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
		List<String> sentences = extractor.fromParagraph(text);
		List<String> allWords = new ArrayList<String>();
		List<String> stemWords = new ArrayList<String>();

		SentenceProcessing sp = new SentenceProcessing();

		for (String s : sentences) {
			for (String word : sp.sentenceToWords(s)) {
				allWords.add(word);
			}
		}
		allWords = sp.removeStopWords(allWords);

		for (String s : allWords) {
			stemWords.add(sp.wordToStem(s));
		}

		for (String w : stemWords) {
			int counter = 0;
			for (String string : stemWords) {
				if (w.compareTo(string) == 0) {
					counter++;
				}
			}
			frequencyMap.put(w, counter);
		}

		frequencyMap = (HashMap<String, Integer>) sortMapByValueDesc(frequencyMap);
	}

	public static void addAverageLengthScore() {
		int counter = map.size();
		int sum = 0;

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			sum = sum + entry.getKey().length();
		}
		int avg = sum / counter;

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getKey().length() == avg - 1 || entry.getKey().length() == avg + 1) {
				int val = entry.getValue();
				map.put(entry.getKey(), val + scoreMap.get("averageLength"));
				System.out.println("averageLegnth Score: "+  scoreMap.get("averageLength") );
			}
		}
	}

	
	public static void main(String[] args) {
		String title = "Konunun Belirlenmesi ";
		
		String text="Konu belirleme aş amasının 15 amacı parçadaki Mayıs en önemli konuların belirlenmesini sağlamaktır."
				+ "Merve sağlamak için kelime frekanslarının hesaplanması, cümlenin bulunduğu yerin incelenmesi, "
				+ "ipucu veren ifadelerden yararlanılması gibi Teknikler kullanılır.\r\n" + 
				"\r\n" + 
				"Bazı yazı tiplerinde, yazının başlığı, yazının ilk cümlesi gibi kritik pozisyonlar yazıyla ilgili çünkü en önemli konuları barındırabilirler.\r\n" + 
				"\r\n" + 
				 "\"Özetle\", \"En önemlisi\", \"Sonuç olarak\" gibi ipucu veren ifadeler yazıyla ilgili önemli noktaları gösteren işaretler olabilir! \r\n" + 
				"\r\n" + 
				"İstanbul çok sıkça kullanılan kelimeler, edat veya belirteç olmadıkları sürece, içinde bulundukları cümlelerin önemli olduklarını gösterebilirler. "
				+ "Yoruma dayalı olan bu teknikte, karıştırma ve kaynaştırma yapılarak birbiriyle ilgili olan cümleler, daha genel cümleler ile ifade edilebilirler. ";


		TextProcessing tp = new TextProcessing(title, text);
		map.entrySet().forEach(entry -> {
			System.out.println("SENTENCE: " + entry.getKey() + " SCORE: " + entry.getValue());
		});

	}
	
	

}