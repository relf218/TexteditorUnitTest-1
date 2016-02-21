package tests;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;


import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;

import net.dnsalias.pcb.utilities.Recoder;

public class testRecorder extends TestCase{
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Test
	public void test1() {
		String[] params = new String[]{"--recode","./testFiles/test1.txt", "", "", ""};
		exit.expectSystemExit();
		System.exit(1);
		//Recoder rc = new Recoder(params);
	}

	@Test
	public void test2() {

		String[] params = new String[]{null, null, null, null, null};
		Recoder rc = new Recoder(params);
        
	}
}
