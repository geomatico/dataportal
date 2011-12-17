package co.geomati.netcdf;

public enum CDMDataType {
	GRID("grid"), IMAGE("image"), STATION("station"), TRAJECTORY("trajectory");
	private String text;

	private CDMDataType(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
