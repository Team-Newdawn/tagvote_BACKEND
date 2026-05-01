package com.newdawn.tagvote.vote.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findAllByCreatedByIdOrderByIdAsc(Long createdById);

    Optional<Vote> findByIdAndCreatedById(Long id, Long createdById);
}
