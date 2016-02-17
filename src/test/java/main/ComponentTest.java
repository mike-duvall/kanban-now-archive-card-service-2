package main;

import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringApplicationConfiguration(classes = {Application.class, ApplicationTestConfig.class})
@WebAppConfiguration
@Sql(scripts = "classpath:/db/test_cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@IntegrationTest({ "server.port=10000", "management.port=10001" })
public @interface ComponentTest {

}
