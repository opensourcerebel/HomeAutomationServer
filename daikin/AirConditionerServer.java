package daikin;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import wakeup.WakeupServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class AirConditionerServer
{
  public static final String SERVER_CONF = "./server.conf";
  public static final String WAKEUP_CONF = "./wakeup.conf";
  public static final String IR_CONF = "./ir.conf";
  public static String secretURL;
  public static final int EXPIRATION_TIME = 60000;
  public volatile String tempControlURL;
  public boolean tempControlURLStatic;
  public boolean tempControlURLActive;
  public Properties props;
  public Properties irProps;
  public Properties wakeupProps;
  public long lastModified;
  public DaikinPackage dp;
  public boolean execCommands;
  public Object lock = new Object();
  public List<String> prevSessions = new ArrayList<String>();
  public String autoTrustIPSt;
  public String autoRejectIPSt;
  public WakeupServer wakeupServer;

  public static void main(String[] args)
  {
    new AirConditionerServer().start();
    // String nextSessionId = new AirConditionerServer().nextSessionId();
    // for(char oneChar : nextSessionId.toCharArray())
    // {
    // System.out.print("'" + oneChar + "',");
    // }
  }

  private void start()
  {
    try
    {
      loadProps();
      loadCurrentPackage();
      setupWakeupProcedure();
      startServer();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private void setupWakeupProcedure()
  {
    wakeupServer = new WakeupServer();
    String isWorking = wakeupProps.getProperty(HTML_WAKE_ISWORKING, "ON");
    if (isWorking.equals("ON"))
    {
      String hour;
      String min;
      String tz;
      String period;
      String params;

      hour = wakeupProps.getProperty(HTML_WAKE_HOUR);
      min = wakeupProps.getProperty(HTML_WAKE_MIN);
      tz = wakeupProps.getProperty(HTML_WAKE_TZ);
      period = wakeupProps.getProperty(HTML_WAKE_PERIOD);
      params = wakeupProps.getProperty(HTML_WAKE_PARAMS);
      
      startWakeupProcedure(hour, min, tz, period, params);
    }
  }

  private void loadCurrentPackage()
  {
    dp = new DaikinPackage();
    dp.setHeader1();
    dp.setHeader2();
    dp.setHeader3();
    dp.setWorkingState(WorkingState.valueOf(props.getProperty(HTML_WORKINGSTATE, "OFF")).getState());
    dp.setModeTo(ModeState.valueOf(props.getProperty(HTML_MODE, "COOL")).getState());
    dp.setDegreesTo(Integer.parseInt(props.getProperty(HTML_DEGREES, "21")));
    dp.setFanTo(FanState.valueOf(props.getProperty(HTML_FAN, "FANSILENT")).getState());
    dp.setHorizontalSwingState(SwingState.valueOf(props.getProperty(HTML_HSWING, "SWINGOFF")).getState());
    dp.setVerticalSwingState(SwingState.valueOf(props.getProperty(HTML_VSWING, "SWINGOFF")).getState());
  }

  private void startServer() throws UnknownHostException, IOException
  {
    secretURL = props.getProperty("secretURL");
    if(secretURL == null)
    {
      throw new RuntimeException("You have to configure a secretURL prop");
    }
    
    execCommands = Boolean.parseBoolean(props.getProperty("exec", "true"));
    tempControlURL = props.getProperty("tempControlURL");
    if (tempControlURL != null)
    {
      System.out.println("!!! Will use static tempControlURL:[" + tempControlURL + "]");
      tempControlURLStatic = true;
      tempControlURLActive = true;
    }
    else
    {
      tempControlURLActive = false;
      tempControlURLStatic = false;
      tempControlURL = "";
    }
    autoRejectIPSt = props.getProperty("autoRejectIPSt");
    if (autoRejectIPSt != null)
    {
      System.out.println("!!! Will auto REJECT IPs starting with autoRejectIPSt:[" + autoRejectIPSt + "]");
    }

    autoTrustIPSt = props.getProperty("autoTrustIPSt");
    if (autoTrustIPSt != null)
    {
      System.out.println("!!! Will auto trust IPs starting with autoTrustIPSt:[" + autoTrustIPSt + "]");
    }
    int port = Integer.parseInt(props.getProperty("port", "80"));
    String onPath = "/";
    String ip = InetAddress.getLocalHost().getHostAddress();
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 3);
    server.createContext(onPath, new MyHandler());
    server.setExecutor(null); // creates a default executor
    server.start();

    System.out.println("Server started on " + ip + " port " + port);
    System.out.println("On is http://" + ip + onPath);
  }

  private void loadProps() throws FileNotFoundException, IOException
  {
    props = new Properties();
    props.load(new FileReader(SERVER_CONF));

    wakeupProps = new Properties();
    wakeupProps.load(new FileReader(WAKEUP_CONF));
    loadIRMapping();
  }

  public void loadIRMapping()
  {
    try
    {
      irProps = new Properties();
      irProps.load(new FileReader(IR_CONF));
      lastModified = getLstModified();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private long getLstModified()
  {
    return new File(IR_CONF).lastModified();
  }

  private static final String HTML_VSWING = "VSWING";
  private static final String HTML_HSWING = "HSWING";
  private static final String HTML_FAN = "FAN";
  private static final String HTML_DEGREES = "DEGREES";
  private static final String HTML_MODE = "MODE";
  private static final String HTML_WORKINGSTATE = "WORKINGSTATE";

  private static final String HTML_WAKE_HOUR = "HOUR";
  private static final String HTML_WAKE_MIN = "MIN";
  private static final String HTML_WAKE_TZ = "TZ";
  private static final String HTML_WAKE_PERIOD = "PERIOD";
  private static final String HTML_WAKE_PARAMS = "PARAMS";
  private static final String HTML_WAKE_FUTURE_INFO = "FUTURE_INFO";
  private static final String HTML_WAKE_ISWORKING = "ISWORKING";

  class MyHandler implements HttpHandler
  {

    public String inputStreamToString(InputStream in) throws Exception
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null)
      {
        sb.append(line).append("\n");
      }
      in.close();
      return sb.toString();
    }

    public void handle(HttpExchange t)
    {
      synchronized (lock)
      {
        try
        {
          String remoteAddr = t.getRemoteAddress().getAddress().getHostAddress();
          boolean ipTrusted = false;
          boolean ipRejected = false;
          if (autoRejectIPSt != null)
          {
            boolean reject = remoteAddr.equals(autoRejectIPSt);
            if (reject)
            {
              ipRejected = true;
              System.out.println("Incoming IP:[" + remoteAddr + "] is rejected according to rule:[" + autoRejectIPSt + "]");
            }
          }

          if (autoTrustIPSt != null && !ipRejected)
          {
            ipTrusted = remoteAddr.startsWith(autoTrustIPSt);
            System.out.println("Incoming IP:[" + remoteAddr + "] is auto trusted:[" + ipTrusted + "] accoring to rule:[" + autoTrustIPSt + "]");
          }

          boolean devActive = false;
          String response = "This is the response";
          URI requestURI = t.getRequestURI();
          // Set<String> keySet = t.getRequestHeaders().keySet();
          InputStream is = t.getRequestBody();
          String params = inputStreamToString(is);
          Map<String, String> map = readData(params);
          System.out.println("++++++++++requestURI:[" + requestURI + "]");
          System.out.println("params:[" + params + "]");
          System.out.println("map:[" + map + "]");

          String path = requestURI.toString();
          if (path.indexOf("/" + secretURL + "s") != -1)
          {
            sendMail(t, map);
          }
          else if (path.indexOf("/" + secretURL) != -1)
          {
            // handle the webpage resources
            if (path.indexOf(".ico") != -1)
            {
              printContents(t, "/favicon.ico", 1, "./", 0);
            }
            else if (path.indexOf(".js") != -1)
            {
              printContents(t, "jquery-1.11.2.min.js", 0, "./dist/js/", 0);
            }
            else
            {
              printContents(t, "/auth.html", 1, "./", 0);
            }
          }
          else if ((tempControlURLActive && path.indexOf(tempControlURL) != -1) || ipTrusted)
          {
            String cssStr = "/dist/css/";
            int cssIndx = path.indexOf(cssStr);
            String fontsStr = "/dist/fonts/";
            int fontsIndx = path.indexOf(fontsStr);
            String jsStr = "/dist/js/";
            int jsIndx = path.indexOf(jsStr);
            if (cssIndx != -1)
            {
              printContents(t, path, cssStr.length(), "." + cssStr, cssIndx);
            }
            else if (fontsIndx != -1)
            {
              printContents(t, path, fontsStr.length(), "." + fontsStr, fontsIndx);
            }
            else if (jsIndx != -1)
            {
              printContents(t, path, jsStr.length(), "." + jsStr, jsIndx);
            }
            else if (path.indexOf("index.html") != -1)
            {
              printContents1(t, "index.html", 0, "./", 0);
            }
            else if (path.indexOf("favicon.ico") != -1)
            {
              printContents(t, "favicon.ico", 0, "./", 0);
            }
            else if (path.indexOf("/aircon/") != -1)
            {
              controlAirCon(t, map);
            }
            else if (path.indexOf("/ir/") != -1)
            {
              handleIR(t, map);
            }
            else if (path.indexOf("/rgb/") != -1)
            {
              handleRGB(t, map);
            }
            else if (path.indexOf("/wakeupstat/") != -1)
            {
              wakeupStat(t);
            }
            else if (path.indexOf("/wakeupcon/") != -1)
            {
              controlWakeup(t, map);
            }
            else if (path.indexOf("/wakeup/") != -1)
            {
              handleWakeup(t, map);
            }
            else if (path.indexOf("/airstat/") != -1)
            {
              airStat(t);
            }
          }
          else
          {
            if (devActive)
            {
              t.sendResponseHeaders(200, response.length());
              OutputStream os = t.getResponseBody();
              os.write(response.getBytes());
              os.close();
            }
            else
            {
              if (isExpired(path))
              {
                printString(t, "<html lang=\"en\"><link rel=\"shortcut icon\" href=\"/"+ secretURL +"/favicon.ico\" />Expired, request again</html>");
              }
              else
              {
                // report!!!
                System.out.println("intruder!!!!");
              }
            }
          }
        }
        catch (Exception x)
        {
          x.printStackTrace();
        }
      }
    }

    private void controlWakeup(HttpExchange t, Map<String, String> map)
    {
      String uri = t.getRequestURI().toString();
      String isWorking;
      String hour;
      String min;
      String tz;
      String period;
      String params;

      if (uri.indexOf("/on") != -1)
      {
        isWorking = "ON";
        hour = wakeupProps.getProperty(HTML_WAKE_HOUR);
        min = wakeupProps.getProperty(HTML_WAKE_MIN);
        tz = wakeupProps.getProperty(HTML_WAKE_TZ);
        period = wakeupProps.getProperty(HTML_WAKE_PERIOD);
        params = wakeupProps.getProperty(HTML_WAKE_PARAMS);

        wakeupProps.setProperty(HTML_WAKE_ISWORKING, isWorking);
      }
      else if (uri.indexOf("/off") != -1)
      {
        isWorking = "OFF";
        hour = wakeupProps.getProperty(HTML_WAKE_HOUR);
        min = wakeupProps.getProperty(HTML_WAKE_MIN);
        tz = wakeupProps.getProperty(HTML_WAKE_TZ);
        period = wakeupProps.getProperty(HTML_WAKE_PERIOD);
        params = wakeupProps.getProperty(HTML_WAKE_PARAMS);

        wakeupProps.setProperty(HTML_WAKE_ISWORKING, isWorking);
      }
      else if (uri.indexOf("/cancel") != -1)
      {
        isWorking = "ON";
        hour = wakeupProps.getProperty(HTML_WAKE_HOUR);
        min = wakeupProps.getProperty(HTML_WAKE_MIN);
        tz = wakeupProps.getProperty(HTML_WAKE_TZ);
        period = wakeupProps.getProperty(HTML_WAKE_PERIOD);
        params = wakeupProps.getProperty(HTML_WAKE_PARAMS);

        wakeupProps.setProperty(HTML_WAKE_ISWORKING, isWorking);
      }
      else
      {
        isWorking = map.get(HTML_WAKE_ISWORKING);
        hour = map.get(HTML_WAKE_HOUR);
        min = map.get(HTML_WAKE_MIN);
        tz = map.get(HTML_WAKE_TZ);
        period = map.get(HTML_WAKE_PERIOD);
        params = map.get(HTML_WAKE_PARAMS);

        wakeupProps.setProperty(HTML_WAKE_ISWORKING, isWorking);
        wakeupProps.setProperty(HTML_WAKE_HOUR, hour);
        wakeupProps.setProperty(HTML_WAKE_MIN, min);
        wakeupProps.setProperty(HTML_WAKE_TZ, tz);
        wakeupProps.setProperty(HTML_WAKE_PERIOD, period);
        wakeupProps.setProperty(HTML_WAKE_PARAMS, params);
      }

      try
      {
        wakeupProps.store(new FileWriter(WAKEUP_CONF), "");
        if (isWorking.equals("OFF"))
        {
          wakeupServer.stopWakeup();
        }
        else
        {
          startWakeupProcedure(hour, min, tz, period, params);
        }

        printCurrentWakeupData(t);
      }
      catch (Exception e)
      {
        e.printStackTrace();
        try
        {
          t.sendResponseHeaders(500, 0);
          OutputStream os = t.getResponseBody();
          os.close();
        }
        catch (Exception x)
        {
          x.printStackTrace();
        }
      }
    }

    private void handleWakeup(HttpExchange t, Map<String, String> map) throws IOException
    {
      String uri = t.getRequestURI().toString();
      if (uri.contains("wakeup.html"))
      {
        printContents1(t, "wakeup.html", 0, "./", 0);
      }
      else
      {

      }
    }

    private boolean isExpired(String path)
    {
      for (String oneSession : prevSessions)
      {
        if (path.indexOf(oneSession) != -1) { return true; }
      }
      return false;
    }
  }

  private void wakeupStat(HttpExchange t)
  {
    printCurrentWakeupData(t);
  }

  private void handleRGB(HttpExchange t, Map<String, String> map)
  {
    String uri = t.getRequestURI().toString();
    String color = uri.substring(uri.lastIndexOf('/') + 1, uri.length());
    Color aColor = Color.decode("#" + color);
    int red = aColor.getRed();
    int green = aColor.getGreen();
    int blue = aColor.getBlue();
    System.out.println(green);
    System.out.println(red);
    System.out.println(blue);
    exec("sudo i2cset -y -f 1 0x48 0x05 0xA0 0x03 " + red + " " + green + " " + blue + " i");
    printString(t, aColor.toString());
  }

  private void printCurrentWakeupData(HttpExchange t)
  {
    printString(t, getCurrentWakeupData());
  }

  boolean tvStateInXBMC;

  private void handleIR(HttpExchange t, Map<String, String> map)
  {
    String uri = t.getRequestURI().toString();
    if (getLstModified() != lastModified)
    {
      loadIRMapping();
    }

    String key = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
    String cmd = irProps.getProperty(key);
    System.out.println("key:" + key);
    System.out.println("cmd:" + cmd);

    if (cmd != null)
    {
      if (cmd.startsWith("1"))
      {
        checkTV();
        cmd = cmd.substring(1, cmd.length());
      }
      else if (cmd.startsWith("0"))
      {
        tvStateInXBMC = true;
        exec("irsend SEND_ONCE hdmi_switch BTN_2");
        sleep(2);
        cmd = cmd.substring(1, cmd.length());
      }

      exec(cmd);
    }

    printString(t, "ok");
  }

  private void checkTV()
  {
    if (tvStateInXBMC)
    {
      exec("irsend SEND_ONCE hdmi_switch BTN_1");
      tvStateInXBMC = false;
      sleep(2);
    }
  }

  public void exec(String[] cmd)
  {
    System.out.print("Executing:[");
    for (String s : cmd)
    {
      System.out.print(s + " ");
    }
    System.out.println("");

    if (!execCommands)
    {
      System.out.println("Will not execute above]");
      return;
    }

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

  private void sleep(int i)
  {
    try
    {
      Thread.sleep(i * 1000);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  private void airStat(HttpExchange t)
  {
    printCurrentData(t);
  }

  private void printCurrentData(HttpExchange t)
  {
    printString(t, getCurrentData());
  }

  private void printString(HttpExchange t, String data)
  {
    try
    {
      byte[] encoded = data.getBytes();
      t.sendResponseHeaders(200, encoded.length);
      OutputStream os = t.getResponseBody();
      os.write(encoded);
      os.close();
    }
    catch (Exception x)
    {
      x.printStackTrace();
    }
  }

  private String getCurrentData()
  {
    //@formatter:off
    String data = 
        "{"
        + "\"" + HTML_WORKINGSTATE + "\":\"" + dp.getWorkingState()         + "\","
        + "\"" + HTML_MODE +         "\":\"" + dp.getMode()                 + "\"," 
        + "\"" + HTML_FAN +          "\":\"" + dp.getFanState()             + "\","
        + "\"" + HTML_DEGREES +      "\":\"" + dp.getDegrees()              + "\","
        + "\"" + HTML_HSWING +       "\":\"" + dp.getHorizontalSwingState() + "\","
        + "\"" + HTML_VSWING +       "\":\"" + dp.getVerticalSwingState()   + "\""
        + "}";
    //@formatter:on
    return data;
  }

  private String getCurrentWakeupData()
  {
    //@formatter:off
    String data = 
        "{"
        + "\"" + HTML_WAKE_HOUR+         "\":\"" + getWakeupHour()      + "\","
        + "\"" + HTML_WAKE_MIN +         "\":\"" + getWakeupMin()       + "\"," 
        + "\"" + HTML_WAKE_TZ +          "\":\"" + getWakeupTZ()        + "\","
        + "\"" + HTML_WAKE_PERIOD +      "\":\"" + getWakeupPeriod()    + "\","
        + "\"" + HTML_WAKE_PARAMS +      "\":\"" + getWakeupParams()    + "\","
        + "\"" + HTML_WAKE_FUTURE_INFO + "\":\"" + getFutureInfo()      + "\","
        + "\"" + HTML_WAKE_ISWORKING +   "\":\"" + getWakeupIsWorking() + "\""
        + "}";
    //@formatter:on
    return data;
  }

  private String getFutureInfo()
  {
    List<String> toRet = new ArrayList<String>();
    new WakeupServer().getTimeToWait(new String[] { getWakeupHour(), getWakeupMin(), getWakeupTZ(), getWakeupPeriod() }, toRet);
    return toRet.get(0);
  }

  private String getWakeupPeriod()
  {
    return wakeupProps.getProperty(HTML_WAKE_PERIOD, "30");
  }

  private String getWakeupIsWorking()
  {
    return wakeupProps.getProperty(HTML_WAKE_ISWORKING, "off");
  }

  private String getWakeupParams()
  {
    return wakeupProps.getProperty(HTML_WAKE_PARAMS, "");
  }

  private String getWakeupTZ()
  {
    return wakeupProps.getProperty(HTML_WAKE_TZ, "Europe/Sofia");
  }

  private String getWakeupMin()
  {
    return wakeupProps.getProperty(HTML_WAKE_MIN, "20");
  }

  private String getWakeupHour()
  {
    return wakeupProps.getProperty(HTML_WAKE_HOUR, "07");
  }

  private void controlAirCon(HttpExchange t, Map<String, String> map)
  {
    String uri = t.getRequestURI().toString();

    String workingState;
    String mode;
    String degrees;
    String fan;
    String vswing;
    String hswing;

    if (uri.indexOf("/on") != -1)
    {
      workingState = "ON";
      dp.setWorkingState(WorkingState.valueOf(workingState).getState());
      mode = dp.getMode();
      degrees = dp.getDegrees();
      fan = dp.getFanState();
      vswing = dp.getVerticalSwingState();
      hswing = dp.getHorizontalSwingState();
    }
    else if (uri.indexOf("/off") != -1)
    {
      workingState = "OFF";
      dp.setWorkingState(WorkingState.valueOf(workingState).getState());
      mode = dp.getMode();
      degrees = dp.getDegrees();
      fan = dp.getFanState();
      vswing = dp.getVerticalSwingState();
      hswing = dp.getHorizontalSwingState();
    }
    else
    {
      workingState = map.get(HTML_WORKINGSTATE);
      mode = map.get(HTML_MODE);
      degrees = map.get(HTML_DEGREES);
      fan = map.get(HTML_FAN);
      vswing = map.get(HTML_VSWING);
      hswing = map.get(HTML_HSWING);
      dp.setWorkingState(WorkingState.valueOf(workingState).getState());
      dp.setModeTo(ModeState.valueOf(mode).getState());
      dp.setDegreesTo(Integer.parseInt(degrees));
      dp.setFanTo(FanState.valueOf(fan).getState());
      dp.setHorizontalSwingState(SwingState.valueOf(hswing).getState());
      dp.setVerticalSwingState(SwingState.valueOf(vswing).getState());
    }

    props.setProperty(HTML_WORKINGSTATE, workingState);
    props.setProperty(HTML_MODE, mode);
    props.setProperty(HTML_DEGREES, degrees);
    props.setProperty(HTML_FAN, fan);
    props.setProperty(HTML_VSWING, vswing);
    props.setProperty(HTML_HSWING, hswing);

    try
    {
      props.store(new FileWriter(SERVER_CONF), "");

      if (updateConfig(dp))
      {
        exec("service lirc restart");
      }

      exec("irsend SEND_ONCE DAIKIN " + dp.getLircNameClean());
      printCurrentData(t);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      try
      {
        t.sendResponseHeaders(500, 0);
        OutputStream os = t.getResponseBody();
        os.close();
      }
      catch (Exception x)
      {
        x.printStackTrace();
      }
    }
  }

  private void exec(String cmd)
  {
    System.out.println("Executing:[" + cmd + "]");

    if (!execCommands)
    {
      System.out.println("[Will not execute above]");
      return;
    }

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

  private Map<String, String> readData(String params)
  {
    Map<String, String> toRet = new HashMap<String, String>();
    StringTokenizer tok = new StringTokenizer(params, "&");
    while (tok.hasMoreTokens())
    {
      String keyValuePair = tok.nextToken().trim();
      StringTokenizer t2 = new StringTokenizer(keyValuePair, "=");
      String key = t2.nextToken();
      String val = "";
      if (t2.hasMoreTokens())
      {
        try
        {
          val = URLDecoder.decode(t2.nextToken(), "UTF8");
        }
        catch (UnsupportedEncodingException e)
        {
          e.printStackTrace();
        }
      }
      toRet.put(key, val);
    }
    return toRet;
  }

  private void printContents(HttpExchange t, String path, int cssLength, String cssFileSystemPath, int cssIndx) throws IOException
  {
    String fileName = path.substring(cssIndx + cssLength, path.length());
    System.out.println("Dir is:[" + cssFileSystemPath + "]");
    Map<String, File> dirContent = getDirContent(cssFileSystemPath);
    System.out.println("Dir content:[" + dirContent + "]");
    System.out.println("File name is:[" + fileName + "]");
    File f = dirContent.get(fileName);
    // System.out.println(f.getAbsolutePath());
    byte[] encoded = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
    t.sendResponseHeaders(200, encoded.length);
    OutputStream os = t.getResponseBody();
    os.write(encoded);
    os.close();
  }

  private void printContents1(HttpExchange t, String path, int cssLength, String cssFileSystemPath, int cssIndx) throws IOException
  {
    String fileName = path.substring(cssIndx + cssLength, path.length());
    System.out.println("Dir is:[" + cssFileSystemPath + "]");
    Map<String, File> dirContent = getDirContent(cssFileSystemPath);
    System.out.println("Dir content:[" + dirContent + "]");
    System.out.println("File name is:[" + fileName + "]");
    File f = dirContent.get(fileName);
    // System.out.println(f.getAbsolutePath());
    String absolutePath = f.getAbsolutePath();
    Path pathToRes = Paths.get(absolutePath);
    byte[] originalResBytes = Files.readAllBytes(pathToRes);
    String originalSt = new String(originalResBytes);
    // System.out.println(originalSt);
    String replacedRes = originalSt.replace("__", "/" + tempControlURL);
    // System.out.println(replacedRes);
    byte[] encoded = replacedRes.getBytes();
    t.sendResponseHeaders(200, encoded.length);
    OutputStream os = t.getResponseBody();
    os.write(encoded);
    os.close();
  }

  Map<String, File> toRet;

  private Map<String, File> getDirContent(String string)
  {
    // if (toRet != null) { return toRet; }

    Map<String, File> toRet = new HashMap<String, File>();
    File file = new File(string);
    System.out.println("Dir resolves to:[" + file.getAbsolutePath() + "]");
    File[] content = file.listFiles();

    for (File f : content)
    {
      toRet.put(f.getName(), f);
    }
    return toRet;
  }

  private SecureRandom random = new SecureRandom();

  public String nextSessionId()
  {
    return new BigInteger(130, random).toString(32);
  }

  private void sendMail(HttpExchange t, Map<String, String> map) throws MessagingException, IOException
  {
    String id = map.get("email");
    String email = "";
    if (id.equals("1"))
    {
      email = "email1";
    }
    else if (id.equals("2"))
    {
      email = "email2";
    }
    else if (id.equals("3"))
    {
      email = "email3";
    }
    else if (id.equals("4"))
    {
      email = "email4";
    }
    String externalServer = props.getProperty("external_ip");
    String externalPort = props.getProperty("external_port");
    if (!tempControlURLStatic)
    {
      tempControlURL = nextSessionId();
      tempControlURLActive = true;
    }

    new Thread()
    {
      public void run()
      {
        try
        {
          Thread.sleep(EXPIRATION_TIME);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
        tempURLExpired();
      }
    }.start();
    String mailURL = "http://" + externalServer + ":" + externalPort + "/" + tempControlURL + "/index.html";
    System.out.println(mailURL);
//    String msg = "<html><a href=\"___\">Click Here to Control the AirConditioner</a></html>".replace("___", mailURL);
//    new SentMailViaGMailAPI().sendMail2(email, "email1", "Airconditioner Control", msg);
    System.out.println("send to:" + email);
    try
    {
      printString(t, ":))");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void tempURLExpired()
  {
    synchronized (lock)
    {
      prevSessions.add(0, tempControlURL);
      if (prevSessions.size() >= 3)
      {
        prevSessions.remove(2);
      }
      System.out.println("!!!! " + tempControlURL + " expired!");
      System.out.println("previous:[" + prevSessions + "]");
      tempControlURL = "";
      tempControlURLActive = false;
    }
  }

  private boolean updateConfig(DaikinPackage dp) throws IOException
  {
    String lircFile = (String) props.get("lircFile");
    // String lircFile = "C:/Users/I036200/Downloads/ir/lircdDaikin.conf";
    // String lircFile = "C:/Users/I036200/Downloads/files_roomautomation/lircd.conf";
    System.out.println("Lirc file:[" + lircFile + "]");
    Path path = Paths.get(lircFile);
    byte[] encoded = Files.readAllBytes(path);
    String currentConfFile = new String(encoded);
    String newConfFile = dp.getLircConfFile(currentConfFile);

    String lircNameClean = dp.getLircNameClean();
    if (currentConfFile.length() != newConfFile.length())
    {
      System.out.println("Adding config:[" + lircNameClean + "]");
      String string = path.getParent().toString();
      String fileName = string + File.separator + path.getFileName();
      File file = new File(fileName);
      if (!file.exists())
      {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(file);
      fw.write(newConfFile);
      fw.flush();
      fw.close();
      System.out.println("sysout:" + file.getAbsolutePath());
      return true;
    }
    else
    {
      System.out.println("Config already exist:[" + dp.getLircNameClean() + "]");
    }
    return false;
  }
  
  private void startWakeupProcedure(String hour, String min, String tz, String period, String params)
  {
    String[] paramsSplit = params.split("\\s+");
    String[] goParams = new String[4 + paramsSplit.length];
    goParams[0] = hour;
    goParams[1] = min;
    goParams[2] = tz;
    goParams[3] = period;
    for (int i = 0; i < paramsSplit.length; i++)
    {
      goParams[4 + i] = paramsSplit[i];
    }

    wakeupServer.go(goParams, null);
  }
}
