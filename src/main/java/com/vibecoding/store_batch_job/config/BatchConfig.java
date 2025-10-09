package com.vibecoding.store_batch_job.config;

import com.vibecoding.store_batch_job.entity.Store;
import com.vibecoding.store_batch_job.repository.StoreRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public RepositoryItemReader<Store> reader(StoreRepository storeRepository) {
        RepositoryItemReader<Store> reader = new RepositoryItemReader<>();
        reader.setRepository(storeRepository);
        reader.setMethodName("findAll");
        reader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        return reader;
    }

    @Bean
    public ItemProcessor<Store, Store> processor() {
        return store -> {
            if ("active".equals(store.getStatus()) && "Texas".equals(store.getState())) {
                return store;
            }
            return null;
        };
    }

    @Bean
    public ItemWriter<Store> writer() {
        return items -> {
            // Write to CSV logic here
        };
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, RepositoryItemReader<Store> reader,
                     ItemProcessor<Store, Store> processor, ItemWriter<Store> writer) {
        return stepBuilderFactory.get("step")
                .<Store, Store>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("storeJob")
                .start(step)
                .build();
    }
}