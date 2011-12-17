package co.geomati.netcdf;

import java.awt.geom.Point2D;
import java.util.AbstractList;
import java.util.List;

public class LongitudeList extends AbstractList<Double> implements List<Double> {

	private List<Point2D> points;

	public LongitudeList(List<Point2D> points) {
		this.points = points;
	}

	@Override
	public Double get(int index) {
		return points.get(index).getX();
	}

	@Override
	public int size() {
		return points.size();
	}
}
