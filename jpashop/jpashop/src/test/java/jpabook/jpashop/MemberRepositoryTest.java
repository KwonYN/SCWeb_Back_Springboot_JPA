package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;   // 필드 주입

    @Test
    @Transactional  // @Transactional이 Test 쪽에 있으면, Test 끝날 때 DB를 Rollback 해버림 - 계속 있으면 반복적으로 테스트를 못하니깐!!
    @Rollback(false)    // 그래서 rollback 안되도록 @Rollback(false)도 써주는 것!
    public void testMember() {
        // given : 멤버 생성 후 이름 set 해놓은 상황
        Member member = new Member();
        member.setName("memberA");

        // when : 멤버를 저장했을 때
        Long saveId = memberRepository.save(member);

        // then : 잘 저장된 것일까?
        Member findMember = memberRepository.find(saveId);
        System.out.println("findMember = " + findMember);
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());

        Assertions.assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보장
        System.out.println("findMember = " + findMember);           // Ex. jpabook.jpashop.Member@61d8a491
        System.out.println("member = " + member);                   // Ex. jpabook.jpashop.Member@61d8a491
        System.out.println("findMember == member ? : " + (findMember==member)); // true
        // ∴ 같은 Transaction 안에서 저장한 것을 조회했다면, 영속성 컨텍스트(Persistence Context)가 같음.
        //   그리고 같은 영속성 컨텍스트에서는 id 값(식별자)이 같으면, 같은 Entity로 식별한다고 함.
    }
}