package com.vibecoding.store_batch_job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoreBatchJobApplication {
	public static void main(String[] args) {
		SpringApplication.run(StoreBatchJobApplication.class, args);
		// Keep the application running
		synchronized (StoreBatchJobApplication.class) {
			try {
				StoreBatchJobApplication.class.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
