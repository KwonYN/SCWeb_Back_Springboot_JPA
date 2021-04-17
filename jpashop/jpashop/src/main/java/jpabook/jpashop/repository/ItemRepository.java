package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository                 // ComponentScan 대상 (자동 빈 객체 등록)
@RequiredArgsConstructor    // lombok
public class ItemRepository {

    private final EntityManager em;

    // 상품 저장
    public void save(Item item) {
        if(item.getId() == null) {  // Item.id 값은 Primary Key!
                                    // Item은 JPA에 저장(persist)하기 전까지는 id값이 없다고 함
            em.persist(item);
        } else {
            em.merge(item); // 일단 update 비슷한 거라고 보면 됨!
        }
    }
    // 상품 조회
    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }
    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
