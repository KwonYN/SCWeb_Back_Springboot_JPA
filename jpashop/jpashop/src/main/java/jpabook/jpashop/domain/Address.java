package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 내장 타입 : 원래 @Embeddable나 Member 클래스의 @Embedded 둘 중 하나만 있어도 된다고는 함!
@Getter // 생성할 때만 값이 딱 세팅이 되고, 변경이 되지 않도록(Immutable) 설계!
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
