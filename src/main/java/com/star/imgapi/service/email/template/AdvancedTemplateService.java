package com.star.imgapi.service.email.template;

import com.star.imgapi.enums.EmailType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdvancedTemplateService {

    private final Map<String, String> templateCache = new ConcurrentHashMap<>();
    private final SpringTemplateEngine dynamicTemplateEngine;

    public AdvancedTemplateService() {
        this.dynamicTemplateEngine = new SpringTemplateEngine();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setCacheable(false);
        dynamicTemplateEngine.setTemplateResolver(templateResolver);
    }

    /**
     * 加载模板（支持缓存和热重载）
     */
    public String loadTemplate(EmailType emailType) {
        return templateCache.computeIfAbsent(emailType.getTemplateName(),
                key -> loadTemplateFromFile(key));
    }

    /**
     * 从文件系统加载模板
     */
    private String loadTemplateFromFile(String templateName) {
        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource(
                    "classpath:/templates/email/" + templateName + ".html");

            return new String(resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TemplateLoadingException("无法加载模板: " + templateName, e);
        }
    }

    /**
     * 动态模板渲染（支持数据库存储的模板）
     */
    public String renderDynamicTemplate(String templateContent, Map<String, Object> variables) {
        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        context.setVariables(variables);

        return dynamicTemplateEngine.process(templateContent, context);
    }

    /**
     * 清除模板缓存（用于热重载）
     */
    public void clearTemplateCache() {
        templateCache.clear();
    }

    /**
     * 清除特定模板缓存
     */
    public void clearTemplateCache(String templateName) {
        templateCache.remove(templateName);
    }
}