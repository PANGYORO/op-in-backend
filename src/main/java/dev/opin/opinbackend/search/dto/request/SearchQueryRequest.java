package dev.opin.opinbackend.search.dto.request;

import org.springframework.data.domain.Pageable;

import lombok.Data;

@Data
public class SearchQueryRequest {
	String query;
	Pageable pageable;
}
