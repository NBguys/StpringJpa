package spring.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import spring.datajpa.entity.Member;
import spring.datajpa.entity.Team;
import spring.datajpa.repository.dto.MemberDto;
import spring.datajpa.repository.dto.UsernameOnly;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void memberTest() {

        Member member = new Member("memberA");

        Member save = memberRepository.save(member);

        Member findMember = memberRepository.findById(save.getId()).get();

        Assertions.assertThat(member.getId()).isEqualTo(findMember.getId());
        Assertions.assertThat(member.getUsername()).isEqualTo(findMember.getUsername());
        Assertions.assertThat(member).isEqualTo(findMember);

    }


    @Test
    void testFindByUsernameAndAgeGreaterThan() {

        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberA",20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> findMember = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        Assertions.assertThat(findMember.get(0).getAge()).isEqualTo(memberB.getAge());

    }

    @Test
    void findByUsername() {
        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberB",20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> findMember = memberRepository.findByUsername("memberA");

        Assertions.assertThat(findMember.get(0).getUsername()).isEqualTo(memberA.getUsername());

    }

    @Test
    @DisplayName("findUser")
    void findUser() {

        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberB",20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        List<Member> members = memberRepository.findUser("memberA", 10);

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
        }

    }


    @Test
    @DisplayName("findUsernameList")
    void findUsernameList() {

        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberB",20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<String> usernameList = memberRepository.findUsernameList();

        for (String s : usernameList) {
            System.out.println("s = " + s);
        }

    }

    @Test
    @DisplayName("findMemberDto")
    void findMemberDto() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberB",20);
        Member memberC = new Member("memberC",30);
        Member memberD = new Member("memberD",40);

        memberA.changeTeam(teamA);
        memberB.changeTeam(teamA);
        memberC.changeTeam(teamB);
        memberD.changeTeam(teamB);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }

    @Test
    @DisplayName("findMembers")
    void findMembers() {

        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberB",20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> members = memberRepository.findMembers(memberA.getUsername());

        for (Member member : members) {
            System.out.println("member = " + member.getUsername()+ " ," + member.getId());
        }

    }

    @Test
    @DisplayName("findByNames")
    void findByNames() {
        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberB",20);

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        List<Member> members = memberRepository.findByNames(Arrays.asList("memberA", "memberB"));

        for (Member member : members) {
            System.out.println("username = " + member.getUsername());
        }

    }@Test
    public void page() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC,
                "username"));
        Page<Member> page = memberRepository.findPageByAge(10, pageRequest);

        Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        Slice<Member> slicePage = memberRepository.findSliceByAge(10, pageRequest);

        Page<Member> memberAllCountBy = memberRepository.findMemberAllCountBy(pageRequest);

        //then
        List<Member> content = page.getContent(); //조회된 데이터
        Assertions.assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        Assertions.assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        Assertions.assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        Assertions.assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?

        Assertions.assertThat(slicePage.getNumber()).isEqualTo(0);
        Assertions.assertThat(slicePage.isFirst()).isTrue(); //첫번째 항목인가?
        Assertions.assertThat(slicePage.hasNext()).isTrue(); //다음 페이지가 있는가?

    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 19);
        Member member3 = new Member("member3", 20);
        Member member4 = new Member("member4", 21);
        Member member5 = new Member("member5", 40);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        //when


        List<Member> members = memberRepository.findByUsername("member3");

        Assertions.assertThat(member3).isEqualTo(members.get(0));

        int resultCount = memberRepository.bulkAgePlus(20); 
        //then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void queryHint() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();
        //when
        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");
        em.flush(); //Update Query 실행X
    }

    @Test
    public void queryCustom() throws Exception {

        List<Member> result = memberRepository.findMemberCustom();

        for (Member member : result) {
            System.out.println("member = " + member.getUsername());
        }
    }

    @Test
    public void jpaEventBaseEntity() throws Exception {
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist
        Thread.sleep(100);
        member.setUsername("member2");
        em.flush(); //@PreUpdate
        em.clear();
        //when
        Member findMember = memberRepository.findById(member.getId()).get();
        //then
        System.out.println("findMember.createdDate = " +
                findMember.getCreatedDate());
        System.out.println("findMember.lastModifiedByDate = " +
                findMember.getLastModifiedDate());
        System.out.println("findMember.createBy = " +
                findMember.getCreatedBy());
        System.out.println("findMember.astModifiedBy = " +
                findMember.getLastModifiedBy());
    }

    @Test
    public void projections() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();
        //when
        List<UsernameOnly> result =
                memberRepository.findProjectionsByUsername("m1");
        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }


}