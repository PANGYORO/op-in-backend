package com.c211.opinbackend.repo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c211.opinbackend.exception.member.MemberExceptionEnum;
import com.c211.opinbackend.exception.member.MemberRuntimeException;
import com.c211.opinbackend.repo.model.requeset.RequestComment;
import com.c211.opinbackend.repo.model.requeset.RequestQnA;
import com.c211.opinbackend.repo.model.requeset.RequestUpdateQnA;
import com.c211.opinbackend.repo.model.response.RepoQnAResponse;
import com.c211.opinbackend.repo.service.commnet.CommentService;
import com.c211.opinbackend.repo.service.repo.RepoQnAService;
import com.c211.opinbackend.util.SecurityUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/qna")
@AllArgsConstructor
public class RepoQnAController {

	private final RepoQnAService repoQnAService;
	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<?> createQnA(@RequestBody RequestQnA requestQnA) {
		String memberEmail = SecurityUtil.getCurrentUserId()
			.orElseThrow(() -> new MemberRuntimeException(MemberExceptionEnum.MEMBER_NOT_EXIST_EXCEPTION));
		RepoQnAResponse repoQnA = repoQnAService.createRepoQnA(requestQnA, memberEmail);
		if (repoQnA == null) {
			return ResponseEntity.badRequest().body(false);
		}
		return ResponseEntity.ok().body(repoQnA);
	}

	@PostMapping("/comment")
	public ResponseEntity<?> createComment(@RequestBody RequestComment comment) {
		String memberEmail = SecurityUtil.getCurrentUserId()
			.orElseThrow(() -> new MemberRuntimeException(MemberExceptionEnum.MEMBER_NOT_EXIST_EXCEPTION));
		return ResponseEntity.ok().body(commentService.creatQnAComment(comment, memberEmail));
	}

	/**
	 * 특정 래포 qna 전체 조회
	 *
	 * @param repoId
	 * @return
	 */
	@GetMapping("/repo/{repoId}")
	public ResponseEntity<?> getQnaList(@PathVariable Long repoId) {
		List<RepoQnAResponse> repoQnAList = repoQnAService.getRepoQnAList(repoId);
		return ResponseEntity.ok().body(repoQnAList);
	}

	@DeleteMapping("/{qnaId}")
	public ResponseEntity<?> deleteQnaList(@PathVariable("qnaId") Long qnaId) {
		try {
			Boolean res = repoQnAService.deleteRepoQnA(qnaId);
			return ResponseEntity.ok().body(res);
		} catch (Exception exception) {
			return ResponseEntity.badRequest().body(false);
		}
	}

	@PatchMapping
	public ResponseEntity<?> update(@RequestBody RequestUpdateQnA requestUpdateQnA) {
		if (repoQnAService.updateRepoQnA(requestUpdateQnA)) {
			return ResponseEntity.ok().body(true);
		}
		return ResponseEntity.ok().body(false);
	}
}
