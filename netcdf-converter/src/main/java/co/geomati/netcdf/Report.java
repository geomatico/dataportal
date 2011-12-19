package co.geomati.netcdf;

import java.util.ArrayList;

public class Report {

	private ArrayList<Line> lines = new ArrayList<Line>();
	private int count = 0;
	private Line currentLine;

	public void addRecord() {
		currentLine = new Line(count++);
		lines.add(currentLine);
	}

	public void datasetError(Throwable error) {
		currentLine.error = error;
	}

	public void setDatasetName(String variableName) {
		currentLine.variableName = variableName;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		for (Line line : lines) {
			ret.append(line).append("\n");
		}

		return ret.toString();
	}

	private class Line {
		private int index;
		private Throwable error;
		public String variableName = "NO NAME";

		public Line(int index) {
			this.index = index;
		}

		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder(Integer.toString(index))
					.append(": ");
			ret.append(variableName).append("->");
			if (error != null) {
				ret.append(error.getMessage());
			} else {
				ret.append("OK");
			}

			return ret.toString();
		}

	}

}
