package pproject.once_upon_a_time.domain.story.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pproject.once_upon_a_time.domain.member.domain.Member;
import pproject.once_upon_a_time.domain.member.repository.MemberRepository;
import pproject.once_upon_a_time.domain.story.domain.Story;
import pproject.once_upon_a_time.global.common.MemberRole;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class StoryRepositoryTest {

    @Autowired StoryRepository storyRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    Statistics stats;

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 100; i++) {
            Member m = memberRepository.save(Member.builder()
                    .kakaoUserId("kakao" + i).name("작성자" + i)
                    .nickname("nick" + i).role(MemberRole.USER).build());
            storyRepository.save(Story.builder()
                    .member(m).title("동화" + i).keywords(new ArrayList<>()).build());
        }

        em.flush(); // 영속성 컨텍스트의 내용을 DB에 반영
        em.clear(); // 영속성 컨텍스트 초기화 (실제 요청처럼 빈 상태에서 조회하도록)

        stats = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        stats.clear(); // 실행된 쿼리 통계 초기화 (여기서부터 쿼리 수를 센다)
    }

    @Test
    void findAll_causesNPlusOne() {
        List<Story> stories = storyRepository.findAll();
        stories.forEach(s -> s.getMember().getName());
        assertThat(stats.getPrepareStatementCount()).isEqualTo(101);
    }

    @Test
    void findAllWithMember_singleQuery() {
        List<Story> stories = storyRepository.findAllWithMember();
        stories.forEach(s -> s.getMember().getName());
        assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
    }
}
