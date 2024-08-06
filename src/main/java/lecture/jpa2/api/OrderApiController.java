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

  /**
   * @결과 분석
   * @- 현재 spring 3.xx 부터는 hibernate6를 사용하고 있는데, fetch join 시에 이렇게 데이터 중복 문제을 알아서 해결하도록 고안되어 나오고 있다.
   */
  @GetMapping("/v3/orders")
  public List<OrderDTO> orderV3(){

    List<Order> orders = orderRepository.findAllWithItem();

    //order ref= lecture.jpa2.domain.Order@32c8cdba id=1
    //order ref= lecture.jpa2.domain.Order@652e9703 id=2

    for (Order order : orders) {
      System.out.println("order ref= " + order + " id=" + order.getId()) ;
    }

    List<OrderDTO> list = orders.stream()
            .map(OrderDTO::new)
            .toList();

    // 우리가 예상한 결과는 데이터 중복이 존재하면서 4개가 나가야 하는데, Hibernate 6 이 spring3.xx부터 기본이되었는데,
    // 알아서 fetch join을 하게 되면 알아서 최적화 되어 나온다.
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
