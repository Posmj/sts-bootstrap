package com.co.kr.controller;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.MyBoardFileDomain;
import com.co.kr.domain.MyBoardListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.MyUploadService;
import com.co.kr.vo.MyFileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/")
public class MyFileListController {
	
	@Autowired
	private MyUploadService uploadService;
	
	// music_req
		@GetMapping("music_req")
		public ModelAndView music_req(){
			 ModelAndView mav = new ModelAndView();
				mav.setViewName("musicreq/boardList.html");
				return mav;
			}
	// music_rev
		@GetMapping("music_rev")
		public ModelAndView music_rev(){
			 ModelAndView mav = new ModelAndView();
				mav.setViewName("musicrev/boardList.html");
				return mav;
			}

	
	@PostMapping(value = "myupload")
	public ModelAndView bdUpload(MyFileListVO myFileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException {
		
		ModelAndView mav = new ModelAndView();
		int bdSeq = uploadService.fileProcess(myFileListVO, request, httpReq);
		myFileListVO.setContent(""); //초기화
		myFileListVO.setTitle(""); //초기화
		
		// 화면에서 넘어올때는 bdSeq String이라 string으로 변환해서 넣어즘
		mav = bdSelectOneCall(myFileListVO, String.valueOf(bdSeq),request);
		mav.setViewName("myboard/boardList.html");
		return mav;
		
	}
	//리스트 하나 가져오기 따로 함수뺌
	public ModelAndView bdSelectOneCall(@ModelAttribute("myFileListVO") MyFileListVO myFileListVO, String bdSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
			
		map.put("bdSeq", Integer.parseInt(bdSeq));
		MyBoardListDomain BoardListDomain = uploadService.boardSelectOne(map);
		System.out.println("boardListDomain"+ BoardListDomain);
		List<MyBoardFileDomain> fileList =  uploadService.boardSelectOneFile(map);
			
		for (MyBoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("mydetail", BoardListDomain);
		mav.addObject("myfiles", fileList);

		//삭제시 사용할 용도
		session.setAttribute("myfiles", fileList);

		return mav;
	}
	//detail
	@GetMapping("mydetail")
	public ModelAndView bdDetail(@ModelAttribute("myFileListVO") MyFileListVO myFileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		//하나파일 가져오기
		mav = bdSelectOneCall(myFileListVO, bdSeq,request);
		mav.setViewName("myboard/boardList.html");
		return mav;
	}
	@GetMapping("myedit")
	public ModelAndView edit(MyFileListVO myFileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
			
		map.put("bdSeq", Integer.parseInt(bdSeq));
		MyBoardListDomain boardListDomain =uploadService.boardSelectOne(map);
		List<MyBoardFileDomain> fileList =  uploadService.boardSelectOneFile(map);
			
		for (MyBoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}

		myFileListVO.setSeq(boardListDomain.getBdSeq());
		myFileListVO.setContent(boardListDomain.getBdContent());
		myFileListVO.setTitle(boardListDomain.getBdTitle());
		myFileListVO.setIsEdit("edit");  // upload 재활용하기위해서
			
		
		mav.addObject("mydetail", boardListDomain);
		mav.addObject("myfiles", fileList);
		mav.addObject("fileLen",fileList.size());
			
		mav.setViewName("myboard/boardEditList.html");
		return mav;
	}
	@PostMapping("myeditSave")
	public ModelAndView editSave(@ModelAttribute("myFileListVO") MyFileListVO myFileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		//저장
		uploadService.fileProcess(myFileListVO, request, httpReq);
		
		mav = bdSelectOneCall(myFileListVO, myFileListVO.getSeq(),request);
		myFileListVO.setContent(""); //초기화
		myFileListVO.setTitle(""); //초기화
		mav.setViewName("myboard/boardList.html");
		return mav;
	}
	
	@GetMapping("myremove")
	public ModelAndView mbRemove(@RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<MyBoardFileDomain> fileList = null;
		if(session.getAttribute("myfiles") != null) {						
			fileList = (List<MyBoardFileDomain>) session.getAttribute("myfiles");
		}

		map.put("bdSeq", Integer.parseInt(bdSeq));
		
		//내용삭제
		uploadService.bdContentRemove(map);

		for (MyBoardFileDomain list : fileList) {
			list.getUpFilePath();
			Path filePath = Paths.get(list.getUpFilePath());
	 
	        try {
	        	
	            // 파일 물리삭제
	            Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
	            // db 삭제 
							uploadService.bdFileRemove(list);
				
	        } catch (DirectoryNotEmptyException e) {
							throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}

		//세션해제
		session.removeAttribute("myfiles"); // 삭제
		mav = bdListCall();
		mav.setViewName("myboard/boardList.html");
		
		return mav;
	}


	//리스트 가져오기 따로 함수뺌
	public ModelAndView bdListCall() {
		ModelAndView mav = new ModelAndView();
		List<MyBoardListDomain> items = uploadService.boardList();
		mav.addObject("items", items);
		return mav;
	}


}