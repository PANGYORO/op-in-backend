package dev.opin.opinbackend.search.mapper;

import java.util.ArrayList;
import java.util.List;

import dev.opin.opinbackend.persistence.entity.Repository;
import dev.opin.opinbackend.persistence.entity.RepositoryContributor;
import dev.opin.opinbackend.persistence.entity.RepositoryTechLanguage;
import dev.opin.opinbackend.persistence.entity.RepositoryTopic;
import dev.opin.opinbackend.repo.model.contributor.RepositoryContributorDto;
import dev.opin.opinbackend.repo.model.response.RepoTechLangDto;
import dev.opin.opinbackend.search.dto.response.RepositoryDto;

public class RepositoryMapper {
	public static RepositoryDto toMyRepoDto(Repository repository) {
		List<RepoTechLangDto> repoTechLangDtoList = getRepoTechLangDtoList(repository);
		List<RepositoryContributorDto> repositoryContributorDtoList = getRepoTechContributorDtoList(repository);
		List<String> topics = getTopicList(repository);

		return RepositoryDto.builder()
			.id(repository.getId())
			.title(repository.getName())
			.content(repository.getDescription())
			.forkNum(repository.getForks())
			.star(repository.getStargazersCount())
			.techLangs(repoTechLangDtoList)
			.htmlUrl(repository.getHtmlUrl())
			.contributors(repositoryContributorDtoList)
			.topicList(topics)
			.updateDate(repository.getUpdatedAt())
			.build();
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

	private static List<RepoTechLangDto> getRepoTechLangDtoList(Repository repository) {
		List<RepositoryTechLanguage> repositoryTechLanguages = repository.getRepositoryTechLanguages();
		List<RepoTechLangDto> repoTechLangDtoList = new ArrayList<>();
		for (RepositoryTechLanguage language : repositoryTechLanguages) {
			repoTechLangDtoList.add(RepoTechLangDto.builder()
				.title(language.getTechLanguage().getTitle())
				.color(language.getTechLanguage().getColor())
				.count(language.getCount())
				.build());
		}
		return repoTechLangDtoList;

	}
}
