package daikin;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;


public class DaikinStateChecker
{
  public static void main(String[] args)
  {
    new DaikinStateChecker().check(args);    
  }

  private void check(String[] args)
  {
    String absolutePath = "";
    if(args.length == 0)
    {
      File[] allFiles = new File("../ir/").listFiles();
      Arrays.sort(allFiles, new Comparator<File>(){
        public int compare(File f1, File f2)
        {
            return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
        } });
      
      System.out.println("Checking: " + allFiles[0].getName());
      absolutePath = allFiles[0].getAbsolutePath();  
    }
    else
    {
      absolutePath = args[0];
    }
    
    try
    {
      Path path2 = FileSystems.getDefault().getPath(absolutePath);
      List<String> allLines = Files.readAllLines(path2, Charset.forName("UTF8"));
      if(allLines.size() != 100)
      {
        System.out.println("Size is not 100 " + allLines.size());
        return;
      }
      
      StringTokenizer tok = new StringTokenizer(allLines.get(0));
      if(!tok.hasMoreTokens())
      {
        System.out.println("Error first line empty" + allLines.get(0));
        return;
      }
      String token = tok.nextToken();
      Long.parseLong(token);
      if(tok.hasMoreTokens())
      {
        System.out.println("Error first line more than one entry" + allLines.get(0));
        return;
      }
      
      if(allLines.get(1).length() != 0)
      {
        System.out.println("Second line not empty:" + allLines.get(1));
        return;
      }
      
      for(int i = 2; i <= 98; i++)
      {
        String line = allLines.get(i);
        tok = new StringTokenizer(line);
        if(!tok.hasMoreTokens())
        {
          System.out.println("Line empty (should have 6)" + line);
          return;
        }
        
        for(int j = 0; j < 5; j++)
        {
          String current = tok.nextToken();
          if(!tok.hasMoreTokens())
          {
            System.out.println("Line have less than 6 elements" + line + "/" + current);
            return;
          }
          else if(j == 5 && tok.hasMoreTokens())
          {
            System.out.println("Line have more than 6 elements" + line + "/" + current);
            return;
          }
        }
      }
      
      String line = allLines.get(99);
      tok = new StringTokenizer(line);
      if(!tok.hasMoreTokens())
      {
        System.out.println("Last line empty");
        return;
      }
      
      tok.nextToken();
      if(tok.hasMoreTokens())
      {
        System.out.println("Last line more than one element " + line);
        return;
      }
      
      System.out.println("File ok!");
    }
    catch(Exception x)
    {
      x.printStackTrace();
    }
  }
}
