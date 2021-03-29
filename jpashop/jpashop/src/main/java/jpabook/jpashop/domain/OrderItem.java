package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne  // 하나의 Order에는 여러 OrderItem을 가질 수 있지만,
    // OrderItem은 하나의 Order만을 가질 수 있음!! (여러 개를 갖는다? 그럼 내 주문에 다른 사람 주문이 껴있다? ㅅㅂ;)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 당시의 주문 가격
    private int count;      // 주문 당시의 주문 수량
}
