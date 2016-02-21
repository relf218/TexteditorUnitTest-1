package net.dnsalias.pcb.utilities;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.awt.Component;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;


//the GUI
public class HelperClass
{
  public static final int UTF8BOM = 1;
  public static final int UTF8 = 2;

  // constructor
  private HelperClass()
  {
  }

  /**
   * @param resourceBase  The path to the base resoure. "resources/MyResources" without extension.\n
   *                      Extension of the resource file has to be .properties\n
   *                      Example: resources/MyResources_en_US.properties.
   * @param encoding      The encoding of the resource file. "UTF-8" e.g.
   * @return         The ResourceBundle for the default Locale. Null if none was found.
   */
  public static ResourceBundle getResourceBundleFromJar(String resourceBase,
          String encoding)
  {
    ResourceBundle bundle = null;
    HelperClass hc = new HelperClass();
    bundle = hc._getResourceBundleFromJar(resourceBase, encoding, Locale.getDefault());
    return bundle;
  }

  /**
   * @param resourceBase  The path to the base resoure. "resources/MyResources" without extension.\n
   *                      Extension of the resource file has to be .properties\n
   *                      Example: resources/MyResources_en_US.properties.
   * @param encoding  The encoding of the resource file. "UTF-8" e.g.
   * @param locale        The Locale thas has to be used.
   * @return         The ResourceBundle for Locale locale. Null if none was found.
   */
  public static ResourceBundle getResourceBundleFromJar(String resourceBase,
          String encoding, Locale locale)
  {
    ResourceBundle bundle = null;
    HelperClass hc = new HelperClass();
    bundle = hc._getResourceBundleFromJar(resourceBase, encoding, locale);
    return bundle;
  }

  private ResourceBundle _getResourceBundleFromJar(String resourceBase,
          String encoding, Locale locale)
  {
    String language = locale.getLanguage();
    String country = locale.getCountry();
    String variant = locale.getVariant();
    String resourceName = null;
    InputStream is = null;
    ResourceBundle bundle = null;

    while(true)
    {
      resourceName = resourceBase + "_" + language + "_" + country + "_" + variant + ".properties";
      is = this.getClass().getClassLoader().getResourceAsStream(resourceName);
      if(is != null)
      {
        break;
      }

      resourceName = resourceBase + "_" + language + "_" + country + ".properties";
      is = this.getClass().getClassLoader().getResourceAsStream(resourceName);
      if(is != null)
      {
        break;
      }

      resourceName = resourceBase + "_" + language + ".properties";
      is = this.getClass().getClassLoader().getResourceAsStream(resourceName);
      if(is != null)
      {
        break;
      }

      resourceName = resourceBase + ".properties";
      is = this.getClass().getClassLoader().getResourceAsStream(resourceName);
      if(is != null)
      {
        break;
      }
      else
      {
        break;
      }
    }

    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
      PropertyResourceBundle resourceBundle = new PropertyResourceBundle(reader);
      bundle = resourceBundle;
    }
    catch(NullPointerException npe)
    {
      System.err.println(resourceBase + ".properties not found");
    }
    catch(UnsupportedEncodingException uee)
    {
      uee.printStackTrace();
    }
    catch(FileNotFoundException fnfe)
    {
      fnfe.printStackTrace();
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
    return bundle;
  }

  /**
   * @param menuBar - The JMenuBar which has to be scanned
   * @param actionCommand - The ActionCommand to search for
   * @return The JMenuItem with the matching ActionCommand
   */
  public static JMenuItem getMenuItemByActionCommand(JMenuBar menuBar,
          String actionCommand)
  {
    JMenuItem item = null;
    HelperClass hc = new HelperClass();
    item = hc._getMenuItemByActionCommand(menuBar, actionCommand);
    return item;
  }

  private JMenuItem _getMenuItemByActionCommand(JMenuBar menuBar,
          String actionCommand)
  {
    MenuElement[] menus = menuBar.getSubElements();
    for(MenuElement menu : menus)
    {
      if(menu instanceof JMenu)
      {
        Component[] menuItems = ((JMenu) menu).getMenuComponents();
        for(Component menuItem : menuItems)
        {
          if(menuItem instanceof JMenuItem && ((JMenuItem) menuItem).getActionCommand().equals(actionCommand))
          {
            return (JMenuItem) menuItem;
          }
        }
      }
    }
    return null;
  }

  /**
   * @param popupMenu The JPopupMenu which has to be scanned
   * @param actionCommand The ActionCommand to search for
   * @return The JMenuItem with the matching ActionCommand
   */
  public static JMenuItem getMenuItemByActionCommand(JPopupMenu popupMenu,
          String actionCommand)
  {
    JMenuItem item = null;
    HelperClass hc = new HelperClass();
    item = hc._getMenuItemByActionCommand(popupMenu, actionCommand);
    return item;
  }

  private JMenuItem _getMenuItemByActionCommand(JPopupMenu popupMenu,
          String actionCommand)
  {
    Component[] menuItems = popupMenu.getComponents();
    for(Component menuItem : menuItems)
    {
      if(menuItem instanceof JMenuItem && ((JMenuItem) menuItem).getActionCommand().equals(actionCommand))
      {
        return (JMenuItem) menuItem;
      }
    }
    return null;
  }

  /**
   * @param in File to be checked for UTF-8
   * @return True if file encoding is UTF-8
   * @throws java.io.IOException
   */
  public static int isUTF_8(FileInputStream in) throws IOException
  {
    HelperClass hc = new HelperClass();

    return hc._isUTF_8(in);
  }

  /**
   * Without going into the full algorithm, just perform UTF-8 decoding on the file
   * looking for an invalid UTF-8 sequence. The correct UTF-8 sequences look like this:
   * 0xxxxxxx  ASCII < 0x80 (128) 
   * 110xxxxx 10xxxxxx  2-byte >= 0x80
   * 1110xxxx 10xxxxxx 10xxxxxx  3-byte >= 0x400 
   * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx  4-byte >= 0x10000 
   * If the file is all ASCII, you cannot know if it was meant to be UTF-8
   * or any other character set compatible with ASCII in the lower 128. 

   * @param in File to be checked for UTF-8
   * @return True if file encoding is UTF-8
   * @throws java.io.IOException
   */
  private int _isUTF_8(FileInputStream in) throws IOException
  {
    int retVal = UTF8BOM;
    boolean utfTraced = false;
    int followUpByte = 0;
    int currentByte = 0;
    int leadingByte = 0;
    FileChannel fc = in.getChannel();
    ByteBuffer bb = ByteBuffer.allocateDirect(1024);
    int[] bom = new int[3];

    //check for BOM
    fc.position(0);
    if(fc.read(bb) != -1)
    {
      bb.flip();
      for(int i = 0; i < 3; i++)
      {
        if(!bb.hasRemaining())
        {
          retVal = 0;
          return retVal;
        }
        bom[i] = bb.get(i);
        if(bom[i] < 1)
        {
          bom[i] &= 0xFF;
        }
      }
      if((bom[0] == 0xEF) && (bom[1] == 0xBB) && (bom[2] == 0xBF))
      {
        return retVal;
      }
    }
    // empty file
    else
    {
      retVal = 0;
      return retVal;
    }

    // check standard UTF-8
    retVal = UTF8;

    fc.position(0);
    bb.clear();
    fc.read(bb);
    bb.flip();

    while(true)
    {
      if(!bb.hasRemaining())
      {
        bb.clear();
        if(fc.read(bb) == -1)
        {
          if(utfTraced == false)
          {
            retVal = 0;
          }
          return retVal;
        }
        bb.flip();
      }

      // ASCII
      currentByte = bb.get();

      //negative values to unsigned
      if(currentByte < 1)
      {
        currentByte &= 0x00FF;
      }

      if(currentByte > 128)
      {
        //leadingByte = currentByte & 0xD0;
        leadingByte = currentByte & 0xE0;
        // 2 byte character
        if(leadingByte == 0xC0)
        {
          utfTraced = true;
          if(!bb.hasRemaining())
          {
            if(fc.read(bb) == -1)
            {
              retVal = 0;
              return retVal;
            }
            bb.flip();
          }
          followUpByte = bb.get();
          /*
          if(followUpByte == -1)
          {
          retVal = false;
          return retVal;
          }
           */
          if(followUpByte < 1)
          {
            followUpByte &= 0x00FF;
          }
          followUpByte &= 0xC0;
          if(followUpByte != 0x80)
          {
            retVal = 0;
            return retVal;
          }
          else
          {
            continue;
          }
        }

        // 3 byte character
        leadingByte = currentByte & 0xF0;
        if(leadingByte == 0xE0)
        {
          utfTraced = true;
          for(int i = 0; i < 2; i++)
          {
            if(!bb.hasRemaining())
            {
              if(fc.read(bb) == -1)
              {
                retVal = 0;
                return retVal;
              }
              bb.flip();
            }

            followUpByte = bb.get();
            /*
            if (followUpByte == -1)
            {
            retVal = false;
            return retVal;
            }
             */
            if(followUpByte < 1)
            {
              followUpByte &= 0xFF;
            }
            followUpByte &= 0xC0;
            if(followUpByte != 0x80)
            {
              retVal = 0;
              return retVal;
            }
          }
          continue;
        }

        // 4 byte character
        leadingByte = currentByte & 0xF8;
        if(leadingByte == 0xF0)
        {
          utfTraced = true;
          for(int i = 0; i < 3; i++)
          {
            if(!bb.hasRemaining())
            {
              if(fc.read(bb) == -1)
              {
                retVal = 0;
                return retVal;
              }
              bb.flip();
            }
            followUpByte = bb.get();
            /*
            if (followUpByte == -1)
            {
            retVal = false;
            return retVal;
            }
             */
            if(followUpByte < 1)
            {
              followUpByte &= 0xFF;
            }
            followUpByte &= 0xC0;
            if(followUpByte != 0x80)
            {
              retVal = 0;
              return retVal;
            }
          }
          continue;
        }

        // invalid bit pattern
        retVal = 0;
        return retVal;
      }
    }
  }
}
