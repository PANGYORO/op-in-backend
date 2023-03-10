package dev.opin.opinbackend.repo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.opin.opinbackend.auth.model.request.MemberEmailRequest;
import dev.opin.opinbackend.repo.model.dto.RepoDto;
import dev.opin.opinbackend.repo.model.response.RepoDetailResponse;
import dev.opin.opinbackend.repo.model.response.RepositoryResponseDto;
import dev.opin.opinbackend.repo.model.response.RepositoryResponseSimpleDto;
import dev.opin.opinbackend.repo.service.repo.RepositoryPostService;
import dev.opin.opinbackend.repo.service.repo.RepositoryService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/repo")
@Slf4j
@AllArgsConstructor
public class RepoController {
	private final RepositoryService repositoryService;
	private final RepositoryPostService repositoryPostService;

	// TODO: 2023/02/12 api 지우기
	@PostMapping("/member")
	public ResponseEntity<?> getReposByEmail(@RequestBody MemberEmailRequest emailRequest) throws Exception {
		String email = emailRequest.getEmail();
		List<RepositoryResponseDto> repositoryResponseDtoList = repositoryService.finRepositoryListByMember(email);
		log.info("size:", repositoryResponseDtoList.size());
		for (RepositoryResponseDto dto : repositoryResponseDtoList) {
			log.info(dto.toString());
		}
		return new ResponseEntity<>(repositoryResponseDtoList, HttpStatus.OK);
	}

	@GetMapping("/member/{memberId}")
	public ResponseEntity<?> getUserRepos(@PathVariable Long memberId) {
		List<RepositoryResponseSimpleDto> results = repositoryService.findRepositorySimpleList(memberId);
		return ResponseEntity.ok().body(results);
	}

	/**
	 * 임시 로 레포지토리를 넣는 컨트롤러입니다
	 * 추후 api 조회랑 배치가 돌면 서비스로 대체하겠습니다
	 *
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> testPost(@RequestBody RepoDto dto) {
		try {
			repositoryService.uploadRepository(dto.getMemberEmail(), dto);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	@GetMapping("/{repoId}")
	public ResponseEntity<?> repoDetail(@PathVariable Long repoId) {
		RepoDetailResponse detailResponse = repositoryService.getDetailResponse(repoId);
		return ResponseEntity.ok().body(detailResponse);
	}

	@PostMapping("/enter")
	public ResponseEntity<?> addEnter(@RequestBody Map<String, String> title) {
		return new ResponseEntity<>(repositoryService.addEnter(title.get("title")), HttpStatus.OK);
	}

}
