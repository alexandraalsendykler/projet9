package com.dummy.myerp.testbusiness.business;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@ExtendWith(SpringExtension.class)

public class ComptabiliteManagerImplIntegrationTest extends BusinessTestCase {
	@Test
	public void addReference() throws Exception {

		EcritureComptable test = new EcritureComptable();
		Date aujourdhui = new Date();
		JournalComptable journalComptableTest = new JournalComptable("BQ", "36");
		test.setDate(aujourdhui);
		test.setId(3);
		test.setJournal(journalComptableTest);
		test.setLibelle("BQ");
		test.setReference("4");

		LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable();
		LigneEcritureComptable ligneEcritureComptable2 = new LigneEcritureComptable();
		
		

		// écrire les tests d'intégration

	}

}