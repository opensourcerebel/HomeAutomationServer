package wakeup;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

public class TimeCalc
{
  public long getWaitTime(DateTime dt, int hour, int minute, int wakeup, List<String> info)
  {
//    System.out.println("Time now is1:[" + dt + "]");
    DateTime futureDateTime = dt.withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(0).minusMinutes(wakeup);
//    System.out.println("Time now is2:[" + futureDateTime + "] adjusted");
    int seconds = Seconds.secondsBetween(dt, futureDateTime).getSeconds();
    if (futureDateTime.isAfter(dt))
    {
//      System.out.println("Future is after:" + seconds + " " + Minutes.minutesBetween(dt, futureDateTime).getMinutes() + " " + Days.daysBetween(dt, futureDateTime).getDays());
    }
    else
    {
//      System.out.println("Future is before:" + seconds);
      futureDateTime = futureDateTime.plusDays(1);
//      System.out.println("Time now is3:[" + futureDateTime + "]");
      seconds = Seconds.secondsBetween(dt, futureDateTime).getSeconds();
//      System.out.println("Future is after:" + seconds);
//      System.out.println("Future is after:" + seconds + " " + Minutes.minutesBetween(dt, futureDateTime) + " " + Days.daysBetween(dt, futureDateTime));
    }
    
    int days = Days.daysBetween(dt, futureDateTime).getDays();
    int minutes = Minutes.minutesBetween(dt, futureDateTime).getMinutes();
    int hours = minutes/60;
    int remainingMin = minutes - hours*60;
    int remainingSec = seconds - (remainingMin*60 + hours*60*60 + days*24*60*60);
    String futureInfo = "Future is after: " + days + " days " + hours + " hours " + remainingMin + " minutes " + remainingSec + " seconds, time now is: " + dt;
    if(info != null)
    {
      info.add(futureInfo);
    }
    System.out.println(futureInfo);
    return seconds;
  }

}
