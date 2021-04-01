package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // JPA에서는 엔티티는 테이블에 대응하는 하나의 클래스라고 생각
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // mappedBy X. 연관관계의 주인(with Member)
    @JoinColumn(name = "member_id")    // == Mapping을 뭘로 할꺼지? (Foreign Key)
    private Member member;

            // OrderItem의 order와 맵핑됨
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    /*
        CascadeType.ALL??
        원래는
        em.persist(orderItemA);
        em.persist(orderItemB);
        em.persist(orderItemC);  이렇게 각각 저장해준 다음에
        em.persist(order);  order를 저장해주어야 했음.
        그런데 Cascade를 사용함으로써
        em.persist(order);만으로 orderItemX를 모두 저장 가능!!
     */

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id") // 일대일 관계는 어디에다가 FK를 둬도 괜찮음. 각각 장단점이 있음.
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]


    // (양방향)연관관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
        /*
            엔티티간 양방향 관계에서
            양쪽 클래스 코드로 들어가서 각각 세팅해주는 것이 아니라
            여기에서 양쪽에 각각 셋팅을 하는 코드를 추가해주는 꼴!

            아래의 코드 역할을 하게 해줌!
            Member member = new Member();
            Order order = new Order();

            member.getOrders().add(order);
            order.setMember(member);
         */
    }
    // 아래 코드도 마찬가지!
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}
