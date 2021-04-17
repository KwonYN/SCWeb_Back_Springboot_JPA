package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service        // "@Repository" 처럼 역시 @Component 붙어 있음
@Transactional(readOnly = true) // readOnly=true : 이 클래스 하위 메서드에 다 readOnly가 먹힘 (읽기용 메서드)
// JPA에서의 모든 data 변경이나 logic들은 가급적이면 한 transaction 안에서 실행이 되어야 함!! → 그래야 연관관계 Lazy(지연로딩)으로 설계 가능!!
@RequiredArgsConstructor// lombok 라이브러리 (Ex. @AllArgsConstructor, @RequiredArgsConstructor, ..)
public class MemberService {

    //  @Autowired  // 1. 필드 주입. 생성자도 자동으로 만들어줌. 실행될 때 딱 셋팅되고, 안 바뀜! (반대로 못바꿈!)
    private final MemberRepository memberRepository;

//    @Autowired  // 2. Setter 주입 : 테스트에 편리. Mock을 직접 주입해줄 수 있잖아?
//                // 하지만 실제로 사용할 일 별로 없고, 어쨌든 변경이 가능하다는 위험요소!;;
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

//    @Autowired // ★ 3. 생성자 주입 : 가장 권장되고 많이 사용됨. 안전함. 테스트 상황에서 생성자 함수 호출 시, 파라미터 안 줘서 호출하면 (가장 좋은) 컴파일에러를 내줌 ㅋㅋ
                 // 생성자 하나일 때 @Autowired 생략 가능
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    } // ▶ 이 생성자마저도 @RequiredArgsConstructor을 써줌으로써 자동 생성 가능!

    /**
     * 회원 가입
     */
    @Transactional // readOnly가 아님. *변경할 수 있어야 하는* 메서드에는 readOnly 안됨!
    public Long join(Member member) {
        validateDuplicateMember(member); // 검증 로직 : 같은 이름은 안된다고 정함!
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // Exception!!!
        List<Member> findMembers
                = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다!");
        }
    }

    /**
     * 회원 조회
     */
    // 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 한 건만 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
