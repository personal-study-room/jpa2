package lecture.jpa2.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

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

  public List<OrderQueryDTO> findAllByDtoOptimization() {
    List<OrderQueryDTO> orders = findOrders();

    Map<Long, List<OrderItemQueryDTO>> orderItemMap = findOrderItemsMap(
            orders.stream()
                    .map(OrderQueryDTO::getOrderId)
                    .toList()
    );

    orders.forEach(orderQueryDTO ->
            orderQueryDTO.setOrderItems(
                    orderItemMap.get(orderQueryDTO.getOrderId())
            )
    );

    return orders;

  }

  private Map<Long, List<OrderItemQueryDTO>> findOrderItemsMap(List<Long> orderIds) {
    List<OrderItemQueryDTO> orderItems = em.createQuery(
                    "select new lecture.jpa2.repository.order.query.OrderItemQueryDTO(oi.order.id, i.name,oi.orderPrice, oi.count) " +
                            "from OrderItem oi " +
                            "join oi.item i " +
                            "where oi.order.id in :orderIds", OrderItemQueryDTO.class
            )
            .setParameter("orderIds", orderIds)
            .getResultList();

    // 사실상 메모리에 올려두고 하는 작업이라고 보면 된다
    Map<Long, List<OrderItemQueryDTO>> orderItemMap = orderItems.stream()
            .collect(groupingBy(OrderItemQueryDTO::getOrderId));
    return orderItemMap;
  }

  public List<OrderQueryDTO> findAllByDtoFlat() {

    List<OrderFlatDTO> resultList = em.createQuery(
                    "select  distinct new lecture.jpa2.repository.order.query.OrderFlatDTO(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) " +
                            "from Order o " +
                            "join o.member m " +
                            "join o.delivery d " +
                            "join o.orderItems oi " +
                            "join oi.item i", OrderFlatDTO.class
            )
            .getResultList();

    return resultList.stream()
            .collect(
                    groupingBy(
                            o -> new OrderQueryDTO(
                                    o.getOrderId(),
                                    o.getName(),
                                    o.getOrderDate(),
                                    o.getOrderStatus(),
                                    o.getAddress()
                            ),
                            mapping(o -> new OrderItemQueryDTO(o.getOrderId(),
                                    o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                    )
            )
            .entrySet().stream()
            .map(
                    e -> {
                      OrderQueryDTO orderQueryDTO = new OrderQueryDTO(
                              e.getKey().getOrderId(),
                              e.getKey().getName(),
                              e.getKey().getOrderDate(),
                              e.getKey().getOrderStatus(),
                              e.getKey().getAddress()
                      );
                      orderQueryDTO.setOrderItems(e.getValue());
                      return orderQueryDTO;
                    }
            )
            .toList();
  }
}
