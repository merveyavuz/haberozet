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

import zemberek.tokenization.TurkishSentenceExtractor;
import zemberek.tokenization.TurkishTokenizer;

public class TextProcessing {

	public static HashMap<String, Integer> map = new HashMap<String, Integer>();
	public static HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
	static TurkishTokenizer tokenizer = TurkishTokenizer.DEFAULT;
	public static HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();

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

	public static void splitToParagraphs() {

	}

	public static void splitToSentences(String input) { // title ve input map e yerlestirilir
		// map.put(title, 0);
		TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
		List<String> sentences = extractor.fromParagraph(input);
		for (String sentence : sentences) {
			map.put(sentence, 0);
		}
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
			}
		}
	}

	public static void main(String[] args) {
		String title = "Konunun Belirlenmesi ";
		String text = "Konu belirleme aş amasının 15 amacı parçadaki Mayıs en önemli konuların belirlenmesini sağlamaktır."
				+ " Bunu sağlamak için kelime frekanslarının hesaplanması, cümlenin bulunduğu yerin incelenmesi, "
				+ "ipucu veren ifadelerden yararlanılması gibi Teknikler kullanılır. Bazı yazı tiplerinde, yazının başlığı, "
				+ "yazının ilk cümlesi gibi kritik pozisyonlar yazıyla ilgili çünkü en önemli konuları barındırabilirler. "
				+ "\"Özetle\", \"En önemlisi\", \"Sonuç olarak\" gibi ipucu veren ifadeler yazıyla ilgili önemli noktaları gösteren işaretler olabilir? "
				+ "İstanbul çok sıkça kullanılan kelimeler, edat veya belirteç olmadıkları sürece, içinde bulundukları cümlelerin önemli olduklarını gösterebilirler.";

		splitToSentences(text);
		addAverageLengthScore();

		
		setWordFrequencyMap(text);
		 System.out.println("--------------frequence words-----------------------");

			frequencyMap.entrySet().forEach(entry -> {
				System.out.println("WORD: " + entry.getKey() + " FREQUENCE: " + entry.getValue());
			});
			
		setSentenceScores(title, text);

		map.entrySet().forEach(entry -> {
			System.out.println("SENTENCE: " + entry.getKey() + " SCORE: " + entry.getValue());
		});

	}

}