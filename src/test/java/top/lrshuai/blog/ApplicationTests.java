package top.lrshuai.blog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import top.lrshuai.blog.entity.Article;
import top.lrshuai.blog.es.dao.DaoImpl;
import top.lrshuai.blog.plugin.Page;
import top.lrshuai.blog.util.DateUtil;
import top.lrshuai.blog.util.ParameterMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	
	@Autowired
	private RedisTemplate<String, List<Map<String,Object>>> rm;
	
	@Autowired
	private RedisTemplate<String, List<Article>> redis;
	
	@Autowired
	private DaoImpl dao;
	
	@Test
	public void test2() throws Exception{
		List<Map<String,Object>> ms = new ArrayList<>();
		Map<String,Object> map = new HashMap<>();
		map.put("name", "rs");
		map.put("age", 20);
		
		Map<String,Object> map1 = new HashMap<>();
		map1.put("name", "rs1");
		map1.put("age", 21);
		
		Map<String,Object> map2 = new HashMap<>();
		map2.put("name", "rs2");
		map2.put("age", 22);
		
		ms.add(map);
		ms.add(map1);
		ms.add(map2);
		rm.opsForValue().set("key_ml", ms);
		System.out.println("放入缓存》。。。。。。。。。。。。。。。。。。。");
		System.out.println("=============================");
		List<Map<String,Object>> mls = rm.opsForValue().get("key_ml");
		System.out.println("mls="+mls);
	}
	
	@Test
	public void test1() {
		List<Article> list = new ArrayList<>();
		Article a1 = new Article("1", "1", "标题1", "html text", "text text", 456, 456, 123, 1, 1, 123, "2017-09-23");
		Article a3 = new Article("1", "1", "标题1", "html text", "text text", 456, 456, 123, 1, 1, 123, "2017-09-23");
		Article a4 = new Article("1", "1", "标题1", "html text", "text text", 456, 456, 123, 1, 1, 123, "2017-09-23");
		Article a5 = new Article("1", "1", "标题1", "html text", "text text", 456, 456, 123, 1, 1, 123, "2017-09-23");
		list.add(a1);
		list.add(a3);
		list.add(a4);
		list.add(a5);
		redis.opsForValue().set("TEST",list);
		
		List<Article> cachelist = redis.opsForValue().get("TEST");
		System.out.println("cachelist="+cachelist.get(0).getArticle_id());
		System.out.println("cachelist="+cachelist);
		System.out.println("cachelist="+redis.opsForValue().get("TEST"));
		System.out.println("artId="+cachelist.get(0).getArticle_id());
	}
	
	@Test
	public void testSave(){
		ParameterMap pm = new ParameterMap();
		pm.put("user_id", "1");
		pm.put("question", "test");
		pm.put("answer", "test");
		pm.put("create_time",DateUtil.getTime());
		dao.save(pm, "robot", "brain");
	}
	
	@Test
	public void testDel(){
		dao.deltele("AWArJh7qdNTfbPdoFE_3", "robot", "brain");
	}
	
	@Test
	public void testQUERY(){
		ParameterMap pm = new ParameterMap();
//		pm.put("should_key", "question");
		pm.put("must_key", "question");
//		pm.put("term_key1", "question");
		pm.put("question_value", "你");
//		pm.put("create_time", "2017-11-01 12:12:12");
//		pm.put("create_time_end", "2017-12-30 12:12:12");
		Page page = new Page();
		page.setCurrentPage(1);
		page.setShowCount(10);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) dao.query(pm, "robot", "brain",page);
		System.out.println("page="+page);
		System.out.println(result);
	}

}
