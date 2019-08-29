package com.nlp.haber.ozet.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.tokenization.Token;
import zemberek.tokenization.TurkishTokenizer;

public class SentenceProcessing {

	public String title;
	public String sentence;
	public int score;
	public static HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
	public static HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();
	public static List<String> yerTamlayanlar = Arrays.asList("dağı", "caddesi", "durağı", "cadde", "durak",
			"tiyatrosu", "tiyatro", "nehri", "bulvarı", "bulvar", "uyruklu", "devleti", "boğazı", "sarayı", "gölü",
			"kalesi", "köprüsü", "parkı", "ovası", "meydanı");
	public static List<String> yerYon = Arrays.asList("Kuzey", "Güney", "Doğu", "Batı");
	public static LinkedHashSet<String> uppercases = new LinkedHashSet<String>();
	public static LinkedHashSet<String> ozelIsimler = new LinkedHashSet<String>();

	public SentenceProcessing() {

	}

	public SentenceProcessing(String title, String sentence, String text) {
		this.title = title;
		this.sentence = sentence;
		System.out.println(sentence);
		setScoreMap();
		 addTitleScore(title, sentence);
		 frequencyMap = TextProcessing.frequencyMap;
		 addFrequencyScore(sentence, frequencyMap);
		 addNumberScore(sentence);
		 addQuotationMarkScore(sentence);
		 addEndingMarkScore(sentence);
		 addDayMonthScore(sentence);
		 addPositiveScore();
		 addNegativeScore();
		addUppercaseScore();
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

		System.out.println("Title score: " + counter * (scoreMap.get("title")));
	}

	public void addFrequencyScore(String sentence, HashMap<String, Integer> frequencyMap) {

		List<String> words = sentenceToWords(sentence);
		words = removeStopWords(words);
		words = sentenceStems(words);

		int percent = 10;
		int limit = (frequencyMap.keySet().size() * percent) / 100;
		List<String> keys = frequencyMap.entrySet().stream().map(Map.Entry::getKey).limit(limit)
				.collect(Collectors.toList());

		LinkedHashSet<String> commonWords = new LinkedHashSet<String>();
		int counter = 0;
		for (String word : words) {
			for (String key : keys) {
				if (key.compareTo(word) == 0) {
					commonWords.add(word);
				}
			}
		}
		counter = commonWords.size();

		if (counter > 0) {
			score += counter * (scoreMap.get("frequency")); // Baslikta gecen kelimelerin degeri scoremapten alinir.

		}

		System.out.println("Frequence score: " + counter * (scoreMap.get("frequency")));

	}

	public void addNumberScore(String sentence) {

		int counter = 0;
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(sentence);
		while (m.find()) {
			counter++;
		}
		score += counter * (scoreMap.get("number"));

		System.out.println("Number score: " + counter * (scoreMap.get("number")));
	}

	public void addQuotationMarkScore(String sentence) {

		int counter = 0;
		Pattern p = Pattern.compile("\"(.*?)\"");
		Matcher m = p.matcher(sentence);
		while (m.find()) {
			counter++;
		}
		score += counter * (scoreMap.get("quotationMark"));

		System.out.println("Quotation score: " + counter * (scoreMap.get("quotationMark")));

	}

	public void addEndingMarkScore(String sentence) {
		int counter = 0;
		Pattern p = Pattern.compile("[!?]");
		Matcher m = p.matcher(sentence);
		while (m.find()) {
			counter++;
		}
		score += counter * (scoreMap.get("quotationMark"));

		System.out.println("endingmark score: " + counter * (scoreMap.get("quotationMark")));
	}

	public void addDayMonthScore(String sentence) {
		String[] searchWords = new String[] { "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos",
				"Eylül", "Ekim", "Kasım", "Aralık", "Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma",
				"Cumartesi" };

		List<String> words = sentenceToWords(sentence);
		int counter = 0;
		for (String w : words) {
			for (String sw : searchWords) {
				if (w.compareTo(sw) == 0) {
					counter++;
				}
			}
		}
		score += counter * (scoreMap.get("dayMonth"));

		System.out.println("dayMonth score: " + counter * (scoreMap.get("dayMonth")));
	}

	public void addPositiveScore() {
		String[] searchWords = new String[] { "özetle", "sonuç", "neticede", "sonuçta", "önemlisi", "kısaca" };

		List<String> words = sentenceToWords(sentence);
		int counter = 0;
		for (String w : words) {
			for (String sw : searchWords) {
				if (w.toLowerCase().compareTo(sw) == 0) {
					counter++;
				}
			}
		}
		score += counter * (scoreMap.get("positive"));
		System.out.println("positive score: " + counter * (scoreMap.get("positive")));

	}

	public void addNegativeScore() {
		String[] searchWords = new String[] { "çünkü", "ancak", "öyleyse", "öyle" };

		List<String> words = sentenceToWords(sentence);
		int counter = 0;
		for (String w : words) {
			for (String sw : searchWords) {
				if (w.toLowerCase().compareTo(sw) == 0) {
					counter++;
				}
			}
		}
		score += counter * (scoreMap.get("negative"));
		System.out.println("negative score: " + counter * (scoreMap.get("negative")));

	}

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			jsonText = jsonText.substring(1, jsonText.length() - 1);

			if (jsonText.compareTo("\"error\":\"Sonuç bulunamadı\"") == 0) {
				jsonText = "{" + jsonText + "}";
			}
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public boolean isPersonName(String word) {
		boolean isPersonName = false;
		try {
			File f = new File("src/main/resources/isimler.txt");
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";

			while ((readLine = b.readLine()) != null) {
				Pattern p = Pattern.compile("\'([^\']*)\'");
				Matcher m = p.matcher(readLine);
				while (m.find()) {
					if (m.group(1).toLowerCase().compareTo(word.toLowerCase()) == 0) {
						isPersonName = true;
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isPersonName;
	}

	public boolean isSpecialName(String word) {
		JSONObject json;
		boolean isSpecialName = false;
		try {
			json = readJsonFromUrl("http://sozluk.gov.tr/gts?ara=" + word.toLowerCase());
			if (json.has("ozel_mi")) {
				if (Integer.parseInt(json.get("ozel_mi").toString()) == 1) {
					isSpecialName = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return isSpecialName;
	}

	public boolean isContainFirstWord(String str) {
		boolean isContainFirstWord = false;
		List<String> words = sentenceToWords(sentence);
		if (str.contains(" ")) {
			String[] splitStr = str.split("\\s+");
			if (words.get(0).compareTo(splitStr[0]) == 0) {
				isContainFirstWord = true;
			}
		} else {
			if (words.get(0).compareTo(str) == 0) {
				isContainFirstWord = true;
			}
		}

		return isContainFirstWord;
	}

	public void addUppercaseScore() {
		List<String> words = sentenceToWords(sentence);

		String ozelIsim = "";
		for (int i = 0; i < words.size(); i++) {
			if (Character.isUpperCase(words.get(i).charAt(0))) {
				ozelIsim += " " + words.get(i);
			} else {
				if (ozelIsim.compareTo("") != 0) {
					uppercases.add(ozelIsim.replaceFirst("^ *", ""));
				}

				ozelIsim = "";
			}
		}

		for (String u : uppercases) {

			/*
			 * if (isContainFirstWord(u)==false) { //ozelIsimler.add(u); }
			 */
			if (u.contains(" ")) {
				String[] splitStr = u.split("\\s+");

				for (String string : splitStr) {
					if (isPersonName(string)) {
						ozelIsimler.add(string);
						// kelime zincirinde olduğu yere göre böl ve bölünen parçaları özel isimlere ekle

					}
				}
			} else if (isPersonName(u) == true) {
				ozelIsimler.add(u);
			} else if (isSpecialName(u) == true) {
				ozelIsimler.add(u);
			}

		}

	}

}
