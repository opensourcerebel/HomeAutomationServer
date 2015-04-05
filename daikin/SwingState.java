package daikin;

import java.util.HashMap;
import java.util.Map;

public enum SwingState
{
  SWINGON(0xF), SWINGOFF(0x0);

  private int state;

  SwingState(int state)
  {
    this.state = state;
  }

  private static Map<Integer, SwingState> map = new HashMap<Integer, SwingState>();

  static
  {
    for (SwingState oneState : SwingState.values())
    {
      map.put(oneState.state, oneState);
    }
  }

  public static SwingState valueOf(int state)
  {
    return map.get(state);
  }
  
  public int getState()
  {
    return state;
  }
}
