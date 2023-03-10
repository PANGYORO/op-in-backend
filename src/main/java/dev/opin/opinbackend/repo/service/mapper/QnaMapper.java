package dev.opin.opinbackend.repo.service.mapper;

import java.util.ArrayList;
import java.util.List;

import dev.opin.opinbackend.persistence.entity.Comment;
import dev.opin.opinbackend.persistence.entity.RepositoryQnA;
import dev.opin.opinbackend.repo.model.response.CommentSimpleResponse;
import dev.opin.opinbackend.repo.model.response.RepoQnAResponse;

public class QnaMapper {
	public static RepoQnAResponse entityToResponseQnA(RepositoryQnA repositoryQnA, List<Comment> commentList) {

		return RepoQnAResponse.builder()
			.qnaId(repositoryQnA.getId())
			.authorMember(repositoryQnA.getAuthorMember().getNickname())
			.authorAvatar(repositoryQnA.getAuthorMember().getAvatarUrl())
			.content(repositoryQnA.getContent())
			.createTime(repositoryQnA.getCreateTime())
			.qnACommentList(entityToCommentResponse(commentList))
			.build();
	}

	public static List<CommentSimpleResponse> entityToCommentResponse(List<Comment> commentList) {
		List<CommentSimpleResponse> res = new ArrayList<>();
		for (Comment comment : commentList) {
			CommentSimpleResponse commentSimpleResponse =
				CommentSimpleResponse.builder()
					.id(comment.getId())
					.commentContent(comment.getContent())
					.memberName(comment.getMember().getNickname())
					.memberAvatarUrl(comment.getMember().getAvatarUrl())
					.build();
			res.add(commentSimpleResponse);
		}
		return res;
	}
}
