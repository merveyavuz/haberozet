package com.nlp.haber.ozet.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;
import zemberek.tokenization.TurkishSentenceExtractor;
import zemberek.tokenization.TurkishTokenizer;

public class TextProcessing {

	public static HashMap<String, Integer> map = new HashMap<String, Integer>();
	public static HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
	static TurkishTokenizer tokenizer = TurkishTokenizer.DEFAULT;

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
			System.out.println("SCORE: " + processing.getSentenceScore());
		}

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

	/*
	 * public static void main(String[] args) { String title =
	 * "Konunun Belirlenmesi "; String text =
	 * "Konu belirleme aş amasının amacı parçadaki en önemli konuların belirlenmesini sağlamaktır. Bunu sağlamak için kelime frekanslarının hesaplanması, cümlenin bulunduğu yerin incelenmesi, ipucu veren ifadelerden yararlanılması gibi teknikler kullanılır. Bazı yazı tiplerinde, yazının başlığı, yazının ilk cümlesi gibi kritik pozisyonlar yazıyla ilgili en önemli konuları barındırabilirler. \"Özetle\", \"En önemlisi\", \"Sonuç olarak\" gibi ipucu veren ifadeler yazıyla ilgili önemli noktaları gösteren işaretler olabilir. Ayrıca çok sıkça kullanılan kelimeler, edat veya belirteç olmadıkları sürece, içinde bulundukları cümlelerin önemli olduklarını gösterebilirler."
	 * ;
	 * 
	 * splitToSentences(text); addAverageLengthScore();
	 * 
	 * System.out.
	 * println("------------------SENTENCE SCORES------------------------------");
	 * setSentenceScores(title, text);
	 * 
	 * map.entrySet().forEach(entry -> { System.out.println("WORD: " +
	 * entry.getKey() + " SCORE: " + entry.getValue()); });
	 * 
	 * }
	 */

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			jsonText = jsonText.substring(1, jsonText.length() - 1);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static void main(String[] args) {
		JSONObject json;
		try {
			json = readJsonFromUrl("http://sozluk.gov.tr/gts?ara=istanbul");
			System.out.println(json.get("ozel_mi"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}