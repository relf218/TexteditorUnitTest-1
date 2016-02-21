package net.dnsalias.pcb.utilities;

import java.io.*;

/**
 *
 * @author Bertram Fritz
 */
public class Recoder
{
  /**
   * --recode inFile inEncoding outfile outEncoding
   */
  private String inFile,  inEnc,  outFile,  outEnc;

  public Recoder(String[] args)
  {
    try
    {
      //System.out.println("Recoder instantiated");
      if(!(args[0].equals("--recode")))
      {
    	System.out.println("EXIT");
        System.out.println("Usage: texteditor.jar --recode inFile inEncoding outfile outEncoding");
        System.exit(1);
      }
      else
      {
    	  System.out.println("ok");
        this.inFile = args[1];
        this.inEnc = args[2];
        this.outFile = args[3];
        this.outEnc = args[4];
        init();
        System.out.println("ok");
      }
    }
    catch(NullPointerException e)
    {
      System.out.println("Usage: texteditor.jar --recode inFile inEncoding outfile outEncoding");
      System.exit(1);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      System.out.println("Usage: texteditor.jar --recode inFile inEncoding outfile outEncoding");
      System.exit(1);
    }
  }

  private void init()
  {
    BufferedReader reader = null;
    BufferedWriter writer = null;

    System.out.println("Recoder working...");
    try
    {
      FileInputStream is = new FileInputStream(inFile);
      reader = new BufferedReader(new InputStreamReader(is, inEnc));
    }
    catch(FileNotFoundException fnfe)
    {
      System.err.println(fnfe.getLocalizedMessage());
      System.exit(1);
    }
    catch(UnsupportedEncodingException uee)
    {
      System.err.println(uee.getLocalizedMessage());
      System.exit(1);
    }
    try
    {
      FileOutputStream os = new FileOutputStream(outFile);
      writer = new BufferedWriter(new OutputStreamWriter(os, outEnc));
    }
    catch(FileNotFoundException fnfe)
    {
      System.err.println(fnfe.getLocalizedMessage());
      System.exit(1);
    }
    catch(UnsupportedEncodingException uee)
    {
      System.err.println(uee.getLocalizedMessage());
      System.exit(1);
    }
    try
    {
      String line = null;
      while((line = reader.readLine()) != null)
      {
        writer.write(line);
        writer.newLine();
      }
      reader.close();
      writer.flush();
      writer.close();
    }
    catch(IOException ioe)
    {
      System.err.println(ioe.getLocalizedMessage());
      System.exit(1);
    }
    catch(NullPointerException npe)
    {
      System.err.println(npe.getLocalizedMessage());
      System.exit(1);
    }
    System.out.println("Recoder finished successfully.");
  }
}
