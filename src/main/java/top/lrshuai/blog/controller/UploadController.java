package top.lrshuai.blog.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import top.lrshuai.blog.util.DateUtil;
import top.lrshuai.blog.util.ImgUtil;
import top.lrshuai.blog.util.MyUtil;

@Controller
@CrossOrigin
@RequestMapping("/up")
public class UploadController {

	private Logger log = Logger.getLogger(this.getClass());
	
	private final ResourceLoader resourceLoader;  
	
	@Value("${upload.root.folder}")
	public String root_fold;
	
	@Value("${img.folder}")
	public String img_fold;
	
	@Value("${user.folder}")
	public String user_folder;
	
	
    @Autowired 
    public UploadController(ResourceLoader resourceLoader) {  
        this.resourceLoader = resourceLoader;  
    }  
	
	@RequestMapping(value = "/img" ,method = RequestMethod.POST)
	@ResponseBody
	public String imgUpload(@RequestParam(value = "file") MultipartFile file){
		if (file.isEmpty()) {
            return null;
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        log.info("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        log.info("上传的后缀名为：" + suffixName);
        // 文件上传后的路径
        String filePath = root_fold+img_fold+DateUtil.getDays()+"/";
        System.out.println("upload_img_folder="+root_fold);
        System.out.println("upload_folder_root="+img_fold);
        System.out.println("filePath="+filePath);
        ImgUtil.createFile(filePath, fileName);
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            System.out.println("upload  success");
        }catch (IOException e) {
            e.printStackTrace();
            return "failed";
        }
		return filePath+fileName;
	}
	
	/**
	 * 上传图片
	 * @param request
	 * @param response
	 * @param file
	 */
	@RequestMapping(value="/uploadImg",method=RequestMethod.POST)
	public void uploadImg(HttpServletRequest request,HttpServletResponse response,@RequestParam(value = "editormd-image-file", required = false) MultipartFile file){
		try {
			String filePath = "/images/"+MyUtil.random(5)+".png";
			String resultPath = ImgUtil.uploadImg(root_fold, filePath, file.getInputStream());
			response.getWriter().write( "{\"success\": 1, \"message\":\"上传成功\",\"url\":\"" + resultPath + "\"}" );
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.getWriter().write( "{\"success\": 0, \"message\":\"上传失败\",\"url\":\""+ "\"}" );
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	/**
     * 显示上传根目录的图片或文件
     * @param filename
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(root_fold, filename).toString()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取图片
     * @param folderName
     * @param date
     * @param filename
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{folderName}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getResource(@PathVariable("folderName") String folderName,@PathVariable("filename") String filename) {
    	try {
    		return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(root_fold+"/"+folderName, filename).toString()));
    	} catch (Exception e) {
    		return ResponseEntity.notFound().build();
    	}
    }
    
    /**
     * 获取音乐
     * @param folderName
     * @param filename
     * @return
     */
//    @RequestMapping(method = RequestMethod.GET, value = "/music/{folderName}/{filename:.+}")
//    public void getMusic(@PathVariable("folderName") String folderName,@PathVariable("filename") String filename,HttpServletResponse response) {
//    	InputStream in =null;
//    	OutputStream out=null;
//    	try {
//    		response.setHeader("Access-Control-Allow-Origin", "*");
//    		response.setHeader("Content-Type", "audio/mpeg");
//    		response.setHeader("Accept-Ranges", "bytes");
//    		System.out.println("进来了，filename="+filename);
//    		Resource resource = resourceLoader.getResource("file:" + Paths.get(root_fold+"/"+folderName, filename).toString());
//    		//这是音频文件的二进制流
//    		in = resource.getInputStream();
//    		out = response.getOutputStream();
//    		byte[] bys = new byte[5120];
//    		int len = 0;
//		    while ((len = in.read(bys)) != -1) {
//		    	 out.write(bys, 0, len);
//		     }
//		    //刷空输出流，并输出所有被缓存的字节
//		    //out.flush();
//    		
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}finally{
//    		if(out != null){
//    			try {
//					out.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//    		}
//    		if(in != null){
//    			try {
//					in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//    			
//    		}
//    	}
//    }
}
