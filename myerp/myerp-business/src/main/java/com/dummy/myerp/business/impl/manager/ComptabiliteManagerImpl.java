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
	public List<SequenceEcritureComptable> getListSequenceEcritureComptableRM() {
		return getDaoProxy().getComptabiliteDao().getListSequenceEcritureComptable();
	}

	/**
	 * {@inheritDoc}
	 */
	// TODO à tester
	@Override
	public synchronized void addReference(EcritureComptable pEcritureComptable) throws FunctionalException {
	List <SequenceEcritureComptable> listSequenceEcritureComptable = this.getListSequenceEcritureComptableRM();
	Calendar calendar = Calendar.getInstance();
    calendar.setTime(pEcritureComptable.getDate());
    int annee = calendar.get(Calendar.YEAR);

	} // code à effacer pas bon et ne sert à rien

	// TODO à implémenter
	// Bien se réferer à la JavaDoc de cette méthode !
	/*
	 * Le principe : 1. Remonter depuis la persitance la dernière valeur de la
	 * séquence du journal pour l'année de l'écriture (table
	 * sequence_ecriture_comptable) 2. S'il n'y a aucun enregistrement pour le
	 * journal pour l'année concernée : 1. Utiliser le numéro 1. Sinon : 1. Utiliser
	 * la dernière valeur + 1 3. Mettre à jour la référence de l'écriture avec la
	 * référence calculée (RG_Compta_5) 4. Enregistrer (insert/update) la valeur de
	 * la séquence en persitance (table sequence_ecriture_comptable)
	 */

	// On va chercher l'année et la dernière valeur, si elle n'existe pas on l'a met
	// à 1 // principe de la persistance c'est que l'objet est resté en mémoire (BDE
	// ici)
	// ?)
	// je regarde la séquence, la BDE me renvoit une séquence si y'en a pas j'en
	// créé une,
	// si existe j'update sinon je la créé
	// on ne persiste que la séquence, pas d'update de l'ecritureUpdate
	//

	// vérifier les méthodes dernières, vérifier celles :
	// getSequenceJournalEcritureComptable / updateSequenceEcritureComptable /
	// insertSequenceEcritureComptable // à faire lundi (vérifier)

	// isntaller docker et démarrer le fichier
	// C:\Users\alexa\eclipse-workspace\projet_B4_FR\docker\dev

	// corriger erreur commit git

	{
		Integer derniereValeur;
		Date dateEcriture = pEcritureComptable.getDate();
		Calendar cal = new GregorianCalendar();
		cal.setTime(dateEcriture);
		int annee = cal.get(Calendar.YEAR);
		try {
			SequenceEcritureComptable sequenceEcriture = getDaoProxy().getComptabiliteDao()
					.getSequenceJournalEcritureComptable(pEcritureComptable);
			derniereValeur = sequenceEcriture.getDerniereValeur();
			sequenceEcriture.setDerniereValeur(derniereValeur + 1);
			getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(sequenceEcriture);

		} catch (NotFoundException exception) {
			derniereValeur = 1;

			getDaoProxy().getComptabiliteDao()
					.insertSequenceEcritureComptable(pEcritureComptable.getJournal().getCode(), annee);
		}
		pEcritureComptable.setReference(
				String.format("%s-%d/%05d", pEcritureComptable.getJournal().getCode(), annee, derniereValeur));
	}

	private void updateSequenceEcritureComptable(SequenceEcritureComptable sequence, String code) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	// TODO à tester
	@Override
	public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptableUnit(pEcritureComptable);
		this.checkEcritureComptableContext(pEcritureComptable);
	}

	/**
	 * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
	 * c'est à dire indépendemment du contexte (unicité de la référence, exercie
	 * comptable non cloturé...)
	 *
	 * @param pEcritureComptable -
	 * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les
	 *                             règles de gestion
	 */
	// TODO tests à compléter
	@SuppressWarnings("unlikely-arg-type")
	protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== Vérification des contraintes unitaires sur les attributs de l'écriture
		Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
		if (!vViolations.isEmpty()) {
			throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
					new ConstraintViolationException(
							"L'écriture comptable ne respecte pas les contraintes de validation", vViolations));
		}

		// ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit
		// être équilibrée
		if (!pEcritureComptable.isEquilibree()) {
			throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
		}

		// ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes
		// d'écriture (1 au débit, 1 au crédit)
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
		// On test le nombre de lignes car si l'écriture à une seule ligne
		// avec un montant au débit et un montant au crédit ce n'est pas valable
		if (pEcritureComptable.getListLigneEcriture().size() < 2 || vNbrCredit < 1 || vNbrDebit < 1) {
			throw new FunctionalException(
					"L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
		}

		// TODO ===== RG_Compta_5 : Format et contenu de la référence
		// vérifier que l'année dans la référence correspond bien à la date de
		// l'écriture, idem pour le code journal...

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
						"L'écriture comptable doit avoir le code dans la référence qui correspond au code journal.");
			}

			String[] part1 = parts[1].split("/");
			String anneeReference = part1[0];
			if (!anneeReference.equals(String.valueOf(annee))) {
				throw new FunctionalException(
						"L'écriture comptable doit avoir l'année dans la référence qui correspond à la date de l'écriture.");
			}
		}
	}

	/**
	 * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au
	 * contexte (unicité de la référence, année comptable non cloturé...)
	 *
	 * @param pEcritureComptable -
	 * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les
	 *                             règles de gestion
	 */
	protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
		if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
			try {
				// Recherche d'une écriture ayant la même référence
				EcritureComptable vECRef = getDaoProxy().getComptabiliteDao()
						.getEcritureComptableByRef(pEcritureComptable.getReference());

				// Si l'écriture à vérifier est une nouvelle écriture (id == null),
				// ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
				// c'est qu'il y a déjà une autre écriture avec la même référence
				if (pEcritureComptable.getId() == null || !pEcritureComptable.getId().equals(vECRef.getId())) {
					throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
				}
			} catch (NotFoundException vEx) {
				// Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la
				// même référence.
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

	@Override
	public List<SequenceEcritureComptable> getListSequenceEcritureComptables() {
		// TODO Auto-generated method stub
		return null;
	}
}
