package co.geomati.netcdf;

public enum IcosDomain {
	ATMOSFERA("atmosfera"), ECOSISTEMA("ecosistema"), OCEANICO("oceanico");

	private String text;

	private IcosDomain(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
