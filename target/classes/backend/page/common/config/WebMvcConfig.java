package backend.page.common.config;

import com.zjj.reggie.intercepter.LoginIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @create: 2023-04-02 21:40
 * @author: Junj_Zou
 * @Description:
 */
@Configuration  // 配置类可不能忘记标注 @Configuration 啊
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:front/");
    }



    @Autowired
    LoginIntercepter loginIntercepter;
    /**
     * 设置登录拦截器
     * 注意: 拦截器也会拦截所有静态资源，所以对于静态资源要放行
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginIntercepter)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/employee/login",
                        "/employee/logout",
                        // 拦截器会默认拦截静态资源，所以静态资源也要放行
                        "/backend/api/**",
                        "/backend/images/**",
                        "/backend/js/**",
                        "/backend/plugins/**",
                        "/backend/styles/**",
                        "/backend/favicon.ico",
                        // 由于我用的后端重定向，所以pages.login.login.html也不要拦截
                        "/backend/pages.login.login.html",
                        "/front/**"

                );
    }
}
