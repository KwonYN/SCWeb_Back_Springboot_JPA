package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 총 주문 2개 생성
 *
 * * userA
 * * * JPA1 book
 * * * JPA2 book
 *
 * * userB
 * * * Spring1 book
 * * * Spring2 book
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member
                = createMember("userA", "Seoul", "gil", "12345");
            em.persist(member);

            Book book1
                = createBook("JPA1 book", 10000, 100);
            em.persist(book1);

            Book book2
                = createBook("JPA2 book", 20000, 200);
            em.persist(book2);

            OrderItem orderItem1
                = OrderItem.createOrderItem(book1, book1.getPrice(), 1);
            OrderItem orderItem2
                = OrderItem.createOrderItem(book2, book2.getPrice(), 1);

            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member
                = createMember("userB", "Incheon", "gil", "54321");
            em.persist(member);

            Book book1
                = createBook("Spring1 book", 15000, 150);
            em.persist(book1);

            Book book2
                = createBook("Sprint2 book", 25000, 250);
            em.persist(book2);

            OrderItem orderItem1
                = OrderItem.createOrderItem(book1, book1.getPrice(), 3);
            OrderItem orderItem2
                = OrderItem.createOrderItem(book2, book2.getPrice(), 7);

            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }


        /**
         * Extract Method : Ctrl + Alt + M
         */
        private Member createMember(String userName,
                                    String city,
                                    String street,
                                    String zipcode) {
            Member member = new Member();
            member.setName(userName);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Book createBook(String bookName,
                                int price,
                                int stockQuantity) {
            Book book = new Book();
            book.setName(bookName);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}

