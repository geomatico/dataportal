package co.geomati.netcdf;

public enum TimeUnit {
	SECOND("second"), MINUTE("minute"), HOUR("hour"), DAYS("days"), COMMON_YEAR(
			"common_year");

	private String text;

	private TimeUnit(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
