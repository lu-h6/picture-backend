package com.lgh.picturebackend.api.aliyunai;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.lgh.picturebackend.api.aliyunai.modal.CreateOutPaintingTaskRequest;
import com.lgh.picturebackend.api.aliyunai.modal.CreateOutPaintingTaskResponse;
import com.lgh.picturebackend.api.aliyunai.modal.GetOutPaintingTaskResponse;
import com.lgh.picturebackend.exception.BusinessException;
import com.lgh.picturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AliYunAiApi {
    @Value("${aliYunAi.apiKey}")
    private String apiKey;

    //创建任务地址
    public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    //查询任务状态 地址
    public static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    /**
     * 创建任务
     *
     * @param createOutPaintingTaskRequest
     * @return
     */

    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        if (createOutPaintingTaskRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        HttpRequest httpRequest = HttpRequest.post(CREATE_OUT_PAINTING_TASK_URL)
                .header("Authorization", "Bearer " + apiKey)
                //添加异步请求头
                .header("X-DashScope-Async", "enable")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(createOutPaintingTaskRequest));
        //处理响应

        try (HttpResponse httpResponse = httpRequest.execute()) {
            if (!httpResponse.isOk()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI扩图失败");
            }
            CreateOutPaintingTaskResponse response = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);
            String errorcode = response.getCode();
            if (StrUtil.isNotBlank(errorcode)) {
                String message = response.getMessage();
                log.error("请求异常：{}", message);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI扩图失败: " + message);
            }
            return response;
        }
    }


    /**
     * 获取任务结果
     *
     * @param taskId 任务ID
     * @return {@link GetOutPaintingTaskResponse}
     */
    public GetOutPaintingTaskResponse getOutPaintingTask(String taskId) {
        if (StrUtil.isBlank(taskId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务ID不能为空");
        }
        String url = String.format(GET_OUT_PAINTING_TASK_URL, taskId);
        try (HttpResponse httpResponse = HttpRequest.get(url)
                .header("Authorization", "Bearer " + apiKey)
                .execute()) {
            if (!httpResponse.isOk()) {
                log.error("获取任务结果失败: {}", httpResponse.getStatus());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务结果失败: " + httpResponse.getStatus());
            }
            return JSONUtil.toBean(httpResponse.body(), GetOutPaintingTaskResponse.class);
        }
    }

}
