package dev.opin.opinbackend.member.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.opin.opinbackend.auth.model.request.MemberEmailRequest;
import dev.opin.opinbackend.auth.model.request.MemberLoginRequest;
import dev.opin.opinbackend.auth.model.request.MemberNicknameRequest;
import dev.opin.opinbackend.auth.model.request.MemberPasswordRequest;
import dev.opin.opinbackend.auth.model.response.MypageResponse;
import dev.opin.opinbackend.auth.service.MailService;
import dev.opin.opinbackend.exception.api.ApiExceptionEnum;
import dev.opin.opinbackend.exception.api.ApiRuntimeException;
import dev.opin.opinbackend.exception.member.MemberExceptionEnum;
import dev.opin.opinbackend.exception.member.MemberRuntimeException;
import dev.opin.opinbackend.member.model.dto.MemberDto;
import dev.opin.opinbackend.member.model.request.TechLanguageRequest;
import dev.opin.opinbackend.member.model.request.TopicAndLanguageRequest;
import dev.opin.opinbackend.member.model.request.TopicRequest;
import dev.opin.opinbackend.member.model.response.FileUploadResponse;
import dev.opin.opinbackend.member.service.MemberService;
import dev.opin.opinbackend.member.service.S3FileUploadService;
import dev.opin.opinbackend.persistence.entity.Member;
import dev.opin.opinbackend.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

	private final MemberService memberService;
	private final MailService mailService;
	private final S3FileUploadService s3FileUploadService;

	/**
	 * ????????? ?????? ????????? ???????????? ???????????? ?????? api
	 * #68
	 *
	 * @return
	 */
	@GetMapping
	public ResponseEntity<?> getMemberMyInfo() {
		MemberDto memberInfo = memberService.getMemberInfoBySecurityContext();
		return ResponseEntity.ok().body(memberInfo);
	}

	// ??????????????? ?????? ??????
	@PostMapping("/mypage")
	public ResponseEntity<?> getMemberInfo(@RequestBody MemberNicknameRequest request) throws Exception {
		MypageResponse mypageResponse = memberService.getMemberInfo(request.getNickname());
		return new ResponseEntity<>(mypageResponse, HttpStatus.OK);
	}

	// ????????? ????????????
	@PostMapping("/email/check")
	public ResponseEntity<?> existEmail(@RequestBody MemberEmailRequest request) throws Exception {
		boolean exist = memberService.existEmail(request.getEmail());
		return new ResponseEntity<>(exist, HttpStatus.OK);
	}

	// ????????? ????????????
	@PostMapping("/nickname/check")
	public ResponseEntity<?> existNickname(@RequestBody MemberNicknameRequest request) throws Exception {
		boolean exist = memberService.existNickname(request.getNickname());
		return new ResponseEntity<>(exist, HttpStatus.OK);
	}

	// ????????? ??????
	@PostMapping("/nickname/put")
	public ResponseEntity<?> modifyNickname(@RequestBody MemberNicknameRequest request) {
		boolean exist = memberService.existNickname(request.getNickname());
		if (exist) {
			throw new MemberRuntimeException(MemberExceptionEnum.MEMBER_EXIST_NICKNAME_EXCEPTION);
		}

		String username = SecurityUtil.getCurrentUserId().orElse(null);
		Member member = memberService.modifyNickname(request.getNickname(), username);
		return new ResponseEntity<>(member.getNickname(), HttpStatus.OK);
	}

	// ???????????? ??????
	@PostMapping("/password/put")
	public ResponseEntity<?> modifyPassword(@RequestBody MemberPasswordRequest request) {
		String username = SecurityUtil.getCurrentUserId().orElse(null);
		boolean val = memberService.modifyPassword(username, request.getPassword());
		return new ResponseEntity<>(val, HttpStatus.OK);
	}

	// ???????????? ??? ?????? ??????
	@PostMapping("/info")
	public ResponseEntity<?> getMemberLogin() {
		Member member = memberService.getMember();
		return new ResponseEntity<>(member, HttpStatus.OK);
	}

	// ?????? ???????????? ?????? ?????????
	@PostMapping("/password/email")
	public ResponseEntity<?> changePwEmail(@RequestBody Map<String, String> email) {
		return ResponseEntity.ok(memberService.changePwEmail(email.get("email")));
	}

	// ?????? ??????
	@PostMapping("/delete")
	public ResponseEntity<?> deleteMember(@RequestBody MemberLoginRequest request) {
		return ResponseEntity.ok(memberService.deleteMember(request.getEmail(), request.getPassword()));
	}

	@PostMapping("/gitMem/delete")
	public ResponseEntity<?> deleteGithubMember(@RequestBody MemberLoginRequest request) {
		return ResponseEntity.ok(memberService.deleteGithubMember(request.getEmail()));
	}

	// ?????????
	@PostMapping("/follow")
	public ResponseEntity<?> followMember(@RequestBody MemberNicknameRequest request) {
		memberService.followMember(request.getNickname());
		return ResponseEntity.ok(true);
	}

	/**
	 * ????????? ??? ?????? ?????????
	 *
	 * @param repoId
	 * @return
	 */
	@PostMapping("/follow/repo/{repoId}")
	public ResponseEntity<?> followRepo(@PathVariable("repoId") Long repoId) {
		String memberEmail = SecurityUtil.getCurrentUserId().orElseThrow(() -> new MemberRuntimeException(
			MemberExceptionEnum.MEMBER_NOT_EXIST_EXCEPTION
		));
		Boolean saveState = memberService.followRepo(repoId, memberEmail);
		return ResponseEntity.ok().body(saveState);
	}

	// ????????? ??????
	// TODO: 2023/02/12 ?????? ????????? ???????????? ????????? ????????? ????????????.
	@PostMapping("/follow/delete")
	public ResponseEntity<?> followDeleteMember(@RequestBody MemberNicknameRequest request) {
		return ResponseEntity.ok(memberService.followDeleteMember(request.getNickname()));
	}

	/**
	 * ?????????????????? ?????? ????????? ??????
	 *
	 * @param repoId
	 * @return ????????? repoId
	 */
	@DeleteMapping("/follow/repo/{repoId}")
	public ResponseEntity<?> followRepoDeleteMember(@PathVariable Long repoId) {
		String memberEmail = SecurityUtil.getCurrentUserId().orElseThrow(() -> new MemberRuntimeException(
			MemberExceptionEnum.MEMBER_NOT_EXIST_EXCEPTION
		));
		Boolean delState = memberService.followDeleteRepo(repoId, memberEmail);
		return ResponseEntity.ok().body(delState);
	}

	/**
	 * ?????????????????? ????????? ?????? ????????? ??????
	 *
	 * @param repoId
	 * @return
	 */

	@GetMapping("/follow/repo/{repoId}")
	public ResponseEntity<?> checkFollowRepo(@PathVariable Long repoId) {
		String memberEmail = SecurityUtil.getCurrentUserId().orElseThrow(() -> new MemberRuntimeException(
			MemberExceptionEnum.MEMBER_NOT_EXIST_EXCEPTION
		));
		Boolean res = memberService.followCheckRepo(repoId, memberEmail);
		return ResponseEntity.ok().body(res);
	}

	//??????????????? ?????? : true/ false
	@PostMapping("/follow/check")
	public ResponseEntity<?> isFollow(@RequestBody MemberNicknameRequest request) {
		return ResponseEntity.ok(memberService.isFollow(request.getNickname()));
	}

	// ???????????? ??? Topic & TechLanguage ??????
	@PostMapping("/topic/language/put")
	public ResponseEntity<?> saveSignUpTopicAndTechLanguage(@RequestBody TopicAndLanguageRequest request) {
		if (memberService.saveSignUpTopicAndTechLanguage(request.getEmail(), request.getTopic(), request.getLan())) {
			return ResponseEntity.ok(true);
		} else {
			throw new ApiRuntimeException(ApiExceptionEnum.API_WORK_FAILED_EXCEPTION);
		}
	}

	// Topic ?????? ??????
	@PostMapping("/topic/put")
	public ResponseEntity<?> saveLoginTopic(@RequestBody TopicRequest request) {
		if (memberService.saveLoginTopic(request.getTitle())) {
			return ResponseEntity.ok(true);
		} else {
			throw new ApiRuntimeException(ApiExceptionEnum.API_WORK_FAILED_EXCEPTION);
		}
	}

	// Tech Language ?????? ??????
	@PostMapping("/language/put")
	public ResponseEntity<?> saveLoginTechLanguage(@RequestBody TechLanguageRequest request) {
		if (memberService.saveLoginTechLanguage(request.getTitle())) {
			return ResponseEntity.ok(true);
		} else {
			throw new ApiRuntimeException(ApiExceptionEnum.API_WORK_FAILED_EXCEPTION);
		}
	}

	// ?????? tech language ????????????
	@GetMapping("/language/all")
	public ResponseEntity<?> getListTechLanguage() {
		return new ResponseEntity<>(memberService.getListTechLanguage(), HttpStatus.OK);
	}

	// member - tech language ?????? ??????
	@PostMapping("/language/delete")
	public ResponseEntity<?> deleteLoginMemberTechLanguage(@RequestBody TechLanguageRequest request) {
		return ResponseEntity.ok(memberService.deleteLoginMemberTechLanguage(request.getTitle()));
	}

	// member - topic ?????? ??????
	@PostMapping("/topic/delete")
	public ResponseEntity<?> deleteLoginMemberTopic(@RequestBody TopicRequest request) {
		return ResponseEntity.ok(memberService.deleteLoginMemberTopic(request.getTitle()));
	}

	//?????? ????????? ?????????
	@PostMapping("/profilePhoto")
	public ResponseEntity<?> uploadProfilePhoto(@RequestParam("profilePhoto") MultipartFile multipartFile) throws
		IOException {
		//S3 Bucket ????????? "/profile"
		FileUploadResponse profile = s3FileUploadService.upload(multipartFile, "profile");
		return ResponseEntity.ok(profile);
	}

	@GetMapping("/repo/recommend")
	public ResponseEntity<?> getRecommendRepositories() {
		return new ResponseEntity<>(memberService.getRecommendRepositories(), HttpStatus.OK);
	}
}
