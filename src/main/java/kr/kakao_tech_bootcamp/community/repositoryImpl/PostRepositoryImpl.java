package kr.kakao_tech_bootcamp.community.repositoryImpl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.kakao_tech_bootcamp.community.dto.response.post.AllPostResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.Author;
import kr.kakao_tech_bootcamp.community.dto.response.post.GetPostDetailResponseDto;
import kr.kakao_tech_bootcamp.community.dto.response.post.ImageResponseDto;
import kr.kakao_tech_bootcamp.community.entity.PostImage;
import kr.kakao_tech_bootcamp.community.repository.post.PostImageRepository;
import kr.kakao_tech_bootcamp.community.repository.post.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.dsl.Expressions.constant;
import static kr.kakao_tech_bootcamp.community.entity.QPost.post;
import static kr.kakao_tech_bootcamp.community.entity.QPostLike.postLike;
import static kr.kakao_tech_bootcamp.community.entity.QUser.user;


@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final PostImageRepository postImageRepository;

    @Override
    public Slice<AllPostResponseDto> getAllPosts(int userId, Pageable pageable) {
        List<OrderSpecifier> orders = new ArrayList<>();
        for(Sort.Order order : pageable.getSort()) {
            Order sortOrder= order.isAscending()? Order.ASC:Order.DESC;

            switch(order.getProperty()) {
                case "createdAt" -> orders.add(new OrderSpecifier<>(sortOrder, post.createdAt));
                case "likesCount" -> orders.add(new OrderSpecifier<>(sortOrder, post.likesCount));
                default -> orders.add(new OrderSpecifier<>(Order.DESC, post.createdAt));
            }
        }

        List<AllPostResponseDto> allPostResponseList = jpaQueryFactory
                .select(Projections.constructor(AllPostResponseDto.class,
                        post.id,
                        post.title,
                        post.createdAt,
                        post.updatedAt,
                        post.likesCount,
                        post.commentsCount,
                        post.viewsCount,
                        new CaseBuilder()
                                .when(postLike.user.id.eq(userId)).then(true)
                                .otherwise(false).as("isLike"),
                        Projections.constructor(Author.class,
                                new CaseBuilder()
                                        .when(user.imageUUID.isNotNull())
                                        .then(user.imageUUID.prepend(constant("http://localhost:8080/uploads/")))
                                        .otherwise((String) null)
                                        .as("profileImageUrl"),
                                user.nickname,
                                new CaseBuilder()
                                        .when(post.user.id.eq(userId)).then(true)
                                        .otherwise(false).as("mine"))
                ))
                .from(post)
                .where(post.user.deletedAt.isNull())        // 삭제되지 않은 사용자의 게시글만 불러오기
                .join(post.user, user)
                .leftJoin(postLike)     // 좋아요 안누른 게시글도 조회해야함
                .on(postLike.post.eq(post)                  // 좋아요 누른 사람이 현재 로그인한 사람인지 확인
                        .and(postLike.user.id.eq(userId)))
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        // 다음 페이지 유무 판별 위해 pageSize + 1개 가져옴.
        boolean hasNextPage = allPostResponseList.size() > pageable.getPageSize();
        if (hasNextPage) allPostResponseList.removeLast();      // 1개 많게 가져왔으니까 마지막 삭제

        return new SliceImpl<>(allPostResponseList, pageable, hasNextPage);
    }

    @Override
    public Optional<GetPostDetailResponseDto> getPostByPostId(int userId, int postId) {
        GetPostDetailResponseDto getPostDetailResponseDto = jpaQueryFactory
                .select(Projections.constructor(GetPostDetailResponseDto.class,
                        post.id,
                        post.title,
                        post.content,
                        post.createdAt,
                        post.updatedAt,
                        post.likesCount,
                        post.commentsCount,
                        post.viewsCount,
                        new CaseBuilder()
                                .when(postLike.user.id.eq(userId)).then(true)
                                .otherwise(false).as("isLike"),
                        Projections.constructor(Author.class,
                                new CaseBuilder()
                                        .when(user.imageUUID.isNotNull())
                                        .then(user.imageUUID.prepend(constant("http://localhost:8080/uploads/")))
                                        .otherwise((String) null)
                                        .as("profileImageUrl"),
                                user.nickname,
                                new CaseBuilder()
                                        .when(post.user.id.eq(userId)).then(true)
                                        .otherwise(false).as("mine")
                        )
                ))
                .from(post)
                .join(post.user, user)
                .leftJoin(postLike)
                .on(postLike.post.eq(post)
                        .and(postLike.user.id.eq(userId)))
                .where(post.id.eq(postId))
                .fetchOne();

        if (getPostDetailResponseDto != null) {
            List<ImageResponseDto> imageList = new ArrayList<>();
            List<PostImage> postImageList = postImageRepository.findByPost_Id(postId);

            System.out.println("imageListSize = " + postImageList.size());
            for (PostImage postImage : postImageList) {
                ImageResponseDto imageResponseDto = ImageResponseDto.from(postImage);
                imageList.add(imageResponseDto);
            }

            getPostDetailResponseDto = getPostDetailResponseDto.withImages(imageList);
        }

        return Optional.ofNullable(getPostDetailResponseDto);   // null일 때 빈 optional 반환
    }
}
