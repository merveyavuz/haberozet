package service;

import java.util.Hashtable;

import org.springframework.stereotype.Service;

import model.Haber;

@Service
public class HaberService {

	Hashtable< String, Haber> news= new Hashtable<String, Haber>();
	
	public HaberService() {
		Haber h= new Haber();
		h.setBaslik("b");
		h.setIcerik("i");
		news.put("b", h);
		Haber h2= new Haber();
		h2.setBaslik("b1");
		h2.setIcerik("i2");
		news.put("b1", h2);
	}
	
	public Haber getHaber(String baslik) {
		if (news.contains(baslik)) {
			return news.get(baslik);
		}else {
			return null;
		}
	}
	public Hashtable<String, Haber> getAll() {
		return news;
	}
}
