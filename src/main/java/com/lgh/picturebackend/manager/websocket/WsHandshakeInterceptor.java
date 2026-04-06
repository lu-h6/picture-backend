package com.lgh.picturebackend.manager.websocket;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.lgh.picturebackend.manager.auth.SpaceUserAuthManager;
import com.lgh.picturebackend.manager.auth.model.SpaceUserPermissionConstant;
import com.lgh.picturebackend.model.entity.Picture;
import com.lgh.picturebackend.model.entity.Space;
import com.lgh.picturebackend.model.entity.User;
import com.lgh.picturebackend.model.enums.SpaceTypeEnum;
import com.lgh.picturebackend.service.PictureService;
import com.lgh.picturebackend.service.SpaceService;
import com.lgh.picturebackend.service.SpaceUserService;
import com.lgh.picturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private UserService userService;
    @Resource
    private PictureService pictureService;
    @Resource
    private SpaceService spaceService;
    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 建立连接前先校验
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes 给WebSocketSession 设置属性
     * @return
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        //获取到当前用户
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            //从请求中获取参数
            String pictureId = httpServletRequest.getParameter("pictureId");
            if (StrUtil.isBlank(pictureId)) {
                log.error("缺少图片参数，拒绝握手！");
                return false;
            }
            //获取登录用户
            User loginUser = userService.getLoginUser(httpServletRequest);
            if (loginUser == null) {
                log.error("用户未登录，拒绝握手！");
                return false;
            }
            // 校验用户是否有当前图片的编辑权限
            Picture picture = pictureService.getById(pictureId);
            if (ObjUtil.isEmpty(picture)) {
                log.error("图片不存在，拒绝握手！");
                return false;
            }
            // 如果是团队空间，并有编辑权限，才能建立连接
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (spaceId != null) {
                space = spaceService.getById(spaceId);
                if (ObjUtil.isEmpty(space)) {
                    log.error("空间不存在，拒绝握手！");
                    return false;
                }
                if (space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()) {
                    log.error("非团队空间，拒绝握手！");
                    return false;
                }
            }
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
                log.error("用户没有图片编辑权限，拒绝握手！");
                return false;
            }
            // 设置用户登录信息属性到WebSocket
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId", Long.valueOf(pictureId));
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
