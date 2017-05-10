package com.microservice.greaterbank.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Murugadoss This class used to add list of transactions.
 *
 */
public class BatchTransactionData {
	/**
	 * invalidTransactions
	 */
	private int invalidTransactions = 0;

	/**
	 * path
	 */
	private Path path;
	/**
	 * validTransactions
	 */
	private final List<TransactionJournalData> validTransactionList = new ArrayList<>();

	/**
	 * @return invalidTransactions
	 */
	public int getInvalidTransactions() {
		return invalidTransactions;
	}

	/**
	 * @param invalidTransactions
	 */
	public void setInvalidTransactions(int invalidTransactions) {
		this.invalidTransactions = invalidTransactions;
	}

	/**
	 * @return the path
	 */

	public Path getPath() {
		return path;
	}

	/**
	 * @param invalidTransactions
	 */
	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * Add transaction
	 */
	public void addTransactionJournal(TransactionJournalData transactionJournalData) {
		if (transactionJournalData.isInvalidTxn()) {
			invalidTransactions++;
			return;
		}
		validTransactionList.add(transactionJournalData);
	}

	/**
	 * @return validTransactions
	 */
	public List<TransactionJournalData> getValidTransactionList() {
		return validTransactionList;
	}
}
