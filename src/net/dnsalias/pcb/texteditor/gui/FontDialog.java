package net.dnsalias.pcb.texteditor.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import net.dnsalias.pcb.utilities.HelperClass;

/**
 * Brings up a modal dialog
 */
public class FontDialog
        extends JDialog
        implements ActionListener, PropertyChangeListener
{
  public static final long serialVersionUID = 19650221L;
  private int option;
  private JFrame owner;
  private JLabel fsl,  esl,  ssl,  stylel;
  private JCheckBox bomcb;
  private JTextField etf;
  private JOptionPane dialogPane;
  private JComboBox ecb,  fcb,  scb,  stylecb;
  private Box box1,  box2,  box3,  box4,  box5,  box6,  box7;
  private String[] availableCharsets = new String[0];
  private String selectedEncoding;
  private String[] availableFonts = new String[0];
  private String[] availableSizes = new String[]
  {
    "6", "8", "10", "12", "14", "16", "18", "20",
    "22", "24", "26", "28", "36", "48", "72"
  };
  private String[] availableStyles = new String[]
  {
    Enums.FontStyle.PLAIN.toString(),
    Enums.FontStyle.BOLD.toString(),
    Enums.FontStyle.ITALIC.toString(),
    Enums.FontStyle.BOLD_ITALIC.toString()
  };
  private Font selectedFont;
  private boolean byteOrderMark;
  private int selectedStyle = Font.PLAIN;
  private SortedMap<String, Charset> charsetsCanonical = null;
  private SortedMap<String, String> charsetsHuman = null;
  private final String ENC_TABLE = "libs/encodings.txt";
  private ResourceBundle FontDialogResources;
  private final static String resourceSrc = "resources/FontDialogResources";

// Constructors
  public FontDialog(JFrame owner, boolean modal, Font currentFont,
          String canonicalEncoding, boolean byteOrderMark)
  {
    this(owner, owner.getTitle(), modal, currentFont, canonicalEncoding, byteOrderMark);
  }

  public FontDialog(JFrame owner, String title, boolean modal, Font currentFont,
          String canonicalEncoding, boolean byteOrderMark)
  {
    super(owner, title, modal);

    this.owner = owner;
    this.selectedFont = currentFont;
    this.byteOrderMark = byteOrderMark;
    try
    {
      this.selectedStyle = currentFont.getStyle();
    }
    catch(NumberFormatException e)
    {
      this.selectedStyle = Font.PLAIN;
    }

    charsetsCanonical = Charset.availableCharsets();

    /* 
     * Create a SortedMap which includes canonical names
     * and human readable names
     * key   = canonical name
     * value = human readable name
     */
    charsetsHuman = human(charsetsCanonical);

    /*    
    TreeSet<java.lang.String> humanNames = new TreeSet<java.lang.String>(new StringComparator());
    humanNames.addAll(charsetsHuman.values());
    // put them in an array for the JComboBox
    availableCharsetsHuman = humanNames.toArray(new String[0]);
    // get the human readable name for preselection of JComboBox
    this.selectedEncoding = charsetsHuman.get(canonicalEncoding);
     */
    TreeSet<java.lang.String> canonNames = new TreeSet<java.lang.String>(new StringComparator());
    canonNames.addAll(charsetsHuman.keySet());

    // put them in an array for the JComboBox
    availableCharsets = canonNames.toArray(new String[0]);

    // get the human readable name for preselection of JComboBox
    this.selectedEncoding = canonicalEncoding;

    // Get all font family names
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    availableFonts = ge.getAvailableFontFamilyNames();

    FontDialogResources = HelperClass.getResourceBundleFromJar(resourceSrc, "UTF-8");
    //FontDialogResources = ResourceBundle.getBundle(resourceSrc);

    createAndShowGUI();
  }
// End of Constructors

  private void createAndShowGUI()
  {
    dialogPane = new JOptionPane(FontDialogResources.getString("Select_font_and_encoding"),
            JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION);
    dialogPane.addPropertyChangeListener(this);


    // font selection items
    fsl = new JLabel(FontDialogResources.getString("Font"), JLabel.LEFT);
    fsl.setAlignmentX(Component.LEFT_ALIGNMENT);

    fcb = new JComboBox(availableFonts);
    fcb.setMaximumSize(fcb.getMinimumSize());
    fcb.setAlignmentX(Component.LEFT_ALIGNMENT);
    fcb.setSelectedItem(selectedFont.getFamily());
    fcb.addActionListener(this);

    // place font selection
    box1 = new Box(BoxLayout.Y_AXIS);
    box1.add(fsl);
    box1.add(fcb);

    // style selection items
    stylel = new JLabel(FontDialogResources.getString("Style"), JLabel.LEFT);
    stylel.setAlignmentX(Component.LEFT_ALIGNMENT);

    stylecb = new JComboBox(availableStyles);
    stylecb.setMaximumSize(stylecb.getMinimumSize());
    stylecb.setAlignmentX(Component.LEFT_ALIGNMENT);


    if(selectedStyle == Font.BOLD)
    {
      stylecb.setSelectedItem(Enums.FontStyle.BOLD.toString());
    }
    else if(selectedStyle == Font.ITALIC)
    {
      stylecb.setSelectedItem(Enums.FontStyle.ITALIC.toString());
    }
    else if(selectedStyle == (Font.BOLD + Font.ITALIC))
    {
      stylecb.setSelectedItem(Enums.FontStyle.BOLD_ITALIC.toString());
    }
    else
    {
      stylecb.setSelectedItem(Enums.FontStyle.PLAIN.toString());
    }
    stylecb.addActionListener(this);

    // place style selection
    box7 = new Box(BoxLayout.Y_AXIS);
    box7.add(stylel);
    box7.add(stylecb);

    // size selection items
    ssl = new JLabel(FontDialogResources.getString("Size"), JLabel.LEFT);
    ssl.setAlignmentX(Component.LEFT_ALIGNMENT);

    scb = new JComboBox(availableSizes);
    scb.setMaximumSize(scb.getMinimumSize());
    scb.setAlignmentX(Component.LEFT_ALIGNMENT);
    scb.setEditable(true);
    try
    {
      scb.setSelectedItem(Integer.toString(selectedFont.getSize()));
    }
    catch(NumberFormatException e)
    {
      scb.setSelectedItem("10");
    }
    scb.addActionListener(this);

    // place size selection
    box2 = new Box(BoxLayout.Y_AXIS);
    box2.add(ssl);
    box2.add(scb);

    // encoding selection
    esl = new JLabel(FontDialogResources.getString("Script"), JLabel.LEFT);
    esl.setAlignmentX(Component.LEFT_ALIGNMENT);

    ecb = new JComboBox(availableCharsets);
    ecb.setMaximumSize(ecb.getMinimumSize());
    ecb.setAlignmentX(Component.LEFT_ALIGNMENT);
    ecb.setSelectedItem(selectedEncoding);
    ecb.addActionListener(this);

    etf = new JTextField();
    etf.setEditable(false);
    etf.setAlignmentX(Component.LEFT_ALIGNMENT);
    etf.setText(String.format("%s", charsetsHuman.get(selectedEncoding)));
    //etf.setText(String.format("%s", getSelectedEncoding()));
    //etf.setMaximumSize(etf.getMinimumSize());
    //byte order mark
    bomcb = new JCheckBox(FontDialogResources.getString("bom"));
    bomcb.setSelected(byteOrderMark);

    if((selectedEncoding.equals("UTF-16BE")) || (selectedEncoding.equals("UTF-16LE")))
    {
      bomcb.setEnabled(true);
    }
    else if((selectedEncoding.equals("UTF-32BE")) || (selectedEncoding.equals("UTF-32LE")))
    {
      bomcb.setEnabled(true);
    }
    else if(selectedEncoding.equals("UTF-8"))
    {
      bomcb.setEnabled(true);
    }
    else
    {
      bomcb.setSelected(false);
      byteOrderMark = false;
      bomcb.setEnabled(false);
    }
    bomcb.addActionListener(this);
    // place encoding selection
    box3 = new Box(BoxLayout.Y_AXIS);
    box3.add(esl);
    box3.add(etf);
    box3.add(ecb);
    box3.add(bomcb);

    box4 = new Box(BoxLayout.X_AXIS);
    box4.add(box3);
    box4.add(Box.createHorizontalGlue());

    // put font and size together
    box5 = new Box(BoxLayout.X_AXIS);
    box5.add(box1);
    box5.add(Box.createHorizontalStrut(10));
    box5.add(box7);
    box5.add(Box.createHorizontalStrut(10));
    box5.add(box2);
    box5.add(Box.createHorizontalGlue());

    // put all together
    box6 = new Box(BoxLayout.Y_AXIS);
    box6.add(Box.createVerticalStrut(20));
    box6.add(box5);
    box6.add(Box.createVerticalStrut(20));
    box6.add(box4);

    // add to dialog panel
    dialogPane.add(box6, 1);

    setContentPane(dialogPane);
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    pack();
    setResizable(false);
    setLocationRelativeTo(owner);

    setVisible(true);
    dialogPane.selectInitialValue();
  }

  public void actionPerformed(ActionEvent e)
  {
    if((e.getSource() == ecb) && (ecb.getSelectedItem() != null))
    {
      selectedEncoding = (String) ecb.getSelectedItem();
      //etf.setText(String.format("%s", getSelectedEncoding()));
      etf.setText(String.format("%s", charsetsHuman.get(selectedEncoding)));
      //System.out.println(String.format("%s - %s", selectedEncoding, getSelectedEncoding()));
      
      if((selectedEncoding.equals("UTF-16BE")) || (selectedEncoding.equals("UTF-16LE")))
      {
        bomcb.setEnabled(true);
      }
      else if((selectedEncoding.equals("UTF-32BE")) || (selectedEncoding.equals("UTF-32LE")))
      {
        bomcb.setEnabled(true);
      }
      else if(selectedEncoding.equals("UTF-8"))
      {
        bomcb.setEnabled(true);
      }
      else
      {
        bomcb.setSelected(false);
        byteOrderMark = false;
        bomcb.setEnabled(false);
      }
    }
    if(e.getSource() == bomcb)
    {
      byteOrderMark = bomcb.isSelected();
    }
    if((e.getSource() == fcb) && (fcb.getSelectedItem() != null))
    {
      selectedFont = new Font((String) fcb.getSelectedItem(),
              selectedStyle, new Integer((String) scb.getSelectedItem()).intValue());
    }
    if((e.getSource() == scb) && (scb.getSelectedItem() != null))
    {
      selectedFont = new Font((String) fcb.getSelectedItem(),
              selectedStyle, new Integer((String) scb.getSelectedItem()).intValue());
    }
    if((e.getSource() == stylecb) && (stylecb.getSelectedItem() != null))
    {
      String selection = (String) stylecb.getSelectedItem();

      if(selection.equals(Enums.FontStyle.BOLD.toString()))
      {
        selectedStyle = Font.BOLD;
      }
      else if(selection.equals(Enums.FontStyle.ITALIC.toString()))
      {
        selectedStyle = Font.ITALIC;
      }
      else if(selection.equals(Enums.FontStyle.BOLD_ITALIC.toString()))
      {
        selectedStyle = (Font.BOLD + Font.ITALIC);
      }
      else
      {
        selectedStyle = Font.PLAIN;
      }

      selectedFont = new Font((String) fcb.getSelectedItem(),
              selectedStyle, new Integer((String) scb.getSelectedItem()).intValue());
    }
  }

  public void propertyChange(PropertyChangeEvent e)
  {
    String prop = e.getPropertyName();
    if(this.isVisible() && e.getSource() == dialogPane && prop.equals(JOptionPane.VALUE_PROPERTY))
    {
      option = ((Integer) dialogPane.getValue()).intValue();
      setVisible(false);
    }
  }

  /**
   * @return JOptionPane.OK_OPTION | JOptionPane.CANCEL_OPTION
   */
  public int getOption()
  {
    return option;
  }

  /**
   * @return The selected charset (encoding)
   */
  public String getSelectedEncoding()
  {
    return selectedEncoding;
  }

  /**
   * @return The canonical name of the selected charset (encoding)
   */
  public String getSelectedEncodingByHumanName()
  {
    String canonical = null;

    if(charsetsHuman.containsValue(selectedEncoding))
    {
      for(String key : charsetsHuman.keySet().toArray(new String[0]))
      {
        if(charsetsHuman.get(key).equals(selectedEncoding))
        {
          canonical = key;
          break;
        }
      }
    }
    return canonical;
  }

  public Font getSelectedFont()
  {

    return selectedFont;
  }

  public boolean isByteOrderMark()
  {
    return byteOrderMark;
  }

  // add charset canonical names with human readable names
  public SortedMap<String, String> human(SortedMap<String, Charset> sourceMap)
  {
    SortedMap<String, String> map = new TreeMap<String, String>(new StringComparator());
    String line, key, value;
    String[] key_value;
    try
    {
      InputStream is = this.getClass().getClassLoader().getResourceAsStream(ENC_TABLE);
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));

      while(true)
      {
        line = reader.readLine();
        if(line == null)
        {
          reader.close();
          break;
        }
        key_value = line.split("\\t");
        key = key_value[0];
        value = key_value[1];

        if(sourceMap.containsKey(key))
        {
          map.put(key, value);
        }
      }
    }
    catch(FileNotFoundException fnfe)
    {
      fnfe.printStackTrace();
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
    return map;
  }

  private static class StringComparator
          implements Comparator<java.lang.String>
  {
    public int compare(String o1, String o2)
    {
      return o1.compareTo(o2);
    }
  }
}
