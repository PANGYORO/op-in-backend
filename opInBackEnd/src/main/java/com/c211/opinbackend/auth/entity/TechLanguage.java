package com.c211.opinbackend.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;

@Entity
@Getter
public class TechLanguage {
	@Id
	@GeneratedValue
	@Column(name = "TECH_LANGUAGE_ID")
	private Long id;
	private String title;
}
