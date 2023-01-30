package com.c211.opinbackend.auth.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "MEMBER")
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MEMBER_ID")
	private Long id;

	@Column(name = "EMAIL")
	@NotNull
	private String email;

	@Column(name = "PASSWORD")
	@NotNull
	private String password;

	@Column(name = "NICKNAME")
	@NotNull
	private String nickname;

	@Column(name = "AVATA_URL")
	private String avataUrl;

	@Column(name = "GITHUB_SYNC_FL")
	@NotNull
	private boolean githubSyncFl;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "GITHUB_SYNC_ID", nullable = true)
	private GithubSync githubSync;

	@Enumerated(EnumType.STRING)
	private Role role;
}