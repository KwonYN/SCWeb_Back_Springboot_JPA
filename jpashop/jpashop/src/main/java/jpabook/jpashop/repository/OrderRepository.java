package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    /**
     * 주문 엔티티 저장
     */
    public void save(Order order) {
        em.persist(order);
    }

    /**
     * 단건 조회
     */
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    /**
     * 검색 : 동적 Query 필요! (회원명, 주문상태로 주문 검색)
     */
    // 첫 번재 방법 : 
    public List<Order> findAll(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // == JPQL을 동적으로 만들기 위함! == //
        // 주문 상태 검색 : 조건에 따라 JPQL 만들어 동적 Query 처리
        if(orderSearch.getOrderStatus() != null) {
            if(isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += "o.status := status";
        }
        // 회원 이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            if(isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += "m.name like :name";
        }

        // Query 생성
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                //.setFirstResult(100)            // Pagination (100개씩)
                .setMaxResults(1000);            // 최대 1000건으로 제한

        if(orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
        /*
            [ Original ]
                em.createQuery("select o from Order o join o.member m"
                                + " where o.status := status"
                                + " where m.name like :name"
                                , Order.class)
                  .setParameter("status", orderSearch.getOrderStatus())
                  .setParameter("name", orderSearch.getMemberName())
                  .setFirstResult(100)
                  .setMaxResults(1000)
                  .getResultList()
         */
    }


    // 위의 로직과 비슷하니 참고하여 이해해보자!
    // 두 번째 방법 : JPA Criteria(표준)으로 해결
    // - 권장하는 방법은 아님ㅋ 실무적이지도, 쉽지도, 유지보수성도... ↓ ㅠㅠ 대안은 없을까? ▶ QueryDSL
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name
                    = cb.like(m.<String>get("name"), "%"+orderSearch.getMemberName()+"%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);

        return query.getResultList();
    }

    // 세 번째 방법 : 복잡한 JPA Criteria의 대안, Querydls
    // 예시 이기 때문에 여러 클래스 등 구현되어 있지 않음. logic에 대한 예시
    /*
    public List<Order> findAllQueryDSL(OrderSearch orderSearch) {

        QOredr order = QOrder.order;
        QMember member = QMember.member;

        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()),
                        nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression statusEq(OrderStatus statusCond) {
        if (statusCond == null) {
            return null;
        }
        return order.status.eq(statusCond);
    }

    private BooleanExpression nameLike(String nameCond) {
        if (!StringUtils.hasText(nameCond)) {
            return null;
        }
        return member.name.like(nameCond);
    }
     */
}
