package app;

import app.repos.ConcertRepository;
import app.repos.HallRepository;
import app.repos.SaleRepository;
import app.repos.SeatSaleRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages={"app", "app.model"})
@EnableJpaRepositories(basePackageClasses = {SaleRepository.class, ConcertRepository.class, SeatSaleRepository.class, HallRepository.class})
//@ComponentScan(basePackageClasses = MainController.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
