package com.codeit.team2.monew.module.domain.useractivity.repository;

import com.codeit.team2.monew.module.domain.useractivity.document.CommentItem;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoCommentItemRepository extends MongoRepository<CommentItem, UUID> {

}
