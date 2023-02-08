package com.c211.opinbackend.persistence.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;

@Entity(name = "COMMIT_HISTORY")
@Getter
public class CommitHistory {
	@Id
	private String sha;

	@JoinColumn(name = "REPOSITORY_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Repository repository;

	private LocalDateTime date;

	private String message;

	private String authorId;
	private String authorName;
	private String authorAvatarUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_sha")
	private CommitHistory parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
	private List<CommitHistory> children;
}
