package daikin;

import java.util.HashMap;
import java.util.Map;

public enum ModeState
{
  AUTO(0x0), DRY(0x2), COOL(0x3), HEAT(0x4), FAN(0x6);

  private int state;

  ModeState(int state)
  {
    this.state = state;
  }

  private static Map<Integer, ModeState> map = new HashMap<Integer, ModeState>();

  static
  {
    for (ModeState oneState : ModeState.values())
    {
      map.put(oneState.state, oneState);
    }
  }

  public static ModeState valueOf(int state)
  {
    return map.get(state);
  }
  
  public int getState()
  {
    return state;
  }
}
