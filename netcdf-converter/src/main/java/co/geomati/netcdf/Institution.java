package co.geomati.netcdf;

public enum Institution {
	CEAM("Centro de Estudios Ambientales del Mediterráneo (CEAM)",
			"http://www.ceam.es/"), //
	AEMET("Centro de Investigación Atmosférica de Izaña, AEMET",
			"http://www.izana.org/"), //
	IC3("Institut Català de Ciències del Clima (IC3)", "http://www.ic3.cat/"), //
	UTM("Unidad de Tecnología Marina (UTM), CSIC", "http://www.utm.csic.es/");

	private String name;
	private String url;

	private Institution(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

}
