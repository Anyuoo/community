package com.anyu.community.mapper;

import com.anyu.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    /**
     * 查询帖子
     *
     * @param userId
     * @param offset 当前页
     * @param limit  一页帖子数
     * @return
     */
    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit);

    /**
     * 查询帖子总数
     *
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 查询帖子
     *
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);


    /**
     * 查询帖子详情
     *
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);
}
