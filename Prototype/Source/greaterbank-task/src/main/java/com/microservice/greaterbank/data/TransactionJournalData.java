package com.microservice.greaterbank.data;

/**
 * @author Murugadoss This journal data used to store individual transactions.
 */
public class TransactionJournalData {
	/**
	 * AccountNumber
	 */
	private int accountNumber;
	/**
	 * TxnAmount
	 */
	private double txnAmount;
	/**
	 * isInvalidTxn
	 */
	private boolean isInvalidTxn;

	/**
	 * @return boolean
	 */
	public boolean isInvalidTxn() {
		return isInvalidTxn;
	}

	/**
	 * @param boolean
	 */
	public void setInvalidTxn(boolean isInvalidTxn) {
		this.isInvalidTxn = isInvalidTxn;
	}

	/**
	 * @return accountNumber
	 */
	public int getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param accountNumber
	 */
	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return txnAmount
	 */
	public double getTxnAmount() {
		return txnAmount;
	}

	/**
	 * @param txnAmount
	 */
	public void setTxnAmount(double txnAmount) {
		this.txnAmount = txnAmount;
	}
}
