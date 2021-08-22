package com.dummy.myerp.testbusiness.business;

import org.junit.Test;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.NotFoundException;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.dummy.myerp.testbusiness.business.BusinessTestCase;



public class ComptabiliteManagerImplIntegrationTest extends BusinessTestCase {
	@Test
	public void test() throws Exception {

		ComptabiliteManager manager = getBusinessProxy().getComptabiliteManager();
		assertTrue(true);
		Date dateEcriture = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(dateEcriture);
		int annee = cal.get(Calendar.YEAR);
		EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(-1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/12/31"));
        vEcritureComptable.setLibelle("Cartouches d’imprimante");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(606),
                "Cartouches d’imprimante", new BigDecimal(43),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4456),
                "TVA 20%", new BigDecimal(8),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Facture F110001", null,
                new BigDecimal(51)));

        manager.addReference(vEcritureComptable);

        	assertThrows(NotFoundException.class, () -> {
            vEcritureComptable.setDate(dateEcriture);
            manager.addReference(vEcritureComptable);
        });
    }
	
}