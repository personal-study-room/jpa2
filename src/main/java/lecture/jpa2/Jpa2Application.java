package lecture.jpa2;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Jpa2Application {

  public static void main(String[] args) {
    SpringApplication.run(Jpa2Application.class, args);
  }

  // 기본적으로 초기화 된 프록시 객체만 노출, 초기화 되지 않은 프록시 객체는 노출 안함
//  @Bean
  public Hibernate5JakartaModule hibernate5JakartaModule() {
    Hibernate5JakartaModule hibernate5JakartaModule = new Hibernate5JakartaModule();
    // 강제 지연 로딩 설정
//    hibernate5JakartaModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING,true);
    return hibernate5JakartaModule;
  }
}
