package net.dnsalias.pcb.texteditor.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import net.dnsalias.pcb.utilities.HelperClass;

/**
 * Brings up a modal dialog
 */
public class AboutDialog extends JDialog implements ActionListener, PropertyChangeListener
{
  public static final long serialVersionUID = 19650221L;
  private String[] options;
  private JFrame owner;
  private ImagePanel imagePanel;
  private JLabel label0, label1,  label2,  label3,  gnuLabel1,  gnuLabel2;
  private Box box1,  box2,  box3;
  private JOptionPane dialogPane;
  private ResourceBundle AboutDialogResources;
  private final static String resourceSrc = "resources/AboutDialogResources";
  private static String imgSource = "libs/SwordFight329x170.jpg";


// Constructors
  public AboutDialog(JFrame owner, boolean modal)
  {
    this(owner, owner.getTitle(), modal);
  }

  public AboutDialog(JFrame owner, String title, boolean modal)
  {
    super(owner, title, modal);

    this.owner = owner;

    AboutDialogResources = HelperClass.getResourceBundleFromJar(resourceSrc, "UTF-8");
    //AboutDialogResources = ResourceBundle.getBundle(resourceSrc);

    createAndShowGUI();
  }
// End of Constructors
  private void createAndShowGUI()
  {
    options = new String[1];
    options[0] = AboutDialogResources.getString("OK");
    dialogPane = new JOptionPane(AboutDialogResources.getString("About"),
        JOptionPane.PLAIN_MESSAGE,
        JOptionPane.DEFAULT_OPTION,
        null, options,
        options[0]);
    dialogPane.addPropertyChangeListener(this);

    label0 = new JLabel(AboutDialogResources.getString("label0"));
    label0.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));
    label1 = new JLabel(AboutDialogResources.getString("label1"));
    label1.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));
    label2 = new JLabel(AboutDialogResources.getString("label2"));
    label2.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));
    label3 = new JLabel(AboutDialogResources.getString("label3"));
    label3.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));
    gnuLabel1 = new JLabel(AboutDialogResources.getString("gnuLabel1"));
    gnuLabel1.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));
    gnuLabel2 = new JLabel(AboutDialogResources.getString("gnuLabel2"));
    gnuLabel2.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));

    box1 = Box.createVerticalBox();
    box1.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    box1.add(label0);
    box1.add(label1);
    box1.add(label2);
    box1.add(label3);
    box1.add(gnuLabel1);
    box1.add(gnuLabel2);

    imagePanel = new ImagePanel(imgSource);
    imagePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

    box2 = Box.createVerticalBox();
    box2.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    box2.add(imagePanel);
    //box2.setMinimumSize(new Dimension(52, 52));
    box2.setPreferredSize(imagePanel.getImageSize());

    box3 = Box.createHorizontalBox();
    box3.add(box2);
    box3.add(box1);

    // add to dialog panel
    dialogPane.add(box3, 1);

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

//        int option = JOptionPane.CLOSED_OPTION;

//        Object selectedValue = dialogPane.getValue();
/*    
      for(int counter = 0, maxCounter = options.length; counter < maxCounter; counter++)
      {
      if(options[counter].equals(selectedValue))
      {
      option = counter;
      }
      }
       */
      setVisible(false);
    }
  }

  private class ImagePanel extends JPanel
  {
    public static final long serialVersionUID = 19650221L;
    private Image img = null;
    private String imgName;

    public ImagePanel(String imgSource)
    {
      super();
      this.imgName = imgSource;

      try
      {
        URL imgURL = this.getClass().getClassLoader().getResource(imgName);
        Toolkit tk = Toolkit.getDefaultToolkit();

        MediaTracker m = new MediaTracker(this);
        img = tk.getImage(imgURL);
        //img = tk.getImage(imgURL).getScaledInstance(48, 48, Image.SCALE_DEFAULT);
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
    }

    @Override
    public void paint(Graphics g)
    {
      super.paint(g);
      if(img != null)
      {
        g.drawImage(img, 0, 0, this);
      }
    }

    public Image getImage()
    {
      return img;
    }

    public Dimension getImageSize()
    {
      Dimension dim = null;

      if(img != null)
      {
        dim = new Dimension(img.getWidth(this), img.getHeight(this));
      }
      return dim;
    }
  }
}
