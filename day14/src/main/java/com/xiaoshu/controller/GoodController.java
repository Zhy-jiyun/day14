package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Good;
import com.xiaoshu.entity.GoodVo;
import com.xiaoshu.entity.Goodtype;
import com.xiaoshu.entity.Log;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.GoodService;
import com.xiaoshu.service.GoodTypeService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

@Controller
@RequestMapping("good")
public class GoodController extends LogController{
	static Logger logger = Logger.getLogger(GoodController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	@Autowired
	private GoodService gs;
	
	@Autowired
	private GoodTypeService ts;
	
	@RequestMapping("goodIndex")
	public String index(Goodtype gt,HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		List<Goodtype> tlist = ts.findType(gt);
		request.setAttribute("tlist",tlist);
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "good";
	}
	
	
	@RequestMapping(value="goodList",method=RequestMethod.POST)
	public void userList(GoodVo gv,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;

			PageInfo<GoodVo> page = gs.findList(gv, pageNum, pageSize);
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",page.getTotal() );
			jsonObj.put("rows", page.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("商品展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveUser")
	public void reserveUser(Good good,HttpServletRequest request,User user,HttpServletResponse response){
		Integer id = good.getId();
		JSONObject result=new JSONObject();
		try {
			Good good2 = gs.findByName(good.getName());
			if (id != null) {   // userId不为空 说明是修改
				if(good2 == null || (good2!=null && good2.equals(id))){
					gs.updateGood(good);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
				
			}else {   // 添加
				if(good2==null){  // 没有重复可以添加
					gs.addGood(good);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteUser")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				gs.delGood(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("count")
	public void countGood(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			List<GoodVo> list = gs.findCount();
			result.put("success", true);
			result.put("data", list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("统计用户信息错误",e);
			result.put("errorMsg", "对不起，统计失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("import")
	public void importGood(MultipartFile goodFile,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
				gs.importGood(goodFile);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导入用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("reserveg")
	public void addGoodtype(Goodtype goodtype,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			Goodtype goodtype2 = ts.findName(goodtype.getTypename());
			if(goodtype2==null)
			{
				ts.addType(goodtype);
				result.put("success", true);
			}else{
				result.put("success", true);
				result.put("errorMsg", "该附表名被使用");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("添加附表信息错误",e);
			result.put("errorMsg", "对不起，添加失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("export")
	public void exportGood(GoodVo gv,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String time = TimeUtil.formatTime(new Date(), "yyyyMMddHHmmss");
		    String excelName = "手动备份"+time;
			Log log = new Log();
			List<GoodVo> list = gs.findList(gv);
			String[] handers = {"编号","商品名称","所属分类名","编号","价格","状态","创建时间"};
			// 1导入硬盘
			ExportExcelToDisk(request,handers,list, excelName);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@SuppressWarnings("resource")
	private void ExportExcelToDisk(HttpServletRequest request,
			String[] handers, List<GoodVo> list, String excleName) throws Exception {
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook();//创建工作簿
			HSSFSheet sheet = wb.createSheet("操作记录备份");//第一个sheet
			HSSFRow rowFirst = sheet.createRow(0);//第一个sheet第一行为标题
			rowFirst.setHeight((short) 500);
			for (int i = 0; i < handers.length; i++) {
				sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
			}
			//写标题了
			for (int i = 0; i < handers.length; i++) {
			    //获取第一行的每一个单元格
			    HSSFCell cell = rowFirst.createCell(i);
			    //往单元格里面写入值
			    cell.setCellValue(handers[i]);
			}
			for (int i = 0;i < list.size(); i++) {
			    //获取list里面存在是数据集对象
			    GoodVo log = list.get(i);
			    //创建数据行
			    HSSFRow row = sheet.createRow(i+1);
			    //设置对应单元格的值
			    row.setHeight((short)400);   // 设置每行的高度
			    //"编号","商品名称","所属分类名","编号","价格","状态","创建时间"
			    row.createCell(0).setCellValue(log.getId());
			    row.createCell(1).setCellValue(log.getName());
			    row.createCell(2).setCellValue(log.getTname());
			    row.createCell(3).setCellValue(log.getCode());
			    row.createCell(4).setCellValue(log.getPrice());
			    row.createCell(5).setCellValue(log.getStatus());
			    row.createCell(6).setCellValue(TimeUtil.formatTime(log.getCreatetime(),"yyyy-MM-dd"));
			}
			//写出文件（path为文件路径含文件名）
				OutputStream os;
				File file = new File("E:\\good.xls");
				
				if (!file.exists()){//若此目录不存在，则创建之  
					file.createNewFile();  
					logger.debug("创建文件夹路径为："+ file.getPath());  
	            } 
				os = new FileOutputStream(file);
				wb.write(os);
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
	}
	@RequestMapping("editPassword")
	public void editPassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if(currentUser.getPassword().equals(oldpassword)){
			User user = new User();
			user.setUserid(currentUser.getUserid());
			user.setPassword(newpassword);
			try {
				userService.updateUser(user);
				currentUser.setPassword(newpassword);
				session.removeAttribute("currentUser"); 
				session.setAttribute("currentUser", currentUser);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("修改密码错误",e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		}else{
			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}
