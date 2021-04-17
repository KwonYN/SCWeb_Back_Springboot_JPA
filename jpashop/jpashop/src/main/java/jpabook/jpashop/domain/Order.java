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


    // ==(양방향)연관관계 편의 메서드== //
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

    // ==주문 생성 메서드== //
    // static이기 때문에 new로 객체 생성 없이 호출 가능
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();          // 주문 객체 생성한 후,
        order.setMember(member);            // 주문한 사람과
        order.setDelivery(delivery);        // 배송지와
        for (OrderItem orderItem: orderItems) { // 주문한 아이템들과
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);     // 현재 주문 상태와
        order.setOrderDate(LocalDateTime.now());// 주문 시간을 넣고

        return order;                           // return!
    }

    // protected Order() {}
    /*  생성자를 protected로 만듦으로써
        이 패키지 외부에서 new로 생성되는 것을 막기 위함!!
        → 그리고 @NoArgsConstructor(access = AccessLevel.PROTECTED)로도 작성 가능
    */

    // ==비즈니스 로직== //
    /**
     * 주문 취소
     */
    public void cancel() {
        // 이미 배송 완료됐으면 주문 취소 못하겠지?
        if(delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송이 완료가 된 상품은 취소가 불가능합니다.");
        }

        // 그게 아니라면 배송 취소 가능한 상태
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem: this.orderItems) {
            orderItem.cancel(); // 재고 수량 원상복귀
        }
    }

    // ==조회 로직== //
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        /* 원래는 loop를 돌면서 더해주었음.
            for(OrderItem orderItem: this.orderItems) {
                totalPrice += orderItem.getTotalPrice();
            }
            → 하지만 "Alt + Enter" 후 "Replace with sum()"해주면 아래처럼 바뀜 ㄷㄷ (Java 8)
         */
        int totalPrice = this.orderItems.stream()
                            .mapToInt(OrderItem::getTotalPrice)
                            .sum();
        return totalPrice;
    }
}
