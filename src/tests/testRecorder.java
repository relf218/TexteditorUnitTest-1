package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import net.dnsalias.pcb.utilities.Recoder;

public class testRecorder {

	@Test
	public void test1() {
		String[] params = new String[]{"--recode","./testFiles/test1.txt", "", ""};
		Recoder rc = new Recoder(params);
		assertTrue(true);
	}

	@Test
	public void test2() {
		String[] params = new String[]{null, null, null, null, null};
		Recoder rc = new Recoder(params);
		assertTrue(true);
	}
}
