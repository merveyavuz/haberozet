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
	public String brand;

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

	public UrlParser(String url, String brand) {
		this.url = url;
		this.brand = brand;
		parse();
	}

	public void parse() {
		String div = "";
		String divElement = "";
		String parseTag = "";
		if (brand.compareTo("hürriyet") == 0) {
			div = "div.rhd-all-article-detail strong";
			divElement = ".rhd-all-article-detail";
			parseTag = "p";
		} else if (brand.compareTo("milliyet") == 0) {
			div = "div.nd-content-column";
			divElement = ".nd-content-column";
			parseTag = "p";
		} else if (brand.compareTo("sabah") == 0) {
			div = "div.detay";
			divElement = ".detay";
			parseTag = "p";
		} else if (brand.compareTo("posta") == 0) {
			div = "div.news-detail__body__content";
			divElement = ".news-detail__body__content";
			parseTag = "p";
		}

		List<String> subTitles = new ArrayList<String>();
		Document document;
		try {
			document = Jsoup.connect(url).get();
			Elements eles = document.select(div);
			for (Element e : eles) {
				subTitles.add(e.text());
				System.out.println(e.text());
			}
			pTitle = document.title();

			Elements html = document.select(divElement);
			Document doc = Jsoup.parse(html.toString());
			Elements link = doc.select(parseTag);

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
