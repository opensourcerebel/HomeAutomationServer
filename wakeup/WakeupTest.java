package wakeup;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

public class WakeupTest
{
  @Test
  public void testFutureAfter()
  {
    DateTime dt = new DateTime().withHourOfDay(7).withMinuteOfHour(30).withSecondOfMinute(0);
    long waitTime = new TimeCalc().getWaitTime(dt, 17, 30, 20, null);
    long expected = 36000;
    assertEquals(expected, waitTime);
  }

  @Test
  public void testFutureBefore()
  {
    DateTime dt = new DateTime().withHourOfDay(17).withMinuteOfHour(30).withSecondOfMinute(0);
    long waitTime = new TimeCalc().getWaitTime(dt, 7, 30, 20, null);
    long expected = 50400;
    assertEquals(expected, waitTime);
  }
  
}
