package dev.opin.opinbackend.repo.service.mapper;

import java.util.ArrayList;
import java.util.List;

import dev.opin.opinbackend.persistence.entity.Member;
import dev.opin.opinbackend.persistence.entity.Repository;
import dev.opin.opinbackend.persistence.entity.RepositoryContributor;
import dev.opin.opinbackend.persistence.entity.RepositoryGithubContributor;
import dev.opin.opinbackend.persistence.entity.RepositoryTechLanguage;
import dev.opin.opinbackend.persistence.entity.RepositoryTopic;
import dev.opin.opinbackend.repo.model.contributor.GithubContributorDto;
import dev.opin.opinbackend.repo.model.contributor.RepositoryContributorDto;
import dev.opin.opinbackend.repo.model.dto.RepoDto;
import dev.opin.opinbackend.repo.model.response.RepoDetailResponse;
import dev.opin.opinbackend.repo.model.response.RepoTechLangDto;
import dev.opin.opinbackend.repo.model.response.RepositoryResponseDto;
import dev.opin.opinbackend.repo.model.response.RepositoryResponseSimpleDto;

public class RepoMapper {

	public static RepositoryResponseSimpleDto toSimepleRepoDto(Repository repository) {
		List<RepoTechLangDto> repoTechLangDtoList = getRepoTechLangDtoList(repository);
		return RepositoryResponseSimpleDto.builder()
			.id(repository.getId())
			.ownerId(repository.getMember().getId())
			.title(repository.getName())
			.content(repository.getDescription())
			.techLangs(repoTechLangDtoList)
			.star(repository.getStargazersCount())
			.forkNum(repository.getForks())
			.updateDate(repository.getUpdatedAt().toLocalDate())
			.build();
	}

	public static RepositoryResponseDto toMyRepoDto(Repository repository) {
		List<RepoTechLangDto> repoTechLangDtoList = getRepoTechLangDtoList(repository);
		List<RepositoryContributorDto> repositoryContributorDtoList = getRepoTechContributorDtoList(repository);
		List<String> topics = getTopicList(repository);
		Member member = repository.getMember();
		RepositoryResponseDto repositoryResponseDto = RepositoryResponseDto.builder()
			.id(repository.getId())
			.ownerId(member == null ? null : member.getId())
			.title(repository.getName())
			.content(repository.getName())
			.techLangs(repoTechLangDtoList)
			.contributors(repositoryContributorDtoList)
			.star(repository.getStargazersCount())
			.forkNum(repository.getForks())
			.topicList(topics)
			.build();
		return repositoryResponseDto;
	}

	public static RepoDetailResponse toDetailResponse(Repository repository) {
		List<RepoTechLangDto> repoTechLangDtoList = getRepoTechLangDtoList(repository);
		List<String> topics = getTopicList(repository);
		Member member = repository.getMember();
		List<RepositoryContributorDto> repositoryContributorDtoList = getRepoTechContributorDtoList(repository);
		List<GithubContributorDto> githubContributors = getGithubContributorDtoList(repository);
		RepoDetailResponse repositoryResponseDto = RepoDetailResponse.builder()
			.id(repository.getId())
			.ownerId(member == null ? null : member.getId())
			.title(repository.getName())
			.content(repository.getName())
			.techLangs(repoTechLangDtoList)
			.star(repository.getStargazersCount())
			.contributors(repositoryContributorDtoList)
			.githubContributors(githubContributors)
			.forkNum(repository.getForks())
			.topicList(topics)
			.date(repository.getUpdatedAt())
			.html(repository.getHtmlUrl())
			.build();
		return repositoryResponseDto;
	}

	private static List<String> getTopicList(Repository repository) {
		List<String> res = new ArrayList<>();
		List<RepositoryTopic> repositoryTopicList = repository.getTopicList();
		for (RepositoryTopic topic : repositoryTopicList) {
			res.add(topic.getTopic().getTitle());
		}
		return res;

	}

	private static List<RepositoryContributorDto> getRepoTechContributorDtoList(Repository repository) {
		List<RepositoryContributorDto> res = new ArrayList<>();
		List<RepositoryContributor> contributorEntityList = repository.getRepositoryContributorList();
		for (RepositoryContributor contributorEntity : contributorEntityList) {
			res.add(RepositoryContributorDto.builder()
				.id(String.valueOf(contributorEntity.getId()))
				.nickname(contributorEntity.getMember().getNickname())
				.profileImg(contributorEntity.getMember().getAvatarUrl())
				.build());
		}
		return res;
	}

	private static List<GithubContributorDto> getGithubContributorDtoList(Repository repository) {
		List<GithubContributorDto> res = new ArrayList<>();
		List<RepositoryGithubContributor> contributorEntityList = repository.getRepositoryGithubContributors();

		for (RepositoryGithubContributor contributorEntity : contributorEntityList) {
			res.add(GithubContributorDto.builder()
				.githubUrl(contributorEntity.getGithubContributor().getGithubUrl())
				.id(contributorEntity.getGithubContributor().getAuthorId())
				.nickname(contributorEntity.getGithubContributor().getNickname())
				.profileImg(contributorEntity.getGithubContributor().getAvatarUrl())
				.build());
		}
		return res;
	}

	private static List<RepoTechLangDto> getRepoTechLangDtoList(Repository repository) {
		List<RepositoryTechLanguage> repositoryTechLanguages = repository.getRepositoryTechLanguages();
		List<RepoTechLangDto> repoTechLangDtoList = new ArrayList<>();
		for (RepositoryTechLanguage language : repositoryTechLanguages) {
			repoTechLangDtoList.add(RepoTechLangDto.builder()
				.title(language.getTechLanguage().getTitle())
				.color(language.getTechLanguage().getColor())
				.build());
		}
		return repoTechLangDtoList;

	}

	public static Repository toEntity(Member member, RepoDto repoDto) {
		Repository repositoryToSave = Repository
			.builder()
			.id(repoDto.getRepoId())
			.member(member)
			.name(repoDto.getName())
			.fullName(repoDto.getFullName())
			.htmlUrl(repoDto.getHtmlUrl())
			.description(repoDto.getDescription())
			.secret(repoDto.getSecret())
			.fork(repoDto.getFork())
			.createdAt(repoDto.getCreateAt())
			.updatedAt(repoDto.getUpdatedAt())
			.pushedAt(repoDto.getPushedAt())
			.size(repoDto.getSize())
			.stargazersCount(repoDto.getStargazersCount())
			.watchersCount(repoDto.getWatcherCount())
			.archived(repoDto.getArchived())
			.disabled(repoDto.getDisabled())
			.forks(repoDto.getForks())
			.watchers(repoDto.getWatchers()).build();
		return repositoryToSave;
	}
}
