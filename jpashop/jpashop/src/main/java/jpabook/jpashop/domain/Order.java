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

    @ManyToOne  // mappedBy X. 연관관계의 주인(with Member)
    @JoinColumn(name = "member_id")    // == Mapping을 뭘로 할꺼지? (Foreign Key)
    private Member member;

    @OneToMany(mappedBy = "order")  // OrderItem의 order와 맵핑됨
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "delivery_id") // 일대일 관계는 어디에다가 FK를 둬도 괜찮음. 각각 장단점이 있음.
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL]
}
