package spring.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spring.datajpa.entity.Member;
import spring.datajpa.repository.MemberRepository;
import spring.datajpa.repository.dto.MemberDto;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5, sort = "username",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    @GetMapping("/members2")
    public Page<MemberDto> list2(Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 50; i++) {
            Member member = new Member("member"+i+1, i+1);
            memberRepository.save(member);
        }
    }

}
