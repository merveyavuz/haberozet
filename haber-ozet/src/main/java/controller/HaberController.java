package controller;

import java.util.Hashtable;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

import com.nlp.haber.ozet.main.TextProcessing;

import model.Haber;
import model.OzetResponse;
import model.Url;
import parser.UrlParser;
import service.HaberService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/news")
public class HaberController {

	@Autowired
	HaberService hs;

	@RequestMapping("/all")
	public Hashtable<String, Haber> getAll() {
		return hs.getAll();
	}

	@PostMapping("/addHaber")
	public OzetResponse processForm(@RequestBody Haber haber) {
		System.out.println("processform çalıştı");
		String title = haber.getBaslik();
		String text = haber.getIcerik();

		TextProcessing tp = new TextProcessing(title, text);
		String summary = tp.getSummary();

		OzetResponse ozet = new OzetResponse();
		ozet.setOzet(summary);

		return ozet;
	}

	@PostMapping("/addLink")
	public OzetResponse processLinkForm(@RequestBody Url u) {
		System.out.println("proseslink çalıştı");
		String url = u.getUrl();
		String brand = u.getBrand();
		System.out.println("url: " + url);
		System.out.println("brand: " + brand);
		UrlParser parser = new UrlParser(url, brand);

		String title = parser.getpTitle();
		String text = parser.getpText();
		System.out.println("Title:" + title);
		System.out.println("Text: " + text);

		TextProcessing tp = new TextProcessing(title, text);
		String summary = tp.getSummary();

		OzetResponse ozet = new OzetResponse();
		ozet.setOzet(summary);

		return ozet;
	}

	  
	@PostMapping("/addFile")
	public OzetResponse uploadFile(@RequestParam("file") MultipartFile file) {

		System.out.println("addFiles'a geldi");
		OzetResponse ozet = new OzetResponse();

		try {

			if (file != null) {
				System.out.println(file.getOriginalFilename());
				String content = new String(file.getBytes());
				System.out.println("İçerik: "+content);
			} else {
				System.out.println("file null");
			}
			return ozet;
		} catch (Exception e) {
			System.out.println("file hatası");
			return ozet;
		}
	}
	


}
