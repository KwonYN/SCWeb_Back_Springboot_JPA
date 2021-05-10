package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)  // 하나의 Order에는 여러 OrderItem을 가질 수 있지만,
    // OrderItem은 하나의 Order만을 가질 수 있음!! (여러 개를 갖는다? 그럼 내 주문에 다른 사람 주문이 껴있다? ㅅㅂ;)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 당시의 주문 가격
    private int count;      // 주문 당시의 주문 수량

    // == 주문 상품 생성 메서드== //
    // static이기 때문에 new로 객체 생성 없이 호출 가능
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count);

        return orderItem;
    }

    // protected OrderItem() {}
    /*  생성자를 protected로 만듦으로써
        이 패키지 외부에서 new로 생성되는 것을 막기 위함!!
        → 그리고 @NoArgsConstructor(access = AccessLevel.PROTECTED)로도 작성 가능
    */

    // ==비즈니스 로직== //
    public void cancel() {
        getItem().addStock(this.count); // 재고 수량 원상복귀
    }

    // ==조회 로직== //
    /**
     * 주문 상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice()*getCount();
    }
}
