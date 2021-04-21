package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.swing.*;

import static org.junit.Assert.*;


/*
    원래 테스트는 각 메서드의 단위 테스트를 잘 작성하는 것이 중요.
    아래 테스트는 전체가 잘 동작하는지 보는 것 정도의 의미임.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional  // 전체 테스트를 돌린다면, 상품주문 테스트 후에 roll back
                //                      → 상품주문_재고수량_초과 테스트 후 rollback
                //                      → 주문취소 테스트 후 rollback이 되야함!
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    String bookName = "C언어 문제 해결 및 알고리즘";
    int bookPrice = 15000;
    int startStock = 100;

    /**
     * 상품 주문이 성공해야 한다.
     */
    @Test
    public void 상품주문() throws Exception {
        int orderCount = 11;

        // given : 주문하는 사람, 주문할 책 세팅
        Member member = createMember();
        Book book = createBook(bookName, bookPrice, startStock);

        // when : 주문서비스를 통해서 주문
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then : 주문이 잘 되었는지 확인
        Order getOrder = orderRepository.findOne(orderId);

               /* message, expected값, actual 값 */
        Assert.assertEquals("상품 주문 시 상태는 ORDER!", OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("주문한 상품의 가짓수가 정확해야 함!", 1, getOrder.getOrderItems().size());
        Assert.assertEquals("주문가격은 \"가격x수량\"!", bookPrice*orderCount, getOrder.getTotalPrice());
        Assert.assertEquals("주문수량만큼 재고가 줄어야 함!", startStock-orderCount, book.getStockQuantity());
        //Assert.assertEquals("주문수량만큼 재고가 줄어야 함!", 88, book.getStockQuantity());
        // 달랐을 때 결과? : at org.junit.Assert.failNotEquals
    }

    /**
     * 상품을 주문할 때 재고 수량을 초과하면 안 된다.
     *  - 만들어 놓은 pabook.jpashop.exception.NotEnoughStockException가 터져야 함!
     *  - 위치?: Item > removeStock > 주문을 하면, 주문을 한 아이템의 수량만큼 재고에서 빠짐
     */
    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량_초과() throws Exception {
        int orderCount = 111;

        // given : 주문하는 사람, 주문할 책 세팅
        Member member = createMember();
        Book book = createBook(bookName, bookPrice, startStock);

        // when : 주문서비스를 통해서 주문
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        // 그런데 재고보다 더 많은 양을 주문하는 것이므로 Exception이 터져야 함!!

        // then : 위에서 NotEnoughStockException가 터져서 밑의 fail절로 가면 안됨!
        fail("재고 수량 부족 예외가 발생해야 함!");
        // NotEnoughStockException 안터지면, 위의 fail이 터짐
        // → Unexpected exception, expected<jpabook.jpashop.exception.NotEnoughStockException>
        //                         but was<java.lang.AssertionError>
    }

    /**
     * 주문 취소가 성공해야 한다.
     */
    @Test
    public void 주문취소() throws Exception {
        int orderCount = 11;

        // given : 주문하는 사람, 주문할 책 세팅 및 주문서비스를 통해서 주문
        Member member = createMember();
        Book book = createBook(bookName, bookPrice, startStock);

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // whenㅍ : 주문 취소
        orderService.cancelOrder(orderId);

        // then : 재고가 정상적으로 잘 복구되었는지?
        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("주문 취소 시 상태는 CANCEL!", OrderStatus.CANCEL, getOrder.getStatus());
        Assert.assertEquals("주문 취소 상품은 재고가 복구되어야 함!", startStock, book.getStockQuantity());
    }

    // == Ctrl + Alt + M → Extract Method == //
    private Member createMember() {
        Member member = new Member();
        member.setName("SCSA13");
        member.setAddress(new Address("서울", "테헤란로", "05685"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(15000);
        book.setStockQuantity(100);
        em.persist(book);
        return book;
    }
}
