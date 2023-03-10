package dev.opin.opinbackend.repo.service.repo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import dev.opin.opinbackend.exception.member.MemberExceptionEnum;
import dev.opin.opinbackend.exception.member.MemberRuntimeException;
import dev.opin.opinbackend.exception.repositroy.RepositoryExceptionEnum;
import dev.opin.opinbackend.exception.repositroy.RepositoryRuntimeException;
import dev.opin.opinbackend.persistence.entity.Comment;
import dev.opin.opinbackend.persistence.entity.Member;
import dev.opin.opinbackend.persistence.entity.Repository;
import dev.opin.opinbackend.persistence.entity.RepositoryQnA;
import dev.opin.opinbackend.persistence.repository.CommentRepository;
import dev.opin.opinbackend.persistence.repository.MemberRepository;
import dev.opin.opinbackend.persistence.repository.RepoQnARepository;
import dev.opin.opinbackend.persistence.repository.RepoRepository;
import dev.opin.opinbackend.repo.model.requeset.RequestQnA;
import dev.opin.opinbackend.repo.model.requeset.RequestUpdateQnA;
import dev.opin.opinbackend.repo.model.response.RepoQnAResponse;
import dev.opin.opinbackend.repo.service.mapper.CommentMapper;
import dev.opin.opinbackend.repo.service.mapper.QnaMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class RepoQnAServiceImpl implements RepoQnAService {
	private final CommentRepository commentRepository;
	private final RepoRepository repoRepository;
	private final RepoQnARepository repoQnARepository;
	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public List<RepoQnAResponse> getRepoQnAList(Long repoId) {
		List<RepoQnAResponse> res = new ArrayList<>();
		repoRepository.findById(repoId)
			.orElseThrow(() -> new RepositoryRuntimeException(RepositoryExceptionEnum.REPOSITORY_EXIST_EXCEPTION));
		List<RepositoryQnA> qnAList = repoQnARepository.findByRepositoryId(repoId);
		for (RepositoryQnA repoQnA : qnAList) {
			List<Comment> byRepositoryQnAId = commentRepository.findByRepositoryQnAId(repoQnA.getId());
			res.add(QnaMapper.entityToResponseQnA(repoQnA, byRepositoryQnAId));
		}
		return res;
	}

	@Override
	@Transactional
	public RepoQnAResponse createRepoQnA(RequestQnA requestQnA, String email) {

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberRuntimeException(MemberExceptionEnum.MEMBER_NOT_EXIST_EXCEPTION));
		Repository repository = repoRepository.findById(requestQnA.getRepoId()).orElseThrow(
			() -> new RepositoryRuntimeException(RepositoryExceptionEnum.REPOSITORY_EXIST_EXCEPTION)
		);
		if (requestQnA.getComment().isBlank() || requestQnA.getComment() == null
			|| requestQnA.getComment().length() == 0) {
			throw new RepositoryRuntimeException(RepositoryExceptionEnum.REPOSITORY_QNA_CONTENT_EMPTY_EXCEPTION);
		}
		try {

			RepositoryQnA repositoryQnA = RepositoryQnA.builder()
				.authorMember(member)
				.repository(repository)
				.content(requestQnA.getComment())
				.createTime(LocalDateTime.now())
				.build();
			RepositoryQnA save = repoQnARepository.save(repositoryQnA);
			return CommentMapper.toRepoQnAResponse(save);
		} catch (Exception exception) {
			return null;
		}

	}

	@Override
	@Transactional
	public Boolean deleteRepoQnA(Long qnaId) {
		try {
			RepositoryQnA repositoryQnA = repoQnARepository.findById(qnaId).orElseThrow(
				() -> new RepositoryRuntimeException(RepositoryExceptionEnum.REPOSITORY_QNA_EXIST_EXCEPTION)
			);
			List<Comment> findQnACommentList = commentRepository.findByRepositoryQnAId(qnaId);
			repositoryQnA.setMemberNull();
			repositoryQnA.setRepositoryNull();
			repoQnARepository.delete(repositoryQnA);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	@Transactional
	public Boolean updateRepoQnA(RequestUpdateQnA requestUpdateQnA) {
		try {
			RepositoryQnA repositoryQnA = repoQnARepository.findById(requestUpdateQnA.getQnaId()).orElseThrow(
				() -> new RepositoryRuntimeException(RepositoryExceptionEnum.REPOSITORY_QNA_EXIST_EXCEPTION)
			);
			repositoryQnA.updateContent(requestUpdateQnA.getPostContent());
			repoQnARepository.save(repositoryQnA);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}
}
