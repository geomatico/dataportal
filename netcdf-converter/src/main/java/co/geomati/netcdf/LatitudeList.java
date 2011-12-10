package co.geomati.netcdf;

import java.awt.geom.Point2D;
import java.util.AbstractList;
import java.util.List;

public class LatitudeList extends AbstractList<Double> implements List<Double> {

	private List<Point2D> points;

	public LatitudeList(List<Point2D> points) {
		this.points = points;
	}

	@Override
	public Double get(int index) {
		return points.get(index).getY();
	}

	@Override
	public int size() {
		return points.size();
	}
}
