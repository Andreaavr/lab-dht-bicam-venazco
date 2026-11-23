package problem.definition;

public abstract class Codification {

	public abstract boolean validState(State state);
	public abstract Object getVariableAleatoryValue(int key);
	public abstract int getAleatoryKey ();
	public abstract int getVariableCount();

}