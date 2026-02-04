package com.lgh.picturebackend.model.dto.picture;

import com.lgh.picturebackend.api.aliyunai.modal.CreateOutPaintingTaskRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建图片扩图任务请求参数
 */
@Data
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

    private static final long serialVersionUID = 1L;
}
