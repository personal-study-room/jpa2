package lecture.jpa2.api;


import lecture.jpa2.domain.Address;
import lecture.jpa2.domain.Order;
import lecture.jpa2.domain.OrderStatus;
import lecture.jpa2.repository.OrderRepository;
import lecture.jpa2.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

  private final OrderRepository orderRepository;

  /**
   * @V1. 엔티티 직접 노출
   * @- Hibernate5Module 모듈 등록, LAZY=null 처리 * - 양방향 관계 문제 발생 -> @JsonIgnore
   */
  @GetMapping("/api/v1/simple-orders")
  public List<Order> orderV1() {
    List<Order> all = orderRepository.findAllByString(new OrderSearch());
    for (Order order : all) {
      order.getMember().getName(); // Lazy로딩 강제
      order.getDelivery().getAddress(); // Lazy로딩 강제
    }
    return all;
  }

  @GetMapping("/api/v2/simple-orders")
  public List<SimpleOrderDTO> orderV2() {
    List<Order> orders = orderRepository.findAllByString(new OrderSearch());
    List<SimpleOrderDTO> result = orders.stream()
            .map(SimpleOrderDTO::new)
            .collect(Collectors.toList());

    return result;
  }


  @Data
  public static class SimpleOrderDTO {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;


    public SimpleOrderDTO(Order order) {
      orderId = order.getId();
      name = order.getMember().getName(); // Lazy 초기화
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress(); // Lazy 초기화
    }
  }
}
