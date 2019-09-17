package model;

public class Haber {

	String baslik;
	String icerik;
	int ozetYuzdesi;

	public int getOzetYuzdesi() {
		return ozetYuzdesi;
	}

	public void setOzetYuzdesi(int ozetYuzdesi) {
		this.ozetYuzdesi = ozetYuzdesi;
	}

	public String getBaslik() {
		return baslik;
	}

	public void setBaslik(String baslik) {
		this.baslik = baslik;
	}

	public String getIcerik() {
		return icerik;
	}

	public void setIcerik(String icerik) {
		this.icerik = icerik;
	}
}
