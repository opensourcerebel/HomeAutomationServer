package daikin;

import java.util.HashMap;
import java.util.Map;


public enum FanState
{
  FAN1(0x3), FAN2(0x4), FAN3(0x5), FAN4(0x6), FAN5(0x7), FANSILENT(0xB), FANAUTO(0xA);

  private int state;

  FanState(int state)
  {
    this.state = state;
  }

  private static Map<Integer, FanState> map = new HashMap<Integer, FanState>();

  static
  {
    for (FanState oneState : FanState.values())
    {
      map.put(oneState.state, oneState);
    }
  }

  public static FanState valueOf(int state)
  {
    return map.get(state);
  }
  
  public int getState()
  {
    return state;
  }
}