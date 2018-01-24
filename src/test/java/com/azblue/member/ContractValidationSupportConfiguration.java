package com.azblue.member;

import io.specto.hoverfly.recorder.HoverflyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.servlet.Filter;

/**
 * APITestConfiguration provides additional server configuration to be used when running API Tests.
 */
@Configuration
@Profile("ContractValidationSupport")
public class ContractValidationSupportConfiguration {

  private @Autowired AutowireCapableBeanFactory beanFactory;

  private int port = 8080;

  @Bean
  public FilterRegistrationBean hoverflyHttpRecordingFilter() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    Filter myFilter = new HoverflyFilter("http://localhost:" + port, "target/generated-sources/hoverfly-recorder.json");
    beanFactory.autowireBean(myFilter);
    registration.setFilter(myFilter);
    registration.addUrlPatterns("/*");
    return registration;
  }

}
