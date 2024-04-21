package spring.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import spring.datajpa.entity.Member;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void MemberTest() {

        Member member = new Member("memberA");

        Member findedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(findedMember.getId());

        Assertions.assertThat(member.getId()).isEqualTo(findMember.getId());
        Assertions.assertThat(member.getUsername()).isEqualTo(findMember.getUsername());
        Assertions.assertThat(member).isEqualTo(findMember);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);
        List<Member> result =
                memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }


    @Test
    void testFindByUsernameAndAgeGreaterThan() {

        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberA",20);

        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        List<Member> findMember = memberJpaRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        Assertions.assertThat(findMember.get(0).getAge()).isEqualTo(memberB.getAge());

    }

    @Test
    void findByUsername() {
        Member memberA = new Member("memberA",10);
        Member memberB = new Member("memberB",20);

        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        List<Member> findMember = memberJpaRepository.findByUsername("memberA");

        Assertions.assertThat(findMember.get(0).getUsername()).isEqualTo(memberA.getUsername());

    }

    @Test
    void findByPage() {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));
        int age = 10;
        int offset = 0;
        int limit = 3;
        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);
        //페이지 계산 공식 적용...
        // totalPage = totalCount / size ...
        // 마지막 페이지 ...
        // 최초 페이지 ..
        //then
        Assertions.assertThat(members.size()).isEqualTo(3);
        Assertions.assertThat(totalCount).isEqualTo(5);

    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));
        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);
        //then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void jpaEventBaseEntity() throws Exception {
        //given
        Member member = new Member("member1");
        memberJpaRepository.save(member); //@PrePersist
        Thread.sleep(100);
        member.setUsername("member2");
        em.flush(); //@PreUpdate
        em.clear();
        //when
        Member findMember = memberJpaRepository.findById(member.getId()).get();
        //then
        System.out.println("findMember.createdDate = " +
                findMember.getCreatedDate());
//        System.out.println("findMember.updatedDate = " +
//                findMember.getUpdatedDate());
    }

}