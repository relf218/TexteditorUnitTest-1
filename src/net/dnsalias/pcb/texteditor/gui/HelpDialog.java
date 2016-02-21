package net.dnsalias.pcb.texteditor.gui;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import net.dnsalias.pcb.utilities.HelperClass;

/**
 * Brings up a modal dialog
 */
public class HelpDialog extends JDialog implements ActionListener, PropertyChangeListener
{
  public static final long serialVersionUID = 19650221L;
  private int option;
  private String[] options;
  private String helpFilePath;
  private JFrame owner;
  private JTextArea tf;
  private JOptionPane dialogPane;
  private ResourceBundle HelpDialogResources;
  private final static String resourceSrc = "resources/HelpDialogResources";

// Constructors
  public HelpDialog(JFrame owner, boolean modal)
  {
    this(owner, owner.getTitle(), modal);
  }

  public HelpDialog(JFrame owner, String title, boolean modal)
  {
    super(owner, title, modal);

    this.owner = owner;

    HelpDialogResources = HelperClass.getResourceBundleFromJar(resourceSrc, "UTF-8");
    //HelpDialogResources = ResourceBundle.getBundle(resourceSrc);
    helpFilePath = HelpDialogResources.getString("HelpFile_Name");
    createAndShowGUI();
  }
// End of Constructors
  private void createAndShowGUI()
  {
    options = new String[1];
    options[0] = HelpDialogResources.getString("OK");
    dialogPane = new JOptionPane(HelpDialogResources.getString("Help"),
        JOptionPane.PLAIN_MESSAGE,
        JOptionPane.DEFAULT_OPTION,
        null, options,
        options[0]);
    dialogPane.addPropertyChangeListener(this);

    JScrollPane sp = new JScrollPane();
    tf = createNewTextArea(sp);
    tf.setEditable(false);
    setAreaText(tf);

    // add to dialog panel
    dialogPane.add(sp, 1);

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
  }

  public void propertyChange(PropertyChangeEvent e)
  {
    String prop = e.getPropertyName();


    // Check buttons
    if(this.isVisible() && e.getSource() == dialogPane && prop.equals(JOptionPane.VALUE_PROPERTY))
    {

      option = JOptionPane.CLOSED_OPTION;

      Object selectedValue = dialogPane.getValue();
      for(int counter = 0,  maxCounter = options.length; counter < maxCounter; counter++)
      {
        if(options[counter].equals(selectedValue))
        {
          option = counter;
        }
      }
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


  // create a new JTextArea in a JScrollPane
  private JTextArea createNewTextArea(JScrollPane scrollpane)
  {
    JTextArea textArea = new JTextArea(28, 60);

    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(textArea.getLineWrap());
    textArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    scrollpane.setViewportView(textArea);
    return textArea;
  }

  private void setAreaText(JTextArea textarea)
  {
    try
    {
      InputStream is = this.getClass().getClassLoader().getResourceAsStream(helpFilePath);
      BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

      textarea.read(reader, null);
      reader.close();
    }
    catch(FileNotFoundException fnfe)
    {
      fnfe.printStackTrace();
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }
}
