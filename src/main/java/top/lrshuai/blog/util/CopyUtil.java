package top.lrshuai.blog.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
/**
 * 
 * @author rstyro
 *
 */
public class CopyUtil {
	public static void main(String[] args) throws Exception {
		String srcClustName="robot";
		String srcIndexName="robot";
		String srcIp="127.0.0.1";
		int srcPort = 9300;
		
		String tagClustName="coolEs";
		String tagIndexName="pua";
		String tagTypeName="speechcraft";
		String tagIp="cool.lrshuai.top";
		int tagPort = 9292;
		
//     esToEs(srcClustName, srcIndexName, srcIp, srcPort, tagClustName, tagIndexName, tagTypeName, tagIp, tagPort);
		
		outToFile(tagClustName, tagIndexName, tagTypeName, tagIp, tagPort, "f:\\speechcraft.txt");
//		fileToEs(tagClustName, tagIndexName, tagTypeName, tagIp, tagPort, "f:\\speechcraft.txt");
		//fileToEs(srcClustName, srcIndexName, tagTypeName, srcIp, srcPort, "f:\\brain.txt");
       // textToEs(tagClustName, tagIndexName, tagTypeName, tagIp, tagPort, "f:\\data.txt");
	}
	
	/**
	 * 数据拷贝
	 * elasticsearch 到 elasticsearch
	 * @param srcClustName 	原集群名称
	 * @param srcIndexName 	原索引
	 * @param srcIp		   	原ip
	 * @param srcPort		原 transport 服务端口(默认9300的端口)
	 * @param tagClustName	目标集群名称
	 * @param tagIndexName	目标索引
	 * @param tagTypeName	目标type
	 * @param tagIp			目标ip
	 * @param tagPort		目标transport服务端口
	 * @throws InterruptedException
	 */
	public static void esToEs(String srcClustName,String srcIndexName,String srcIp,int srcPort,String tagClustName,String tagIndexName,String tagTypeName,String tagIp,int tagPort) throws InterruptedException{
		Settings srcSettings = Settings.builder()  
              .put("cluster.name", srcClustName)
//              .put("client.transport.sniff", true)  
//              .put("client.transport.ping_timeout", "30s")  
//              .put("client.transport.nodes_sampler_interval", "30s")
              .build();  
      TransportClient srcClient = new PreBuiltTransportClient(srcSettings);  
      srcClient.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(srcIp, srcPort)));  
        
      Settings tagSettings = Settings.builder()  
              .put("cluster.name", tagClustName)
//              .put("client.transport.sniff", true)  
//              .put("client.transport.ping_timeout", "30s")  
//              .put("client.transport.nodes_sampler_interval", "30s")
              .build();  
      TransportClient tagClient = new PreBuiltTransportClient(tagSettings);  
      tagClient.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(tagIp, tagPort)));  
        
      SearchResponse scrollResp = srcClient.prepareSearch(srcIndexName)  
              .setScroll(new TimeValue(1000))
              .setSize(1000)
              .execute().actionGet();  
        
      BulkRequestBuilder bulk = tagClient.prepareBulk();  
      ExecutorService executor = Executors.newFixedThreadPool(5);  
      while(true){  
          bulk = tagClient.prepareBulk();  
          final BulkRequestBuilder bulk_new = bulk;  
          System.out.println("查询条数="+scrollResp.getHits().getHits().length);
          for(SearchHit hit : scrollResp.getHits().getHits()){  
              IndexRequest req = tagClient.prepareIndex().setIndex(tagIndexName)  
          .setType(tagTypeName).setSource(hit.getSourceAsMap()).request();  
              bulk_new.add(req);  
          }  
          executor.execute(new Runnable() { 
              @Override  
              public void run() {  
                  bulk_new.execute();  
              }  
          });  
          Thread.sleep(100);
          scrollResp = srcClient.prepareSearchScroll(scrollResp.getScrollId())  
                  .setScroll(new TimeValue(1000)).execute().actionGet();  
          if(scrollResp.getHits().getHits().length == 0){  
              break;  
          }  
      }
      //该方法在加入线程队列的线程执行完之前不会执行
      executor.shutdown();
      System.out.println("执行结束");
      tagClient.close();
      srcClient.close();
	}
	
	/**
	 * elasticsearch 数据到文件
	 * @param clustName 	集群名称
	 * @param indexName 	索引名称
	 * @param typeName  	type名称
	 * @param sourceIp  	ip
	 * @param sourcePort 	transport 服务端口
	 * @param filePath		生成的文件路径
	 */
	public static void outToFile(String clustName,String indexName,String typeName,String sourceIp,int sourcePort,String filePath){
		Settings settings = Settings.builder()  
                .put("cluster.name", clustName)
//                .put("client.transport.sniff", true)  
//                .put("client.transport.ping_timeout", "30s")  
//                .put("client.transport.nodes_sampler_interval", "30s")
                .build();  
        TransportClient client = new PreBuiltTransportClient(settings);  
        client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(sourceIp, sourcePort)));  
        SearchRequestBuilder builder = client.prepareSearch(indexName);
        if(typeName != null){
        	builder.setTypes(typeName);
        }
        builder.setQuery(QueryBuilders.matchAllQuery());
        builder.setSize(10000);
        builder.setScroll(new TimeValue(6000));
        SearchResponse scrollResp = builder.execute().actionGet();
	        try {
	        	//把导出的结果以JSON的格式写到文件里
	            BufferedWriter out = new BufferedWriter(new FileWriter(filePath, true));
	            long count = 0;
	            while (true) {
	            	for(SearchHit hit : scrollResp.getHits().getHits()){
	            		String json = hit.getSourceAsString();
	            		if(!json.isEmpty() && !"".equals(json)){
	            			out.write(json);
	            			out.write("\r\n");
	            			count++;
	            		}
	            	} 
	            	scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())  
	                      .setScroll(new TimeValue(6000)).execute().actionGet();  
	              if(scrollResp.getHits().getHits().length == 0){  
	                  break;  
	              } 
	            }
	            System.out.println("总共写入数据:"+count);
	            out.close();
	            client.close();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
	}
	
	/**
	 * 把json 格式的文件导入到elasticsearch 服务器
	 * @param clustName		集群名称
	 * @param indexName		索引名称
	 * @param typeName		type 名称
	 * @param sourceIp		ip
	 * @param sourcePort	端口
	 * @param filePath		json格式的文件路径
	 */
	@SuppressWarnings("deprecation")
	public static void fileToEs(String clustName,String indexName,String typeName,String sourceIp,int sourcePort,String filePath){
		Settings settings = Settings.builder()  
				.put("cluster.name", clustName)
				.build();  
		TransportClient client = new PreBuiltTransportClient(settings);  
		client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(sourceIp, sourcePort)));  
		try {
			//把导出的结果以JSON的格式写到文件里
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String json = null;
            int count = 0;
            //开启批量插入
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            while ((json = br.readLine()) != null) {
                bulkRequest.add(client.prepareIndex(indexName, typeName).setSource(json));
                //每一千条提交一次
                count++;
                if (count% 1000==0) {
                    System.out.println("提交了1000条");
                    BulkResponse bulkResponse = bulkRequest.execute().actionGet();
                    if (bulkResponse.hasFailures()) {
                    	System.out.println("message:"+bulkResponse.buildFailureMessage());
                    }
                    //重新创建一个bulk
                    bulkRequest = client.prepareBulk();
                }
            }
            //最后如果不满1000 也提一次
            bulkRequest.execute().actionGet();
            System.out.println("总提交了：" + count);
            br.close();
            client.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void textToEs(String clustName,String indexName,String typeName,String sourceIp,int sourcePort,String filePath){
		Settings settings = Settings.builder()  
				.put("cluster.name", clustName)
//				.put("client.transport.sniff", true)  
//				.put("client.transport.ping_timeout", "30s")  
//				.put("client.transport.nodes_sampler_interval", "30s")
				.build();  
		TransportClient client = new PreBuiltTransportClient(settings);  
		client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(sourceIp, sourcePort)));  
		
		try {
			//把导出的结果以JSON的格式写到文件里
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String json = null;
			int count = 0;
			//开启批量插入
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			XContentBuilder builder = null;
			String keyvalue="";
			while ((json = br.readLine()) != null) {
				++count;
				keyvalue = keyvalue+json+"lkjh";
				if(count % 3 == 0){
					builder = XContentFactory.jsonBuilder().startObject();
					String[] datas = keyvalue.split("lkjh");
					String key = datas[0].trim();
					String value = datas[1].trim();
					if(datas.length > 2){
						System.out.println("keyvalue="+keyvalue+",count="+count+",key="+key+",value="+value);
						break;
					}
					if(Tools.notEmpty(key) && Tools.notEmpty(value)){
						builder.field("question", key);
						builder.field("answer", value);
						builder.field("create_time","2017-12-11 11:11:11");
						builder.field("user_id", "1");
						builder.endObject();
						bulkRequest.add(client.prepareIndex(indexName, typeName).setSource(builder));
					}else{
						System.out.println("keyvalue="+keyvalue+",count="+count+",key="+key+",value="+value);
					}
					keyvalue="";
					builder = null;
					datas=null;
				}
			}
			bulkRequest.execute().actionGet();
			System.out.println("总提交了：" + count);
			System.out.println("总提交了：" + count/3);
			br.close();
			client.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
