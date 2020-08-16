package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Goodtype;
import com.xiaoshu.entity.GoodtypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GoodtypeMapper extends BaseMapper<Goodtype> {

	List<Goodtype> findType(Goodtype goodtype);

	void addType(Goodtype goodtype);
}