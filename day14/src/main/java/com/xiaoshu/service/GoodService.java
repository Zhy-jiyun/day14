package com.xiaoshu.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.GoodMapper;
import com.xiaoshu.dao.GoodtypeMapper;
import com.xiaoshu.entity.Good;
import com.xiaoshu.entity.GoodVo;
import com.xiaoshu.entity.Goodtype;

import redis.clients.jedis.Jedis;

@Service
public class GoodService {

	@Autowired
	private GoodMapper gm;
	
	@Autowired
	private GoodtypeMapper tm;
	
	
	public PageInfo<GoodVo> findList(GoodVo gv,Integer pageNum,Integer pageSize)
	{
		PageHelper.startPage(pageNum, pageSize);
		List<GoodVo> list=gm.findList(gv);
		return new PageInfo<>(list);
	}
	public Good findByName(String name)
	{
		Good g=new Good();
		g.setName(name);
		return gm.selectOne(g);
	}
	public void updateGood(Good good)
	{
		good.setCreatetime(new Date());
		gm.updateByPrimaryKeySelective(good);
	}
	public void addGood(Good good)
	{
		good.setCreatetime(new Date());
		gm.insert(good);
		
		Jedis j = new Jedis("127.0.0.1", 6379);
		Good g=new Good();
		g.setName(good.getName());
		Good good2 = gm.selectOne(g);
		j.set(good2.getId() + "", g.getName());
	}
	public void delGood(Integer id)
	{
		gm.deleteByPrimaryKey(id);
	}
	public List<GoodVo> findList(GoodVo goodVo)
	{
		return gm.findList(goodVo);
	}
	public void importGood(MultipartFile goodFile) throws InvalidFormatException, IOException
	{
		Workbook workbook = WorkbookFactory.create(goodFile.getInputStream());
		Sheet at = workbook.getSheetAt(0);
		int lastRowNum = at.getLastRowNum();
		for (int i = 0; i <lastRowNum; i++) {
			Row row = at.getRow(i+1);
			String name = row.getCell(0).toString();
			String tname = row.getCell(1).toString();
			String code = row.getCell(2).toString();
			Integer price = (int)row.getCell(3).getNumericCellValue();
			String status = row.getCell(4).toString();
			Date createtime = row.getCell(5).getDateCellValue();
			Good s=new Good();
			s.setName(name);
			s.setCode(code);
			s.setPrice(price);
			s.setStatus(status);
			s.setCreatetime(createtime);

			Goodtype t=new Goodtype();
			t.setTypename(tname);
			Goodtype gt = tm.selectOne(t);
			
			s.setTypeid(gt.getId());
			gm.insert(s);
	}
}
	public List<GoodVo> findCount()
	{
		return gm.findCount();
	}
}
