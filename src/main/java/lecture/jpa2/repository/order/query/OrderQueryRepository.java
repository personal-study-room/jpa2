package lecture.jpa2.repository.order.query;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

  private final EntityManager em;

  public List<OrderQueryDTO> findOrderQueryDTOs() {
    List<OrderQueryDTO> results = findOrders();

    results.forEach(result -> {
      List<OrderItemQueryDTO> orderItems = findOrderItems(result.getOrderId());
      result.setOrderItems(orderItems);
    });

    return results;
  }

  private List<OrderItemQueryDTO> findOrderItems(Long orderId) {
    return em.createQuery(
        "select new lecture.jpa2.repository.order.query.OrderItemQueryDTO(oi.order.id, i.name,oi.orderPrice, oi.count) "
            + "from OrderItem oi "
            + "join oi.item i "
            + "where oi.order.id = :orderId", OrderItemQueryDTO.class
    )
        .setParameter("orderId", orderId)
        .getResultList();
  }

  private List<OrderQueryDTO> findOrders() {
    return em.createQuery(
        "select new lecture.jpa2.repository.order.query.OrderQueryDTO(o.id, m.name, o.orderDate, o.status, d.address) "
            + "from Order o "
            + "join o.member m "
            + "join o.delivery d", OrderQueryDTO.class
    ).getResultList();
  }
}
