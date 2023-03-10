package dev.opin.opinbackend.repo.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RepositoryPostResponse {

	private long id;

	private String title;

	private String content;

	private Boolean mergeFL;

	private LocalDateTime date;

	private Boolean closeState;

	private String repoTitle;

	private long likeCount;

	private long commentCount;
}
