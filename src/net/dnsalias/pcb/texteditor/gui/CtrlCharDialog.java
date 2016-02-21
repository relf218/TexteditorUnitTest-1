package net.dnsalias.pcb.texteditor.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import net.dnsalias.pcb.utilities.HelperClass;

/**
 * Brings up a modal dialog
 */
public class CtrlCharDialog
        extends JDialog
        implements ActionListener, PropertyChangeListener
{
  public static final long serialVersionUID = 19650221L;
  private int option;
  private int selection,  formerSelection;
  private boolean isValid = false;
  private JFrame owner;
  private JTextField tsf;
  private JOptionPane dialogPane;
  private Box box1;
  private ResourceBundle CtrlCharDialogResources;
  private final static String resourceSrc = "resources/CtrlCharDialogResources";


// Constructors
  public CtrlCharDialog(JFrame owner, boolean modal, int formerSelection)
  {
    this(owner, owner.getTitle(), modal, formerSelection);
  }

  public CtrlCharDialog(JFrame owner, String title, boolean modal,
          int formerSelection)
  {
    super(owner, title, modal);

    this.owner = owner;
    this.selection = formerSelection;
    this.formerSelection = formerSelection;

    CtrlCharDialogResources = HelperClass.getResourceBundleFromJar(resourceSrc, "UTF-8");
    //TabsizeDialogResources = ResourceBundle.getBundle(resourceSrc);

    createAndShowGUI();
  }
// End of Constructors

  private void createAndShowGUI()
  {
    dialogPane = new JOptionPane(CtrlCharDialogResources.getString("Enter_NumValue"),
            JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION);
    dialogPane.addPropertyChangeListener(this);

    // tabsize textfield
    tsf = new JTextField();
    tsf.setDocument(new LengthLimitedDocument(3));
    tsf.setAlignmentX(Component.LEFT_ALIGNMENT);
    tsf.addPropertyChangeListener(this);
    //tsf.addActionListener(this);
    tsf.setText(Integer.toString(selection));

    // place tabsize textfield
    box1 = new Box(BoxLayout.Y_AXIS);
    box1.add(tsf);

    // add to dialog panel
    dialogPane.add(box1, 1);

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
      isValid = false;
      try
      {
        selection = Integer.parseInt(tsf.getText());
        if((selection < 0) || selection > 255)
        {
          isValid = false;
          selection = formerSelection;
          createAndShowGUI();
        }
        else
        {
          isValid = true;
        }
      }
      catch(NumberFormatException nfe)
      {
        isValid = false;
        selection = formerSelection;
        createAndShowGUI();
      }


      option = ((Integer) dialogPane.getValue()).intValue();

      if(option == JOptionPane.CANCEL_OPTION)
      {
        selection = formerSelection;
        setVisible(false);
      }
      if((option == JOptionPane.OK_OPTION) && (isValid == true))
      {
        setVisible(false);
      }
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
   * @return the selected tabsize
   */
  public int getSelection()
  {
    return selection;
  }

  private class LengthLimitedDocument
          extends PlainDocument
  {
    public static final long serialVersionUID = 19650221L;
    int lengthLimit;

    public LengthLimitedDocument(int lengthLimit)
    {
      super();
      this.lengthLimit = lengthLimit;
    }

    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException
    {
      if(str == null)
      {
        return;
      }
      if((getLength() + str.length()) <= lengthLimit)
      {
        super.insertString(offset, str, a);
      }
    }
  }
}
