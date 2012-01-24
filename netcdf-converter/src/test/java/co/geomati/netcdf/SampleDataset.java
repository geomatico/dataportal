package co.geomati.netcdf;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.geomati.netcdf.dataset.Dataset;
import co.geomati.netcdf.dataset.DatasetDoubleVariable;
import co.geomati.netcdf.dataset.DatasetVariable;
import co.geomati.netcdf.dataset.GeoreferencedStation;
import co.geomati.netcdf.dataset.TimeSerie;

public class SampleDataset implements Dataset, GeoreferencedStation, TimeSerie {

	@Override
	public DatasetVariable[] getMainVariables() {
		return new DatasetVariable[] { new DatasetDoubleVariable() {

			@Override
			public String getUnits() {
				return "grados";
			}

			@Override
			public String getStandardName() {
				return "air_temperature";
			}

			@Override
			public String getName() {
				return "temperatura";
			}

			@Override
			public String getLongName() {
				return "Air temperature at see level";
			}

			@Override
			public Number getFillValue() {
				return -9999;
			}

			@Override
			public List<Double> getData() {
				ArrayList<Double> ret = new ArrayList<Double>();
				for (int i = 0; i < getTimeStamps().size(); i++) {
					for (int j = 0; j < getStationPositions().size(); j++) {
						ret.add(30d + i);
					}
				}

				return ret;
			}
		} };
	}

	@Override
	public String getName() {
		return "test_dataset";
	}

	@Override
	public TimeUnit getTimeUnits() {
		return TimeUnit.SECOND;
	}

	@Override
	public Date getReferenceDate() {
		return new Date();
	}

	@Override
	public IcosDomain getIcosDomain() {
		return IcosDomain.OCEANS;
	}

	@Override
	public Institution getInstitution() {
		return Institution.IC3;
	}

	@Override
	public int getStationCount() {
		return getStationPositions().size();
	}

	@Override
	public List<Point2D> getStationPositions() {
		ArrayList<Point2D> ret = new ArrayList<Point2D>();
		ret.add(new Point2D.Double(10, 10));
		ret.add(new Point2D.Double(20, 20));
		ret.add(new Point2D.Double(30, 30));
		return ret;
	}

	@Override
	public List<Integer> getTimeStamps() {
		List<Integer> ret = new ArrayList<Integer>();
		ret.add(10);
		ret.add(20);
		ret.add(30);
		ret.add(40);
		ret.add(50);
		ret.add(60);
		return ret;
	}

}