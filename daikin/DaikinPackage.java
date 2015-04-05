package daikin;

public class DaikinPackage
{
  private static final String SPACE = "              ";
  private static final String NEW_LINE = "\n";
  public static final String LIRC_FRAME_START = DaikinDecoder.IR_FRAME_SEPARATOR_PULSE + DaikinDecoder.LIRC_SPACE + DaikinDecoder.IR_FRAME_SEPARATOR_SPACE + DaikinDecoder.LIRC_SPACE;
  public static final String LIRC_PACKAGE_START = DaikinDecoder.IR_SEPARATOR_PULSE + DaikinDecoder.LIRC_SPACE + DaikinDecoder.IR_PACKAGE_START + DaikinDecoder.LIRC_SPACE;
  public static final String LIRC_MIDDLE_PACKAGE_START = DaikinDecoder.IR_SEPARATOR_PULSE + DaikinDecoder.LIRC_SPACE + DaikinDecoder.IR_FRAME_START + DaikinDecoder.LIRC_SPACE;
  public static final String LIRC_ZERO = DaikinDecoder.IR_SEPARATOR_PULSE + DaikinDecoder.LIRC_SPACE + DaikinDecoder.IR_ZERO + DaikinDecoder.LIRC_SPACE;
  public static final String LIRC_ONE = DaikinDecoder.IR_SEPARATOR_PULSE + DaikinDecoder.LIRC_SPACE + DaikinDecoder.IR_ONE + DaikinDecoder.LIRC_SPACE;

  public int[] frameOneStatic = new int[] { 0x11, 0xDA, 0x27, 0x00, 0xC5, 0x30, 0x00, 0x07 };
  public int[] frameTwoStatic = new int[] { 0x11, 0xDA, 0x27, 0x00, 0x42, 0x00, 0x08, 0x5C };

  public int[] frameOne = new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
  public int[] frameTwo = new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
  public int[] frameThree = new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

  public void setFramesToZero()
  {
    for (int i = 0; i < frameOne.length; i++)
    {
      frameOne[i] = 0;
    }
    for (int i = 0; i < frameTwo.length; i++)
    {
      frameTwo[i] = 0;
    }
    for (int i = 0; i < frameThree.length; i++)
    {
      frameThree[i] = 0;
    }
  }

  private void setCheckSUM(int[] frame, int checkSumIdx)
  {
    int sum = 0;
    int size = frame.length - 1;
    for (int i = 0; i < size; i++)
    {
      sum += frame[i];
    }
    frame[checkSumIdx] = sum & 0xFF;
  }

  public void setTimeTo(int day, int hour, int minutes)
  {
    int minutesFromMidnight = hour * 60 + minutes;
    // System.out.println(getBin(minutesFromMidnight) + "-total");
    int minutesFromMidnightFirstPart = 0x00FF & minutesFromMidnight;
    // System.out.println(getBin(minutesFromMidnightFirstPart) + "-first part");
    int minutesFromMidnightSecondPart = 0x0700 & minutesFromMidnight;
    // System.out.println(getBin(minutesFromMidnightSecondPart) + "-second pard");
    int minutesFromMidnightSecondPartShifted = minutesFromMidnightSecondPart >> 8;
    // System.out.println(getBin(minutesFromMidnightSecondPartShifted) + "-second moved");
    // System.out.println(getBin(day) + "-days");
    int dayShifted = day << 3;
    // System.out.println(getBin(dayShifted) + "-days prepared");
    dayShifted = dayShifted | minutesFromMidnightSecondPartShifted;
    // System.out.println(getBin(dayShifted) + "-shifted");

    frameTwo[5] = minutesFromMidnightFirstPart;
    frameTwo[6] = dayShifted;
    // System.out.println(Integer.toHexString(frameTwo[5]));
    // System.out.println(Integer.toHexString(frameTwo[6]));
    setCheckSUM(frameTwo, 7);
  }

  public static String getBin(int minutesFromMidnight)
  {
    return String.format("%16s", Integer.toBinaryString(minutesFromMidnight)).replace(' ', '0');
  }

  public static String getHex(int i)
  {
    return String.format("%02x", i);
  }

  public void setFanTo(int fanState)
  {
    setBytes4to8(fanState, 8, frameThree, 18);
  }

  private void setBytes4to8(int state, int idx, int[] frame, int checkSumIdx)
  {
    frame[idx] = 0x0F & frame[idx];// null first 4 bytes
    frame[idx] = (state << 4) | frame[idx];// fill first 4 bytes
    setCheckSUM(frame, checkSumIdx);
  }

  public void setModeTo(int mode)
  {
    setBytes4to8(mode, 5, frameThree, 18);
  }

  public void setDegreesTo(int i)
  {
    frameThree[6] = i * 2;
    setCheckSUM(frameThree, 18);
  }

  public String getFanState()
  {
    return FanState.valueOf(frameThree[8] >> 4).toString();
  }

  public String getMode()
  {
    return ModeState.valueOf(frameThree[5] >> 4).toString();
  }

  public String getWorkingState()
  {
    return WorkingState.valueOf(frameThree[5] & 0x01).toString();
  }

  public String getDegrees()
  {
    return "" + frameThree[6] / 2;
  }

  public void setWorkingState(int state)
  {
    frameThree[5] = frameThree[5] & 0xFE;
    frameThree[5] = frameThree[5] | state;
    setCheckSUM(frameThree, 18);
  }

  public void setHeader1()
  {
    frameOne = frameOneStatic;
  }

  public void setHeader2()
  {
    frameTwo = frameTwoStatic;
  }

  public void setHeader3()
  {
    frameThree[0] = 0x11;
    frameThree[1] = 0xDA;
    frameThree[2] = 0x27;
    frameThree[3] = 0x00;
    frameThree[5] = 0x08;

    frameThree[8] = 0xB0;
    frameThree[11] = 0x06;
    frameThree[12] = 0x60;
    frameThree[13] = 0x00;
    frameThree[14] = 0x00;
    frameThree[15] = 0xC1;
    frameThree[16] = 0x80;
    frameThree[17] = 0x00;
  }

  public String getDayOfWeek()
  {
    int value = frameTwo[6] & 0x38;
    value = value >> 3;
    System.out.println(value);
    return DayofWeekState.valueOf(value).toString();
  }

  public String getHourOfDay()
  {
    int value = frameTwo[6] & 0x7;
    value = value << 8;
    value = value | frameTwo[5];
    int hours = value / 60;
    int minutes = value - (hours * 60);
    return "" + hours + ":" + minutes;
  }

  public void setHorizontalSwingState(int state)
  {
    setLastForBits(state, frameThree, 8);
    setCheckSUM(frameThree, 18);
  }

  public void setVerticalSwingState(int state)
  {
    setLastForBits(state, frameThree, 9);
    setCheckSUM(frameThree, 18);
  }

  private void setLastForBits(int state, int[] frame, int frameIdx)
  {
    frame[frameIdx] = frame[frameIdx] & 0xF0;
    frame[frameIdx] = frame[frameIdx] | state;
  }

  public String getHorizontalSwingState()
  {
    return SwingState.valueOf(frameThree[8] & 0xF).toString();
  }

  public String getVerticalSwingState()
  {
    return SwingState.valueOf(frameThree[9] & 0xF).toString();
  }

  public String getIROne()
  {
    return LIRC_ONE;
  }

  public String getIRZero()
  {
    return LIRC_ZERO;
  }

  public String getPackageStart()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(NEW_LINE + SPACE);
    for (int i = 0; i < 5; i++)
    {
      sb.append(LIRC_ZERO);
      if (i == 2)
      {
        sb.append(NEW_LINE + SPACE);
      }
    }
    sb.append(LIRC_PACKAGE_START);
    sb.append(NEW_LINE + SPACE);
    return sb.toString();
  }

  public String getFrameStart()
  {
    return LIRC_PACKAGE_START;
  }

  public String getLircData()
  {
    StringBuilder toRet = new StringBuilder();
    toRet.append(getPackageStart());
    int counter = 0;
    counter++;
    toRet.append(LIRC_FRAME_START);

    counter = printFrame(toRet, counter, frameOne);

    counter++;
    toRet.append(LIRC_MIDDLE_PACKAGE_START);
    counter = 0;
    toRet.append(NEW_LINE + SPACE);
    counter++;
    toRet.append(LIRC_FRAME_START);

    counter = printFrame(toRet, counter, frameTwo);

    counter++;
    toRet.append(LIRC_MIDDLE_PACKAGE_START);
    counter = 0;
    toRet.append(NEW_LINE + SPACE);
    counter++;
    toRet.append(LIRC_FRAME_START);

    counter = printFrame(toRet, counter, frameThree);

    toRet.append(NEW_LINE + SPACE);
    toRet.append(DaikinDecoder.IR_SEPARATOR_PULSE + DaikinDecoder.LIRC_SPACE);
    toRet.append(NEW_LINE);
    toRet.append(NEW_LINE);
    
    return toRet.toString();
  }

  private int printFrame(StringBuilder toRet, int counter, int[] frame)
  {
    for (int i = 0; i < frame.length; i++)
    {
      String asBin = getBin(frame[i]);
      // System.out.println(asBin);
      char[] data = asBin.toCharArray();
      for (int bitsIdx = 15; bitsIdx >= 8; bitsIdx--)
      {
        if (counter == 3)
        {
          counter = 0;
          toRet.append(NEW_LINE + SPACE);
        }

        if (data[bitsIdx] == '1')
        {
          // System.out.println("1");
          toRet.append(LIRC_ONE);
        }
        else
        {
          // System.out.println("0");
          toRet.append(LIRC_ZERO);
        }

        counter++;
      }
      // System.out.println("X");
    }
    return counter;
  }

  public String getByteReversed(int b)
  {
    String asBin = getBin((byte) b);
    char[] data = asBin.toCharArray();
    StringBuilder sb = new StringBuilder();
    for (int i = 15; i >= 8; i--)
    {
      if (data[i] == '1')
      {
        // System.out.println("1");
        sb.append(LIRC_ONE);
      }
      else
      {
        // System.out.println("0");
        sb.append(LIRC_ZERO);
      }
    }

    return sb.toString();
  }

  String lircId = "      begin raw_codes";
  String startPattern = "begin remote\n" + "\n" + "  name  DAIKIN\n" + "  flags RAW_CODES\n" + "  eps            30\n" + "  aeps          100\n" + "\n" + "  gap          34978\n" + "\n" + lircId + "\n";
  String closingPattern = "\n      end raw_codes\n" + "end remote\n";

  public String getLircConfFileFirst()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(startPattern);
    sb.append(getLircNameAndData());
    String endPattern = closingPattern;
    sb.append(endPattern);
    return sb.toString();
  }
  
  public String getLircNameAndData()
  {
    return getLircName() + getLircData();
  }

  public String getLircName()
  {
    return "          name " + getLircNameClean();
  }

  public String getLircNameClean()
  {
    return getWorkingState() + "_" + getMode() + "_" + getDegrees() + "_" + getFanState() + "_" + getHorizontalSwingState() + "_" + getVerticalSwingState();
  }

  public boolean hasConf(String currentConfFile)
  {
    return currentConfFile.indexOf(getLircName()) != -1;
  }

  public String getLircConfFileAppend(String currentConfFile)
  {
    int idx = currentConfFile.indexOf(lircId);
    int splitPoint = idx + lircId.length() + 1;
    String firstPart = currentConfFile.substring(0, splitPoint);
    String secondPart = currentConfFile.substring(splitPoint, currentConfFile.length());
    
    return firstPart + getLircNameAndData() + secondPart;
  }

  public String getLircConfFile(String currentConfFile)
  {
    if(hasConf(currentConfFile))
    {
      return currentConfFile;
    }
    return getLircConfFileAppend(currentConfFile);
  }
}
