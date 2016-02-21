package net.dnsalias.pcb.texteditor.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import net.dnsalias.pcb.utilities.HelperClass;


/**
 * Brings up a modal dialog
 */
public class RecentDialog extends JDialog
{
  public static final long serialVersionUID = 19650221L;
  private JFrame owner;
  private JList list;
  private int selectedIndex = -1;
  private String selected = null;
  private ResourceBundle RecentDialogResources;
  private final static String resourceSrc = "resources/RecentDialogResources";

// Constructor
  public RecentDialog(JFrame owner, JList list)
  {
    super(owner, true);
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    this.owner = owner;
    this.list = list;
    RecentDialogResources = HelperClass.getResourceBundleFromJar(resourceSrc, "UTF-8");

    if(list.getModel().getSize() > 0)
    {
      createAndShowGUI();
    }
  }


  private void createAndShowGUI()
  {
    addWindowListener(new WindowClosingEventHandlerClass());
    setTitle(RecentDialogResources.getString("Select_Item"));
    //setUndecorated(true);
    list.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    //list.setBackground(Color.LIGHT_GRAY);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.addMouseListener(new ListMouseListener());
    list.addKeyListener(new ListKeyListener());

    add(list);
    pack();
    setResizable(false);
    setLocationRelativeTo(owner);

    setVisible(true);
  }


  private class ListMouseListener extends MouseAdapter
  {
    @Override
    public void mouseClicked(MouseEvent e)
    {
      mouseEventHandler(e);
    }


    private void mouseEventHandler(MouseEvent e)
    {
      if(e.getClickCount() == 2)
      {
        selectedIndex = list.locationToIndex(e.getPoint());
        getSelected(selectedIndex);
      }
    }
  }


  private class ListKeyListener extends KeyAdapter
  {
    @Override
    public void keyPressed(KeyEvent e)
    {
      keyEventHandler(e);
    }


    private void keyEventHandler(KeyEvent e)
    {
      if(e.getKeyCode() == KeyEvent.VK_ENTER)
      {
        selectedIndex = list.getSelectedIndex();
        getSelected(selectedIndex);
      }

      if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
      {
        selectedIndex = -1;
        selected = null;
        getSelected(selectedIndex);
      }
    }
  }


  private void getSelected(int index)
  {
    if(index > -1)
    {
      try
      {
        selected = (String)list.getModel().getElementAt(index);
      }
      catch(ArrayIndexOutOfBoundsException e)
      {
      }
    }
    // close the menu
    windowClosingEventHandler();
  }


  private class WindowClosingEventHandlerClass extends WindowAdapter
  {
    @Override
    public void windowClosing(WindowEvent e)
    {
      selectedIndex = -1;
      selected = null;
      windowClosingEventHandler();
    }
  }


  private void windowClosingEventHandler()
  {
    setVisible(false);
  }


  public String getSelectedString()
  {
    return selected;
  }


  public int getSelectedIndex()
  {
    return selectedIndex;
  }
}
