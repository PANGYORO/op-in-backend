package dev.opin.opinbackend.batch.step;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import dev.opin.opinbackend.batch.dto.github.CommitDto;
import dev.opin.opinbackend.batch.dto.github.ContributorDto;
import dev.opin.opinbackend.batch.dto.github.PullRequestDto;
import dev.opin.opinbackend.batch.dto.github.RepositoryDto;
import dev.opin.opinbackend.batch.dto.github.UserDto;
import dev.opin.opinbackend.constant.GitHub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class Action {
	private ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
		.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
		.build();

	private WebClient webClient = WebClient.builder()
		.exchangeStrategies(exchangeStrategies)
		.build();

	public static UserDto getMemberInfo(String githubUserName) {
		return WebClient.create()
			.get()
			.uri(GitHub.getUserInfoUrl(githubUserName))
			.retrieve()
			.bodyToMono(UserDto.class).block();
	}

	public RepositoryDto[] getMemberRepository(String githubToken, String githubUserName, String page) {
		return webClient
			.get()
			.uri(GitHub.getUserRepoUrl(githubUserName, page))
			.header("Authorization", "token " + githubToken)
			.retrieve().bodyToMono(RepositoryDto[].class).block();
	}

	public Map<String, Long> getRepositoryLanguages(String githubToken, String repositoryFullName, String page) {
		return webClient
			.get()
			.uri(
				GitHub.getPublicRepositoryLanguageUrl(repositoryFullName, page)
			)
			.header("Authorization", "token " + githubToken)
			.retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {
			}).block();
	}

	public static Map<String, Long> getRepositoryLanguages2(String repositoryName, String githubUserName) {
		return WebClient.create()
			.get()
			.uri(
				GitHub.getPublicRepositoryLanguageUrl(
					repositoryName, githubUserName)
			).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {
			}).block();
	}

	public CommitDto[] getRepositoryCommits(String githubToken, String repositoryFullName, String page) {
		return webClient
			.get()
			.uri(GitHub.getPublicRepositoryCommitUrl(repositoryFullName, page))
			.header("Authorization", "token " + githubToken)
			.retrieve().bodyToMono(CommitDto[].class).block();
	}

	public PullRequestDto[] getRepositoryPulls(String githubToken, String repositoryFullName, String page) {
		return webClient
			.get()
			.uri(GitHub.getPublicRepositoryPullsUrl(repositoryFullName, page))
			.header("Authorization", "token " + githubToken)
			.retrieve().bodyToMono(PullRequestDto[].class).block();
	}

	public ContributorDto[] getContributors(String githubToken, String repositoryFullName, String page) {
		return webClient
			.get()
			.uri(GitHub.getPublicRepositoryContributorsUrl(repositoryFullName, page))
			.header("Authorization", "token " + githubToken)
			.retrieve().bodyToMono(ContributorDto[].class).block();

	}
}
