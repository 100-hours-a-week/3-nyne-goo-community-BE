package kr.kakao_tech_bootcamp.community.repositoryImpl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.kakao_tech_bootcamp.community.dto.response.comment.AllCommentResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.Author;
import kr.kakao_tech_bootcamp.community.repository.comment.CommentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;


import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.constant;
import static kr.kakao_tech_bootcamp.community.entity.QComment.comment;
import static kr.kakao_tech_bootcamp.community.entity.QPost.post;
import static kr.kakao_tech_bootcamp.community.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<AllCommentResponseDto> getAllComments(int userId, int postId, Pageable pageable) {
        List<AllCommentResponseDto> allCommentResponseDtoList =  jpaQueryFactory
                .select(Projections.constructor(AllCommentResponseDto.class,
                        comment.id.as("commentId"),
                        comment.content,
                        comment.createdAt,
                        comment.updatedAt,
                        Projections.constructor(Author.class,
                                new CaseBuilder()
                                        .when(user.imageUUID.isNotNull())
                                        .then(true)
                                        .otherwise(false)
                                        .as("profileImageUrl"),
                                user.nickname,
                                new CaseBuilder()
                                        .when(comment.user.id.eq(userId))
                                        .then(true)
                                        .otherwise(false)
                                        .as("mine"))
                ))
                .from(comment)
                .join(comment.post, post)
                .join(comment.user, user)
                .where(comment.post.id.eq(postId))
                .orderBy(comment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        boolean hasNextPage = allCommentResponseDtoList.size() > pageable.getPageSize();
        if (hasNextPage) allCommentResponseDtoList.removeLast();

        return new SliceImpl<>(allCommentResponseDtoList, pageable, hasNextPage);
    }
}
