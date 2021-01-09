package app;

import app.repos.ConcertRepository;
import app.repos.HallRepository;
import app.repos.SaleRepository;
import app.repos.SeatSaleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages={"app", "app.model"})
@EnableJpaRepositories(basePackageClasses = {SaleRepository.class, ConcertRepository.class, SeatSaleRepository.class, HallRepository.class})
//@ComponentScan(basePackageClasses = MainController.class)
public class Application {
    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        System.out.println("Working for 2 min...");
        Thread.sleep(1000 * 60 * 2);
        System.out.println("Closing application");
        ctx.close();
    }
}
