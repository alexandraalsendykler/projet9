package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.SequenceEcritureComptableRM;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

	// ==================== Attributs ====================

	// ==================== Constructeurs ====================
	/**
	 * Instantiates a new Comptabilite manager.
	 */
	public ComptabiliteManagerImpl() {
	}

	// ==================== Getters/Setters ====================
	@Override
	public List<CompteComptable> getListCompteComptable() {
		return getDaoProxy().getComptabiliteDao().getListCompteComptable();
	}

	@Override
	public List<JournalComptable> getListJournalComptable() {
		return getDaoProxy().getComptabiliteDao().getListJournalComptable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EcritureComptable> getListEcritureComptable() {
		return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
	}

	@Override
	public List<SequenceEcritureComptable> getListSequenceEcritureComptables() {
		return getDaoProxy().getComptabiliteDao().getListSequenceEcritureComptable();
	}

	/**
	 * {@inheritDoc}
	 */
	// TODO ?? tester
	@Override
	public synchronized void addReference(EcritureComptable pEcritureComptable) throws FunctionalException {

		// TODO ?? impl??menter
		// Bien se r??ferer ?? la JavaDoc de cette m??thode !
		{
			Integer derniereValeur;
			Date dateEcriture = pEcritureComptable.getDate();
			Calendar cal = new GregorianCalendar();
			cal.setTime(dateEcriture);
			int annee = cal.get(Calendar.YEAR);
			String reference = pEcritureComptable.getReference();
			String code = reference.split("-")[0];
			SequenceEcritureComptable sequenceEcriture = null;
			try {
				sequenceEcriture = getDaoProxy().getComptabiliteDao().getSequenceEcritureComptable(code, annee);
				derniereValeur = sequenceEcriture.getDerniereValeur();
				sequenceEcriture.setDerniereValeur(derniereValeur + 1);
				getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(sequenceEcriture);

			} catch (NotFoundException exception) {
				derniereValeur = 1;
				sequenceEcriture = new SequenceEcritureComptable(annee, derniereValeur, code);
				getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(sequenceEcriture);
			}
			pEcritureComptable.setReference(
					String.format("%s-%d/%05d", pEcritureComptable.getJournal().getCode(), annee, derniereValeur));
		}
		
		// m??thode finie // ?? tester avec m??thode d'int??gration 
	}

	private void updateSequenceEcritureComptable(SequenceEcritureComptable sequence, String code) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	// TODO ?? tester
	@Override
	public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptableUnit(pEcritureComptable);
		this.checkEcritureComptableContext(pEcritureComptable);
	}

	/**
	 * V??rifie que l'Ecriture comptable respecte les r??gles de gestion unitaires,
	 * c'est ?? dire ind??pendemment du contexte (unicit?? de la r??f??rence, exercie
	 * comptable non clotur??...)
	 *
	 * @param pEcritureComptable -
	 * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les
	 *                             r??gles de gestion
	 */
	// TODO tests ?? compl??ter
	@SuppressWarnings("unlikely-arg-type")
	protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== V??rification des contraintes unitaires sur les attributs de l'??criture
		Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
		if (!vViolations.isEmpty()) {
			throw new FunctionalException("L'??criture comptable ne respecte pas les r??gles de gestion.",
					new ConstraintViolationException(
							"L'??criture comptable ne respecte pas les contraintes de validation", vViolations));
		}

		// ===== RG_Compta_2 : Pour qu'une ??criture comptable soit valide, elle doit
		// ??tre ??quilibr??e
		if (!pEcritureComptable.isEquilibree()) {
			throw new FunctionalException("L'??criture comptable n'est pas ??quilibr??e.");
		}

		// ===== RG_Compta_3 : une ??criture comptable doit avoir au moins 2 lignes
		// d'??criture (1 au d??bit, 1 au cr??dit)
		int vNbrCredit = 0;
		int vNbrDebit = 0;
		for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
			if (BigDecimal.ZERO
					.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(), BigDecimal.ZERO)) != 0) {
				vNbrCredit++;
			}
			if (BigDecimal.ZERO
					.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(), BigDecimal.ZERO)) != 0) {
				vNbrDebit++;
			}
		}
		// On test le nombre de lignes car si l'??criture ?? une seule ligne
		// avec un montant au d??bit et un montant au cr??dit ce n'est pas valable
		if (pEcritureComptable.getListLigneEcriture().size() < 2 || vNbrCredit < 1 || vNbrDebit < 1) {
			throw new FunctionalException(
					"L'??criture comptable doit avoir au moins deux lignes : une ligne au d??bit et une ligne au cr??dit.");
		}

		// TODO ===== RG_Compta_5 : Format et contenu de la r??f??rence
		// v??rifier que l'ann??e dans la r??f??rence correspond bien ?? la date de
		// l'??criture, idem pour le code journal...

		if (pEcritureComptable.getReference() != null) { // prendre de note de bien suivre la stacktrace
			String reference = pEcritureComptable.getReference();
			String codeJournal = pEcritureComptable.getJournal().getCode();
			Date dateEcriture = pEcritureComptable.getDate();
			Calendar cal = new GregorianCalendar();
			cal.setTime(dateEcriture);
			int annee = cal.get(Calendar.YEAR);

			String[] parts = reference.split("-");
			String part0 = parts[0];
			if (!part0.equals(codeJournal)) {
				throw new FunctionalException(
						"L'??criture comptable doit avoir le code dans la r??f??rence qui correspond au code journal.");
			}

			String[] part1 = parts[1].split("/");
			String anneeReference = part1[0];
			if (!anneeReference.equals(String.valueOf(annee))) {
				throw new FunctionalException(
						"L'??criture comptable doit avoir l'ann??e dans la r??f??rence qui correspond ?? la date de l'??criture.");
			}
		}
	}

	/**
	 * V??rifie que l'Ecriture comptable respecte les r??gles de gestion li??es au
	 * contexte (unicit?? de la r??f??rence, ann??e comptable non clotur??...)
	 *
	 * @param pEcritureComptable -
	 * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les
	 *                             r??gles de gestion
	 */
	protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== RG_Compta_6 : La r??f??rence d'une ??criture comptable doit ??tre unique
		if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
			try {
				// Recherche d'une ??criture ayant la m??me r??f??rence
				EcritureComptable vECRef = getDaoProxy().getComptabiliteDao()
						.getEcritureComptableByRef(pEcritureComptable.getReference());

				// Si l'??criture ?? v??rifier est une nouvelle ??criture (id == null),
				// ou si elle ne correspond pas ?? l'??criture trouv??e (id != idECRef),
				// c'est qu'il y a d??j?? une autre ??criture avec la m??me r??f??rence
				if (pEcritureComptable.getId() == null || !pEcritureComptable.getId().equals(vECRef.getId())) {
					throw new FunctionalException("Une autre ??criture comptable existe d??j?? avec la m??me r??f??rence.");
				}
			} catch (NotFoundException vEx) {
				// Dans ce cas, c'est bon, ??a veut dire qu'on n'a aucune autre ??criture avec la
				// m??me r??f??rence.
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptable(pEcritureComptable);
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEcritureComptable(Integer pId) {
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

}
