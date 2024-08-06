package lecture.jpa2.api;


import lecture.jpa2.domain.Order;
import lecture.jpa2.repository.OrderRepository;
import lecture.jpa2.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class OrderApiController {

  private final OrderRepository orderRepository;


  @GetMapping("/orders")
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

}
