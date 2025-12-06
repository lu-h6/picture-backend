package com.lgh.picturebackend.aop;


import com.lgh.picturebackend.annotation.AuthCheck;
import com.lgh.picturebackend.exception.BusinessException;
import com.lgh.picturebackend.exception.ErrorCode;
import com.lgh.picturebackend.model.entity.User;
import com.lgh.picturebackend.model.enums.UserRoleEnum;
import com.lgh.picturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;


    /**
     * 拦截器
     *
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     * @return
     * 这段代码定义了一个环绕通知（@Around）的拦截器方法 doInterceptor，
     * 用于处理带有 @AuthCheck 注解的方法。它通过 authCheck.mustRole() 获取所需角色，
     * 并从请求上下文中获取当前登录用户信息，为后续权限校验做准备。
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        // 获取当前登录用户
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);
        //通过    ADMIN("管理员","admin"); 对常量 中value的对比，拿到对应的枚举
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //如果不需要权限，放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        //以下代码必须有权限，才能通过
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (userRoleEnum == null ){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //必须是管理员
        if (userRoleEnum != UserRoleEnum.ADMIN && userRoleEnum != mustRoleEnum) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
