package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.MyBoardContentDomain;
import com.co.kr.domain.MyBoardFileDomain;
import com.co.kr.domain.MyBoardListDomain;

@Mapper
public interface MyUploadMapper {

	//list
	public List<MyBoardListDomain> boardList();
	//content insert
	public void contentUpload(MyBoardContentDomain boardContentDomain);
	//file insert
	public void fileUpload(MyBoardFileDomain boardFileDomain);

	//content update
	public void bdContentUpdate(MyBoardContentDomain boardContentDomain);
	//file updata
	public void bdFileUpdate(MyBoardFileDomain boardFileDomain);

  //content delete 
	public void bdContentRemove(HashMap<String, Object> map);
	
	//file delete 
	public void bdFileRemove(MyBoardFileDomain boardFileDomain);
	
	//select one
	public MyBoardListDomain boardSelectOne(HashMap<String, Object> map);

	//select one file
	public List<MyBoardFileDomain> boardSelectOneFile(HashMap<String, Object> map);

}