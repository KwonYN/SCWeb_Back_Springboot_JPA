package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
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

    /**
     * 비즈니스 로직 추가
     */
    /*
        재고 수량 변경
        보통 Service 클래스에서 Item.stockQuantity를 가져와서 거기에서 더해준 다음에 setStockQuantity 메서드를 호출해서 변경을 해주었을 것임.
        하지만 객체지향적으로 생각을 해보았을 때, 데이터를 가지고 있는 쪽(Item 클래스)에 비즈니스 메서드가 있는 것이 가장 좋음!!
        그래야 응집력이 더 높아짐. 관리하기도 편하다고 함.
        → 데이터를 변경해야 할 때는 Setter를 사용하는 것이 아니라,
          핵심 비즈니스 메서드(아래의 메서드들)를 사용해서 변경해야 함!!!
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }
    public void removeStock(int quantity) {
        int resStock = this.stockQuantity - quantity;
        if(resStock < 0) {
            throw new NotEnoughStockException("Need more stock!");
        }
        this.stockQuantity = resStock;
    }
}
