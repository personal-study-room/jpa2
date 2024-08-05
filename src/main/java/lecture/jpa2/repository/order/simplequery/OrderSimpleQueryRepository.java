package lecture.jpa2.repository.order.simplequery;


import jakarta.persistence.EntityManager;
import lecture.jpa2.repository.OrderSimpleQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

  private final EntityManager em;

  // DTO로 한번에 조회한다는 것은 어떻게보면 api 스펙이 그대로 들어온 것.
  // 때문에 repository에 api spec이 그대로 반영되었다는 점에서 조금 유연성이 떨어짐.
  public List<OrderSimpleQueryDTO> findOrderDTO() {
    return em.createQuery(
            "select new lecture.jpa2.repository.OrderSimpleQueryDTO(o.id, m.name, o.orderDate, o.status, d.address) from Order o " +
                    "join o.member m " +
                    "join o.delivery d", OrderSimpleQueryDTO.class
    ).getResultList();
  }
}
