package com.newdawn.tagvote.tag.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByQuestionIdOrderByIdAsc(Long questionId);

    @EntityGraph(attributePaths = {"question", "question.vote"})
    Optional<Tag> findWithQuestionAndVoteById(Long id);
}
