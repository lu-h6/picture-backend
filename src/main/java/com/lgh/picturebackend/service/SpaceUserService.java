package com.lgh.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lgh.picturebackend.model.dto.space.SpaceAddRequest;
import com.lgh.picturebackend.model.dto.space.SpaceQueryRequest;
import com.lgh.picturebackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.lgh.picturebackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.lgh.picturebackend.model.entity.Space;
import com.lgh.picturebackend.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lgh.picturebackend.model.entity.User;
import com.lgh.picturebackend.model.vo.SpaceUserVO;
import com.lgh.picturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author luguihong
 * @description 针对表【space_user(空间用户关联)】的数据库操作Service
 * @createDate 2026-03-14 15:28:34
 */
public interface SpaceUserService extends IService<SpaceUser> {
    /**
     * 创建空间成员
     *
     * @param spaceUserAddRequest
     * @param loginUser
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验空间成员
     *
     * @param spaceUser
     * @param add       是否为创建时校验
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取空间成员包装类（单条）
     **/
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间成员包装类（分页）
     *
     * @param spaceUserList
     **/
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

    /**
     * 获取查询的 queryWrapper
     *
     * @param spaceUserQueryRequest 空间查询请求类
     * @return 可用来查询的 queryWrapper
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);


}
