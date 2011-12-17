package co.geomati.netcdf;

public enum IcosDomain {
	ATMOSPHERE("atmosphere"), ECOSYSTEM("ecosystem"), OCEANIC("oceanic");

	private String text;

	private IcosDomain(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
