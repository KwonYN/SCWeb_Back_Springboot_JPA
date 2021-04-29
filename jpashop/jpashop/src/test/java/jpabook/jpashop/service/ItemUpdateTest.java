package jpabook.jpashop.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import jpabook.jpashop.domain.item.Book;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        // given
        Book book = em.find(Book.class, 1L);

        // when : "한 트랜젝션 안"에서 변경하는 상황
        book.setName("한 트랜젝션 내에서 엔티티의 정보를 바꾼다!");
        /*
            [ Dirty Checking (변경 감지) ]
            (한 트랜젝션 안에서 데이터가 변경이 된 후) TX commit이 되면,
            JPA는 이 변경된 데이터를 찾아내어 update query를 자동으로 생성하여 DB에 반영을 함.
            이 메커니즘을 통해 기본적으로 JPA에서 엔티티의 데이터를 변경할 수 있음.
         */

        // then
    }
}
