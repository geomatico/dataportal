package co.geomati.netcdf.utm;

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.List;

import co.geomati.netcdf.IcosDomain;
import co.geomati.netcdf.Institution;
import co.geomati.netcdf.TimeUnit;
import co.geomati.netcdf.dataset.Dataset;
import co.geomati.netcdf.dataset.DatasetVariable;
import co.geomati.netcdf.dataset.Trajectory;

public class UTMTrajectoryDataset implements Dataset, Trajectory {

	private MainUTMVariable variable;
	private UTMTimeVariable timeVariable;
	private List<Point2D> trajectoryPoints;

	public UTMTrajectoryDataset(MainUTMVariable variable,
			UTMTimeVariable timeVariable, List<Point2D> trajectoryPoints) {
		this.variable = variable;
		this.timeVariable = timeVariable;
		this.trajectoryPoints = trajectoryPoints;
	}

	@Override
	public IcosDomain getIcosDomain() {
		return IcosDomain.OCEANS;
	}

	@Override
	public Institution getInstitution() {
		return Institution.UTM;
	}

	@Override
	public DatasetVariable getMainVariable() {
		return variable;
	}

	@Override
	public TimeUnit getTimeUnits() {
		return timeVariable.getUnits();
	}

	@Override
	public Date getReferenceDate() {
		return timeVariable.getReferenceDate();
	}

	@Override
	public List<Integer> getTimeStamps() {
		return timeVariable.getTimestamps();
	}

	@Override
	public List<Point2D> getTrajectoryPoints() {
		return trajectoryPoints;
	}
}
