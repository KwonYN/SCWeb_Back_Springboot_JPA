package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    private Long id;    // 상품을 수정하기 위한 id값

    private String name;
    private int price;
    private int stockQuantity;
    // 여기까지 상품(Item)의 공통 속송

    private String author;
    private String isbn;
    // 이 부분은 Book만의 속성
}
