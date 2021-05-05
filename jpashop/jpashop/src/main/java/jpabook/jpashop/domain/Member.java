package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity // JPA에서는 엔티티는 테이블에 대응하는 하나의 클래스라고 생각
@Getter @Setter
public class Member {

    @Id @GeneratedValue // Primary Key로 등록
    @Column(name = "member_id")
    private Long id;
    // + 코드에서는 그냥 id라고 해도 클래스가 있고, 객체.id 이런 식으로 접근하기 때문에 안 헷갈림
    // 하지만 Table은 달라... (실무 관점) 찾기 쉽지 않고, Join하기에도 어려워.. → 그래서 member_id와 같은 식으로 지음

    // @NotEmpty : Entity를 노출시키지 않고, DTO에서 Validation을 걸어주면 됨!
    private String name;

    @Embedded   // 원래 @Embedded나 Address 클래스의 @Embeddable 둘 중 하나만 있어도 된다고는 함!
    private Address address;

    @OneToMany(mappedBy = "member") // 연관관계의 주인 아님. Order table에 있는 "member"에 의해 맵핑됨..;
    private List<Order> orders = new ArrayList<>();
    // + @JoinColumn(~~)와 @xTox(mappedBy=~~)는 서로 한 쌍!!
    //   이 부분이 FK       이 부분이 맵핑되는 것
}
