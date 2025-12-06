package com.lgh.picturebackend.controller;

import cn.hutool.http.server.HttpServerResponse;
import com.lgh.picturebackend.annotation.AuthCheck;
import com.lgh.picturebackend.common.BaseResponse;
import com.lgh.picturebackend.common.ResultUtils;
import com.lgh.picturebackend.constant.UserConstant;
import com.lgh.picturebackend.exception.BusinessException;
import com.lgh.picturebackend.exception.ErrorCode;
import com.lgh.picturebackend.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;


@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private CosManager cosManager;

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", fileName);
        File file = null;
        try {
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            return ResultUtils.success(filepath);
        } catch (IOException e) {
            log.error("file upload error, filepath: " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                file.delete();
            }
        }


    }

//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    @PostMapping("/test/download")
//    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
//        COSObjectInputStream cosObjectInput = null;
//        try {
//            COSObject cosObject = cosManager.getObject(filepath);
//             cosObjectInput = cosObject.getObjectContent();
//            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
//            response.setContentType("application/octet-stream;charset=UTF-8");
//            response.setHeader("Content-Disposition", "attachment;filename=" + filepath);
//            response.getOutputStream().write(bytes);
//            response.getOutputStream().flush();
//        } catch (IOException e) {
//            log.info("file download error, filepath: " + filepath, e);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
//        }finally {
//            if (cosObjectInput != null){
//                cosObjectInput.close();
//            }
//        }
//    }


}
