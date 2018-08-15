package top.lrshuai.blog.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import top.lrshuai.blog.controller.base.BaseController;
import top.lrshuai.blog.util.CodeUtil;
import top.lrshuai.blog.util.Const;

@Controller
@RequestMapping("/error")
public class ErrorController extends BaseController{
	
	@RequestMapping("/{pageNumber}")
	public String error400(@PathVariable("pageNumber") String pageNumber){
		log.info("visit error page:"+pageNumber+"From ip:"+this.getIpAddr());
		if("302".equals(pageNumber)) {
			String isAuth = (String) this.getSession().getAttribute(Const.AUTH_PAGE);
			if(isAuth == null) {
				return "error/404";
			}
		}
		return "error/"+pageNumber;
	}
	/**
	 * 生成验证码
	 * @throws IOException 
	 */
	@GetMapping(value = "/authCode")
	public String code(HttpServletResponse response){
		OutputStream os = null;
		try {
			// 获取图片
			Object[] img = CodeUtil.CreateCode();
			System.out.println("code="+img[1].toString());
			BufferedImage image = (BufferedImage) img[0];
			// 输出到浏览器
			response.setContentType("image/png");
			os = response.getOutputStream();
			ImageIO.write(image, "png", os);
			os.flush();
			// 用于验证的字符串存入session
			this.getSession().setAttribute(Const.AUTH_CODE, img[1].toString());
		} catch (IOException e) {
			log.error("验证码输出异常",e);
		}finally {
			if(os != null) {
				try {
					os.close();
				} catch (IOException e) {
					System.out.println("close error");
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
}
