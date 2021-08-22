package com.dummy.myerp.model.bean.comptabilite;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CompteComptableTest {

	@Test
	public void getByNumero() {
		CompteComptable compte1 = new CompteComptable(1, "test");
		CompteComptable compte2 = new CompteComptable(2, "test");
		List<CompteComptable> plist = new ArrayList<CompteComptable>();
		plist.add(compte1);
		plist.add(compte2);
		Assert.assertEquals(compte1.toString(), compte1, CompteComptable.getByNumero(plist, 1));
	}
}
