package daikin;

import java.util.HashMap;
import java.util.Map;

public enum DayofWeekState
{
  SUNDAY(0x1), MONDAY(0x2), TUESDAY(0x3), WEDNESDAY(0x4), THURSDAY(0x5), FRIDAY(0x6), SATURDAY(0x7);

  private int state;

  DayofWeekState(int state)
  {
    this.state = state;
  }

  private static Map<Integer, DayofWeekState> map = new HashMap<Integer, DayofWeekState>();

  static
  {
    for (DayofWeekState oneState : DayofWeekState.values())
    {
      map.put(oneState.state, oneState);
    }
  }

  public static DayofWeekState valueOf(int state)
  {
    return map.get(state);
  }
  
  public int getState()
  {
    return state;
  }
}
