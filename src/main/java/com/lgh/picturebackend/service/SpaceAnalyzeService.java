package com.lgh.picturebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lgh.picturebackend.model.dto.space.analyze.*;
import com.lgh.picturebackend.model.entity.Space;
import com.lgh.picturebackend.model.entity.User;
import com.lgh.picturebackend.model.vo.space.analyze.*;

import java.util.List;

public interface SpaceAnalyzeService extends IService<Space> {

    /**
     * 获取空间使用情况分析
     *
     * @param spaceUsageAnalyzeRequest 空间使用情况分析请求类
     * @param loginUser                登录用户
     * @return 空间使用情况分析响应类
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);
}
