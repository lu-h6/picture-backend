package com.lgh.picturebackend.manager;

import cn.hutool.core.io.FileUtil;
import com.lgh.picturebackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;

@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(cosClientConfig.getBucket(), key, file);


        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象（附带图片信息）git config user.email
     * git config user.name
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种图片的处理）
        PicOperations picOperations = new PicOperations();
        // 0 不返回原图信息，1返回原图信息，默认为0
        picOperations.setIsPicInfo(1);

        ArrayList<PicOperations.Rule> rules = new ArrayList<>();
        //图片压缩（转换成webp格式）
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setFileId(webpKey);
        // 对哪个桶里的文件进行处理
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setRule("imageMogr2/format/webp");
        rules.add(compressRule);

        //缩略图处理 仅对 》 20kb 的图片 生成
        if (FileUtil.size(file) > 2 * 1024) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            //缩放规则 /thumbnail/<width>x<height> (如果大于原图规则，则不处理）
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 256, 256));
            rules.add(thumbnailRule);
        }

        // 构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }


    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    public void deleteObject(String key) {
        cosClient.deleteObject(cosClientConfig.getBucket(),key);
    }

}


