package controller;

import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import model.Haber;
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

	@RequestMapping("{baslik}")
	public Haber getHaber(@PathVariable("baslik") String baslik) {
		return hs.getHaber(baslik);
	}

	/*
	 * @GetMapping("/addHaber") public String sendForm(Haber haber) {
	 * 
	 * return "addHaber"; }
	 */
	@PostMapping("/addHaber")
	public Haber processForm(@RequestBody Haber haber) {
		return haber;
	}

}
