package com.example.demo;

import com.example.demo.conf.FacadeIT;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

class ApplicationStartupIT extends FacadeIT {

  @Test
  void contextLoads(ApplicationContext context) {
    org.assertj.core.api.Assertions.assertThat(context).isNotNull();
  }
}
