package com.newdawn.tagvote.question.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findAllByVoteIdOrderByIdAsc(Long voteId);

    List<Question> findAllByVoteCreatedByIdOrderByIdAsc(Long createdById);

    Optional<Question> findByIdAndVoteCreatedById(Long id, Long createdById);

    @EntityGraph(attributePaths = {"vote", "tags"})
    Optional<Question> findWithVoteAndTagsById(Long id);

    @Query("""
            select distinct q
            from Question q
            left join fetch q.tags
            where q.vote.id = :voteId
            order by q.id asc
            """)
    List<Question> findAllWithTagsByVoteId(Long voteId);
}
