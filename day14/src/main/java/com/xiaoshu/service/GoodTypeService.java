package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoshu.dao.GoodtypeMapper;
import com.xiaoshu.entity.Goodtype;

@Service
public class GoodTypeService {

	@Autowired
	private GoodtypeMapper tm;
	
	
	public List<Goodtype> findType(Goodtype goodtype)
	{	
		return tm.findType(goodtype);
	}
	public void addType(Goodtype goodtype)
	{
		tm.addType(goodtype);
	}
	public Goodtype findName(String name)
	{
		Goodtype t=new Goodtype();
		t.setTypename(name);
		return tm.selectOne(t);
	}
}
