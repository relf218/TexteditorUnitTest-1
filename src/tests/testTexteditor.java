package tests;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.Test;



import net.dnsalias.pcb.texteditor.gui.Texteditor;

public class testTexteditor {

	@Test
	public void testWindowClosingEventHandler() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
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

}
