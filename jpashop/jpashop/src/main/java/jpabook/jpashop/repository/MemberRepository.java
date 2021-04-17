package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository // @Component Annotation 붙어 있음 : @ComponentScan 대상 
            // → @SpringBootApplication 붙어있는 클래스 밑에 있는 경로의 모든 @Component를 스캔함! (SpringBean 자동 등록됨)
            //   ( jpabook.jpashop 패키지에 있는 JpashopApplication 클래스 )
@RequiredArgsConstructor// lombok
public class MemberRepository {

    //  @PersistenceContext // JPA Entity Manager를 em에 주입받을 수 있음!
    // Spring Data JPA 사용 시, final 붙이고 + @RequiredArgsConstructor로 Entity Manager 생성자 주입 가능!! → 앞으로 이렇게 쓸 거임!
    private final EntityManager em;

//    @PersistenceUnit  // Entity Manager Factory 직접 주입받고 싶다면? 그런데 쓸일은 없다고 보면 됨!
//    private EntityManagerFactory emf;

    // 영속성 컨텍스트에 member 저장 → Transaction commit 시점에 DB에 반영됨
    public void save(Member member) {
        em.persist(member);
    }

    // Primary Key인 id로 member 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // 모든 member를 리스트로 반환
    public List<Member> findAll() {
                                   // JPQL : SQL이랑 조금 다름
        return em.createQuery("select m from Member m",
                               Member.class) // Member 클래스 타입
                               .getResultList();
    }/* SQL : Table을 대상으로 Query를 날림
        JPQL : Entity 객체를 대상으로 Query를 날림
            → Entity 객체인 Member를 alias로 m을 주고, 그 m을 select!
    */

    // 특정 name 갖는 member를 리스트로 반환
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name",
                              Member.class)
                              .setParameter("name", name)   // :name 파라미터 바인딩
                              .getResultList();
    }
}
