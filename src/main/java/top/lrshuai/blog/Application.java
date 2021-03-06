package top.lrshuai.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("top.lrshuai.blog.dao")
@EnableScheduling
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error/400"));            
		container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));            
		container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));     
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
//		return super.configure(builder);
		return builder.sources(this.getClass());
	}
	
}
