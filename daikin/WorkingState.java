package daikin;

import java.util.HashMap;
import java.util.Map;

public enum WorkingState
{
  OFF(0x0), ON(0x1);

  private int state;

  WorkingState(int state)
  {
    this.state = state;
  }

  private static Map<Integer, WorkingState> map = new HashMap<Integer, WorkingState>();

  static
  {
    for (WorkingState oneState : WorkingState.values())
    {
      map.put(oneState.state, oneState);
    }
  }

  public static WorkingState valueOf(int state)
  {
    return map.get(state);
  }
  
  public int getState()
  {
    return state;
  }
}
