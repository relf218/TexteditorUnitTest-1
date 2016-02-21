package net.dnsalias.pcb.texteditor.gui;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.prefs.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import net.dnsalias.pcb.texteditor.actions.ButtonActionFactory;
import net.dnsalias.pcb.utilities.HelperClass;
//import net.dnsalias.pcb.utilities.Recoder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

//the GUI
public class Texteditor
        implements ActionListener, ItemListener
{

  private static ArrayList<Texteditor> InstanceList = null;
  private final String defaultEncoding = "ISO-8859-1";
  private File file = null;
  private String fileString = null;
  private JFrame frame;
  private JScrollPane sp, newSp;
  private JTextArea ta, newTa;
  private int caretPosition, currentLine, currentCol;
  private int lastCtrlChar;
  private boolean matchCase, holeWords;
  private String searchString, replaceString;
  private JMenuBar menubar;
  private JMenu menu;
  private JPopupMenu popupMenu;
  private JMenuItem menuitem, cutMenuItem, copyMenuItem, pasteMenuItem;
  private JMenuItem popupCutMenuItem, popupCopyMenuItem, popupPasteMenuItem;
  private JToolBar toolbar;
  private JCheckBoxMenuItem cbMenuitem;
  private ButtonGroup bgLineEnding;
  private JRadioButtonMenuItem rbLineEndingWindows, rbLineEndingUnix, rbLineEndingMac, rbLineEndingDefault;
  private Box statusBar;
  private SearchDialog dialog;
  private JLabel statusLine, memInfoLine;
  private DefaultListModel recentListModel = new DefaultListModel();
  private JList recent = new JList(recentListModel);
  private Dimension size;
  private Point location;
  private boolean hasChanged = false;
  private boolean caretEventHandlerEnabled = false;
  private boolean documentEventHandlerEnabled = false;
  private final String appName = "texteditor";
  private String currentEncoding = defaultEncoding;
  private String currentDecoding = defaultEncoding;
  private boolean byteOrderMark = false;
  private Font currentFont;
  private int tabsize;
  private boolean lineWrap;
  private HashMap<String, Action> editActions;
  private UndoManager undoManager;
  private ResourceBundle TexteditorResources;
  private final static String resourceSrc = "resources/TexteditorResources";
  private Runtime runtime = Runtime.getRuntime();
  private java.util.Timer cyclicTimer;
  private Preferences prefs;
  private Action fsa, foa, fna, redoa, undoa;
  private JButton copyToolbarButton, pasteToolbarButton, cutToolbarButton;
  private JButton undoToolbarButton, redoToolbarButton;
  private JButton fileNewToolbarButton, fileOpenToolbarButton, fileSaveToolbarButton;
  //private String[] arguments;
  private boolean skipEncodingProperty = false;
  private boolean isReadOnly = false;

  // constructor
  public Texteditor(String[] args)
  {
    Options options = new Options();
    options.addOption("h", "help", false, "display help");
    options.addOption("d", "display", false, "display only mode (useful only with -f)");
    options.addOption("e", "encoding", true, "encoding to use");
    options.addOption("f", "file", true, "file to open");

    CommandLineParser parser = new GnuParser();
    try
    {
      CommandLine line = parser.parse(options, args);

      if(line.hasOption("help"))
      {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(appName, options);
        System.exit(0);
      }

      if(line.hasOption("display"))
      {
        isReadOnly = true;
      }

      if(line.hasOption("encoding"))
      {
        currentEncoding = line.getOptionValue("encoding");
        skipEncodingProperty = true;
      }

      if(line.hasOption("file"))
      {
        this.file = new File(line.getOptionValue("file"));
      }

    }
    catch(ParseException exp)
    {
      // oops, something went wrong
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
      System.exit(1);
    }

    /*
     * Properties props = System.getProperties(); Set keySet = props.keySet();
     * for(Object key: keySet) { System.out.println(String.format("%s: %s",
     * key.toString(), props.get(key).toString())); }
     */
    // get current encoding
    // currentEncoding = System.getProperty("file.encoding", defaultEncoding);
    init();

    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {

      public void run()
      {
        TexteditorResources = HelperClass.getResourceBundleFromJar(resourceSrc, "UTF-8");

        createAndShowGUI();

        cyclicTimer = new java.util.Timer("cyclicTimer", true);
        cyclicTimer.scheduleAtFixedRate(new timerTask(), 1000, 10000);

        if(file != null)
        {
          newSp = new JScrollPane();
          newTa = createNewTextArea(newSp);
          if(readFile(file) == true)
          {
            initTextArea();
            getLineEnding();
          }
          else
          {
            String msg = String.format("%s\n%s",
                    TexteditorResources.getString("FileNotFoundError"),
                    fileString);
            JOptionPane.showMessageDialog(frame, msg, appName, JOptionPane.ERROR_MESSAGE);
            newTa = null;
          }
        }
        addInstanceToList();
      }
    });
  }

  private void addInstanceToList()
  {
    if(InstanceList == null)
    {
      InstanceList = new ArrayList<Texteditor>();
    }
    InstanceList.add(this);
    System.out.println("running instances: " + InstanceList.size());
  }
  // Init Components

  private void init()
  {
    Properties props = new Properties();
    try
    {
      prefs = Preferences.userNodeForPackage(Texteditor.class);

      props.setProperty("Frame.LocationX", prefs.get("Frame.LocationX", "0"));
      props.setProperty("Frame.LocationY", prefs.get("Frame.LocationY", "0"));
      props.setProperty("Font.Name", prefs.get("Font.Name", "Dialog"));
      props.setProperty("Font.Size", prefs.get("Font.Size", "12"));
      props.setProperty("Font.Style", prefs.get("Font.Style", "0"));
      props.setProperty("Font.Encoding", prefs.get("Font.Encoding", currentEncoding));
      props.setProperty("ByteOrderMark", prefs.get("ByteOrderMark", Boolean.toString(byteOrderMark)));
      props.setProperty("Tab.Size", prefs.get("Tab.Size", Integer.toString(tabsize)));
      props.setProperty("LineWrap", prefs.get("LineWrap", Boolean.toString(lineWrap)));
      props.setProperty("TextArea.Width", prefs.get("TextArea.Width", "640"));
      props.setProperty("TextArea.Height", prefs.get("TextArea.Height", "480"));
      props.setProperty("Recent.1", prefs.get("Recent.1", ""));
      props.setProperty("Recent.2", prefs.get("Recent.2", ""));
      props.setProperty("Recent.3", prefs.get("Recent.3", ""));
      props.setProperty("Recent.4", prefs.get("Recent.4", ""));
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    // assign values
    String fontName = props.getProperty("Font.Name", "Dialog");
    String fontSize = props.getProperty("Font.Size", "12");
    String fontStyle = props.getProperty("Font.Style");
    int iFontStyle = Font.PLAIN;
    if(fontStyle != null && !(fontStyle.equals("")))
    {
      try
      {
        iFontStyle = Integer.parseInt(fontStyle);
      }
      catch(NumberFormatException e)
      {
      }
    }
    try
    {
      currentFont = new Font(fontName, iFontStyle, Integer.parseInt(fontSize));
    }
    catch(NumberFormatException e)
    {
    }

    if(!skipEncodingProperty)
    {
      currentEncoding = props.getProperty("Font.Encoding", System.getProperty(
              "file.encoding", defaultEncoding));
    }

    String sBom = props.getProperty("ByteOrderMark", "false");
    byteOrderMark = Boolean.parseBoolean(sBom);

    String sTabSize = props.getProperty("Tab.Size", "8");
    try
    {
      tabsize = Integer.parseInt(sTabSize);
      if(tabsize < 2)
      {
        tabsize = 2;
      }
    }
    catch(NumberFormatException e)
    {
    }

    String sLineWrap = props.getProperty("LineWrap", "false");
    lineWrap = Boolean.parseBoolean(sLineWrap);

    try
    {
      String sWidth = props.getProperty("TextArea.Width", "640");
      String sHeight = props.getProperty("TextArea.Height", "480");

      size = new Dimension(Integer.parseInt(sWidth), Integer.parseInt(sHeight));
    }
    catch(NumberFormatException e)
    {
      size = new Dimension(640, 480);
    }

    try
    {
      String sPositionX = props.getProperty("Frame.LocationX", "0");
      String sPositionY = props.getProperty("Frame.LocationY", "0");
      location = new Point(Integer.parseInt(sPositionX), Integer.parseInt(sPositionY));
    }
    catch(NumberFormatException e)
    {
      location = new Point(0, 0);
    }

    String recentFile = null;
    recentFile = props.getProperty("Recent.4", "");
    if(!(recentFile.equals("")))
    {
      recentListModel.add(0, recentFile);
    }
    recentFile = props.getProperty("Recent.3", "");
    if(!(recentFile.equals("")))
    {
      recentListModel.add(0, recentFile);
    }
    recentFile = props.getProperty("Recent.2", "");
    if(!(recentFile.equals("")))
    {
      recentListModel.add(0, recentFile);
    }
    recentFile = props.getProperty("Recent.1", "");
    if(!(recentFile.equals("")))
    {
      recentListModel.add(0, recentFile);
    }
    props = null;
  }

  // Destructor
  @Override
  protected void finalize()
  {
    cyclicTimer.cancel();

    Properties props = new Properties();

    props.setProperty("Frame.LocationX", Integer.toString((int) location.getX()));
    props.setProperty("Frame.LocationY", Integer.toString((int) location.getY()));
    props.setProperty("Font.Name", currentFont.getFamily());
    props.setProperty("Font.Size", Integer.toString(currentFont.getSize()));
    props.setProperty("Font.Style", Integer.toString(currentFont.getStyle()));
    props.setProperty("Font.Encoding", currentEncoding);
    props.setProperty("ByteOrderMark", Boolean.toString(byteOrderMark));
    props.setProperty("Tab.Size", Integer.toString(tabsize));
    props.setProperty("LineWrap", Boolean.toString(lineWrap));
    props.setProperty("TextArea.Width", Integer.toString((int) size.getWidth()));
    props.setProperty("TextArea.Height", Integer.toString((int) size.getHeight()));

    for(int i = 0; i < recentListModel.getSize(); i++)
    {
      props.setProperty("Recent." + Integer.toString(i + 1),
              (String) recentListModel.getElementAt(i));
    }
    try
    {
      prefs = Preferences.userNodeForPackage(Texteditor.class);

      prefs.put("Frame.LocationX", props.getProperty("Frame.LocationX", "0"));
      prefs.put("Frame.LocationY", props.getProperty("Frame.LocationY", "0"));
      prefs.put("Font.Name", props.getProperty("Font.Name", "Dialog"));
      prefs.put("Font.Size", props.getProperty("Font.Size", "12"));
      prefs.put("Font.Style", props.getProperty("Font.Style", "0"));
      prefs.put("Font.Encoding", props.getProperty("Font.Encoding"));
      prefs.put("ByteOrderMark", props.getProperty("ByteOrderMark", "false"));
      prefs.put("Tab.Size", props.getProperty("Tab.Size", "8"));
      prefs.put("LineWrap", props.getProperty("LineWrap", "false"));
      prefs.put("TextArea.Width", props.getProperty("TextArea.Width", "640"));
      prefs.put("TextArea.Height", props.getProperty("TextArea.Height", "480"));

      for(int i = 0; i < recentListModel.getSize(); i++)
      {
        prefs.put("Recent." + Integer.toString(i + 1), props.getProperty(
                "Recent." + Integer.toString(i + 1), ""));
      }
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }

    try
    {
      super.finalize();
    }
    catch(Throwable ignored)
    {
    }
  }

  public Point getLocation()
  {
    return location;
  }

  // setup and show GUI
  private void createAndShowGUI()
  {
    // Make sure we have nice window decorations.
    // IMHO not really nice
    //JFrame.setDefaultLookAndFeelDecorated(true);

    // Create and set up the window
    frame = new JFrame(appName);
    if(InstanceList == null)
    {
      frame.setLocation(location);
    }
    else
    {
      Texteditor prev = InstanceList.get(InstanceList.size() - 1);
      Point p = prev.getLocation();
      Point newLocation = new Point(p.x + 40, p.y + 40);
      frame.setLocation(newLocation);
    }

    // Set an icon for the frame
    Image img = null;
    try
    {
      String imgName = "libs/duke.gif";
      URL imgURL = this.getClass().getClassLoader().getResource(imgName);
      Toolkit tk = Toolkit.getDefaultToolkit();

      MediaTracker m = new MediaTracker(frame);
      img = tk.getImage(imgURL);
      m.addImage(img, 0);
      m.waitForAll();
    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    if(img != null)
    {
      frame.setIconImage(new ImageIcon(img).getImage());
    }

    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setLayout(new BorderLayout(5, 0));
    frame.addWindowListener(new WindowClosingEventHandlerClass());
    frame.addComponentListener(new ComponentAdapter()
    {

      @Override
      public void componentMoved(ComponentEvent ce)
      {
        if(ce.getSource() == frame)
        {
          location = ((JFrame) ce.getSource()).getLocation();
        }
      }
    });

    // Create the menubar and Toolbar
    menubar = new JMenuBar();
    toolbar = new JToolBar();

    // Create the File menu
    menu = new JMenu(TexteditorResources.getString("Menu_File"));
    menu.setMnemonic(TexteditorResources.getString("Menu_File_Mnemonic").charAt(0));

    ButtonActionFactory factory = null;
    AbstractAction action = null;

    // Create the file menu and toolbar items
    factory = new ButtonActionFactory();

    // new
    class FileNewAction
            extends AbstractAction
    {

      public FileNewAction(String s, Icon i)
      {
        super(s, i);
      }

      public void actionPerformed(ActionEvent e)
      {
        fileNewAction();
      }
    }

    fna = new FileNewAction(
            TexteditorResources.getString("Btn_New"),
            (Icon) new ImageIcon(this.getClass().getClassLoader().getResource("icons/page_add.png")));
    //(Icon) new ImageIcon(this.getClass().getClassLoader().getResource("toolbarButtonGraphics/general/New24.gif")));
    fna.setEnabled(!isReadOnly);
    menuitem = new JMenuItem(fna);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_New_Mnemonic").charAt(0));
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    menuitem.setEnabled(!isReadOnly);
    menu.add(menuitem);

    fileNewToolbarButton = new JButton(fna);
    fileNewToolbarButton.setText("");
    fileNewToolbarButton.setToolTipText(TexteditorResources.getString("Btn_New_Tooltip"));
    toolbar.add(fileNewToolbarButton);

    // open
    class FileOpenAction
            extends AbstractAction
    {

      public FileOpenAction(String s, Icon i)
      {
        super(s, i);
      }

      public void actionPerformed(ActionEvent e)
      {
        fileOpenAction();
      }
    }

    foa = new FileOpenAction(
            TexteditorResources.getString("Btn_Open"),
            (Icon) new ImageIcon(this.getClass().getClassLoader().getResource("icons/folder_page.png")));
    foa.setEnabled(!isReadOnly);

    menuitem = new JMenuItem(foa);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_Open_Mnemonic").charAt(0));
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    menuitem.setEnabled(!isReadOnly);
    menu.add(menuitem);

    fileOpenToolbarButton = new JButton(foa);
    fileOpenToolbarButton.setText("");
    fileOpenToolbarButton.setToolTipText(TexteditorResources.getString("Btn_Open_Tooltip"));
    toolbar.add(fileOpenToolbarButton);

    // recent files
    menuitem = new JMenuItem(TexteditorResources.getString("Btn_Recent"));
    menuitem.setMnemonic(TexteditorResources.getString("Btn_Recent_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.FileOperation.RECENT.toString());
    menuitem.addActionListener(this);
    menuitem.setEnabled(!isReadOnly);
    menu.add(menuitem);

    // save
    class FileSaveAction
            extends AbstractAction
    {

      public FileSaveAction(String s, Icon i)
      {
        super(s, i);
      }

      public void actionPerformed(ActionEvent e)
      {
        if(saveFile(frame, file) == true)
        {
          hasChanged = false;
        }
      }
    }

    fsa = new FileSaveAction(
            TexteditorResources.getString("Btn_Save"),
            (Icon) new ImageIcon(this.getClass().getClassLoader().getResource("icons/page_save.png")));
    //(Icon) new ImageIcon(this.getClass().getClassLoader().getResource("toolbarButtonGraphics/general/Save24.gif")));

    menuitem = new JMenuItem(fsa);
    menuitem.setMnemonic(TexteditorResources.getString(
            "Btn_Save_Mnemonic").charAt(0));
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    fsa.setEnabled(false);
    menuitem.setEnabled(false);
    menu.add(menuitem);

    fileSaveToolbarButton = new JButton(fsa);
    fileSaveToolbarButton.setText("");
    fileSaveToolbarButton.setToolTipText(TexteditorResources.getString("Btn_Save_Tooltip"));
    toolbar.add(fileSaveToolbarButton);

    // save as
    action = factory.getFileSaveAsAction();
    menuitem = new JMenuItem(action);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_SaveAs_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.FileOperation.SAVEAS.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
    menuitem.addActionListener(this);
    menuitem.setEnabled(!isReadOnly);
    menu.add(menuitem);

    // print
    action = factory.getFilePrintAction();
    menuitem = new JMenuItem(action);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_Print_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.FileOperation.PRINT.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
    menuitem.addActionListener(this);
    menu.add(menuitem);

    // new window
    action = factory.getFileInstanceAction();
    menuitem = new JMenuItem(action);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_NewInstance_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.FileOperation.NEWINST.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
    menuitem.addActionListener(this);
    menuitem.setEnabled(!isReadOnly);
    menu.add(menuitem);

    menu.add(new JSeparator());

    // exit
    action = factory.getFileExitAction();
    menuitem = new JMenuItem(action);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_Exit_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.FileOperation.EXIT.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
    menuitem.addActionListener(this);
    menu.add(menuitem);

    // Add the File menu to menubar
    menubar.add(menu);
    toolbar.add(new JToolBar.Separator());

    // Create the Edit menu
    // Mnemonics and Accelerators for DragAndDrop are set for every JTextArea in
    // method createTextEditActions
    menu = new JMenu(TexteditorResources.getString("Menu_Edit"));


    menu.setMnemonic(TexteditorResources.getString("Menu_Edit_Mnemonic").charAt(0));
    menu.setActionCommand(Enums.MenuOperation.EDIT.toString());
    // menu.addActionListener(this);

    // undo
    class UndoAction
            extends AbstractAction
    {

      public UndoAction(String s, Icon i)
      {
        super(s, i);
      }

      public void actionPerformed(ActionEvent e)
      {
        if(undoManager.canUndo() == true)
        {
          undoManager.undo();
        }
      }
    }

    undoa = new UndoAction(
            TexteditorResources.getString("Btn_Undo"),
            (Icon) new ImageIcon(this.getClass().getClassLoader().getResource("icons/arrow_undo.png")));
    //(Icon) new ImageIcon(this.getClass().getClassLoader().getResource("toolbarButtonGraphics/general/Undo24.gif")));

    menuitem = new JMenuItem(undoa);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_Undo_Mnemonic").charAt(0));
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
    menuitem.setEnabled(false);
    menu.add(menuitem);

    undoToolbarButton = new JButton(undoa);
    undoToolbarButton.setText("");
    undoToolbarButton.setToolTipText(TexteditorResources.getString("Btn_Undo_Tooltip"));
    toolbar.add(undoToolbarButton);

    // redo
    class RedoAction
            extends AbstractAction
    {

      public RedoAction(String s, Icon i)
      {
        super(s, i);
      }

      public void actionPerformed(ActionEvent e)
      {
        if(undoManager.canRedo() == true)
        {
          undoManager.redo();
        }
      }
    }

    redoa = new RedoAction(
            TexteditorResources.getString("Btn_Redo"),
            (Icon) new ImageIcon(this.getClass().getClassLoader().getResource("icons/arrow_redo.png")));
    //(Icon) new ImageIcon(this.getClass().getClassLoader().getResource("toolbarButtonGraphics/general/Redo24.gif")));

    menuitem = new JMenuItem(redoa);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_Redo_Mnemonic").charAt(0));
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
    redoa.setEnabled(false);
    menu.add(menuitem);

    menu.add(new JSeparator());

    redoToolbarButton = new JButton(redoa);
    redoToolbarButton.setText("");
    redoToolbarButton.setToolTipText(TexteditorResources.getString("Btn_Redo_Tooltip"));
    toolbar.add(redoToolbarButton);

    toolbar.add(new JToolBar.Separator());

    // Create the clipboard menu items
    cutMenuItem = new JMenuItem("Cut", 'u');
    menu.add(cutMenuItem);
    cutToolbarButton = new JButton();
    toolbar.add(cutToolbarButton);

    copyMenuItem = new JMenuItem("Copy", 'c');
    menu.add(copyMenuItem);
    copyToolbarButton = new JButton();
    toolbar.add(copyToolbarButton);

    pasteMenuItem = new JMenuItem("Paste", 'p');
    menu.add(pasteMenuItem);
    pasteToolbarButton = new JButton();
    toolbar.add(pasteToolbarButton);

    toolbar.add(new JToolBar.Separator());
    menu.add(new JSeparator());

    // find
    action = factory.getEditFindAction();
    menuitem = new JMenuItem(action);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_Find_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.SearchOperation.FIND.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
    menuitem.addActionListener(this);
    menu.add(menuitem);

    // find again
    action = factory.getEditFindAgainAction();
    menuitem = new JMenuItem(action);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_FindAgain_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.SearchOperation.FINDAGAIN.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
    menuitem.addActionListener(this);
    menu.add(menuitem);

    // select all
    menuitem = new JMenuItem(TexteditorResources.getString("Btn_SelectAll"));
    menuitem.setMnemonic(TexteditorResources.getString("Btn_SelectAll_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.SearchOperation.SELECTALL.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    menuitem.addActionListener(this);
    menu.add(menuitem);
    menu.add(new JSeparator());

    // insert control character
    menuitem = new JMenuItem(TexteditorResources.getString("Btn_InsertCtrlChar"));
    menuitem.setMnemonic(TexteditorResources.getString("Btn_InsertCtrlChar_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.EditOperation.INSERTCTRLCHAR.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
    menuitem.addActionListener(this);
    menu.add(menuitem);

    // Add the Edit menu to menubar
    menubar.add(menu);

    // Create the Format menu
    menu = new JMenu(TexteditorResources.getString("Menu_Format"));
    menu.setMnemonic(TexteditorResources.getString("Menu_Format_Mnemonic").charAt(0));

    // Create the menu items
    menuitem = new JMenuItem(TexteditorResources.getString("Btn_Font"));
    menuitem.setMnemonic(TexteditorResources.getString("Btn_Font_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.SettingsOperation.FONT.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
    menuitem.addActionListener(this);
    menu.add(menuitem);

    menuitem = new JMenuItem(TexteditorResources.getString("Btn_TabSize"));
    menuitem.setMnemonic(TexteditorResources.getString("Btn_TabSize_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.SettingsOperation.TABSIZE.toString());
    menuitem.addActionListener(this);
    menu.add(menuitem);

    cbMenuitem = new JCheckBoxMenuItem(TexteditorResources.getString("Btn_LineWrap"), lineWrap);
    cbMenuitem.setMnemonic(TexteditorResources.getString("Btn_LineWrap_Mnemonic").charAt(0));
    cbMenuitem.addItemListener(this);
    menu.add(cbMenuitem);

    JMenu subMenu = new JMenu(TexteditorResources.getString("Btn_Eol"));


    subMenu.setMnemonic(TexteditorResources.getString("Btn_Eol_Mnemonic").charAt(0));
    bgLineEnding = new ButtonGroup();

    rbLineEndingWindows = new JRadioButtonMenuItem(TexteditorResources.getString("Btn_Eol_Windows"));
    rbLineEndingWindows.setMnemonic(TexteditorResources.getString("Btn_Eol_Windows_Mnemonic").charAt(0));
    rbLineEndingWindows.setActionCommand(Enums.SettingsOperation.EOL.toString());
    rbLineEndingWindows.addActionListener(this);


    bgLineEnding.add(rbLineEndingWindows);
    subMenu.add(rbLineEndingWindows);

    rbLineEndingUnix = new JRadioButtonMenuItem(TexteditorResources.getString("Btn_Eol_Unix"));
    rbLineEndingUnix.setMnemonic(TexteditorResources.getString("Btn_Eol_Unix_Mnemonic").charAt(0));
    rbLineEndingUnix.setActionCommand(Enums.SettingsOperation.EOL.toString());
    rbLineEndingUnix.addActionListener(this);
    bgLineEnding.add(rbLineEndingUnix);
    subMenu.add(rbLineEndingUnix);

    rbLineEndingMac = new JRadioButtonMenuItem(TexteditorResources.getString("Btn_Eol_Mac"));
    rbLineEndingMac.setMnemonic(TexteditorResources.getString("Btn_Eol_Mac_Mnemonic").charAt(0));
    rbLineEndingMac.setActionCommand(Enums.SettingsOperation.EOL.toString());
    rbLineEndingMac.addActionListener(this);
    bgLineEnding.add(rbLineEndingMac);
    subMenu.add(rbLineEndingMac);

    rbLineEndingDefault = new JRadioButtonMenuItem(TexteditorResources.getString("Btn_Eol_Default"));
    rbLineEndingDefault.setMnemonic(TexteditorResources.getString("Btn_Eol_Default_Mnemonic").charAt(0));
    rbLineEndingDefault.setActionCommand(Enums.SettingsOperation.EOL.toString());
    rbLineEndingDefault.addActionListener(this);
    bgLineEnding.add(rbLineEndingDefault);


    subMenu.add(rbLineEndingDefault);
    rbLineEndingDefault.setSelected(true);
    menu.add(subMenu);

    // Add the Settings menu to menubar
    menubar.add(menu);

    // Create the Help menu
    menu = new JMenu(TexteditorResources.getString("Menu_Help"));


    menu.setMnemonic(TexteditorResources.getString("Menu_Help_Mnemonic").charAt(0));

    // Create the menu items
    // about
    action = factory.getHelpAboutAction();
    menuitem = new JMenuItem(action);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_About_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.HelpOperation.ABOUT.toString());
    menuitem.addActionListener(this);
    menu.add(menuitem);

    // help
    action = factory.getHelpHelpAction();
    menuitem = new JMenuItem(action);
    menuitem.setMnemonic(TexteditorResources.getString("Btn_Help_Mnemonic").charAt(0));
    menuitem.setActionCommand(Enums.HelpOperation.HELP.toString());
    menuitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    menuitem.addActionListener(this);


    menu.add(menuitem);

    // Add the Help menu to menubar
    menubar.add(Box.createHorizontalGlue());
    menubar.add(menu);

    // Add the menubar to frame
    frame.setJMenuBar(menubar);
    // Add the toolbar to frame
    frame.add(toolbar, BorderLayout.PAGE_START);
    // Add the JTextArea
    sp = new JScrollPane();

    ta = createNewTextArea(sp);

    if(size == null)
    {
      ta.setColumns(80);
      ta.setRows(25);
    }
    size = sp.getPreferredSize();


    ta.getDocument().addDocumentListener(new DocumentEventHandlerClass());
    undoManager = new UndoManager();
    ta.getDocument().addUndoableEditListener(undoManager);
    ta.addCaretListener(new CaretEventHandlerClass());
    ta.addComponentListener(
            new ComponentAdapter()
            {

              @Override
              public void componentResized(
                      ComponentEvent ce)
              {
                size = sp.getSize();
              }
              /*
               * public void componentMoveded(ComponentEvent ce) { if(ce.getSource() ==
               * frame) { location = ((JFrame)ce.getSource()).getLocation(); } }
               */
            });

    // add mouse listener
    ta.addMouseListener(new MouseEventHandlerClass());

    // Get Actions from DefaultEditorKit for edit operations
    createTextEditActions(ta);

    // add textarea to scrollpane
    frame.add(sp, BorderLayout.CENTER);

    // add the status bar
    statusBar = Box.createHorizontalBox();
    // statusBar.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
    statusBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    // statusBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

    statusLine = new JLabel();
    statusLine.setVerticalAlignment(JLabel.CENTER);
    statusLine.setFont(statusLine.getFont().deriveFont(8));
    statusBar.add(statusLine, BorderLayout.WEST);

    statusBar.add(Box.createHorizontalGlue());

    memInfoLine = new JLabel(TexteditorResources.getString("Memory_usage"));


    memInfoLine.setVerticalAlignment(JLabel.CENTER);


    memInfoLine.setHorizontalAlignment(JLabel.RIGHT);


    memInfoLine.setFont(memInfoLine.getFont().deriveFont(8));
    statusBar.add(memInfoLine, BorderLayout.EAST);

    UpdateThread dummy = new UpdateThread(statusLine, buildStatusLine(statusLine));

    // add status bar to frame
    frame.add(statusBar, BorderLayout.SOUTH);

    // Display the main window.
    enableButtons();
    frame.pack();
    frame.setVisible(
            true);
    documentEventHandlerEnabled = true;
    caretEventHandlerEnabled = true;


    ta.setCaretPosition(0);
  }

// create a new JTextArea in a JScrollPane
  private JTextArea createNewTextArea(JScrollPane scrollpane)
  {
    JTextArea textArea = new JTextArea();
    if(currentFont == null)
    {
      currentFont = textArea.getFont();
    }

    textArea.setFont(currentFont);
    textArea.setTabSize(tabsize);
    textArea.setLineWrap(lineWrap);
    textArea.setWrapStyleWord(textArea.getLineWrap());
    textArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    scrollpane.setViewportView(textArea);
    scrollpane.setPreferredSize(size);
    textArea.setEditable(!isReadOnly);
    return textArea;
  }

  /**
   * @return get application name
   */
  public String getAppname()
  {
    return appName;
  }

  // catch ActionEvents
  public void actionPerformed(ActionEvent ae)
  {
    String actioncommand = ae.getActionCommand();

    if(actioncommand.equals(Enums.FileOperation.EXIT.toString()))
    {
      windowClosingEventHandler();
    }

    if(actioncommand.equals(Enums.FileOperation.SAVE.toString()))
    {
      if(saveFile(frame, file) == true)
      {
        hasChanged = false;
      }
    }

    if(actioncommand.equals(Enums.FileOperation.NEWINST.toString()))
    {
      Texteditor dummy = new Texteditor(null);
    }

    if(actioncommand.equals(Enums.FileOperation.SAVEAS.toString()))
    {
      File saveAsPath = null;

      try
      {
        if(file != null)
        {
          saveAsPath = file.getCanonicalFile();
        }
      }
      catch(NullPointerException e)
      {
      }
      catch(IOException e)
      {
      }

      if(saveFileAs(frame, saveAsPath) == true)
      {
        hasChanged = false;
        if(!isReadOnly)
        {
          fsa.setEnabled(true);
        }
        //HelperClass.getMenuItemByActionCommand(menubar,
        //        Enums.FileOperation.SAVE.toString()).setEnabled(true);

        try
        {
          fileString = file.getCanonicalPath();
          frame.setTitle(fileString + " - " + appName);

          if(recentListModel.contains(fileString) == false)
          {
            recentListModel.add(0, fileString);
          }
          else
          {
            int i = recentListModel.indexOf(fileString);
            if(i != 0)
            {
              recentListModel.remove(i);
              recentListModel.add(0, fileString);
            }
          }
          while(recentListModel.getSize() > 4)
          {
            recentListModel.remove(4);
          }
        }
        catch(IOException ioe)
        {
          frame.setTitle(appName);
        }
        catch(NullPointerException npe)
        {
          frame.setTitle(appName);
        }
      }
    }

    if(actioncommand.equals(Enums.FileOperation.RECENT.toString()))
    {
      RecentDialog dlg = new RecentDialog(frame, recent);
      recentFileSelected(dlg.getSelectedIndex());
    }

    if(actioncommand.equals(Enums.FileOperation.PRINT.toString()))
    {
      String footer = TexteditorResources.getString("Page") + " {0}";

      PrinterJob pj = PrinterJob.getPrinterJob();
      PageFormat pf = pj.pageDialog(pj.defaultPage());
      Printable p = ta.getPrintable(null, new MessageFormat(footer));
      pj.setPrintable(p, pf);

      try
      {
        if(pj.printDialog())
        {
          pj.print();
        }
        //ta.print(null, new MessageFormat(footer));
      }
      catch(PrinterException e)
      {
      }
    }

    if(actioncommand.equals(Enums.SettingsOperation.FONT.toString()))
    {
      FontDialog dlg = new FontDialog(frame, true, currentFont,
              currentEncoding, byteOrderMark);

      if(dlg.getOption() == JOptionPane.OK_OPTION)
      {
        if(dlg.getSelectedFont() != null)
        {
          currentFont = dlg.getSelectedFont();
          ta.setFont(currentFont);
        }

        if(dlg.getSelectedEncoding() != null)
        {
          currentEncoding = dlg.getSelectedEncoding();
          currentDecoding = currentEncoding;
        }
        byteOrderMark = dlg.isByteOrderMark();
      }
      UpdateThread dummy = new UpdateThread(statusLine, buildStatusLine(statusLine));

      return;
    }

    if(actioncommand.equals(Enums.SettingsOperation.TABSIZE.toString()))
    {
      TabsizeDialog dlg = new TabsizeDialog(frame, true, tabsize);

      if(dlg.getOption() == JOptionPane.OK_OPTION)
      {
        tabsize = dlg.getSelection();
        ta.setTabSize(tabsize);
      }

      return;
    }

    if(actioncommand.equals(Enums.SettingsOperation.EOL.toString()))
    {
      Document doc = null;

      try
      {
        doc = ta.getDocument();
        if(ae.getSource() == rbLineEndingWindows)
        {
          doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
        }
        else if(ae.getSource() == rbLineEndingUnix)
        {
          doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
        }
        else if(ae.getSource() == rbLineEndingMac)
        {
          doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r");
        }
        else
        {
          doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, System.getProperty("line.separator"));
        }

      }
      catch(NullPointerException npe)
      {
        npe.printStackTrace();
      }

      return;
    }

    if(actioncommand.equals(Enums.SearchOperation.FIND.toString()))
    {
      dialog = new SearchDialog(frame, false, searchString, replaceString,
              matchCase, holeWords, ta, this);

      HelperClass.getMenuItemByActionCommand(menubar,
              Enums.SearchOperation.FIND.toString()).setEnabled(false);
      HelperClass.getMenuItemByActionCommand(menubar,
              Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(false);
      HelperClass.getMenuItemByActionCommand(popupMenu,
              Enums.SearchOperation.FIND.toString()).setEnabled(false);
      HelperClass.getMenuItemByActionCommand(popupMenu,
              Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(false);

      dialog.setVisible(true);
      return;
    }

    if(actioncommand.equals(Enums.SearchOperation.FINDAGAIN.toString()))
    {
      // perform a FINDNEXT
      if((searchString != null) && !(searchString.equals("")))
      {
        if(matchCase == true)
        {
          caretPosition = ta.getText().indexOf(searchString, caretPosition);
          if(caretPosition < 0)
          {
            caretPosition = 0;
            caretPosition = ta.getText().indexOf(searchString, caretPosition);
          }

          if(caretPosition != -1)
          {
            ta.setCaretPosition(caretPosition);
            ta.moveCaretPosition(caretPosition + searchString.length());
            caretPosition = ta.getCaretPosition();
          }

        }
        else
        {
          String lowerCaseSearchString = searchString.toLowerCase();
          String textToSearchIn = ta.getText().toLowerCase();
          caretPosition = textToSearchIn.indexOf(lowerCaseSearchString,
                  caretPosition);
          if(caretPosition < 0)
          {
            caretPosition = 0;
            caretPosition = textToSearchIn.indexOf(lowerCaseSearchString,
                    caretPosition);
          }

          if(caretPosition != -1)
          {
            ta.setCaretPosition(caretPosition);
            ta.moveCaretPosition(caretPosition + lowerCaseSearchString.length());
            caretPosition = ta.getCaretPosition();
          }

        }
      }
      else // perform a FIND
      {
        /*
         * JButton btn = new JButton();
         * btn.setActionCommand(Enums.SearchOperation.FIND.toString());
         * btn.addActionListener(this); btn.doClick();
         */
      }
      return;
    }

    if(actioncommand.equals(Enums.SearchOperation.SELECTALL.toString()))
    {
      ta.selectAll();
      return;
    }

    if(actioncommand.equals(Enums.HelpOperation.HELP.toString()))
    {
      HelpDialog ignored = new HelpDialog(frame, false);
      return;
    }

    if(actioncommand.equals(Enums.EditOperation.INSERTCTRLCHAR.toString()))
    {
      CtrlCharDialog dlg = new CtrlCharDialog(frame, true, lastCtrlChar);

      if(dlg.getOption() == JOptionPane.OK_OPTION)
      {
        lastCtrlChar = dlg.getSelection();
        char[] ctrlCharArray = new char[]
        {
          (char) lastCtrlChar
        };
        ta.insert(new String(ctrlCharArray), ta.getCaretPosition());
      }
      return;
    }

    if(actioncommand.equals(Enums.HelpOperation.ABOUT.toString()))
    {
      AboutDialog ignored = new AboutDialog(frame, true);
      return;
    }

    if(actioncommand.equals(Enums.EditOperation.UNDO.toString()))
    {
      if(undoManager.canUndo() == true)
      {
        undoManager.undo();
      }
    }

    if(actioncommand.equals(Enums.EditOperation.REDO.toString()))
    {
      if(undoManager.canRedo() == true)
      {
        undoManager.redo();
      }
    }
  }

  /**
   * catch ItemEvents for JCheckboxMenuItem
   */
  public void itemStateChanged(ItemEvent e)
  {
    JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getItem();
    lineWrap = item.isSelected();
    ta.setLineWrap(lineWrap);
    ta.setWrapStyleWord(ta.getLineWrap());
  }

  private void initTextArea()
  {
    sp.setVisible(false);
    frame.remove(sp);
    sp = newSp;
    ta = newTa;
    ta.getDocument().addDocumentListener(new DocumentEventHandlerClass());
    undoManager = new UndoManager();
    ta.getDocument().addUndoableEditListener(undoManager);
    ta.addCaretListener(new CaretEventHandlerClass());
    ta.addComponentListener(new ComponentAdapter()
    {

      @Override
      public void componentResized(ComponentEvent ce)
      {
        size = sp.getSize();
      }
    });
    ta.setCaretPosition(0);
    ta.setTabSize(tabsize);

    // add mouse listener
    ta.addMouseListener(new MouseEventHandlerClass());

    // Get Actions from DefaultEditorKit for edit operations
    createTextEditActions(ta);

    frame.add(sp, BorderLayout.CENTER);
    if(!isReadOnly)
    {
      fsa.setEnabled(true);
    }
    //HelperClass.getMenuItemByActionCommand(menubar,
    //        Enums.FileOperation.SAVE.toString()).setEnabled(true);

    try
    {
      fileString = file.getCanonicalPath();
      frame.setTitle(fileString + " - " + appName);
    }
    catch(IOException ioe)
    {
      frame.setTitle(appName);
    }
    catch(NullPointerException npe)
    {
      frame.setTitle(appName);
    }
    frame.pack();

    sp.setVisible(true);
    UpdateThread dummy = new UpdateThread(statusLine, buildStatusLine(statusLine));

    hasChanged = false;
  }

  /**
   * catch WindowEvents
   */
  private class WindowClosingEventHandlerClass
          extends WindowAdapter
  {

    @Override
    public void windowClosing(WindowEvent e)
    {
      windowClosingEventHandler();
    }
  }

  /**
   * handles the WindowClosing event also called by menu close
   */
  private void windowClosingEventHandler()
  {
    if(hasChanged == false)
    {
      finalize();
      if(removeInstanceFromList())
      {
        System.exit(0);
      }
    }
    else
    {
      int answer = 0;

      while(true)
      {
        answer = JOptionPane.showConfirmDialog(frame,
                TexteditorResources.getString("Save_Changes"), frame.getTitle(),
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if(answer == JOptionPane.NO_OPTION)
        {
          finalize();
          if(removeInstanceFromList())
          {
            System.exit(0);
          }
        }

        if(answer == JOptionPane.YES_OPTION)
        {
          if(saveFile(frame, file) == true)
          {
            finalize();
            if(removeInstanceFromList())
            {
              System.exit(0);
            }
          }
        }
        else
        {
          break;
        }
      }
    }
  }

  private boolean removeInstanceFromList()
  {
    InstanceList.remove(this);
    this.frame.setVisible(false);
    this.frame.dispose();

    if(InstanceList.isEmpty())
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * handles the file saving procedures
   *
   * @returnvalue true if file successfully saved
   * @todo do this in a separate task
   */
  private boolean saveFile(Frame parent, File saveFilePath)
  {
    boolean result = false;

    documentEventHandlerEnabled = false;
    caretEventHandlerEnabled = false;

    File path = saveFilePath;

    try
    {
      if(path == null || path.isDirectory())
      {
        FileDialog dlg = new FileDialog(parent,
                Enums.FileDialogMethod.SAVEAS, saveFilePath);
        path = dlg.getPath();
      }

      FileOutputStream os = new FileOutputStream(path);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,
              currentEncoding));

      //write byte order mark
      if(byteOrderMark && currentEncoding.equals("UTF-8"))
      {
        os.write(0xEF);
        os.write(0xBB);
        os.write(0xBF);
        os.flush();
      }
      else if(byteOrderMark && !(currentEncoding.equals("UTF-8")))
      {
        writer.write("\uFEFF");
        writer.flush();
      }
      else
      {
      }
      ta.write(writer);
      writer.close();
      file = path;
      result = true;
    }
    catch(NullPointerException e)
    {
      result = false;
    }
    catch(FileNotFoundException e)
    {
      JOptionPane.showMessageDialog(frame,
              TexteditorResources.getString("SaveError") + " " + path.toString(), frame.getTitle(),
              JOptionPane.INFORMATION_MESSAGE);
      result = false;
    }
    catch(IOException e)
    {
      JOptionPane.showMessageDialog(frame,
              TexteditorResources.getString("SaveError") + " " + path.toString(), frame.getTitle(),
              JOptionPane.INFORMATION_MESSAGE);
      result = false;
    }

    documentEventHandlerEnabled = true;
    caretEventHandlerEnabled = true;
    return result;
  }

  private void fileNewAction()
  {
    int answer = 0;
    while(hasChanged == true)
    {
      answer = JOptionPane.showConfirmDialog(frame,
              TexteditorResources.getString("Save_Changes"),
              frame.getTitle(),
              JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.QUESTION_MESSAGE);

      if(answer == JOptionPane.NO_OPTION)
      {
        hasChanged = false;
        continue;
      }

      if(answer == JOptionPane.YES_OPTION)
      {
        if(saveFile(frame, file) == true)
        {
          hasChanged = false;
          continue;
        }
      }
      else
      {
        return;
      }
    }
    file = null;
    newSp = new JScrollPane();
    newTa = createNewTextArea(newSp);

    initTextArea();

    return;
  }

  /**
   * handles the file savingAs procedures
   *
   * @returnvalue true if file successfully saved
   * @todo do this in a separate task
   */
  private boolean saveFileAs(Frame parent, File saveFilePath)
  {
    boolean result = false;

    documentEventHandlerEnabled = false;
    caretEventHandlerEnabled = false;

    File path = saveFilePath;

    try
    {
      FileDialog dlg = new FileDialog(parent,
              Enums.FileDialogMethod.SAVEAS,
              saveFilePath);
      path = dlg.getPath();

      FileOutputStream os = new FileOutputStream(path);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,
              currentEncoding));

      //write byte order mark
      if(byteOrderMark && currentEncoding.equals("UTF-8"))
      {

        os.write(0xEF);
        os.write(0xBB);
        os.write(0xBF);
        os.flush();
      }
      else if(byteOrderMark && !(currentEncoding.equals("UTF-8")))
      {
        writer.write("\uFEFF");
        writer.flush();
      }
      else
      {
      }
      ta.write(writer);
      writer.close();
      file = path;
      result = true;
    }
    catch(NullPointerException e)
    {
      result = false;
    }
    catch(FileNotFoundException e)
    {
      JOptionPane.showMessageDialog(frame,
              TexteditorResources.getString("SaveError") + " " + path.toString(), frame.getTitle(),
              JOptionPane.INFORMATION_MESSAGE);
      result = false;
    }
    catch(IOException e)
    {
      JOptionPane.showMessageDialog(frame,
              TexteditorResources.getString("SaveError") + " " + path.toString(), frame.getTitle(),
              JOptionPane.INFORMATION_MESSAGE);
      result = false;
    }

    documentEventHandlerEnabled = true;
    caretEventHandlerEnabled = true;
    return result;
  }

  /**
   * handles the file opening procedures
   *
   * @returnvalue true if file successfully opened
   */
  private boolean openFile(Frame parent, File openFilePath)
  {
    boolean result = false;

    File path = openFilePath;

    FileDialog dlg = new FileDialog(parent,
            Enums.FileDialogMethod.OPEN,
            openFilePath);
    path = dlg.getPath();

    if(path != null)
    {
      result = readFile(path);
    }

    return result;
  }

  private void fileOpenAction()
  {
    int answer = 0;
    while(hasChanged == true)
    {
      answer = JOptionPane.showConfirmDialog(frame,
              TexteditorResources.getString("Save_Changes"),
              frame.getTitle(),
              JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.QUESTION_MESSAGE);

      if(answer == JOptionPane.NO_OPTION)
      {
        hasChanged = false;
        continue;
      }

      if(answer == JOptionPane.YES_OPTION)
      {
        if(saveFile(frame, file) == true)
        {
          hasChanged = false;
          continue;
        }
      }
      else
      {
        return;
      }

    }

    newSp = new JScrollPane();
    newTa = createNewTextArea(newSp);
    if(openFile(frame, file) == true)
    {
      initTextArea();
      getLineEnding();

      try
      {
        String filePath = file.getCanonicalPath();
        if(recentListModel.contains(filePath) == false)
        {
          recentListModel.add(0, filePath);
        }
        else
        {
          int i = recentListModel.indexOf(filePath);
          if(i != 0)
          {
            recentListModel.remove(i);
            recentListModel.add(0, filePath);
          }
        }
        while(recentListModel.getSize() > 4)
        {
          recentListModel.remove(4);
        }
      }
      catch(IOException e)
      {
      }
    }
    else
    {
      newTa = null;
    }

    return;
  }

  private boolean readFile(File path)
  {
    boolean result = false;

    try
    {
      documentEventHandlerEnabled = false;
      caretEventHandlerEnabled = false;

      if(path.canRead() == false)
      {
        result = false;
      }
      else
      {
        FileInputStream is = new FileInputStream(path);
        // guess encoding UTF-16/UTF-32
        is = new FileInputStream(path);

        byte[] header32 = new byte[4];
        is.read(header32, 0, header32.length);
        is.close();

        int ihdr32_0 = header32[0] << 24;
        ihdr32_0 &= 0xFF000000;

        int ihdr32_1 = header32[1] << 16;
        ihdr32_1 &= 0x00FF0000;

        int ihdr32_2 = header32[2] << 8;
        ihdr32_2 &= 0x0000FF00;

        int ihdr32_3 = header32[3];
        ihdr32_3 &= 0x000000FF;

        int ihdr16 = ihdr32_0 | ihdr32_1;
        ihdr16 = ihdr16 >> 16;
        ihdr16 &= 0x0000FFFF;

        int ihdr32 = ihdr32_0 | ihdr32_1 | ihdr32_2 | ihdr32_3;

        if(ihdr32 == 0x0000FEFF)
        {
          currentDecoding = "UTF-32";
          currentEncoding = "UTF-32BE";
          byteOrderMark = true;
        }
        else if(ihdr32 == 0xFFFE0000)
        {
          currentDecoding = "UTF-32";
          currentEncoding = "UTF-32LE";
          byteOrderMark = true;
        }
        else if(ihdr16 == 0xFEFF)
        {
          currentDecoding = "UTF-16";
          currentEncoding = "UTF-16BE";
          byteOrderMark = true;
        }
        else if(ihdr16 == 0xFFFE)
        {
          currentDecoding = "UTF-16";
          currentEncoding = "UTF-16LE";
          byteOrderMark = true;
        }
        else
        {
          currentDecoding = currentEncoding;
          byteOrderMark = false;
        }

        // guess encoding UTF-8
        is = new FileInputStream(path);
        int utf8type = HelperClass.isUTF_8(is);
        is.close();

        if(utf8type == HelperClass.UTF8BOM)
        {
          currentDecoding = "UTF-8";
          currentEncoding = "UTF-8";
          byteOrderMark = true;
        }
        else if(utf8type == HelperClass.UTF8)
        {
          currentDecoding = "UTF-8";
          currentEncoding = "UTF-8";
          byteOrderMark = false;
        }
        else
        {
        }

        is = new FileInputStream(path);
        BufferedReader rdr = new BufferedReader(
                new InputStreamReader(is, currentDecoding));

        //@TODO TEST this workaround to skip first 3 bytes if UTF-8 && byteOrderMark
        if(utf8type == HelperClass.UTF8BOM)
        {
          byte[] dummy = new byte[3];
          is.read(dummy);
          dummy = null;
        }
        newTa.read(rdr, null);
        rdr.close();
        file = path;
        newTa.setCaretPosition(0);
        result = true;
      }

    }
    catch(NullPointerException e)
    {
      e.printStackTrace();
      result = false;
    }
    catch(UnsupportedEncodingException e)
    {
      e.printStackTrace();
      result = false;
    }
    catch(FileNotFoundException e)
    {
      e.printStackTrace();
      result = false;
    }
    catch(IOException e)
    {
      e.printStackTrace();
      result = false;
    }

    documentEventHandlerEnabled = true;
    caretEventHandlerEnabled = true;
    return result;
  }

  public void setSearchString(String searchString)
  {
    if(searchString != null && !(searchString.equals("")))
    {
      this.searchString = searchString;
      HelperClass.getMenuItemByActionCommand(menubar,
              Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(true);
      HelperClass.getMenuItemByActionCommand(popupMenu,
              Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(true);
    }

    HelperClass.getMenuItemByActionCommand(menubar,
            Enums.SearchOperation.FIND.toString()).setEnabled(true);
    HelperClass.getMenuItemByActionCommand(popupMenu,
            Enums.SearchOperation.FIND.toString()).setEnabled(true);
  }

  public void setReplaceString(String replaceString)
  {
    this.replaceString = replaceString;
  }

  public void setMatchCase(boolean matchCase)
  {
    this.matchCase = matchCase;
  }

  public void setHoleWords(boolean holeWords)
  {
    this.holeWords = holeWords;
  }

  private void recentFileSelected(int selectedIndex)
  {
    int answer = JOptionPane.YES_OPTION;
    while(hasChanged == true)
    {
      answer = JOptionPane.showConfirmDialog(frame,
              TexteditorResources.getString("Save_Changes"),
              frame.getTitle(),
              JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.QUESTION_MESSAGE);

      if(answer == JOptionPane.NO_OPTION)
      {
        hasChanged = false;
        continue;
      }

      if(answer == JOptionPane.YES_OPTION)
      {
        if(saveFile(frame, file) == true)
        {
          hasChanged = false;
          continue;
        }
      }
      else
      {
        return;
      }
    }

    if(selectedIndex > -1)
    {
      try
      {
        fileString = (String) recentListModel.getElementAt(selectedIndex);
        file = new File(fileString);
        if(file != null)
        {
          newSp = new JScrollPane();
          newTa = createNewTextArea(newSp);
          if(readFile(file) == true)
          {
            Object openedFile = recentListModel.remove(selectedIndex);
            recentListModel.add(0, openedFile);
            initTextArea();
            getLineEnding();
          }
          else
          {
            String msg = String.format("%s\n%s",
                    TexteditorResources.getString("FileNotFoundError"),
                    fileString);
            JOptionPane.showMessageDialog(frame, msg, appName, JOptionPane.ERROR_MESSAGE);
            newTa = null;
          }

        }
        // close the menu
        javax.accessibility.AccessibleContext ac = menubar.getAccessibleContext();
        if(ac != null)
        {
          ac.getAccessibleSelection().clearAccessibleSelection();
          recent.clearSelection();
        }
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
      }
    }
  }

  /**
   * handles mouseclicks
   */
  private class MouseEventHandlerClass
          extends MouseAdapter
  {

    private JMenuItem menuItem;
    private ButtonActionFactory factory = null;
    private AbstractAction action;

    public MouseEventHandlerClass()
    {
      super();
      // Create the popup menu
      popupMenu = new JPopupMenu();

      // Fill the popup menu
      factory = new ButtonActionFactory();

      // menuItem = new JMenuItem(TexteditorResources.getString("Btn_Undo"));
      action = factory.getEditUndoAction();
      menuItem = new JMenuItem(action);
      menuItem.setActionCommand(Enums.EditOperation.UNDO.toString());
      menuItem.addActionListener(Texteditor.this);
      menuItem.setEnabled(false);
      popupMenu.add(menuItem);

      // menuItem = new JMenuItem(TexteditorResources.getString("Btn_Redo"));
      action = factory.getEditRedoAction();
      menuItem = new JMenuItem(action);
      menuItem.setActionCommand(Enums.EditOperation.REDO.toString());
      menuItem.addActionListener(Texteditor.this);
      menuItem.setEnabled(false);
      popupMenu.add(menuItem);

      popupMenu.add(new JSeparator());

      popupCutMenuItem = new JMenuItem(TexteditorResources.getString("Btn_Cut"));
      popupCopyMenuItem = new JMenuItem(TexteditorResources.getString("Btn_Copy"));
      popupPasteMenuItem = new JMenuItem(TexteditorResources.getString("Btn_Redo"));

      createTextEditPopupActions(ta);

      popupMenu.add(popupCutMenuItem);
      popupMenu.add(popupCopyMenuItem);
      popupMenu.add(popupPasteMenuItem);

      popupMenu.add(new JSeparator());

      action = factory.getEditFindAction();
      menuItem = new JMenuItem(action);
      menuItem.setActionCommand(Enums.SearchOperation.FIND.toString());
      menuItem.addActionListener(Texteditor.this);
      popupMenu.add(menuItem);

      action = factory.getEditFindAgainAction();
      menuItem = new JMenuItem(action);
      menuItem.setActionCommand(Enums.SearchOperation.FINDAGAIN.toString());
      menuItem.addActionListener(Texteditor.this);
      popupMenu.add(menuItem);

      menuItem = new JMenuItem(TexteditorResources.getString("Btn_SelectAll"));
      menuItem.setActionCommand(Enums.SearchOperation.SELECTALL.toString());
      menuItem.addActionListener(Texteditor.this);
      popupMenu.add(menuItem);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
      mouseEventHandler(e);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
      mouseEventHandler(e);
    }

    private void mouseEventHandler(MouseEvent e)
    {
      // if(e.getButton() == MouseEvent.BUTTON3)
      if(e.isPopupTrigger())
      {

        if((searchString != null) && !(searchString.equals("")))
        {
          HelperClass.getMenuItemByActionCommand(popupMenu,
                  Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(true);
        }
        else
        {
          HelperClass.getMenuItemByActionCommand(popupMenu,
                  Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(false);
        }
        enableButtons();
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  /**
   * handles CaretEvents to get the actual cursor position
   */
  private class CaretEventHandlerClass
          implements CaretListener
  {

    public void caretUpdate(CaretEvent e)
    {
      caretEventHandler(e);
    }

    private void caretEventHandler(CaretEvent e)
    {
      if(caretEventHandlerEnabled == true)
      {
        caretPosition = ta.getCaretPosition();
        try
        {
          currentLine = ta.getLineOfOffset(caretPosition) + 1;
          currentCol = caretPosition - ta.getLineStartOffset(currentLine - 1) + 1;
        }
        catch(BadLocationException ble)
        {
          currentLine = -1;
          currentCol = -1;
        }
        UpdateThread dummy = new UpdateThread(statusLine, buildStatusLine(statusLine));
      }
      enableButtons();
    }
  }

  private void enableButtons()
  {
    // Enable-Disable menu items
    if(undoManager.canUndo())
    {
      undoa.setEnabled(true);
      //HelperClass.getMenuItemByActionCommand(menubar,
      //        Enums.EditOperation.UNDO.toString()).setEnabled(true);
      HelperClass.getMenuItemByActionCommand(popupMenu,
              Enums.EditOperation.UNDO.toString()).setEnabled(true);
    }
    else
    {
      undoa.setEnabled(false);
      //HelperClass.getMenuItemByActionCommand(menubar,
      //        Enums.EditOperation.UNDO.toString()).setEnabled(false);
      HelperClass.getMenuItemByActionCommand(popupMenu,
              Enums.EditOperation.UNDO.toString()).setEnabled(false);
    }

    if(undoManager.canRedo())
    {
      redoa.setEnabled(true);
      //HelperClass.getMenuItemByActionCommand(menubar,
      //        Enums.EditOperation.REDO.toString()).setEnabled(true);
      HelperClass.getMenuItemByActionCommand(popupMenu,
              Enums.EditOperation.REDO.toString()).setEnabled(true);
    }
    else
    {
      redoa.setEnabled(false);
      //HelperClass.getMenuItemByActionCommand(menubar,
      //        Enums.EditOperation.REDO.toString()).setEnabled(false);
      HelperClass.getMenuItemByActionCommand(popupMenu,
              Enums.EditOperation.REDO.toString()).setEnabled(false);
    }

    if(ta.getSelectedText() != null)
    {
      cutMenuItem.setEnabled(true);
      popupCutMenuItem.setEnabled(true);

      copyMenuItem.setEnabled(true);
      popupCopyMenuItem.setEnabled(true);
    }
    else
    {
      cutMenuItem.setEnabled(false);
      popupCutMenuItem.setEnabled(false);

      copyMenuItem.setEnabled(false);
      popupCopyMenuItem.setEnabled(false);
    }

    if((searchString != null) && !(searchString.equals("")))
    {
      HelperClass.getMenuItemByActionCommand(menubar,
              Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(true);
      HelperClass.getMenuItemByActionCommand(popupMenu,
              Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(true);
    }
    else
    {
      HelperClass.getMenuItemByActionCommand(menubar,
              Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(false);
      HelperClass.getMenuItemByActionCommand(popupMenu,
              Enums.SearchOperation.FINDAGAIN.toString()).setEnabled(false);
    }
  }

  /**
   * handles DocmentEvents to trace changes
   */
  private class DocumentEventHandlerClass
          implements DocumentListener
  {

    public void changedUpdate(DocumentEvent e)
    {
      documentEventHandler(e);
    }

    public void insertUpdate(DocumentEvent e)
    {
      documentEventHandler(e);
    }

    public void removeUpdate(DocumentEvent e)
    {
      documentEventHandler(e);
    }

    private void documentEventHandler(DocumentEvent e)
    {
      if(documentEventHandlerEnabled == true)
      {
        hasChanged = true;
        // System.out.println("DocumentEvent occured");
      }
    }
  }

  private void createTextEditActions(JTextComponent textComponent)
  {
    URL imgURL = null;
    String imgName = null;

    editActions = new HashMap<String, Action>();
    Action[] actionsArray = textComponent.getActions();
    for(int i = 0; i < actionsArray.length; i++)
    {
      Action a = actionsArray[i];
      editActions.put((String) a.getValue(Action.NAME), a);
    }

    Action a = editActions.get(DefaultEditorKit.cutAction);
    cutToolbarButton.setAction(a);
    cutToolbarButton.setToolTipText(TexteditorResources.getString("Btn_Cut_Tooltip"));

    cutMenuItem.setAction(a);
    cutMenuItem.setText(TexteditorResources.getString("Btn_Cut"));
    cutMenuItem.setMnemonic(TexteditorResources.getString("Btn_Cut_Mnemonic").charAt(0));
    cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
            ActionEvent.CTRL_MASK));

    try
    {
      //imgName = "toolbarButtonGraphics/general/Cut24.gif";
      imgName = "icons/cut_red.png";
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
      ImageIcon i = new ImageIcon(imgURL);
      cutMenuItem.setIcon(i);
      cutToolbarButton.setText("");
      cutToolbarButton.setIcon(i);
    }



    a = editActions.get(DefaultEditorKit.copyAction);
    copyToolbarButton.setAction(a);
    copyToolbarButton.setToolTipText(TexteditorResources.getString("Btn_Copy_Tooltip"));

    copyMenuItem.setAction(a);
    copyMenuItem.setText(TexteditorResources.getString("Btn_Copy"));
    copyMenuItem.setMnemonic(TexteditorResources.getString("Btn_Copy_Mnemonic").charAt(0));
    copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
            ActionEvent.CTRL_MASK));

    try
    {
      //imgName = "toolbarButtonGraphics/general/Copy24.gif";
      imgName = "icons/page_copy.png";
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
      ImageIcon i = new ImageIcon(imgURL);
      copyMenuItem.setIcon(i);
      copyToolbarButton.setText("");
      copyToolbarButton.setIcon(i);
    }


    a = editActions.get(DefaultEditorKit.pasteAction);
    pasteToolbarButton.setAction(a);
    pasteToolbarButton.setToolTipText(TexteditorResources.getString("Btn_Paste_Tooltip"));

    pasteMenuItem.setAction(a);
    pasteMenuItem.setText(TexteditorResources.getString("Btn_Paste"));
    pasteMenuItem.setMnemonic(TexteditorResources.getString(
            "Btn_Paste_Mnemonic").charAt(0));
    pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
            ActionEvent.CTRL_MASK));

    try
    {
      //imgName = "toolbarButtonGraphics/general/Paste24.gif";
      imgName = "icons/page_paste.png";
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
      ImageIcon i = new ImageIcon(imgURL);
      pasteMenuItem.setIcon(i);
      pasteToolbarButton.setText("");
      pasteToolbarButton.setIcon(i);
    }
  }

  private void getLineEnding()
  {
    String eol = null;
    Document doc = null;

    try
    {
      doc = ta.getDocument();
      eol = (String) doc.getProperty(DefaultEditorKit.EndOfLineStringProperty);

      if(eol.equals("\r\n"))
      {
        rbLineEndingWindows.setSelected(true);
        rbLineEndingUnix.setSelected(false);
        rbLineEndingMac.setSelected(false);
        rbLineEndingDefault.setSelected(false);
      }
      else if(eol.equals("\n"))
      {
        rbLineEndingUnix.setSelected(true);
        rbLineEndingWindows.setSelected(false);
        rbLineEndingMac.setSelected(false);
        rbLineEndingDefault.setSelected(false);
      }
      else if(eol.equals("\r"))
      {
        rbLineEndingMac.setSelected(true);
        rbLineEndingWindows.setSelected(false);
        rbLineEndingUnix.setSelected(false);
        rbLineEndingDefault.setSelected(false);
      }
      else
      {
        rbLineEndingDefault.setSelected(true);
        rbLineEndingWindows.setSelected(false);
        rbLineEndingUnix.setSelected(false);
        rbLineEndingMac.setSelected(false);
      }

    }
    catch(NullPointerException npe)
    {
      npe.printStackTrace();
    }
  }

  private void createTextEditPopupActions(JTextComponent textComponent)
  {
    URL imgURL = null;
    String imgName = null;

    HashMap<String, Action> popupEditActions = new HashMap<String, Action>();
    Action[] actionsArray = textComponent.getActions();
    for(int i = 0; i < actionsArray.length; i++)
    {
      Action a = actionsArray[i];
      popupEditActions.put((String) a.getValue(Action.NAME), a);
    }

    popupCutMenuItem.setAction(popupEditActions.get(DefaultEditorKit.cutAction));
    popupCutMenuItem.setText(TexteditorResources.getString("Btn_Cut"));
    try
    {
      //imgName = "toolbarButtonGraphics/general/Cut16.gif";
      imgName = "icons/cut_red.png";
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
      popupCutMenuItem.setIcon(new ImageIcon(imgURL));
    }

    popupCopyMenuItem.setAction(popupEditActions.get(DefaultEditorKit.copyAction));
    popupCopyMenuItem.setText(TexteditorResources.getString("Btn_Copy"));
    try
    {
      //imgName = "toolbarButtonGraphics/general/Copy16.gif";
      imgName = "icons/page_copy.png";
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
      popupCopyMenuItem.setIcon(new ImageIcon(imgURL));
    }

    popupPasteMenuItem.setAction(popupEditActions.get(DefaultEditorKit.pasteAction));
    popupPasteMenuItem.setText(TexteditorResources.getString("Btn_Paste"));
    try
    {
      //imgName = "toolbarButtonGraphics/general/Paste16.gif";
      imgName = "icons/page_paste.png";
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
      popupPasteMenuItem.setIcon(new ImageIcon(imgURL));
    }

  }

  private String buildStatusLine(JLabel label)
  {
    String s = null;

    try
    {
      // System.out.println(String.format("Locale: %s_%s",
      // frame.getLocale().getLanguage(),
      // frame.getLocale().getCountry()));
      s = label.getText();
      s = String.format("%s %d : %d  %s %d  %s: %s %s",
              TexteditorResources.getString("Line_abbr"),
              currentLine,
              ta.getLineCount(),
              TexteditorResources.getString("Column_abbr"),
              currentCol,
              TexteditorResources.getString("Script"),
              currentEncoding,
              byteOrderMark ? TexteditorResources.getString("bom") : "");
      label.setText(s);
    }
    catch(NullPointerException npe)
    {
    }
    return s;
  }

// cyclic execution of cyclicTimer to display memory usage
  private class timerTask
          extends java.util.TimerTask
  {

    public void run()
    {
      synchronized(this)
      {
        cyclicTimer.purge();
        // System.gc();

        long usedMemoryKB = (runtime.totalMemory() - runtime.freeMemory()) / 1024;
        long maxMemoryKB = runtime.maxMemory() / 1024;

        memInfoLine.setText(String.format("%s: %d / %d %s",
                TexteditorResources.getString("Memory_usage"),
                usedMemoryKB,
                maxMemoryKB,
                TexteditorResources.getString("KBytes")));
      }
    }
  }
}
