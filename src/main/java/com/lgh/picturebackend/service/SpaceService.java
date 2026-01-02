package com.lgh.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lgh.picturebackend.model.dto.space.SpaceAddRequest;
import com.lgh.picturebackend.model.dto.space.SpaceQueryRequest;
import com.lgh.picturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lgh.picturebackend.model.entity.User;
import com.lgh.picturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author luguihong
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-12-07 15:31:27
 */
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
    /**
     * 校验空间
     *
     * @param space
     * @param add   是否为创建时校验
     */
    void validSpace(Space space, boolean add);

    /**
     * 获取空间包装类（单条）
     **/
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间包装类（分页）
     **/
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取查询的 queryWrapper
     *
     * @param spaceQueryRequest 空间查询请求类
     * @return 可用来查询的 queryWrapper
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    void fillSpaceBySpaceLevel(Space space);
}
