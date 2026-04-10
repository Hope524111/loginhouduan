//package com.xxz.loginhouduan.interceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//@Component
//public class AuthenticationInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//        // 检查用户是否已登录，这里可以根据你的实际逻辑进行判断
//        boolean isLoggedIn = checkUserLoggedIn(request);
//        if (!isLoggedIn) {
//            // 用户未登录，返回消息给前端
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码为未授权
//            response.getWriter().write("用户未登录");
//            return false; // 拦截请求
//        }
//        return true; // 放行请求
//    }
//
//    // 这里需要根据你的实际逻辑来判断用户是否已登录
//    private boolean checkUserLoggedIn(HttpServletRequest request) {
//        HttpSession session = request.getSession(false); // 如果会话不存在则不创建新会话
//        if (session != null && session.getAttribute("loggedInUser") != null) {
//            // 如果会话存在并且已经设置了 loggedInUser 属性，表示用户已登录
//            return true;
//        } else {
//            // 否则用户未登录
//            return false;
//        }
//    }
//
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                           ModelAndView modelAndView) throws Exception {
//        // 在请求处理之后调用，但在视图被渲染之前（Controller 方法调用之后）
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//            throws Exception {
//        // 在整个请求结束之后调用，也就是在 DispatcherServlet 渲染了对应的视图之后执行
//    }
//}
