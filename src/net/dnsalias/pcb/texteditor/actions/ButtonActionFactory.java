package net.dnsalias.pcb.texteditor.actions;

import java.util.ResourceBundle;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import net.dnsalias.pcb.utilities.HelperClass;

public class ButtonActionFactory
{
  private ResourceBundle Resources;
  private final static String resourceSrc = "resources/TexteditorResources";

  public ButtonActionFactory()
  {
    Resources = HelperClass.getResourceBundleFromJar(resourceSrc, "UTF-8");
  //Resources = ResourceBundle.getBundle(resourceSrc);
  }

  /**********************************************
   * File_Open
   **********************************************/
  public AbstractAction getFileOpenAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/Open16.gif";
      String imgName = "icons/folder_page.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new FileOpenAction(Resources.getString("Btn_Open"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create FileOpenAction");
    }
    return action;
  }

  class FileOpenAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public FileOpenAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * File_Save
   **********************************************/
  public AbstractAction getFileSaveAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/Save16.gif";
      String imgName = "icons/page_save.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new FileSaveAction(Resources.getString("Btn_Save"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create FileSaveAction");
    }
    return action;
  }

  class FileSaveAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public FileSaveAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * File_SaveAS
   **********************************************/
  public AbstractAction getFileSaveAsAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/SaveAs16.gif";
      String imgName = "icons/page_copy.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new FileSaveAsAction(Resources.getString("Btn_SaveAs"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create FileSaveAsAction");
    }
    return action;
  }

  class FileSaveAsAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public FileSaveAsAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * File_Print
   **********************************************/
  public AbstractAction getFilePrintAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/Print16.gif";
      String imgName = "icons/printer.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new FilePrintAction(Resources.getString("Btn_Print"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create FilePrintAction");
    }
    return action;
  }

  class FilePrintAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public FilePrintAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * File_Exit
   **********************************************/
  public AbstractAction getFileExitAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/Stop16.gif";
      String imgName = "icons/stop.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new FileExitAction(Resources.getString("Btn_Exit"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create FileExitAction");
    }
    return action;
  }

  class FileExitAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public FileExitAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * File_Instance
   **********************************************/
  public AbstractAction getFileInstanceAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/Add16.gif";
      String imgName = "icons/page_white_add.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new FileInstanceAction(Resources.getString("Btn_NewInstance"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create FileInstanceAction");
    }
    return action;
  }

  class FileInstanceAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public FileInstanceAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * Edit_Undo
   **********************************************/
  public AbstractAction getEditUndoAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/Undo16.gif";
      String imgName = "icons/arrow_undo.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new EditUndoAction(Resources.getString("Btn_Undo"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create EditUndoAction");
    }
    return action;
  }

  class EditUndoAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public EditUndoAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * Edit_Redo
   **********************************************/
  public AbstractAction getEditRedoAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/Redo16.gif";
      String imgName = "icons/arrow_redo.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new EditRedoAction(Resources.getString("Btn_Redo"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create EditRedoAction");
    }
    return action;
  }

  class EditRedoAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public EditRedoAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * Edit_Find
   **********************************************/
  public AbstractAction getEditFindAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/Find16.gif";
      String imgName = "icons/find.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new EditFindAction(Resources.getString("Btn_Find"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create EditFindAction");
    }
    return action;
  }

  class EditFindAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public EditFindAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * Edit_FindAgain
   **********************************************/
  public AbstractAction getEditFindAgainAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/FindAgain16.gif";
      String imgName = "icons/find.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new EditRedoAction(Resources.getString("Btn_FindAgain"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create EditFindAgainAction");
    }
    return action;
  }

  class EditFindAgainAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public EditFindAgainAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * Help_About
   **********************************************/
  public AbstractAction getHelpAboutAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/About16.gif";
      String imgName = "icons/information.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new HelpAboutAction(Resources.getString("Btn_About"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create HelpAboutAction");
    }
    return action;
  }

  class HelpAboutAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public HelpAboutAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }

  /**********************************************
   * Help_About
   **********************************************/
  public AbstractAction getHelpHelpAction()
  {
    AbstractAction action = null;
    URL imgURL = null;

    try
    {
      //String imgName = "toolbarButtonGraphics/general/Help16.gif";
      String imgName = "icons/help.png";
      imgURL = this.getClass().getClassLoader().getResource(imgName);
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    if(imgURL != null)
    {
      action = new HelpHelpAction(Resources.getString("Btn_Help"), new ImageIcon(imgURL));
    }
    else
    {
      System.err.println("Couldn't create HelpHelpAction");
    }
    return action;
  }

  class HelpHelpAction extends AbstractAction
  {
    public static final long serialVersionUID = 19650221L;

    public HelpHelpAction(String name, Icon icon)
    {
      super(name, icon);
    }

    public void actionPerformed(ActionEvent e)
    {
    }
  }
}
