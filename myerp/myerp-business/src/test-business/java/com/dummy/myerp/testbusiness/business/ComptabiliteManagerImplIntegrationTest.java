package com.dummy.myerp.testbusiness.business;

import org.junit.Test;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;

import static org.junit.Assert.assertTrue;

public class ComptabiliteManagerImplIntegrationTest extends BusinessTestCase {
	@Test
	public void test() {
		ComptabiliteManager manager = getBusinessProxy().getComptabiliteManager();
		assertTrue(true);
	}
}