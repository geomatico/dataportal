package co.geomati.netcdf;

public enum IcosDomain {
	ATMOSPHERE("atmosphere"), ENVIRONMENT("environment"), OCEANS("oceans");

	private String text;

	private IcosDomain(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
