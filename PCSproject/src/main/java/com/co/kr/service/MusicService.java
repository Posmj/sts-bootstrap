package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.MusicFileDomain;
import com.co.kr.domain.MusicListDomain;
import com.co.kr.vo.MusicVO;

public interface MusicService {
	
		// 인서트
		public int fileProcess(MusicVO musicListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
		
		// 전체 리스트 조회   // 지난시간 작성
		public List<MusicListDomain> musicList();

		// 하나 삭제
		public void musicContentRemove(HashMap<String, Object> map);

		// 하나 삭제
		public void musicFileRemove(MusicFileDomain musicFileDomain);
		
		// 하나 리스트 조회
		public MusicListDomain musicSelectOne(HashMap<String, Object> map);
		
		// 하나 파일 리스트 조회
		public List<MusicFileDomain> musicSelectOneFile(HashMap<String, Object> map);
		
		

}
