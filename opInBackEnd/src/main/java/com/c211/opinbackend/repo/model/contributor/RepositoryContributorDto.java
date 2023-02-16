package com.c211.opinbackend.repo.model.contributor;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RepositoryContributorDto {
	private String nickname;
	private String id;
	private String profileImg;

}
