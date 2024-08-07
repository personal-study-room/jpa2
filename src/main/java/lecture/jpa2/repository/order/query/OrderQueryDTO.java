package lecture.jpa2.repository.order.query;

import java.time.LocalDateTime;
import java.util.List;
import lecture.jpa2.domain.Address;
import lecture.jpa2.domain.OrderStatus;
import lombok.Data;

@Data
public class OrderQueryDTO {

  private Long orderId;
  private String name;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private Address address;
  private List<OrderItemQueryDTO> orderItems;

  public OrderQueryDTO(Long id, String name, LocalDateTime orderDate, OrderStatus orderStatus,
      Address address) {
    this.orderId = id;
    this.name = name;
    this.orderDate = orderDate;
    this.orderStatus = orderStatus;
    this.address = address;
  }
}
