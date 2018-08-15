//package top.lrshuai.blog.util;
//
//
//import java.io.File;
//import java.util.List;
//
//import com.hankcs.hanlp.seg.common.Term;
//import com.hankcs.hanlp.tokenizer.BasicTokenizer;
//
//public class NLPDemo {
//	public static void main(String[] args) {
//		System.out.println(new File(".").getAbsolutePath());
//		String text = "举办纪念活动铭记二战历史，不忘战争带给人类的深重灾难，是为了防止悲剧重演，确保和平永驻；" +
//                "铭记二战历史，更是为了提醒国际社会，需要共同捍卫二战胜利成果和国际公平正义，" +
//                "必须警惕和抵制在历史认知和维护战后国际秩序问题上的倒行逆施。";
//		List<Term> keywords  = BasicTokenizer.segment(text);
//		System.out.println(keywords.get(0).word);
//        System.out.println(keywords);
//        String text2="你叫什么名字和名字，哈哈？";
//        List<Term> keywords2  = BasicTokenizer.segment(text2);
//        System.out.println(keywords2);
//        
//	}
//}
