package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "category_item", // category_item이라는 테이블(중간 엔티티) 생성되어 있는 것을 확인할 수 있어!
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();
    // 물론 @ManyToMany는 쓰지 않는 것을 강력하게 권장!!
    // → 일대다, 다대일 구조로 치환(?)하여 중간 테이블을 직접 만들자!(?)


    // 같은 Entity 내에서 연관관계를 건 것!!
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
}
