package com.nlp.haber.ozet.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.tokenization.Token;
import zemberek.tokenization.TurkishSentenceExtractor;
import zemberek.tokenization.TurkishTokenizer;

public class SentenceProcessing {

	public String title;
	public String sentence;
	public int score;
	public static HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
	public static HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();

	public SentenceProcessing(String title, String sentence, String text) {
		this.title = title;
		this.sentence = sentence;

		System.out.println("Title: " + title);
		System.out.println("Sentence: " + sentence);
		setScoreMap();
		addTitleScore(title, sentence);
		System.out.println("-----------------TITLE SCORE--------------" + score);
		setWordFrequencyMap(text);
		frequencyMap = (HashMap<String, Integer>) sortMapByValueDesc(frequencyMap);
		addFrequencyScore(sentence, frequencyMap);
		addNumberScore(sentence);
		addQuotationMarkScore(sentence);
		addEndingMarkScore(sentence);
		addDayMonthScore(sentence);
		addPositiveScore();
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

	public int getSentenceScore() {
		return score;
	}

	public List<String> sentenceToWords(String sentence) { // gelen cumle kelimelere ayirilir
		TurkishTokenizer tokenizer = TurkishTokenizer.builder()
				.ignoreTypes(Token.Type.Punctuation, Token.Type.NewLine, Token.Type.SpaceTab).build();
		List<Token> tokens = tokenizer.tokenize(sentence);
		List<String> words = new ArrayList<String>();
		for (Token token : tokens) {
			words.add(token.content);
		}
		return words;
	}

	public List<String> sentenceStems(List<String> sentence) { // gelen cumledeki kelimeler koklerine ayrilir
		List<String> sentenceStems = new ArrayList<String>();
		for (String word : sentence) {
			sentenceStems.add(wordToStem(word));
		}
		return sentenceStems;
	}

	public String wordToStem(String word) { // gelen kelime kokune ayrilir
		TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
		WordAnalysis results = morphology.analyze(word);
		String stem = "";
		for (SingleAnalysis result : results) {
			stem = result.getStem();
		}
		return stem;
	}

	public List<String> removeStopWords(List<String> sentence) {
		for (int i = sentence.size() - 1; i >= 0; i--) {
			if (TurkishStopWords.DEFAULT.contains(sentence.get(i))) {
				sentence.remove(i);
			}
		}
		return sentence;
	}

	public void addTitleScore(String title, String input) {
		List<String> baseList = sentenceToWords(title);
		baseList = removeStopWords(baseList);

		List<String> compareList = sentenceToWords(input);
		compareList = removeStopWords(compareList);

		baseList = sentenceStems(sentenceToWords(title));
		compareList = sentenceStems(sentenceToWords(input));

		baseList.retainAll(compareList); // İki dizinin ortak kelimelerini tutar

		int counter = baseList.size(); // Ortak kelime kadar puan degeri alacagindan counter tutulur.
		if (counter > 0) {
			score += counter * (scoreMap.get("title")); // Baslikta gecen kelimelerin degeri scoremapten alinir.
		}
	}

	public void setWordFrequencyMap(String text) {
		TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
		List<String> sentences = extractor.fromParagraph(text);
		List<String> allWords = new ArrayList<String>();
		List<String> stemWords = new ArrayList<String>();

		for (String s : sentences) {
			for (String word : sentenceToWords(s)) {
				allWords.add(word);
			}
		}
		allWords = removeStopWords(allWords);

		for (String s : allWords) {
			stemWords.add(wordToStem(s));
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

	public void addFrequencyScore(String sentence, HashMap<String, Integer> frequencyMap) {

		List<String> words = sentenceToWords(sentence);
		words = removeStopWords(words);
		words = sentenceStems(words);

		System.out.println("--------------sentence words-----------------------");
		for (String string : words) {
			System.out.println(string);
		}

		System.out.println("--------------frequence words-----------------------");

		frequencyMap.entrySet().forEach(entry -> {
			System.out.println("WORD: " + entry.getKey() + " FREQUENCE: " + entry.getValue());
		});

		System.out.println("-------------------------------------");
		System.out.println("size: " + frequencyMap.size());
		int percent = 10;
		int limit = (frequencyMap.keySet().size() * percent) / 100;
		List<String> keys = frequencyMap.entrySet().stream().map(Map.Entry::getKey).limit(limit)
				.collect(Collectors.toList());

		System.out.println("--------------10 percent of frequence words-----------------------");
		for (String string : keys) {
			System.out.println(string);
		}

		System.out.println("--------------common words-----------------------");

		LinkedHashSet<String> commonWords = new LinkedHashSet<String>();
		int counter = 0;
		for (String word : words) {
			for (String key : keys) {
				if (key.compareTo(word) == 0) {
					System.out.println("common word: " + word);
					// counter++;
					commonWords.add(word);
				}
			}
		}
		counter = commonWords.size();

		if (counter > 0) {
			score += counter * (scoreMap.get("frequency")); // Baslikta gecen kelimelerin degeri scoremapten alinir.

		}

	}

	public void addNumberScore(String sentence) {

		int counter = 0;
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(sentence);
		while (m.find()) {
			counter++;
			System.out.println(m.group());
		}
		score += counter * (scoreMap.get("number"));

	}

	public void addQuotationMarkScore(String sentence) {

		int counter = 0;
		Pattern p = Pattern.compile("\"(.*?)\"");
		Matcher m = p.matcher(sentence);
		while (m.find()) {
			counter++;
			System.out.println(m.group());
		}
		score += counter * (scoreMap.get("quotationMark"));

	}

	public void addEndingMarkScore(String sentence) {
		int counter = 0;
		Pattern p = Pattern.compile("[!?]");
		Matcher m = p.matcher(sentence);
		while (m.find()) {
			counter++;
			System.out.println(m.group());
		}
		score += counter * (scoreMap.get("quotationMark"));

	}

	public void addDayMonthScore(String sentence) {
		String[] searchWords = new String[] { "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos",
				"Eylül", "Ekim", "Kasım", "Aralık", "Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma",
				"Cumartesi" };
		
		List <String> words= sentenceToWords(sentence);
		int counter=0;
		for (String w : words) {
			for (String sw : searchWords) {
				if (w.compareTo(sw)==0) {
					counter++;
				}
			}
		}
		score+=counter * (scoreMap.get("dayMonth"));
	}
	
	public void addPositiveScore() {
		String[] searchWords = new String[] {"özetle", "sonuç", "neticede", "sonuçta", "özetle","kısaca"};
		
		List <String> words= sentenceToWords(sentence);
		int counter=0;
		for (String w : words) {
			for (String sw : searchWords) {
				if (w.toLowerCase().compareTo(sw)==0) {
					counter++;
				}
			}
		}
		score+=counter * (scoreMap.get("positive"));
		
	}
	
	public void addNegativeScore() {
		String[] searchWords = new String[] {"çünkü", "ancak", "öyleyse", "öyle"};
		
		List <String> words= sentenceToWords(sentence);
		int counter=0;
		for (String w : words) {
			for (String sw : searchWords) {
				if (w.toLowerCase().compareTo(sw)==0) {
					counter++;
				}
			}
		}
		score+=counter * (scoreMap.get("negative"));
		
	}
	
	public void addUppercaseScore() {
		List <String> words= sentenceToWords(sentence);
		int counter=0;
		
		
		for (int i=1; i<words.size(); i++) {
			if (Character.isUpperCase(words.get(i).charAt(0))) {
				score+= counter * (scoreMap.get("uppercase"));
			}
		}
		
		
		
		
	}
	
	
	
	
	
	
}
