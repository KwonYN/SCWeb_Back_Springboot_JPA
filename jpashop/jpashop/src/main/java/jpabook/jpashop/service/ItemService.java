package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
