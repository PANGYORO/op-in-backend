package dev.opin.opinbackend.member.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dev.opin.opinbackend.member.model.response.FileUploadResponse;
import dev.opin.opinbackend.persistence.entity.Member;
import dev.opin.opinbackend.persistence.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3FileUploadService {

	private final AmazonS3Client amazonS3Client;

	private final MemberRepository memberRepository;

	private final MemberService memberService;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	// 프론트에서 받아온 MultipartFile -> file 로 변환 후 upload
	// dir => profile
	public FileUploadResponse upload(MultipartFile multipartFile, String dirName) throws IOException {

		log.info(multipartFile.getOriginalFilename());

		// MultipartFile -> file 로 변환
		File uploadFile = convert(multipartFile)
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

		Member member = memberService.getMember();
		return uploads(member, uploadFile, dirName);
	}

	//
	private FileUploadResponse uploads(Member member, File uploadFile, String dirName) {
		LocalDateTime localDateTime = LocalDateTime.now();
		String now = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		String fileName = dirName + "/" + Long.toString(member.getId()) + now + uploadFile.getName();

		String uploadImageUrl = putS3(uploadFile, fileName);
		removeNewFile(uploadFile);

		// Member AvataUrl 설정
		member.setAvatarUrl(uploadImageUrl);
		memberRepository.save(member);

		//FileUploadResponse DTO로 반환해준다.
		return new FileUploadResponse(fileName, uploadImageUrl);
	}

	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
			CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		} else {
			log.info("파일이 삭제되지 못했습니다.");
		}
	}

	private Optional<File> convert(MultipartFile file) throws IOException {
		File convertFile = new File(file.getOriginalFilename());
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}

		return Optional.empty();
	}

}
