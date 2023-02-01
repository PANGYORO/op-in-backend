package com.c211.opinbackend.auth.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.c211.opinbackend.repo.entitiy.Repository;
import com.c211.opinbackend.repo.entitiy.RepositoryTechLanguage;
import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "MEMBER")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	@Id
	@GeneratedValue
	@Column(name = "MEMBER_ID")
	private Long id;

	@Column(name = "MEMBER_EMAIL")
	@NotNull
	private String email;

	@Column(name = "PASSWORD")
	@NotNull
	private String password;

	@Column(name = "NICKNAME")
	@NotNull
	private String nickname;

	@Column(name = "AVATA_URL")
	private String avatarUrl;

	@Column(name = "GITHUB_SYNC_FL")
	@NotNull
	private boolean githubSyncFl;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "GITHUB_SYNC_ID", nullable = true)
	private GithubSync githubSync;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "member")
	List<Repository> repositoryList = new ArrayList<>();

	@OneToMany(mappedBy = "repository")
	List<RepositoryTechLanguage> repositoryTechLanguages = new ArrayList<>();
}
