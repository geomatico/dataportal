package co.geomati.netcdf.ceam;

import java.util.ArrayList;
import java.util.Iterator;

public class VariableGroup implements Iterable<Variable> {

	private ArrayList<Variable> variables = new ArrayList<Variable>();

	public boolean isEmpty() {
		return variables.isEmpty();
	}

	public void add(Variable var) {
		variables.add(var);
	}

	public Variable get(int i) {
		return variables.get(i);
	}

	public int size() {
		return variables.size();
	}

	@Override
	public Iterator<Variable> iterator() {
		return variables.iterator();
	}

}
