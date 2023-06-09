package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.co.kr.domain.MyBoardFileDomain;
import com.co.kr.domain.MyBoardListDomain;
import com.co.kr.vo.MyFileListVO;

public interface MyUploadService {
	
	// 인서트
	public int fileProcess(MyFileListVO myFileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	
	// 전체 리스트 조회   // 지난시간 작성
	public List<MyBoardListDomain> boardList();

	// 하나 삭제
	public void bdContentRemove(HashMap<String, Object> map);

	// 하나 삭제
	public void bdFileRemove(MyBoardFileDomain boardFileDomain);
	
	// 하나 리스트 조회
	public MyBoardListDomain boardSelectOne(HashMap<String, Object> map);
	
	// 하나 파일 리스트 조회
	public List<MyBoardFileDomain> boardSelectOneFile(HashMap<String, Object> map);
		
}