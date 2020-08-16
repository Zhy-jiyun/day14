package com.xiaoshu.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class GoodVo extends Good{
	private String tname;

	private Integer num;
	
	
			
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createtime1;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createtime2;
	
	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public Date getCreatetime1() {
		return createtime1;
	}

	public void setCreatetime1(Date createtime1) {
		this.createtime1 = createtime1;
	}

	public Date getCreatetime2() {
		return createtime2;
	}

	public void setCreatetime2(Date createtime2) {
		this.createtime2 = createtime2;
	}

	
	
}
