package com.microservice.greaterbank.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microservice.greaterbank.data.TransactionJournalData;
import com.microservice.greaterbank.common.CommonHandler;
import com.microservice.greaterbank.data.BatchTransactionData;
import com.microservice.greaterbank.data.PostingData;

/**
 * @author Murugadoss. This service is responsible for posting transaction,
 *         report generation and file process.
 *
 */
@Service
public class CustomerLoanTransactionService {
	/**
	 * debug
	 */
	private static final Logger debug = LoggerFactory.getLogger(CustomerLoanTransactionService.class);
	/**
	 * Pending File Path
	 */
	@Value("${fileupload.pendingFilePath}")
	private String pendingFilePath;
	/**
	 * Report File Path
	 */
	@Value("${fileupload.reportFilePath}")
	private String reportFilePath;
	/**
	 * Processed File Path
	 */
	@Value("${fileupload.processedFilePath}")
	private String processedFilePath;
	/**
	 * Account Number Map
	 */
	private Map<Integer, PostingData> acctNumberMap = new HashMap<>();

	/**
	 * This API used to process customer transactions.
	 */
	public void processCustomerTransaction() {
		debug.info("*********Process Customer Transaction Begins");
		// prepare batch transaction list by reading the file from pending file
		// path
		List<BatchTransactionData> batchTxnList = new ArrayList<>();
		for (Path path : CommonHandler.listFile(Paths.get(pendingFilePath))) {
			batchTxnList.add(prepareBatchTransactionData(path));

		}
		debug.info("*********Batch Transaction List Size [ " + batchTxnList.size() + " ]");
		//
		for (BatchTransactionData batchTransactionData : batchTxnList) {
			// initiate posting for valid transactions
			for (TransactionJournalData journalData : batchTransactionData.getValidTransactionList()) {
				postTransaction(journalData);
			}
			// generate report and move completed file.
			doAfterPosting(batchTransactionData);
		}

	}

	/**
	 * This private API used to initiate report generation and move completed
	 * files.
	 */
	private void doAfterPosting(BatchTransactionData batchTransactionData) {
		generateReport(batchTransactionData);
		moveCompletedFile(batchTransactionData);
	}

	/**
	 * This private API used to prepare report and write in the report
	 * directory.
	 */
	private void generateReport(BatchTransactionData batchTransactionData) {
		String filename = batchTransactionData.getPath().getFileName().toString();
		Path path = Paths.get(reportFilePath, "finance_customer_transactions_report-"
				+ filename.replace("finance_customer_transactions-", "").replace(".csv", "") + ".txt");
		String report = prepareTextReport(batchTransactionData);
		debug.info("*********Generated Report : " + report);
		CommonHandler.write(path, report);
	}

	/**
	 * This private API used to move completed file into processed directory.
	 */
	private void moveCompletedFile(BatchTransactionData batchTransactionData) {
		debug.info("*********Processed File Moved to : " + processedFilePath);
		CommonHandler.move(batchTransactionData.getPath(),
				Paths.get(processedFilePath, batchTransactionData.getPath().getFileName().toString()));
	}

	/**
	 * This private API used to prepare report.
	 */
	private String prepareTextReport(BatchTransactionData batchTransactionData) {
		Long accountCount = batchTransactionData.getValidTransactionList().stream()
				.map(TransactionJournalData::getAccountNumber).distinct().count();
		double totalCr = batchTransactionData.getValidTransactionList().stream().filter(t -> t.getTxnAmount() > 0)
				.mapToDouble(TransactionJournalData::getTxnAmount).sum();
		double totalDr = batchTransactionData.getValidTransactionList().stream().filter(t -> t.getTxnAmount() < 0)
				.mapToDouble(TransactionJournalData::getTxnAmount).sum();
		int invalidTransactions = batchTransactionData.getInvalidTransactions();

		String report = "File Processed: " + batchTransactionData.getPath().getFileName().toString()
				+ "\nTotal Accounts: " + String.format("%,d", accountCount) + "\nTotal Credits : "
				+ String.format("$%,.2f", totalCr) + "\nTotal Debits  : " + String.format("$%,.2f", totalDr)
				+ "\nSkipped Transactions: " + invalidTransactions;
		return report;
	}
	/**
	 * This private API used to go through each transaction and prepare journal
	 * data and add it with batch list
	 */
	public static BatchTransactionData prepareBatchTransactionData(Path path) {
		List<String> rowList = CommonHandler.read(path);
		if (rowList.size() > 0) {
			rowList.remove(0);
		}

		BatchTransactionData txnData = new BatchTransactionData();
		txnData.setPath(path);

		for (String list : rowList) {
			String[] cell = list.replace(" ", "").split(",");
			// there is no transaction data
			if (cell.length != 2) {
				continue;
			}
			TransactionJournalData journalData = new TransactionJournalData();
			String accountNumber = cell[0];
			String txnAmount = cell[1];
			// invalid account
			if (!StringUtils.isNumeric(accountNumber)) {
				journalData.setInvalidTxn(true);
			}
			// invalid transaction amount
			else if (!NumberUtils.isCreatable(txnAmount)) {
				journalData.setInvalidTxn(true);
			}
			// zero transaction amount
			else if (0.0 == Double.parseDouble(txnAmount)) {
				journalData.setInvalidTxn(true);
			}
			// valid transaction
			else {
				journalData.setAccountNumber(Integer.parseInt(accountNumber));
				journalData.setTxnAmount(Double.parseDouble(txnAmount));
			}
			txnData.addTransactionJournal(journalData);
		}
		return txnData;
	}
	/**
	 * This private API post transactions.
	 */
	private void postTransaction(TransactionJournalData journalData) {
		PostingData postingData = acctNumberMap.get(journalData.getAccountNumber());
		// check if the account already processed
		if (postingData == null) {
			postingData = new PostingData();
			postingData.setAcctNumber(journalData.getAccountNumber());
			postingData.setBalanceLoanAmount(0.0);
		}
		double txnAmount = journalData.getTxnAmount();
		double balanceLoanAmount = postingData.getBalanceLoanAmount();
		if (txnAmount < 0) {
			balanceLoanAmount += Math.abs(txnAmount);
		} else {
			balanceLoanAmount -= txnAmount;
		}
		postingData.setBalanceLoanAmount(balanceLoanAmount);
		acctNumberMap.put(postingData.getAcctNumber(), postingData);
	}
}
