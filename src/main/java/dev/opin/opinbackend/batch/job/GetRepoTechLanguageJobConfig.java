package dev.opin.opinbackend.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.opin.opinbackend.batch.listener.LoggerListener;
import dev.opin.opinbackend.batch.step.Action;
import dev.opin.opinbackend.batch.step.GetRepoTechLanguageTasklet;
import dev.opin.opinbackend.persistence.repository.BatchTokenRepository;
import dev.opin.opinbackend.persistence.repository.RepoRepository;
import dev.opin.opinbackend.persistence.repository.RepoTechLanguageRepository;
import dev.opin.opinbackend.persistence.repository.TechLanguageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GetRepoTechLanguageJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final RepoRepository repoRepository;
	private final TechLanguageRepository techLanguageRepository;
	private final RepoTechLanguageRepository repoTechLanguageRepository;
	private final Action action;
	private final BatchTokenRepository batchTokenRepository;

	@Bean
	public Job getRepoTechLanguageJob(Step accessTokenTestStep, Step getAllRepositoryTechLanguageStep,
		Step batchTokenResetStep) {
		return jobBuilderFactory.get("getRepoTechLanguageJob")
			.incrementer(new RunIdIncrementer())
			.listener(new LoggerListener())
			.start(accessTokenTestStep)
			.on("FAILED").to(batchTokenResetStep).on("*").end()
			.from(accessTokenTestStep)
			.on("*").to(getAllRepositoryTechLanguageStep).on("*").to(batchTokenResetStep).end()
			.build();
	}

	@JobScope
	@Bean
	public Step getAllRepositoryTechLanguageStep() {
		return stepBuilderFactory.get("getAllRepositoryTechLanguageStep")
			.tasklet(
				new GetRepoTechLanguageTasklet(batchTokenRepository, repoRepository, action, techLanguageRepository,
					repoTechLanguageRepository))
			.build();
	}

}
