package dev.opin.opinbackend.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.opin.opinbackend.persistence.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByNickname(String nickname);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByEmailAndPassword(String email, String password);

	Optional<Member> findByGithubUserName(String name);

	Optional<Member> findByGithubId(String githubId);

	List<Member> findAllByGithubIdIsNotNull();

	List<Member> findAllByNicknameContaining(String nickname);

	Page<Member> findAllByNicknameContaining(String nickname, Pageable pageable);

	Page<Member> findAll(Pageable pageable);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	boolean existsByGithubId(String githubId);

	boolean existsByEmailAndAndGithubSyncFl(String email, boolean sync);

	@Override
	void delete(Member entity);

	Member save(Member member);
}
