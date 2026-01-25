package com.scriptbliss.bandhan.shared.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {
	private List<T> content;
	private PaginationInfo pagination;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PaginationInfo {
		private int page;
		private int size;
		private long totalElements;
		private int totalPages;
		private boolean first;
		private boolean last;
		private boolean hasNext;
		private boolean hasPrevious;
	}
}