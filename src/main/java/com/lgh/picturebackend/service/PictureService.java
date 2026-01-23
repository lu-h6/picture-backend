package com.lgh.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lgh.picturebackend.model.dto.picture.*;
import com.lgh.picturebackend.model.entity.Picture;
import com.lgh.picturebackend.model.entity.User;
import com.lgh.picturebackend.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author luguihong
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-11-23 21:43:41
*/
public interface PictureService extends IService<Picture> {


    /**
     * 上传图片
     *
     * @param inputSource 文件
     * @param pictureUploadRequest
     * @return
     */
    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 获取查询的 queryWrapper
     * @param pictureQueryRequest 图片请求类
     * @return 可用来查询的 queryWrapper
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取单个图片的 VO 对象
     * @param picture picture 对象
     * @param request request 请求
     * @return 对应图片的 VO
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);
    /**
     * 分页获取图片 VO 对象
     * @param picturePage  page 对象
     * @param request request 请求
     * @return 分页的 VO
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);
    /**
     * 校验参数
     * @param picture 需要校验的 picture 对象
     */
    void validPicture(Picture picture);


    /**
     * 图片审核
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest,User loginUser);

    /**
     * 填充审核参数
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture, User loginUser);


    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest,User userLogin);


    /**
     * 清理图片文件
     * @param oldPicture
     */
    void clearPictureFile(Picture oldPicture);

    void deletePicture(long pictureId, User loginUser);

    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);


    /**
     * 校验图片
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser,Picture picture);

    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);
}
