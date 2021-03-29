package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 한 테이블에 다 때려박는 전략
@DiscriminatorColumn(name = "dtype")
public abstract class Item { // for 상속관계 → abstract(추상 클래스)

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    // 공동 속성
    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}
