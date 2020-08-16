package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Good;
import com.xiaoshu.entity.GoodExample;
import com.xiaoshu.entity.GoodVo;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GoodMapper extends BaseMapper<Good> {

	List<GoodVo> findList(GoodVo gv);

	List<GoodVo> findCount();
	
	
}