package lecture.jpa2.repository.order.query;


import lecture.jpa2.domain.Address;
import lecture.jpa2.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderFlatDTO {
  private Long orderId;
  private String name;
  private LocalDateTime orderDate;
  private OrderStatus orderStatus;
  private Address address;
  private String itemName;
  private int orderPrice;
  private int count;
}
