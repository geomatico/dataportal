package co.geomati.netcdf;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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
			String lineSeparator = "**************************\n";
			StringBuilder ret = new StringBuilder(lineSeparator).append(index)
					.append(": ");
			ret.append(variableName);
			if (error != null) {
				ret.append("\n").append("CANNOT CONVERT\n")
						.append(getStackTrace(error));
			} else {
				ret.append("->").append("OK");
			}

			return ret.toString();
		}

		public String getStackTrace(Throwable throwable) {
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			throwable.printStackTrace(printWriter);
			return writer.toString();
		}

	}

}
