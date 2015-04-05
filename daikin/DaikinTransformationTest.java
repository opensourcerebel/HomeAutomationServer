package daikin;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DaikinTransformationTest
{
  String lircData = "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     25000     \n" +
      "3400     1750     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     35000     \n" +
      "3400     1750     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     450     430     35000     \n" +
      "3400     1750     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     1320     \n" +
      "430     450     430     1320     430     1320     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     \n";
  
  String firstConfFile = "begin remote\n" +
      "\n" +
      "  name  DAIKIN\n" +
      "  flags RAW_CODES\n" +
      "  eps            30\n" +
      "  aeps          100\n" +
      "\n" +
      "  gap          34978\n" +
      "\n" +
      "      begin raw_codes\n" +
      "name OFF_AUTO_21_FANSILENT_SWINGOFF_SWINGOFF\n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     25000     \n" +
      "3400     1750     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     35000     \n" +
      "3400     1750     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     450     430     35000     \n" +
      "3400     1750     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     1320     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     1320     \n" +
      "430     450     430     1320     430     1320     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     \n\n" +
      "      end raw_codes\n" +
      "end remote\n";
  
  String confFileAppend = "begin remote\n" +
      "\n" +
      "  name  DAIKIN\n" +
      "  flags RAW_CODES\n" +
      "  eps            30\n" +
      "  aeps          100\n" +
      "\n" +
      "  gap          34978\n" +
      "\n" +
      "      begin raw_codes\n" +
      "name OFF_COOL_21_FANSILENT_SWINGOFF_SWINGOFF\n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     25000     \n" +
      "3400     1750     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     35000     \n" +
      "3400     1750     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     1320     430     450     430     1320     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     35000     \n" +
      "3400     1750     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     \n" +
      "\nname OFF_AUTO_21_FANSILENT_SWINGOFF_SWINGOFF\n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     25000     \n" +
      "3400     1750     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     35000     \n" +
      "3400     1750     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     1320     430     1320     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     1320     430     450     430     1320     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     450     430     35000     \n" +
      "3400     1750     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     1320     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     1320     430     1320     430     450     \n" +
      "430     1320     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     450     \n" +
      "430     450     430     450     430     1320     \n" +
      "430     450     430     1320     430     1320     \n" +
      "430     450     430     1320     430     1320     \n" +
      "430     \n" +
      "\n" +
      "      end raw_codes\n" +
      "end remote\n";
  
  @Test
  public void testSetTimeTo8020()
  {
    int[] realData = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x39, 0x0A, 0x43 };
    DaikinPackage dp = new DaikinPackage();
    dp.setTimeTo(DayofWeekState.SUNDAY.getState(), 9, 29);
    int[] frameToCheck = dp.frameTwo;
    assertArrayEquals(realData, frameToCheck);
  }

  @Test
  public void testSetFan0()
  {
    int[] realData = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30 };
    DaikinPackage dp = new DaikinPackage();
    dp.setFanTo(FanState.FAN1.getState());
    int[] frameToCheck = dp.frameThree;
    assertArrayEquals(realData, frameToCheck);
  }
  
  @Test
  public void testSetFan1()
  {
    int[] realData = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40};
    DaikinPackage dp = new DaikinPackage();
    dp.setFanTo(FanState.FAN2.getState());
    int[] frameToCheck = dp.frameThree;
   
    assertArrayEquals(realData, frameToCheck);
  }
  
  @Test
  public void testSetModeCool()
  {
    int[] realData = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30};
    DaikinPackage dp = new DaikinPackage();
    dp.setModeTo(ModeState.COOL.getState());
    int[] frameToCheck = dp.frameThree;
  
    assertArrayEquals(realData, frameToCheck);
  }
  
  @Test
  public void testSetModeHeat()
  {
    int[] realData = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40 };
    DaikinPackage dp = new DaikinPackage();
    
    dp.setModeTo(ModeState.HEAT.getState());
    int[] frameToCheck = dp.frameThree;
//    for (int i = 0; i < realData.length; i++)
//    {
//      System.out.println("i:" + i);
//      System.out.println("ex:" + DaikinPackage.getBin(realData[i]) + " " + DaikinPackage.getHex(realData[i]) + " " + realData[i]);
//      System.out.println("te:" + DaikinPackage.getBin(frameToCheck[i]) + " " + DaikinPackage.getHex(frameToCheck[i]) + " " + frameToCheck[i]);
//    }
    assertArrayEquals(realData, frameToCheck);
  }
  
  @Test
  public void testSetDegrees()
  {
    int[] realData = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x28, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x28 };
    DaikinPackage dp = new DaikinPackage();
    dp.setDegreesTo(20);
    int[] frameToCheck = dp.frameThree;
   
    assertArrayEquals(realData, frameToCheck);
  }
  
  @Test
  public void testSetWorkingStateOn()
  {
    int[] realData = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };
    DaikinPackage dp = new DaikinPackage();
    dp.setWorkingState(WorkingState.ON.getState());
    int[] frameToCheck = dp.frameThree;
   
    assertArrayEquals(realData, frameToCheck);
  }
  
  @Test
  public void testSetHorizontalSwingState()
  {
    //_21_OFF_H_swing_V_ON_FRI_0820
    int[] realData = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0F };
    DaikinPackage dp = new DaikinPackage();
    dp.setHorizontalSwingState(SwingState.SWINGON.getState());
    int[] frameToCheck = dp.frameThree;
    assertArrayEquals(realData, frameToCheck);
  }
  @Test
  public void testSetVerticalSwingState()
  {
    //_21_OFF_H_swing_V_ON_FRI_0820
    int[] realData = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0F };
    DaikinPackage dp = new DaikinPackage();
    dp.setVerticalSwingState(SwingState.SWINGON.getState());
    int[] frameToCheck = dp.frameThree;
    assertArrayEquals(realData, frameToCheck);
  }
  
  @Test
  public void testSetWorkingStateRealData()
  {
    //_21_OFF_AUTO_FRI_0820
    int[] realData1 = { 0x11,0xDA,0x27,0x00,0xC5,0x30,0x00,0x07 };
    int[] realData2 = { 0x11,0xDA,0x27,0x00,0x42,0xF4,0x31,0x79 };
    int[] realData3 = { 0x11,0xDA,0x27,0x00,0x00,0x08,0x2A,0x00,0xB0,0x00,0x00,0x06,0x60,0x00,0x00,0xC1,0x80,0x00,0x9B };
                                                 
    
    DaikinPackage dp = new DaikinPackage();
    dp.setHeader1();
    dp.setHeader2();
    dp.setHeader3();
    dp.setWorkingState(WorkingState.OFF.getState());
    dp.setModeTo(ModeState.AUTO.getState());
    dp.setFanTo(FanState.FANSILENT.getState());
    dp.setDegreesTo(21);
    dp.setTimeTo(DayofWeekState.FRIDAY.getState(), 8, 20);
   
//    int[] frameToCheck = dp.frameThree;
//    for (int i = 0; i < frameToCheck.length; i++)
//    {
//      System.out.println("i:" + i);
//      System.out.println("ex:" + DaikinPackage.getBin(realData3[i]) + " " + DaikinPackage.getHex(realData3[i]) + " " + realData3[i]);
//      System.out.println("te:" + DaikinPackage.getBin(frameToCheck[i]) + " " + DaikinPackage.getHex(frameToCheck[i]) + " " + frameToCheck[i]);
//    }
    assertArrayEquals(realData1, dp.frameOne);
    assertArrayEquals(realData2, dp.frameTwo);
    assertArrayEquals(realData3, dp.frameThree);
    
    assertEquals(lircData, dp.getLircData());
    assertEquals(firstConfFile, dp.getLircConfFileFirst());
  }
  
  
  @Test
  public void testExportPackageStart()
  {
    DaikinPackage dp = new DaikinPackage();
    String data = dp.getPackageStart();
    String xx = DaikinPackage.LIRC_ZERO;
    String ff = DaikinPackage.LIRC_PACKAGE_START;
    String expected = xx + xx + xx + "\n" + xx + xx + ff + "\n";
    assertEquals(expected, data);
  }
  
  @Test
  public void testExportFrameStart1()
  {
    DaikinPackage dp = new DaikinPackage();
    String data = dp.getByteReversed(0x55);
    String one = DaikinPackage.LIRC_ONE;
    String zero = DaikinPackage.LIRC_ZERO;
    String expected = one + zero + one + zero + one + zero + one + zero;
    assertEquals(expected, data);
  }
  
  @Test
  public void testSearchLircName()
  {
    DaikinPackage dp = new DaikinPackage();
    dp.setWorkingState(WorkingState.OFF.getState());
    dp.setModeTo(ModeState.AUTO.getState());
    dp.setFanTo(FanState.FANSILENT.getState());
    dp.setDegreesTo(21);
    dp.setTimeTo(DayofWeekState.FRIDAY.getState(), 8, 20);
    String currentConfFile = dp.getLircConfFileFirst();
    assertEquals(true, dp.hasConf(currentConfFile));
    dp.setModeTo(ModeState.COOL.getState());
    assertEquals(false, dp.hasConf(currentConfFile));
    String newConfFile = dp.getLircConfFile(currentConfFile);
    assertEquals(confFileAppend, newConfFile);
  }  
  
//  @Test
//  public void testWithRealLircConf()
//  {
//    try
//    {
//      DaikinPackage dp = new DaikinPackage();
//      dp.setWorkingState(WorkingState.OFF.getState());
//      dp.setModeTo(ModeState.AUTO.getState());
//      dp.setFanTo(FanState.FANSILENT.getState());
//      dp.setDegreesTo(21);
//      dp.setTimeTo(DayofWeekState.FRIDAY.getState(), 8, 20);
//     
//      
//      byte[] encoded = Files.readAllBytes(Paths.get("C:\\Users\\I036200\\Downloads\\ir\\lircdDaikin2.conf"));
//      String currentConfFile = new String(encoded);
////      System.out.println(currentConfFile);
////      System.out.println("==");
////      System.out.println(dp.getLircConfFile(currentConfFile));
////      dp.getLircConfFile(currentConfFile);
//    }
//    catch (Exception e)
//    {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//  }
}
