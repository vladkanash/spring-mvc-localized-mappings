package config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import request.handler.LocalizedRequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableWebMvc
@ComponentScan("controller")
public class WebConfig extends WebMvcConfigurationSupport {

    private final static String MAPPINGS_BASENAME = "mappings";

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping(Properties mappingProperties) {

        var handlerMapping = new LocalizedRequestMappingHandlerMapping(mappingProperties, MAPPINGS_BASENAME);

        handlerMapping.setOrder(0);
        handlerMapping.setDetectHandlerMethodsInAncestorContexts(true);
        handlerMapping.setInterceptors(getInterceptors(this.mvcConversionService(), this.mvcResourceUrlProvider()));
        handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());
        return handlerMapping;
    }

    @Bean
    public InternalResourceViewResolver setupViewResolver() {
        var resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);

        return resolver;
    }

    @Bean
    public Properties mappingProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("mappings.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}
