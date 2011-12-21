package co.geomati.netcdf.ceam;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;

import co.geomati.netcdf.ConverterException;
import co.geomati.netcdf.dataset.GeoreferencedStation;

public class BADMTimeSerieStationDataset extends BADMTimeSerieDataset implements
		GeoreferencedStation {

	private Point2D position;

	public BADMTimeSerieStationDataset(Variable main, Variable timeVariable,
			Point2D position) throws ConverterException {
		super(main, timeVariable);
		this.position = position;
	}

	@Override
	public int getStationCount() {
		return 1;
	}

	@Override
	public List<Point2D> getStationPositions() {
		return Collections.singletonList(position);
	}

}
