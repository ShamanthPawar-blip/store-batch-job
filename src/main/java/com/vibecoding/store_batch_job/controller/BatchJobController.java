package com.vibecoding.store_batch_job.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batch")
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job storeJob;

    public BatchJobController(JobLauncher jobLauncher, Job storeJob) {
        this.jobLauncher = jobLauncher;
        this.storeJob = storeJob;
    }

    @GetMapping("/run")
    public ResponseEntity<String> runJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(storeJob, params);
            return ResponseEntity.ok("✅ Batch job triggered successfully!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to start job: " + e.getMessage());
        }
    }
}
