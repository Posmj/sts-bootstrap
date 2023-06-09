package com.co.kr.controller;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.co.kr.code.Code;
import com.co.kr.domain.LoginDomain;
import com.co.kr.domain.MusicFileDomain;
import com.co.kr.domain.MusicListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.MusicService;
import com.co.kr.vo.MusicVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/")
public class MusicController {
	
	@Autowired
	private MusicService musicService;

	// music_reg
	@GetMapping("music_reg")
	public ModelAndView music_reg(){
	    ModelAndView mav = new ModelAndView();
		mav.setViewName("musicreg/boardList.html");
		return mav;
	}
	


	
	@PostMapping(value = "musicupload")
	public ModelAndView musicUpload(MusicVO musicVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException {
		
		ModelAndView mav = new ModelAndView();
		int bdSeq = musicService.fileProcess(musicVO, request, httpReq);
		musicVO.setArtist(""); //초기화
		musicVO.setTitle(""); //초기화
		musicVO.setGenre(""); //초기화
		
		// 화면에서 넘어올때는 bdSeq String이라 string으로 변환해서 넣어즘
		mav = musicSelectOneCall(musicVO, String.valueOf(bdSeq),request);
		mav.setViewName("musicreg/boardList.html");
		return mav;
		
	}
	//리스트 하나 가져오기 따로 함수뺌
	@GetMapping(value = "musicList")
	public ModelAndView musicSelectOneCall(@ModelAttribute("musicVO") MusicVO musicVO, String bdSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		map.put("bdSeq",Integer.parseInt(bdSeq));
		
		MusicListDomain musicListDomain = musicService.musicSelectOne(map);
		System.out.println("musicListDomain"+ musicListDomain);
		List<MusicFileDomain> fileList =  musicService.musicSelectOneFile(map);
			
		for (MusicFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("musicdetail", musicListDomain);
		mav.addObject("musicfiles", fileList);

		//삭제시 사용할 용도
		session.setAttribute("musicfiles", fileList);

		return mav;
	}

	//detail
	@GetMapping("musicdetail")
	public ModelAndView musicDetail(@ModelAttribute("musicVO") MusicVO musicVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		//하나파일 가져오기
		mav = musicSelectOneCall(musicVO, bdSeq,request);
		mav.setViewName("musicreg/boardList.html");
		return mav;
	}
	@GetMapping("musicedit")
	public ModelAndView musicedit(MusicVO musicVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
			
		map.put("bdSeq", Integer.parseInt(bdSeq));
		MusicListDomain musicListDomain =musicService.musicSelectOne(map);
		List<MusicFileDomain> fileList =  musicService.musicSelectOneFile(map);
			
		for (MusicFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}

		musicVO.setSeq(musicListDomain.getBdSeq());
		musicVO.setArtist(musicListDomain.getArtist());
		musicVO.setGenre(musicListDomain.getGenre());
		musicVO.setTitle(musicListDomain.getTitle());
		musicVO.setIsEdit("musicedit");  // upload 재활용하기위해서
			
		
		mav.addObject("musicdetail", musicListDomain);
		mav.addObject("musicfiles", fileList);
		mav.addObject("fileLen",fileList.size());
			
		mav.setViewName("musicreg/boardEditList.html");
		return mav;
	}
	@PostMapping("musiceditSave")
	public ModelAndView musiceditSave(@ModelAttribute("musicVO") MusicVO musicVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		//저장
		musicService.fileProcess(musicVO, request, httpReq);
		
		mav = musicSelectOneCall(musicVO, musicVO.getSeq(),request);
		musicVO.setArtist(""); //초기화
		musicVO.setGenre(""); //초기화
		musicVO.setTitle(""); //초기화
		mav.setViewName("musicreg/boardList.html");
		return mav;
	}
	
	@GetMapping("musicremove")
	public ModelAndView musicRemove(@RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<MusicFileDomain> fileList = null;
		if(session.getAttribute("musicfiles") != null) {						
			fileList = (List<MusicFileDomain>) session.getAttribute("musicfiles");
		}

		map.put("bdSeq", Integer.parseInt(bdSeq));
		
		//내용삭제
		musicService.musicContentRemove(map);

		for (MusicFileDomain list : fileList) {
			Path filePath = Paths.get(list.getUpFilePath());
	 
	        try {
	        	
	            // 파일 물리삭제
	            Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
	            // db 삭제 
							musicService.musicFileRemove(list);
				
	        } catch (DirectoryNotEmptyException e) {
							throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}

		//세션해제
		session.removeAttribute("musicfiles"); // 삭제
		mav = musicListCall();
		mav.setViewName("musicreg/boardList.html");
		
		return mav;
	}
	//리스트 가져오기 따로 함수뺌
	public ModelAndView musicListCall() {
		ModelAndView mav = new ModelAndView();
		List<MusicListDomain> items = musicService.musicList();
		mav.addObject("items", items);
		return mav;
	}


}