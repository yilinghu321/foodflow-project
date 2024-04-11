package com.foodflow.interceptor;

import com.foodflow.constant.JwtClaimsConstant;
import com.foodflow.properties.JwtProperties;
import com.foodflow.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("Intercepting a request, {}", request.getRequestURL().toString());

        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        if (!StringUtils.hasLength(token)) {
            log.info("No token found.");
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return false;
        }

        //2、校验令牌
        try {
            log.info("Authorizing:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("Current employee id：{}", empId);
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            log.info("Invalid token.");
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
