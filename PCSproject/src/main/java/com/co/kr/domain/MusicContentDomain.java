package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class MusicContentDomain {

	private Integer bdSeq;
	private String mbId;

	private String Title;
	private String Artist;
	private String Genre;
	
}
