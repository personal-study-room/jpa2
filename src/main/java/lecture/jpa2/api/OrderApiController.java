package lecture.jpa2.api;


import lecture.jpa2.domain.Address;
import lecture.jpa2.domain.Order;
import lecture.jpa2.domain.OrderItem;
import lecture.jpa2.domain.OrderStatus;
import lecture.jpa2.repository.OrderRepository;
import lecture.jpa2.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderApiController {

  private final OrderRepository orderRepository;


  @GetMapping("/v1/orders")
  public List<Order> orderV1(){
    // 1번
    List<Order> all = orderRepository.findAllByString(new OrderSearch());

    for (Order order : all) {
      // 2번
      order.getMember().getName(); // lazy 초기화
      // 2번
      order.getDelivery().getAddress(); // lazy 초기화
      // (orderItem 1번 + item 2번) * 2 = 6번
      order.getOrderItems().forEach(orderItem -> orderItem.getItem().getName()); // lazy 초기화
    }
    return all;
  }


  @GetMapping("/v2/orders")
  public List<OrderDTO> orderV2(){
    // 1번
    List<Order> orders = orderRepository.findAllByString(new OrderSearch());

    List<OrderDTO> list = orders.stream()
            .map(OrderDTO::new)
            .toList();

    return list;
  }

  @Data
  static class OrderDTO {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDTO> orderItems;

    public OrderDTO(Order order) {
      this.orderId = order.getId();
      name = order.getMember().getName();
      orderDate = order.getOrderDate();
      orderStatus = order.getStatus();
      address = order.getDelivery().getAddress();

      orderItems = order.getOrderItems().stream()
              .map(OrderItemDTO::new)
              .toList();
    }
  }

  @Getter
  static class OrderItemDTO {
    private String itemName;//상품 명
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderItemDTO(OrderItem orderItem) {
      itemName = orderItem.getItem().getName();
      orderPrice = orderItem.getOrderPrice();
      count = orderItem.getCount();
    }
  }

}
