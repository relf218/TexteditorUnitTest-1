package net.dnsalias.pcb.texteditor.gui;

import java.util.*;
import net.dnsalias.pcb.utilities.HelperClass;

public class Enums
{
  private final static String resourceSrc = "resources/EnumsResources";

  /**commands for menu operations
   */
  public enum MenuOperation
  {
    FILE("FILE"),
    EDIT("EDIT"),
    SETTINGS("SETTINGS"),
    HELP("HELP");
    private final String name;

    MenuOperation(String name)
    {
      this.name = name;
    }

    @Override
    public String toString()
    {
      return name;
    }
  }

  /**commands for file operations
   */
  public enum FileOperation
  {
    OPEN("OPEN"),
    NEW("NEW"),
    RECENT("RECENT"),
    CLOSE("CLOSE"),
    EXIT("EXIT"),
    SAVE("SAVE"),
    SAVEAS("SAVEAS"),
    PRINT("PRINT"),
    NEWINST("NEWINST");
    private final String name;

    FileOperation(String name)
    {
      this.name = name;
    }

    @Override
    public String toString()
    {
      return name;
    }
  }

  /**commands for settings operations
   */
  public enum SettingsOperation
  {
    FONT("FONT"),
    TABSIZE("TABSIZE"),
    LINEWRAP("LINEWRAP"),
    EOL("EOL");
    private final String name;

    SettingsOperation(String name)
    {
      this.name = name;
    }

    @Override
    public String toString()
    {
      return name;
    }
  }

  /**commands for edit operations
   */
  public enum SearchOperation
  {
    FIND("FIND"),
    REPLACE("REPLACE"),
    REPLACEALL("REPLACEALL"),
    CLOSE("CLOSE"),
    FINDAGAIN("FINDAGAIN"),
    SELECTALL("SELECTALL");
    private final String name;

    SearchOperation(String name)
    {
      this.name = name;
    }

    @Override
    public String toString()
    {
      return name;
    }
  }

  /**commands for file dialogs
   */
  public enum FileDialogMethod
  {
    OPEN("OPEN"),
    SAVE("SAVE"),
    SAVEAS("SAVEAS");
    private final String name;

    FileDialogMethod(String name)
    {
      this.name = name;
    }

    @Override
    public String toString()
    {
      return name;
    }
  }

  /**commands for text edit
   */
  public enum EditOperation
  {
    UNDO("UNDO"),
    REDO("REDO"),
    INSERTCTRLCHAR("INSERTCTRLCHAR");
    private final String name;

    EditOperation(String name)
    {
      this.name = name;
    }

    @Override
    public String toString()
    {
      return name;
    }
  }

  /**font styles
   */
  public enum FontStyle
  {
    PLAIN("Plain"),
    BOLD("Bold"),
    ITALIC("Italic"),
    BOLD_ITALIC("Bold_Italic");
    private final String name;

    FontStyle(String name)
    {
      this.name = name;
    }

    @Override
    public String toString()
    {
      ResourceBundle EnumsResources = HelperClass.getResourceBundleFromJar(resourceSrc, "UTF-8");
      //ResourceBundle EnumsResources = ResourceBundle.getBundle(resourceSrc);
      return EnumsResources.getString(name);
    }
  }

  /**commands for help dialogs
   */
  public enum HelpOperation
  {
    ABOUT("ABOUT"),
    HELP("HELP");
    private final String name;

    HelpOperation(String name)
    {
      this.name = name;
    }

    @Override
    public String toString()
    {
      return name;
    }
  }
}
