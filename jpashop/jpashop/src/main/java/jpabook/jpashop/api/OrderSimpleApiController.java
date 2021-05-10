package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 연관관계 구성 : XtoOne 연관관계인, 즉 Collection이 아닌 것들에 대한 조회
 * * Order
 * * Order -> Member
 * * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // V1. 엔티티를 노출하는 좋지 않은 방법
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // 아무 조건도 없으니(그냥 OrderSearch) 모든 주문s 가져올 것!
        for (Order order : all) {
            order.getMember().getName();        // Lazy 강제 초기화 -> 노출
            order.getDelivery().getAddress();   // Lazy 강제 초기화 -> 노출
        }
        return all;
    }
    /*  [ 위 코드의 문제점? ]
        1. Order → Member, Member → Order (양방향 연관)관계로 인해 무한루프..
           계속 엔티티 호출호출.. 큰일남;;
           => Member → Order, OrderItem → Order, Delivery → Order 부분에
              @JsonIgnore 해줌으로써 노출을 막음
                    (Cf. XtoMany는 디폴트가 Lazy!)
                
        2. fetchType.Lazy는 실제 엔티티 대신에 Proxy 엔티티, 즉 실제 엔티티를 상속 받은
           새로운 엔티티를 통해서 조회
           => ByteBuddyInterceptor라는 객체인데, 이게 순수한 자바 객체가 아니기에
              Jackson 라이브러리에서 얘를 어떻게 해? 라는 타입 에러가 튀어나옴
           => Hibernate5Module 설치한 후,
              JpashopaApplication에 Hibernate5Module를 스프링 Bean으로 등록
           => "지연 로딩은 아직 DB에서 조회한 것이 아니기 때문에
               지연 로딩 무시해!" 라는 기능을 함.
           (하.. 그런데 이렇게 조회하지 말라고 했으니, 이런게 있구나~ 정도 알아두자)
           
        3. 무엇보다도...
           ★ 엔티티를 외부에 노출했기에 권장 하지 않는 방법!!
           => 엔티티가 바뀌게 되면 API 스펙 또한 다 변하게 되니까!!
           
        4. 성능상의 문제
           => OrderItem의 경우, 필요가 없는 정보인데 얘까지 조회하기 위한 쿼리가 날아감.
                               (우리가 정함)
     */

    // V2. 엔티티를 DTO로 반환
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @Data           // DTO, API 스펙을 명확히 규정해야 함!
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // order → member로 Lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();// order → delivery로 Lazy 초기화
        }
    }
    /*  [ DTO로 변환한 후, API 요청에 의해 정보 줌. 그럼 이건 문제 없을까? ]
        1. 지연 로딩으로 인해 Query가 여전히 많이 나감
           => 결과적으로 ordersV1과 똑같은 수의 쿼리가 나감!!
           => order 조회 1번
              + order → member 조회 N번   ( order.getMember().getName(); )
              + order → delivery 조회 N번 ( order.getDelivery().getAddress(); )
              --------------------------
              최악의 경우 총 1+N*2번 쿼리 실행 (order의 수가 N개)

        2. fetchType을 LAZY에서 EAGER로 바꾸면 되지 않나!??!
           => 양방향 연관관계도 신경써주어야 하고, (양쪽으로 계속 호출)
              다른 쿼리까지도 나감;; 성능도 안좋아짐.

        3. 만약 userA가 주문을 두 번 했다면? (Ex. 어제 주문, 또 오늘도 주문)
           => order 조회 1번           = 2개의 order가 조회
              + order → member 조회 1번
              + order → delivery 조회 1번 ---- Loop1
              + order → delivery 조회 1번 ---- Loop2
              --------------------------
              같은 member가 영속성 컨텍스트 안에 존재하므로
              쿼리를 또 다시 날리지 않음!! (최악은 5번인데 이 케이스는 4번 쿼리 나감!)

        4. 그럼 어떻게 해결..?
           => V3. fetch join!
     */

    // V3. 엔티티를 DTO로 반환 - fetch join으로 쿼리 성능 최적화
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result
                = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }
    /*
        V2와 V3는 똑같은 조회 결과!
        하지만 나가는 쿼리의 양에서 차이가 있다!!
            - V2
              . order 1번 쿼리
              . order -> member N번 쿼리 (order 조회 결과 개수)
              . order -> delivery N번 쿼리 (order 조회 결과 개수)
              . => 총 1 + N + N 번의 쿼리
            - V3
              . order 1번 쿼리에 member, delivery가 join 되어 날라감
              . => 총 1번의 쿼리
    */

    // V4. 엔티티를 조회하고, DTO로 중간에 변환하여 return하는 것이 아닌
    //     바로 DTO로 조회하는 것
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
    /*
        OrderRepository - findOrderDtos 메서드를 확인해보자.
        직접 원하는 데이터들을 select 하도록 JPQL을 짰기 때문에
        V3에 비해 select 되는 데이터들이 적다, 즉 조금 더 효율적이다.

        하지만.. [ V3와 V4 간에는 Trade-off가 존재!! ]
        - V3 : 엔티티를 DTO로 반환 - fetch join으로 쿼리 성능 최적화
          . order 엔티티를 가져온 후, fetch join으로 원하는 애들만 추가로 선택하여 DTO로 반환
          . order 엔티티 자체를 건드린 코드가 아니라,
            엔티티 조회 결과 데이터를 가지고 fetch join을 통해 원하는 데이터를 뽑아냄

        - V4 : 엔티티를 조회하고, DTO로 중간에 변환하여 return하는 것이 아닌, 바로 DTO로 조회하는 것
          . SQL 짜듯이 JPQL을 직접 짜서
            내가 원하는 데이터를 보이게 한 것

        - 둘의 차이?
          . V4는 화면에는 "최적화"되어 있지만, 재사용성이 높지 않음
            > 원하는 다른 API를 위해 그에 맞는 DTO 또 만들어야

          . V3는 "재사용성"이 비교적 높음
            > 성능 자체로만 봤을 때는 V4가 아주 쪼오금 더 낫긴 함
            > 하지만 그 조차도 굉장히 미미함.. 그래서 V3을 사용하는 것이 우선은 권장됨.

          . 쉽게 말해 V3는 엔티티를 조회, V4는 직접 만든 DTO로 조회
            > V3: V3는 엔티티로 받아 fetch join 한 결과를 DTO로 변환한 것. (엔티티로 조회)
            > V4: new 명령어를 사용하여 JPQL 결과를 DTO로 즉시 변환한 것. (DTO로 조회)

         Repository는 Entity를 조회하는 용으로 사용하는 것을 권장한다!!
           - V4는 DTO를 바로 조회하지만, 성능적으로 좋다.
             그렇다 하더라도 성능이 정말 극적으로 좋아질까? 아닐 것이다.
             하지만 경우에 따라서는 V4와 같이 사용하면 좋은 케이스도 있을 것이다.
             > repository 패키지 밑에 DTO로 바로 조회가능한 코드를 놓는 다른 패키지를 생성하자.
               그 패키지 밑에 OrderSimpleQueryDto 코드를 놓고 따로 관리해보자!
     */
}
