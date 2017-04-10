package fi.thl.thldtkk.api.metadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
    HibernateJpaAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class,
    DataSourceAutoConfiguration.class
})
@ComponentScan("fi.thl.thldtkk.api")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
