package wakeup;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;

public class WakeupServer extends TimerTask
{
  public static void main(String[] args)
  {
    if (args.length == 0)
      args = new String[] { "15", "29", "Europe/Sofia", "30", "direct", "fakeExec" };
    new WakeupServer().go(args, null);
  }

  int wakeupPeriod;
  volatile long wait;

  ScheduledExecutorService exec;

  public void go(String[] args, List<String> info)
  {
    if (exec != null)
    {
      stopWakeup();
    }

    long timeToWait = getTimeToWait(args, info);
    int oneDay = 1000 * 60 * 60 * 24;
    boolean direct = false;
    if (args.length >= 5)
    {
      direct = args[4].equals("direct");
      System.out.println("direct:[" + direct + "]");
    }

    if (direct)
    {
      timeToWait = 0;
    }

    if (args.length >= 6)
    {
      fakeExec = args[5].equals("fakeExec");
      System.out.println("fakeExec:[" + fakeExec + "]");
    }

    if (args.length >= 7)
    {
      lightActive = args[6].equals("L_ON");
      System.out.println("lightActive:[" + lightActive + "]");
    }

    int wakyupInMs = wakeupPeriod * 60 * 1000;
    int lines = 600;
    wait = wakyupInMs / lines;
    System.out.println("wait time is:" + wait);
    exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleAtFixedRate(this, timeToWait * 1000L, oneDay, TimeUnit.MILLISECONDS);
    System.out.println("Wakup procedure started");
  }

  public long getTimeToWait(String[] args, List<String> info)
  {
    int hours = Integer.parseInt(args[0]);
    int minutes = Integer.parseInt(args[1]);
    DateTime currentTime = new DateTime(DateTimeZone.forID(args[2]));
    wakeupPeriod = Integer.parseInt(args[3]);
    // System.out.println("Wakeup perod is:[" + wakeupPeriod + "]");
    TimeCalc timeCalc = new TimeCalc();
    long timeToWait = timeCalc.getWaitTime(currentTime, hours, minutes, wakeupPeriod, info);
    return timeToWait;
  }

  private boolean lightIsOn = false;
  private boolean tvIsOn = false;

  public void run()
  {
    System.out.println("Running");
    DateTime dt = new DateTime();
    System.out.println(dt);
    // int dayOfWeek = dt.getDayOfWeek();
    // if (dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY) { return; }

    try
    {
      List<String> allLines = Files.readAllLines(Paths.get("./colors.txt"), Charset.forName("UTF8"));
      if (lightActive)
      {
        exec("sudo i2cset -y -f 1 0x48 0x05 0xA0 0x03 0 0 0 i");
        lightIsOn = true;
      }
      for (String line : allLines)
      {
        long startTime = System.currentTimeMillis();
        String cmd = "sudo i2cset -y -f 1 0x48 0x05 0xA0 0x03 " + line + " i";
        if (lightActive)
        {
          int res = exec(cmd);
          if (res > 0)
          {
            exec(cmd);
          }
        }
        else
        {
          System.out.println("Will not execute light");
        }
        long duration = System.currentTimeMillis() - startTime;
        long waitTime = wait - duration;
        if (waitTime > 0)
        {
          sleepLong(waitTime);
        }
      }
      tvIsOn = true;
      exec("/home/xbian/sh/wakeup.sh");
      DateTime dtFinish = new DateTime();
      System.out.println("Finished on " + dtFinish + " for " + Minutes.minutesBetween(dt, dtFinish).getMinutes());
    }
    catch (Exception e)
    {
      System.out.println("Task aborted");
      e.printStackTrace();
    }
  }

  private void sleepLong(long i) throws InterruptedException
  {
    Thread.sleep(i);
  }

  private void sleep(int i) throws InterruptedException
  {
    Thread.sleep(i * 1000);
  }

  volatile boolean fakeExec;
  volatile boolean lightActive = true;

  private int exec(String cmd)
  {
    System.out.println("Executing:[" + cmd + "]");
    if (fakeExec) { return 0; }

    Process p;
    try
    {
      p = Runtime.getRuntime().exec(cmd);
      int exitVal = p.waitFor();
      System.out.println("Process exited:[" + exitVal + "]");
      return exitVal;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return -1;
  }

  private void exec(String[] cmd)
  {
    System.out.print("Executing:[");
    for (String s : cmd)
    {
      System.out.print(s + " ");
    }
    System.out.println("");
    Process p;
    try
    {
      p = Runtime.getRuntime().exec(cmd);
      int exitVal = p.waitFor();
      System.out.println("Process exited:[" + exitVal + "]");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void stopWakeup()
  {
    System.out.println("Wakup procedure halted");
    if (exec != null)
    {
      exec.shutdownNow();
      exec = null;
      if(lightIsOn)
      {
        exec("/home/xbian/sh/turnOffLight.sh");
      }
      
      if(tvIsOn)
      {
        exec("/home/xbian/sh/turnOffTV.sh");
      }
    }
  }
}
