package com.lgh.picturebackend.api.imagesearch.sub;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.lgh.picturebackend.exception.BusinessException;
import com.lgh.picturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图的地址
 */
@Slf4j
public class GetImagePageUrlApi {
    /**
     * 获取以图搜图的地址
     */
// GetImagePageUrlApi.java
    public static String getImagePageUrl(String imageUrl) {
        // 准备请求参数
        HashMap<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");

        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址 (确保格式正确)
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        String acsToken = "025";

        HttpResponse httpResponse = null;
        try {
            // 发送请求
            httpResponse = HttpRequest.post(url)
                    .header("acs-token", acsToken)
                    // 【新增】补充请求头，模拟浏览器
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .form(formData)
                    .timeout(5000)
                    .execute();

            if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                log.error("HTTP 请求失败，状态码：{}, 响应：{}", httpResponse.getStatus(), httpResponse.body());
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败，状态码：" + httpResponse.getStatus());
            }

            // 解析响应
            String body = httpResponse.body();
            log.debug("百度接口原始响应：{}", body); // 【新增】打印原始响应

            Map<String, Object> result = JSONUtil.toBean(body, Map.class);

            // 【核心修复】处理响应结果
            // 百度接口规范：status=0 表示成功，status!=0 表示失败
            Object statusObj = result.get("status");
            if (result == null || statusObj == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口返回数据格式异常");
            }

            // 原代码逻辑错误：它认为 status==0 是失败。实际上 0 通常是成功。
            // 修改为：如果 status 不等于 0，则视为失败
            if (!Integer.valueOf(0).equals(statusObj)) {
                String msg = result.get("msg") != null ? result.get("msg").toString() : "未知错误";
                log.error("百度接口业务返回失败，status: {}, msg: {}", statusObj, msg);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口业务调用失败：" + msg);
            }

            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (data == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口返回 data 为空");
            }

            // 对 Url 进行解码
            String rawUrl = (String) data.get("url");
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);

            if (StrUtil.isBlank(searchResultUrl)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效的结果地址");
            }
            return searchResultUrl;

        } catch (Exception e) {
            // 【新增】打印完整堆栈和上下文
            log.error("获取图片地址失败，参数 imageUrl:{}, 错误详情:", imageUrl, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取图片搜索结果地址失败：" + e.getMessage());
        }
    }


    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        String result = getImagePageUrl(imageUrl);
        System.out.println("搜索成功，结果 URL：" + result);
    }


}
