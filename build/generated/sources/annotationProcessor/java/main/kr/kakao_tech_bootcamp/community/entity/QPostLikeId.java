package kr.kakao_tech_bootcamp.community.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostLikeId is a Querydsl query type for PostLikeId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPostLikeId extends BeanPath<PostLikeId> {

    private static final long serialVersionUID = -102532573L;

    public static final QPostLikeId postLikeId = new QPostLikeId("postLikeId");

    public final NumberPath<Integer> postId = createNumber("postId", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QPostLikeId(String variable) {
        super(PostLikeId.class, forVariable(variable));
    }

    public QPostLikeId(Path<? extends PostLikeId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostLikeId(PathMetadata metadata) {
        super(PostLikeId.class, metadata);
    }

}

