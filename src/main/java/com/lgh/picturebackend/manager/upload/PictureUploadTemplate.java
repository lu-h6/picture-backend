package com.lgh.picturebackend.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.lgh.picturebackend.config.CosClientConfig;
import com.lgh.picturebackend.exception.BusinessException;
import com.lgh.picturebackend.exception.ErrorCode;
import com.lgh.picturebackend.exception.ThrowUtils;
import com.lgh.picturebackend.manager.CosManager;
import com.lgh.picturebackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param inputSource      文件
     * @param uploadPathPrefix 上传路径前缀
     * @return
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {

        //校验图片
        validPicture(inputSource);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginFilename(inputSource);
        //自己拼接上传文件名称，而不用原始文件名称，防止文件名重复
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFileName);
        //判断结果并返回
        File file = null;
        try {
            //创建临时文件
            file = File.createTempFile(uploadPath, null);
            processFile(inputSource, file);  //将上传的文件数据保存到临时文件上
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            //获取图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            //获取到图片获取结果
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                //获取压缩之后得到的图片信息
                CIObject ciObject = objectList.get(0);
                //缩略图默认就等于压缩图
                CIObject thumbnailCiObject = ciObject;
                if (objectList.size() > 1) {
                    thumbnailCiObject = objectList.get(1);
                }

                //封装压缩图的返回结果
                return buildResult(originalFilename, ciObject, thumbnailCiObject,imageInfo);
            }
            return buildResult(imageInfo, uploadPath, originalFilename, file);
        } catch (IOException e) {
            log.error("file upload error, filepath: " + uploadPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //临时文件处理
            extracted(file);
        }

    }

    /**
     * 封装返回结果（压缩后的）
     *
     * @param originalFilename
     * @param ciObject
     * @param thumbnailCiObject
     * @return
     */
    private UploadPictureResult buildResult(String originalFilename, CIObject ciObject, CIObject thumbnailCiObject,ImageInfo imageInfo) {
        //封装返回结果
        int width = ciObject.getWidth();
        int height = ciObject.getHeight();
        double picScale = NumberUtil.round((double) width / height, 2).doubleValue();


        //封装返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        //设置压缩后的原图地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + ciObject.getKey());
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(ciObject.getSize().longValue());
        uploadPictureResult.setPicWidth(width);
        uploadPictureResult.setPicHeight(height);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(ciObject.getFormat());
        uploadPictureResult.setPicColor(imageInfo.getAve());


        //设置缩略图地址
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());


        return uploadPictureResult;
    }


    /**
     * 处理文件
     *
     * @param inputSource
     * @param file
     */
    protected abstract void processFile(Object inputSource, File file) throws IOException;

    /**
     * 获取原始文件名称
     *
     * @return
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 校验图片
     *
     * @param inputSource
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 构建返回结果
     *
     * @param imageInfo        对象存储返回的对象信息
     * @param uploadPath
     * @param originalFilename
     * @param file
     * @return
     */
    private UploadPictureResult buildResult(ImageInfo imageInfo, String uploadPath, String originalFilename, File file) {
        //封装返回结果
        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();
        double picScale = NumberUtil.round((double) width / height, 2).doubleValue();


        //封装返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setPicWidth(width);
        uploadPictureResult.setPicHeight(height);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicColor(imageInfo.getAve());


        return uploadPictureResult;
    }

    /**
     * 临时文件处理
     *
     * @param file
     */
    private static void extracted(File file) {
        if (file != null) {
            boolean delete = file.delete();
            if (!delete) {
                log.error("file delete error, filepath: " + file.getAbsoluteFile());
            }
        }
    }

}
