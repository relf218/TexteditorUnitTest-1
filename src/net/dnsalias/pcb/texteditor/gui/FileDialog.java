package net.dnsalias.pcb.texteditor.gui;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
//import net.dnsalias.pcb.utilities.JFileChooser;
/**
 * Brings up a JFileDialog
 */
public class FileDialog implements PropertyChangeListener
{
  private File path;
  private JFileChooser chooser;
  private Component parent;

  public FileDialog(Component parent, Enums.FileDialogMethod method)
  {
    this(parent, method, null);
  }

  public FileDialog(Component parent, Enums.FileDialogMethod method, File path)
  {
    this.parent = parent;
    this.path = path;

    if(method == Enums.FileDialogMethod.OPEN)
    {
      this.parent = parent;
      this.path = path;
      fileOpenDialog();
    }

    if(method == Enums.FileDialogMethod.SAVEAS)
    {
      this.parent = parent;
      this.path = path;
      fileSaveAsDialog();
    }
  }

  private File fileOpenDialog()
  {
    chooser = new JFileChooser(path, FileSystemView.getFileSystemView());
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(false);

    int result = chooser.showOpenDialog(parent);
    if(result == JFileChooser.APPROVE_OPTION)
    {
      path = chooser.getSelectedFile();
      if(path.isDirectory())
      {
        path = null;
      }
    }
    else
    {
      path = null;
    }
    return path;
  }
  /*
  private File fileSaveDialog()
  {
  chooser = new JFileChooser(path, FileSystemView.getFileSystemView());
  chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
  chooser.setMultiSelectionEnabled(false);
  int result = chooser.showSaveDialog(parent);
  if(result == JFileChooser.APPROVE_OPTION)
  {
  path = chooser.getSelectedFile();
  if(path.isDirectory())
  {
  path = null;
  }
  }
  else
  {
  path = null;
  }
  return path;
  }
   */

  private File fileSaveAsDialog()
  {
    chooser = new JFileChooser(path, FileSystemView.getFileSystemView());
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(false);
    chooser.setSelectedFile(path);

    int result = chooser.showSaveDialog(parent);
    if(result == JFileChooser.APPROVE_OPTION)
    {
      path = chooser.getSelectedFile();
      if(path.isDirectory())
      {
        path = null;
      }
    }
    else
    {
      path = null;
    }
//    System.out.println(path.toString());    
    return path;
  }

  public File getPath()
  {
    return path;
  }

  public void propertyChange(PropertyChangeEvent e)
  {
    String prop = e.getPropertyName();
    if(chooser.isVisible() && e.getSource() == chooser && chooser.getDialogType() == JFileChooser.SAVE_DIALOG && prop.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY))
    {
      path = new File(chooser.getSelectedFile().getParent() + System.getProperty("file.separator") + path.getName());
    }
  }
}
