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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
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
            return null; // Filter out stores that don't match the criteria
        };
    }

    @Bean
    public FlatFileItemWriter<Store> writer() {
        return new FlatFileItemWriterBuilder<Store>()
                .name("storeCsvWriter")
                .resource(new FileSystemResource("output/stores.csv"))
                .lineAggregator(new DelimitedLineAggregator<Store>() {
                    {
                        setFieldExtractor(new FieldExtractor<Store>() {
                            @Override
                            public Object[] extract(Store store) {
                                return new Object[]{
                                        store.getId(),
                                        store.getName(),
                                        store.getStatus(),
                                        store.getState(),
                                        store.getOpenDate(),
                                        store.getCloseDate()
                                };
                            }
                        });
                    }
                })
                .headerCallback(writer -> writer.write("ID,Name,Status,State,OpenDate,CloseDate"))
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, RepositoryItemReader<Store> reader,
                     ItemProcessor<Store, Store> processor, FlatFileItemWriter<Store> writer) {
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