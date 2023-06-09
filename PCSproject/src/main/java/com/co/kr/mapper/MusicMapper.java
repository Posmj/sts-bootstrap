package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.co.kr.domain.MusicContentDomain;
import com.co.kr.domain.MusicFileDomain;
import com.co.kr.domain.MusicListDomain;

@Mapper
public interface MusicMapper {
	
		//list
		public List<MusicListDomain> musicList();
		//content insert
		public void musiccontentUpload(MusicContentDomain musicContentDomain);
		//file insert
		public void musicfileUpload(MusicFileDomain musicFileDomain);

		//content update
		public void musicContentUpdate(MusicContentDomain musicContentDomain);
		//file updata
		public void musicFileUpdate(MusicFileDomain musicFileDomain);

	  //content delete 
		public void musicContentRemove(HashMap<String, Object> map);
		
		//file delete 
		public void musicFileRemove(MusicFileDomain musicFileDomain);
		
		//select one
		public MusicListDomain musicSelectOne(HashMap<String, Object> map);

		//select one file
		public List<MusicFileDomain> musicSelectOneFile(HashMap<String, Object> map);
		
		
		

}
