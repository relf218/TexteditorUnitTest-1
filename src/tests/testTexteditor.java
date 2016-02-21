package tests;

import static org.junit.Assert.*;

import java.awt.HeadlessException;
import java.awt.TextArea;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextArea;

import org.junit.Test;



import net.dnsalias.pcb.texteditor.gui.Texteditor;

public class testTexteditor {

	@Test
	public void testWindowClosingEventHandlerFalse() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		Texteditor te = new Texteditor(null);
		Method privateMethod = Texteditor.class.getDeclaredMethod("windowClosingEventHandler", null);
        privateMethod.setAccessible(true);
        Field hasChanged = Texteditor.class.getDeclaredField("hasChanged");
        hasChanged.setAccessible(true);
        hasChanged.set(te, false);
        privateMethod.invoke(te, null);
        ArrayList<Texteditor> list = (ArrayList<Texteditor>)hasChanged.get(te);
        assertEquals(list.size(), 0);
	}
	
	@Test
	public void testWindowClosingEventHandlerTrue() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		Texteditor te = new Texteditor(null);
		Method privateMethod = Texteditor.class.getDeclaredMethod("windowClosingEventHandler", null);
        privateMethod.setAccessible(true);
        Field hasChanged = Texteditor.class.getDeclaredField("hasChanged");
        hasChanged.setAccessible(true);
        hasChanged.set(te, true);
        privateMethod.invoke(te, null);
        ArrayList<Texteditor> list = (ArrayList<Texteditor>)hasChanged.get(te);
        assertEquals(list.size(), 1);
	}
	
	public void testGetLineEnding1() throws NoSuchMethodException, SecurityException, HeadlessException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException{
		Texteditor te = new Texteditor(null);
		Method privateMethod = Texteditor.class.getDeclaredMethod("getLineEnding", null);
		privateMethod.setAccessible(true);
		Field privateField = Texteditor.class.getDeclaredField("ta");
		privateField.setAccessible(true);
		privateField.set(te, new TextArea("\r\n"));
		JTextArea ta = (JTextArea) privateField.get(this);
		privateMethod.invoke(te, null);
		JRadioButtonMenuItem rbLineEndingWindows = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingWindows")).get(this);
		JRadioButtonMenuItem rbLineEndingUnix = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingUnix")).get(this);
		JRadioButtonMenuItem rbLineEndingMac = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingMac")).get(this);
		JRadioButtonMenuItem rbLineEndingDefault = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingDefault")).get(this);
		assertEquals(rbLineEndingWindows.isSelected(), true);
		assertEquals(rbLineEndingUnix.isSelected(), false);
		assertEquals(rbLineEndingMac.isSelected(), false);
		assertEquals(rbLineEndingDefault.isSelected(), false);
	}
	
	public void testGetLineEnding2() throws NoSuchMethodException, SecurityException, HeadlessException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException{
		Texteditor te = new Texteditor(null);
		Method privateMethod = Texteditor.class.getDeclaredMethod("getLineEnding", null);
		privateMethod.setAccessible(true);
		Field privateField = Texteditor.class.getDeclaredField("ta");
		privateField.setAccessible(true);
		privateField.set(te, new TextArea("\n"));
		JTextArea ta = (JTextArea) privateField.get(this);
		privateMethod.invoke(te, null);
		JRadioButtonMenuItem rbLineEndingWindows = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingWindows")).get(this);
		JRadioButtonMenuItem rbLineEndingUnix = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingUnix")).get(this);
		JRadioButtonMenuItem rbLineEndingMac = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingMac")).get(this);
		JRadioButtonMenuItem rbLineEndingDefault = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingDefault")).get(this);
		assertEquals(rbLineEndingWindows.isSelected(), true);
		assertEquals(rbLineEndingUnix.isSelected(), false);
		assertEquals(rbLineEndingMac.isSelected(), false);
		assertEquals(rbLineEndingDefault.isSelected(), false);
	}
	
	public void testGetLineEnding3() throws NoSuchMethodException, SecurityException, HeadlessException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException{
		Texteditor te = new Texteditor(null);
		Method privateMethod = Texteditor.class.getDeclaredMethod("getLineEnding", null);
		privateMethod.setAccessible(true);
		Field privateField = Texteditor.class.getDeclaredField("ta");
		privateField.setAccessible(true);
		privateField.set(te, new TextArea("\r"));
		JTextArea ta = (JTextArea) privateField.get(this);
		privateMethod.invoke(te, null);
		JRadioButtonMenuItem rbLineEndingWindows = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingWindows")).get(this);
		JRadioButtonMenuItem rbLineEndingUnix = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingUnix")).get(this);
		JRadioButtonMenuItem rbLineEndingMac = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingMac")).get(this);
		JRadioButtonMenuItem rbLineEndingDefault = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingDefault")).get(this);
		assertEquals(rbLineEndingWindows.isSelected(), true);
		assertEquals(rbLineEndingUnix.isSelected(), false);
		assertEquals(rbLineEndingMac.isSelected(), false);
		assertEquals(rbLineEndingDefault.isSelected(), false);
	}
	
	public void testGetLineEnding4() throws NoSuchMethodException, SecurityException, HeadlessException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException{
		Texteditor te = new Texteditor(null);
		Method privateMethod = Texteditor.class.getDeclaredMethod("getLineEnding", null);
		privateMethod.setAccessible(true);
		Field privateField = Texteditor.class.getDeclaredField("ta");
		privateField.setAccessible(true);
		privateField.set(te, new TextArea("else"));
		JTextArea ta = (JTextArea) privateField.get(this);
		privateMethod.invoke(te, null);
		JRadioButtonMenuItem rbLineEndingWindows = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingWindows")).get(this);
		JRadioButtonMenuItem rbLineEndingUnix = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingUnix")).get(this);
		JRadioButtonMenuItem rbLineEndingMac = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingMac")).get(this);
		JRadioButtonMenuItem rbLineEndingDefault = (JRadioButtonMenuItem) (Texteditor.class.getDeclaredField("rbLineEndingDefault")).get(this);
		assertEquals(rbLineEndingWindows.isSelected(), true);
		assertEquals(rbLineEndingUnix.isSelected(), false);
		assertEquals(rbLineEndingMac.isSelected(), false);
		assertEquals(rbLineEndingDefault.isSelected(), false);
	}
}
