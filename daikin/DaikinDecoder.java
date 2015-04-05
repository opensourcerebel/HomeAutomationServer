package daikin;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DaikinDecoder
{
  public static final int IR_SEPARATOR_PULSE = 430;
  public static final int IR_FRAME_SEPARATOR_PULSE = 3400;
  public static final int IR_FRAME_SEPARATOR_SPACE= 1750;
  public static final int IR_ONE = 1320;
  public static final int IR_ZERO = 450;
  public static final int IR_PACKAGE_START = 25000;
  public static final int IR_FRAME_START = 35000;
  public static final String LIRC_SPACE = "     ";

  public static void main(String[] args)
  {
    new DaikinDecoder().decodeSignal();
  }

  private void decodeSignal()
  {
    //horizontal swing byte 9 bits 0 4 (1111 on 0000 off), byte 10 the same for vertical swing
//    DaikinPackage dp = analyze3("C:\\Users\\I036200\\Downloads\\ir\\20C_0", 1);
//    analyze3("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_H_swing_V_ON_FRI_0820", 1);
//    analyze3("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_H_swing_H_ON_FRI_0820", 1);
//    analyze3("C:/Users/I036200/Downloads/ir/ir2/on_25_heat_silent_hon_voff_sun_0000", 1);
    analyze3("C:/Users/I036200/Downloads/ir/ir2/on_21_auto_silent_hon_voff_sat_0000", 1);
    analyze3("C:/Users/I036200/Downloads/ir/ir2/on_21_dry_auto_hon_voff_sat_0000", 1);
    analyze3("C:/Users/I036200/Downloads/ir/ir2/on_21_cool_auto_hon_voff_sat_0000", 1);//SILENT!
    analyze3("C:/Users/I036200/Downloads/ir/ir2/on_21_heat_silent_hon_voff_sat_0000", 1);
    analyze3("C:/Users/I036200/Downloads/ir/ir2/on_21_fan_silent_hon_voff_sat_0000", 1);
    DaikinPackage dp = analyze3("C:/Users/I036200/Downloads/ir/ir2/on_21_auto_silent_hon_voff_sat_0000", 1);
    System.out.println(getStringFromArr(dp.frameOne));
    System.out.println(getStringFromArr(dp.frameTwo));
    System.out.println(getStringFromArr(dp.frameThree));
    System.out.println(dp.getDayOfWeek());
    System.out.println(dp.getHourOfDay());
    System.out.println(dp.getFanState());
    System.out.println(dp.getMode());
    System.out.println(dp.getWorkingState());
    System.out.println(dp.getDegrees());
    System.out.println(dp.getHorizontalSwingState());
    System.out.println(dp.getVerticalSwingState());
    System.out.println(dp.getLircConfFileFirst());
    // analyze("C:\\Users\\I036200\\Downloads\\20C_Quiet_OFF");
    // analyze("C:\\Users\\I036200\\Downloads\\20C_Quiet_OFF1");
    // analyze("C:\\Users\\I036200\\Downloads\\20C_Quiet_OFF2");
    // analyze("C:\\Users\\I036200\\Downloads\\20C_Quiet_ON");
    // analyze("C:\\Users\\I036200\\Downloads\\20C_Quiet_ON1");
    // analyze("C:\\Users\\I036200\\Downloads\\20C_Quiet_ON2");
     //DaikinPackage dp = analyze3("C:\\Users\\I036200\\Downloads\\ir\\20C_0", 1);
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\20C_1", 1);
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\20C_2", 1);
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\20C_3", 1);
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\20C_4", 1);
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\20C_5a", 1);
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\20C_6s", 1);
    // for (int i = 0; i <= 6; i++)
    // {
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\20C_1_" + i);
    // }
    // for (int i = 1; i <= 3; i++)
    // {
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\2" + i + "C");
    // }
    // for (int i = 1; i <= 3; i++)
    // {
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\2" + i + "C_1");
    // }
    //
    // for (int i = 1; i <= 3; i++)
    // {
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\2" + i + "C_2");
    // }
    //
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23C_3");
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23C_4");
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23C_5");
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\23X_1", 1);//14 32
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\23X_2", 1);//14 32
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\23X_3", 1);//14 33
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\23X_4", 1);//14 55
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\23X_5", 1);//14 58
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\23X_6", 1);//14 59
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_1500");//1500 - 132
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_1510");//1510 - 142
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_1608");//1608 - 200
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_1844");//1844 -
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_1845");//1845 -
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_SUN_0929");//1845 -
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_SUN_0928");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_MON_0928");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_MON_0928-2");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_TUE_0928");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_WED_0928");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_THU_0928");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_THU_0900");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_THU_0901");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_THU_0902");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_THU_0903");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_THU_0904");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_THU_0930-2");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_THU_1030-2");//!!!
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\23X_7_THU_1130-2");//!!!
    // frame 2, byte 6 7 - time, first 11 bits time in min from day start, last 4 bits day of week, SUN in 000
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\_21_ON_H_Silent_FRI_0820");
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_H_Silent_FRI_0820");
    // frame 3 byte 6 bit 1 - on 1, off 0
//     analyze3("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_AUTO_FRI_0820", 1);
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_HUMI_FRI_0820");
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_HUMI_FRI_0820-2");
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_Cool_Silent_FRI_0820");
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_H_Silent_FRI_0820");
    // analyze("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_Fan_FRI_0820");
//    analyze3("C:\\Users\\I036200\\Downloads\\ir\\workingDaiking.txt", 0);
//    analyze3("C:\\Users\\I036200\\Downloads\\ir\\workingDaiking1.txt", 0);
//   analyze3("C:\\Users\\I036200\\Downloads\\ir\\_21_OFF_FAN", 1);
//   analyze3("C:\\Users\\I036200\\Downloads\\ir\\_21_ON_FAN", 1);
    // frame 4 byte 7 is temp * 2
  }

  private String getStringFromArr(int[] array)
  {
    StringBuilder sb = new StringBuilder();
    for(int oneInt : array)
    {
      sb.append(String.format("%02x", oneInt).toUpperCase());
      sb.append(", ");
    }
    return sb.toString();
  }

  private DaikinPackage analyze3(String path, int startPosition)
  {
    DaikinPackage toRet = new DaikinPackage();
    Path path2 = FileSystems.getDefault().getPath(path);
    System.out.println("reading:["  + path2 + "]");
    try
    {
      List<String> allTimes = getTimes(path2, startPosition);

      int counter = 1;
      int pulseSUM = 0;
      int pulseCount = 0;
      int spaceSUM = 0;
      int spaceCount = 0;
      int spaceSUM1 = 0;
      int spaceCount1 = 0;
      int counterBytes = 0;
      int counterBITS = 0;
      int checkSUM = 0;
      String accumulator = "";
      StringBuilder sb = new StringBuilder();
      StringBuilder sbBinary = new StringBuilder();
      boolean normalize = true;
      int lastCmd = 0;
      List<Integer> normalizedData = new ArrayList<Integer>();
      boolean printData = false;
      int frameCounter = 0;
      for (int i = 0; i < allTimes.size(); i++)
      {
        String oneTime = allTimes.get(i);

        String status = "pulse ";
        int time = Integer.parseInt(oneTime);
        int time0 = time;
        if (counter % 2 == 0)
        {
          status = "space ";
          if (time < 500)
          {
            spaceSUM += time;
            spaceCount++;
            if (normalize)
            {
              time = IR_ZERO;
            }
          }
          else if (time < 1400)
          {
            spaceSUM1 += time;
            spaceCount1++;
            if (normalize)
            {
              time = IR_ONE;
            }
          }
          else if (time < 1800)
          {
            if (normalize)
              time = IR_FRAME_SEPARATOR_SPACE;
          }
          else if (time > 34300 && time < 35500)
          {
            if (normalize)
            {
              time = IR_FRAME_START;
            }
          }
          else if (time < 26000 && time > 24000)
          {
            if (normalize)
            {
              time = IR_PACKAGE_START;
            }
          }
        }

        if (counter % 2 != 0)
        {
          if (time < 800)
          {
            pulseSUM = pulseSUM + time;
            pulseCount++;
            if (normalize)
              time = IR_SEPARATOR_PULSE;
          }
          else if (time > 3300 && time < 3600)
          {
            if (normalize)
              time = IR_FRAME_SEPARATOR_PULSE;
          }
        }

        // if (counter % 2 != 0)
        if(printData)
        {
          String st = status + zeroFilled(time0) + " avg: " + zeroFilled(time) + " delta:" + zeroFilled(Math.abs(time - time0));
          pushS(st, sb);
          // System.out.println(st);
        }

        if (counter % 2 == 0)
        {
          // int counterOdd = counter / 2;
          if (time == IR_ZERO || time == IR_ONE)
          {
            String bitValue = "0";
            if (time == IR_ONE)
            {
              bitValue = "1";
            }
            // pushS(counterBytes + " 0", sbBinary);
            counterBytes++;
            accumulator = accumulator + bitValue;
            counterBITS++;

            if (counterBITS == 8)
            {
              // int cmd = getCMDByte(accumulator);
              // pushS("0x" + Integer.toHexString(cmd), sbBinary);
              int no = counterBytes / 8;
              int cmd = printByte(no, accumulator, sbBinary);
              counterBITS = 0;
              checkSUM += cmd;
              accumulator = "";
              lastCmd = cmd;
              int[] currentFrame = null;
              if(frameCounter == 1)
              {
                currentFrame = toRet.frameOne;
              }
              else if (frameCounter == 2)
              {
                currentFrame = toRet.frameTwo;
              }
              else if (frameCounter == 3)
              {
                currentFrame = toRet.frameThree;
              }
              currentFrame[no-1] = cmd;
            }
          }
          else
          {
            if (time != IR_FRAME_SEPARATOR_SPACE)
            {
              String reverse = reverse(accumulator);
              String hexString = "";
              if (!"".equals(reverse))
              {
                hexString = Integer.toHexString(Integer.parseInt(reverse));
              }
              int realCheckSUM = checkSUM - lastCmd;
              String sumHex = Integer.toHexString(realCheckSUM).toUpperCase();
              String sumHexByte = Integer.toHexString(realCheckSUM & 0xff).toUpperCase();
              pushS("check SUM: 0x" + sumHex + ", byte: 0x" + sumHexByte, sbBinary);
              pushS("-------space:" + time + ", bits:" + reverse + " " + hexString, sbBinary);
              frameCounter ++;
            }
            else
            {
              pushS("-------space:" + time, sbBinary);
            }
            counterBytes = 0;
            counterBITS = 0;
            accumulator = "";
            checkSUM = 0;
          }

        }
        counter++;
        normalizedData.add(time);
      }
      int realCheckSUM = checkSUM - lastCmd;
      String sumHex = Integer.toHexString(realCheckSUM).toUpperCase();
      String sumHexByte = Integer.toHexString(realCheckSUM & 0xff).toUpperCase();
      pushS("check SUM: 0x" + sumHex + ", byte: 0x" + sumHexByte, sbBinary);
      printAvg(sb, "time average pulse 500:" + (pulseSUM / pulseCount));
      printAvg(sb, "time average space 300:" + (spaceSUM / spaceCount));
      printAvg(sb, "time average space 1200:" + (spaceSUM1 / spaceCount1));

      int printLimiter = 0;
      for(int i = 0; i < normalizedData.size(); i ++)
      {
        pushS1(normalizedData.get(i) + "     ", sbBinary);
        printLimiter++;
        if(printLimiter == 6)
        {
          printLimiter = 0;
          pushS("", sbBinary);
        }
      }
//      pushS("", sbBinary);
//      for(String s : allTimes)
//      {
//        pushS1(s + "     ", sbBinary);
//      }
      pushS(sbBinary.toString(), sb);
      String string = path2.getParent().toString();
      String fileName = string + File.separator + path2.getFileName() + ".txt";
      File file = new File(fileName);
      if (!file.exists())
      {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(fileName);
      fw.write(sb.toString());
      fw.flush();
      fw.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return toRet;
  }

  public String zeroFilled(int time)
  {
    return String.format("%05d", time);
  }

  private List<String> getTimes(Path path2, int startPosition) throws IOException
  {
    List<String> allLines = Files.readAllLines(path2, Charset.forName("UTF8"));
//    System.out.println("all all lines:" + allLines.size());
    List<String> times = new ArrayList<String>();
//    int dataCounter = 0;
    for (int i = startPosition; i < allLines.size(); i++)
    {
      
      String line = allLines.get(i);
      StringTokenizer tok = new StringTokenizer(line, " ");
      while (tok.hasMoreTokens())
      {
//        dataCounter++;
        times.add(tok.nextToken());
      }
    }
//    System.out.println("datac:" + dataCounter);
    return times;
  }

  private int printByte(int no, String accumulator, StringBuilder sbBinary)
  {
    String reverse = reverse(accumulator);
    int toRet = Integer.parseInt(reverse, 2);
    // String hexString = Integer.toHexString(toRet);
    String hexString = String.format("%02x", toRet);
    String intSt = String.format("%03d", toRet);
    String noSt = String.format("%02d", no);
    pushS(noSt + " " + reverse + " 0x" + hexString.toUpperCase() + " " + intSt, sbBinary);
    return toRet;
  }

  private String reverse(String accumulator)
  {
    return new StringBuilder(accumulator).reverse().toString();
  }

  private void printAvg(StringBuilder sb, String st)
  {
//    System.out.println(st);
    pushS(st, sb);
  }

  private void pushS(String st, StringBuilder sb)
  {
    sb.append(st);
    sb.append('\n');
  }
  
  private void pushS1(String st, StringBuilder sb)
  {
    sb.append(st);
  }
}
