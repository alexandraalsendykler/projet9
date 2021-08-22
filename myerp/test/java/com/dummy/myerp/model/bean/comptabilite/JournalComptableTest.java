package com.dummy.myerp.model.bean.comptabilite;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class JournalComptableTest {
	
	@Test
	public void getByCode() {
		JournalComptable journal1 = new JournalComptable("1", "test");
		JournalComptable journal2 = new JournalComptable("2", "test");
		List<JournalComptable> plist = new ArrayList<JournalComptable>();
		plist.add(journal1);
		plist.add(journal2);
		Assert.assertEquals(journal1.toString(), journal1, JournalComptable.getByCode(plist, "1"));
	}

}
