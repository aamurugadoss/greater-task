package com.microservice.greaterbank.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import com.microservice.greaterbank.service.CustomerLoanTransactionService;

/**
 * @author Murugadoss This job is responsible for processing the loan
 *         transaction from the external file. This job starts automatically
 *         twice in a day based on the cron expression passed.
 */
@Component
@EnableScheduling
public class CustomerLoanTransactionJob {
	private static final Logger debug = LoggerFactory.getLogger(CustomerLoanTransactionJob.class);
	@Autowired
	private CustomerLoanTransactionService transactionService;

	@Schedules({ @Scheduled(cron = "0 02 06  * * ?"), 
			@Scheduled(cron = "0 02 21 * * ?"),
	})
	public void shceduleCustomeTransaction() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		debug.info("*********Job Triggered at " + dateFormat.format(new Date()));
		try {
			transactionService.processCustomerTransaction();
			debug.info("*********Job Completed at " + dateFormat.format(new Date()));
		} catch (RuntimeException e) {
			debug.error("Error Occured " + e.getMessage());
		}
	}
}
