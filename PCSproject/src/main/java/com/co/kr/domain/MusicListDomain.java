package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class MusicListDomain {

	
	private String bdSeq;
	private String mbId;
	private String title;
	private String artist;
	private String genre;
	private String CreateAt;
	private String UpdateAt;
}
