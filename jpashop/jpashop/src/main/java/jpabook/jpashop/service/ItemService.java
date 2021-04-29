package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import jpabook.jpashop.domain.item.Book;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {  // 상품 서비스 클래스는 상품 리포지토리 클래스에 단순한 위임만 하는 클래스!!

    private final ItemRepository itemRepository;

    /**
     * item 저장
     */
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * item 조회
     */
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    /**
     * 변경 감지 (Dirty Checking)
     */
    @Transactional
    public void updateItem(Long itemId,
                           String name, int price, int stockQuantity) {
        // id를 기반로 DB에서 영속성 상태의 엔티티를 찾아옴. 얘는 "영속성" 상태인 것!!
        Book findItem = (Book) itemRepository.findOne(itemId);

        /*
        findItem.setName(bookParam.getName());
        findItem.setPrice(bookParam.getPrice());
        findItem.setStockQuantity(bookParam.getStockQuantity());
        findItem.setAuthor(bookParam.getAuthor());
        findItem.setIsbn(bookParam.getIsbn());
        */
        /*
            영속 상태의 컨텍스트를 변경하는 경우,
            JPA에서 변경을 감지(Dirty Checking)하기 때문에
            itemRepository.save로 저장할 필요가 없음!!
            → @Transactional에 의해서 transaction commit이 실행됨
            → JPA에서는 flush라는 것을 날림
            (영속성 컨텍스트에 의해 관리되는 엔티티의 변경을 찾아서 변경이 있으면 update query를 날림)
        */

        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }
}
