package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// Test를 위해서 @Component 주석처리;;
//@Repository // ComponentScan의 대상이 되는 Annotation 중 하나
public class MemberRepository {

    // [java 표준] 모든 것이 Spring Container 위에서 동작할텐데
    //            EntityManager를 주입해주는 Annotation. 걍써 ㅋㅋ
    @PersistenceContext
    private EntityManager em;

    // 저장
    public Long save(Member member) {
        em.persist(member);
        return member.getId();  // Member 클래스 - lombok @Getter에 의해서
    }

    // 조회
    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
