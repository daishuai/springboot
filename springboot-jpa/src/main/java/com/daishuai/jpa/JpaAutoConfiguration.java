package com.daishuai.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Daishuai
 * @description
 * @date 2019/9/22 10:16
 */
@Configuration
@ComponentScan
@EnableJpaRepositories
@EntityScan
public class JpaAutoConfiguration {
}
