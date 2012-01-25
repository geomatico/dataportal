package co.geomati.netcdf.utm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import co.geomati.netcdf.TimeUnit;

public class UTMTimeVariable implements UTMVariable {

	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.S");

	private TimeUnit units;
	private Date referenceDate;
	private ArrayList<Integer> timestamps = new ArrayList<Integer>();

	public UTMTimeVariable(TimeUnit units, Date referenceDate) {
		super();
		this.units = units;
		this.referenceDate = referenceDate;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public TimeUnit getUnits() {
		return units;
	}

	public ArrayList<Integer> getTimestamps() {
		return timestamps;
	}

	@Override
	public void addSample(String sample) {
		try {
			timestamps.add((int) (df.parse(sample).getTime() / 1000));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
