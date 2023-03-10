package dev.opin.opinbackend.search.mapper;

import java.util.stream.Collectors;

import dev.opin.opinbackend.persistence.entity.RepositoryPost;
import dev.opin.opinbackend.search.dto.response.SearchPostDto;

public class PostMapper {
	public static SearchPostDto toSearchPostDto(RepositoryPost post) {
		return SearchPostDto.builder()
			.id(post.getId())
			.title(post.getTitleContent().getTitle())
			.content(post.getTitleContent().getContent())
			.comments(post.getCommentsList()
				.stream()
				.map(comment -> CommentMapper.toPostCommentDto(comment))
				.collect(Collectors.toList())
			)
			.authorMemberName(post.getMember().getNickname())
			.authorMemberAvatar(post.getMember().getAvatarUrl())
			.commentCount(post.getCommentsList().size())
			.likeCount(post.getLikeList().size())
			.mergeFl(post.getMergeFL())
			.closeState(post.getCloseState())
			.date(post.getDate())
			.build();
	}
}
