package spring.datajpa.repository.dto;

public class MemberProjection {

    private Long id;
    private String username;
    private String teamName;

    public MemberProjection(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
