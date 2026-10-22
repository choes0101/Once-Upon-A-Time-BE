package pproject.once_upon_a_time.domain.story.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pproject.once_upon_a_time.domain.story.domain.Story;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByTitleContainingIgnoreCase(String title);
    List<Story> findByMemberId(Long memberId);
    List<Story> findByMemberIdAndTitleContainingIgnoreCase(Long memberId, String title);

    @Query("select s from Story s join fetch s.member")
    List<Story> findAllWithMember();
}
