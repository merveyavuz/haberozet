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

	public  HashMap<String, Integer> map;
	public  HashMap<String, Integer> scoreMap ;
	public  TurkishTokenizer tokenizer ;
	public  static HashMap<String, Integer> frequencyMap ;
	

	public  List<String> summarySentences;
	public  List<String> sortedSummarySentences ;
	public String title;
	public String text;
	public String summary;
	public  String[] paragraphs;
	public  int lineNo = 0;

	public TextProcessing() {}
	public TextProcessing(String title, String text) {
		this.title = title;
		this.text = text;
		this.summary="";
		this.map= new HashMap<String, Integer>();
		this.scoreMap= new HashMap<String, Integer>();
		this.tokenizer= TurkishTokenizer.DEFAULT;
		this.frequencyMap= new HashMap<String, Integer>();
		this.summarySentences = new ArrayList<String>();
		this.sortedSummarySentences= new ArrayList<String>();
	
		setScoreMap();
		splitToParagraphs(text);
		setWordFrequencyMap(text);
		setSentenceScores(title, text);
		setSummarySentences(50);
		setSummary();
	}

	public  void setScoreMap() {
		scoreMap.put("frequency", 10);
		scoreMap.put("entry", 20);
		scoreMap.put("result", 2);
		scoreMap.put("averageLength", 10);
	}

	public  void splitToParagraphs(String text) {
		String patternStr = "(?<=(\r\n|\r|\n))([ \\t]*$)+";
		paragraphs = Pattern.compile(patternStr, Pattern.MULTILINE).split(text);
	}

	public  void splitToSentences(String input) { // title ve input map e yerlestirilir
		// map.put(title, 0);
		TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
		List<String> sentences = extractor.fromParagraph(input);
		for (String sentence : sentences) {
			lineNo++;
			map.put(lineNo + "#" + sentence, 0);
		}
	}

	public  int getParagraphScore(String sentence) {
		int paragraphScore = 0;

		if (paragraphs[0].contains(sentence)) {
			paragraphScore = scoreMap.get("entry");
			System.out.println("Paragraph Score: " + paragraphScore);
		} else if (paragraphs[paragraphs.length - 1].contains(sentence)) {
			paragraphScore = scoreMap.get("result");
			System.out.println("Paragraph Score: " + paragraphScore);
		}

		return paragraphScore;

	}

	public  int getAverageLengthScore(String sentence) {
		int alScore = 0;
		int counter = map.size() - 2;
		int sum = 0;

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			String sent = entry.getKey().split("#")[1];
			sum = sum + sent.length();
		}
		int avg = sum / counter;

		if (sentence.length() == avg - 1 || sentence.length() == avg + 1) {
			alScore = scoreMap.get("averageLength");
			System.out.println("averageLegnth Score: " + scoreMap.get("averageLength"));
		}
		return alScore;
	}

	public  void setSentenceScores(String title, String text) {
		splitToSentences(text);

		for (Entry<String, Integer> entry : map.entrySet()) {
			System.out.println("----------------------------------------------------------------------------------");
			String sent = entry.getKey().split("#")[1];
			SentenceProcessing processing = new SentenceProcessing(title, sent, text);
			int s = getParagraphScore(sent) + processing.getSentenceScore() + getAverageLengthScore(sent);
			map.put(entry.getKey(), s);
			System.out.println("----------------------------------------------------------------------------------");
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

	public  void setWordFrequencyMap(String text) {
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

	public  void setSummarySentences(int percent) {
		int summary = map.keySet().size() - ((map.keySet().size() * percent) / 100);

		map = (HashMap<String, Integer>) sortMapByValueDesc(map);
		System.out.println("SORTED MAP: ");
		map.entrySet().forEach(entry -> {
			System.out.println(entry.getKey());
			summarySentences.add(entry.getKey());
		});

		for (int i = 0; i < summary; i++) {
			sortedSummarySentences.add(summarySentences.get(i));
		}

		Collections.sort(sortedSummarySentences, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return extractInt(o1) - extractInt(o2);
			}

			int extractInt(String s) {
				String num = s.split("#")[0];
				return num.isEmpty() ? 0 : Integer.parseInt(num);
			}
		});

	}

	
	public void setSummary() {
		for (String ss : sortedSummarySentences) {
			ss = ss.split("#")[1];
			summary+=" "+ss;
		}
		summary= summary.trim();
	}
	
	public String getSummary() {
		return summary;
	}
	
	public static void main(String[] args) {
	//	String title = "Haber Yazısı Ve Özellikleri";

	/*	String text = "Bir olay ya da olgu üzerine edinilen bilgiye haber denir. Bu bilginin gazete, dergi gibi yayın organlarıyla ya da radyo, televizyon gibi iletişim araçlarıyla topluma sunulmak üzere yazılı metin hâline getirilmesine de haber yazısı adı verilir.\r\n"
				+ "\r\n"
				+ "Tarih boyunca insanlar iletişim ihtiyacını karşılamak için çeşitli yollar aramış bu amaçla kil tabletlere, ceylan derilerine ve kayalara yazılarını yazmışlardır. Matbaanın icadıyla yazın hayatında önemli değişiklikler olmuş zamanla gazetecilik ortaya çıkmıştır. Gazeteler iletişim araçlarının yaygın olmadığı dönemlerde yegâne iletişim aracı olarak kullanılmıştır.\r\n"
				+ "\r\n"
				+ "Kitle iletişim araçlarının gelişmesi ile birlikte haber ve habere ulaşma yolları değişmiştir. Günümüzde insanlar gazete, TV ve İnternet ile iletişim ihtiyaçlarını karşılamaktadırlar. Habere ulaşmanın çok kolay olduğu günümüzde habere hızlı ve güvenilir kaynaklardan ulaşabilmek daha da önemli hâle gelmiştir. Habere sahip olan birey ve toplumlar, yaşamın değişen ve gelişen koşullarını hızla yorumlayıp uyum sağlayabilmekte ve böylelikle siyasi, ekonomik, sosyal ve kültürel alanlarda avantajlı konuma gelebilmektedir.\r\n"
				+ "\r\n"
				+ "Haber, kaynağını yaşamdan alır ve özellikle çok hızlı değişimlerin olduğu günümüz dünyasında habersiz kalmak, adeta yaşamın dışında kalmak anlamına gelmektedir. Günümüzde sayıları her geçen gün artan yazılı, görsel ve işitsel yayın organları, topluma haber iletmekte ve insanları yaşananlardan haberdar etmektedir.";
*/
	/*	String title="Yangın Nedeniyle Binada Mahsur Kaldı";
		String text="Şişli’de, 7 katlı boş bir binanın en son katında henüz belirlenemeyen bir nedenle yangın çıktı. Hurda toplayan bir kişi 6. katta mahsur kalınca, itfaiye ekipleri tarafından merdiven yardımıyla kurtarıldı.\r\n" + 
				"\r\n" + 
				"Yangın, saat 17.30 sıralarında Halaskargazi Caddesi 196 numaradaki binada meydana geldi. "
				+ "Bir süre önce boşaltıldığı öğrenilen binanın son katında henüz belirlenemeyen bir nedenle yangın çıktı. "
				+ "O sırada hurda topladığı iddia edilen bir kişi duman yüzünden binada mahsur kaldı. "
				+ "Binadaki dumanı gören çevredeki vatandaşlar durumu itfaiye ekiplerine bildirdi. "
				+ "Olay yerine gelen itfaiye ekipleri bir yandan yangına müdahale ederken, bir yandan da merdiven yardımıyla 6. katta mahsur kalan kişiyi kurtardı. "
				+ "Hayati tehlikesi bulunmadığı belirtilen bu kişi Şişli Hamidiye Etfal Eğitim ve Araştırma Hastanesi’ne kaldırıldı.";
		TextProcessing tp = new TextProcessing(title, text);
		map.entrySet().forEach(entry -> {
			System.out.println("SENTENCE: " + entry.getKey() + " SCORE: " + entry.getValue());
		});
		System.out.println();
		setSummarySentences(50);

		System.out.println();
		System.out.println("Sıralanmış özet cümleleri: ");
		for (String ss : sortedSummarySentences) {
			System.out.println(ss);
		}
		
	*/
		

	}

}