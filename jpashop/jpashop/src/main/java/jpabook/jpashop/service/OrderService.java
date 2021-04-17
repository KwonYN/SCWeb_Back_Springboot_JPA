package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;    // member id로 조회
    private final ItemRepository itemRepository;        // item id로 조회
    private final OrderRepository orderRepository;      // OrderRepository에서!

    /**
     * 주문
     */
    @Transactional // 데이터를 변경하는 것이기 때문 (readOnly 풀어줌)
    public Long order(Long memberId, Long itemId, int count) {

        // Entity 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송 정보 생성
        /*
            원래는 deliveryRepository도 따로 생성을 한 다음에 --- repository 패키지 내
            deliveryRepository.save();                   --- service 패키지 내 다른 *Service 클래스 봐봐
            Delivery delivery = Delivery.createDelivery(params...); 로 생성을 해주어야 하는 것!!
            → 하지만 쉬운 예제를 위해 Delivery 구체적으로 구현 및 사용 X
        */
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성 - static이기에 생성 없이 불러올 수 있음
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성 - static이기에 생성 없이 불러올 수 있음
        Order order = Order.createOrder(member, delivery, orderItem);
        // 그리고 원래는 orderItem을 여러 개 넘길 수 있도록 해야 하는데 쉬운 예제를 위해 하나만!!;;

        // 주문 저장
        orderRepository.save(order);
        /*  Cf.
            Order 클래스에서 보면,
            orderItems랑 delivery가 CascadeType.ALL로 되어 있음.
            즉, order만 persist해도 관련된 엔티티(?) 객체 모두 persist가 되는 것!!

            자, 그럼 어디까지 cascade해야 하느냐??
            → 딱 잘라 말하기는 애매할 수 있음.
              OrderItem이랑 Delivery를 보자. 얘네들은 Order에서만 사용한다! (private owner)
              즉, 다른 엔티티에서는 사용이 되지 않는다는 것
              이러한 경우에만 cascade 해주자! (Ch.6 "주문 서비스 개발" 5:55)
              - Delivery의 경우, 구현 제대로 안해주긴 했는데
                만약 다른 엔티티에서도 쓴다? 그럼 여기에서 cascade해주면 안됨!;;
        */

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 그 엔티티의 주문 취소
        order.cancel();
    }
        /*  [ JPA의 강점! ]
            JPA 사용하지 않고 개발했을 때는,(JDBC, iBatis)
                위의 코드처럼 데이터를 변경하고 나서도                     --- 로직
                그 데이터를 이용해서 직접 SQL을 직접 짜서 DB도 변경했어야 함
            JPA로 개발하게 된다면,
                order.cancel();로 엔티티 내 데이터만 바꾸어도
                JPA가 알아서 변경 포인트를 잡아내어 DB Update Query 날려줌!!;
        */

    /**
     * 검색
     */
//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
//    }
}
