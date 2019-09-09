package parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UrlParser {
	public String url;
	public String pTitle;
	public String pText;

	public String getpTitle() {
		return pTitle;
	}

	public void setpTitle(String pTitle) {
		this.pTitle = pTitle;
	}

	public String getpText() {
		return pText;
	}

	public void setpText(String pText) {
		this.pText = pText;
	}

	public UrlParser(String url) {
		this.url = url;
		parse();
	}

	public void parse() {
		List<String> subTitles = new ArrayList<String>();

		Document document;
		try {
			document = Jsoup
					.connect(url)
					.get();
			Elements eles = document.select("div.rhd-all-article-detail strong");
			for (Element e : eles) {
				subTitles.add(e.text());
				System.out.println(e.text());
			}
			pTitle = document.title(); // Get title

			Elements html = document.select(".rhd-all-article-detail"); // Get price
			Document doc = Jsoup.parse(html.toString());
			Elements link = doc.select("p");

			String text = "";

			for (int i = 0; i < link.size(); i++) {
				if (link.get(i).text().compareTo("") != 0) {
					String line = link.get(i).text();
					for (String st : subTitles) {
						if (link.get(i).text().contains(st)) {
							line = line.replace(st, "");
						}
					}
					text += line + "\n\r";

				}

				pText = text;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
