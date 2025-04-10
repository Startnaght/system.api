package com.star.imgapi.validation;

/**
 * 验证分组定义
 * 用于在不同场景下使用不同的验证规则
 */
public interface ValidationGroups {

    /**
     * 创建操作验证分组
     */
    interface Create {}

    /**
     * 更新操作验证分组
     */
    interface Update {}

    /**
     * 查询操作验证分组
     */
    interface Query {}

    /**
     * 文件上传验证分组
     */
    interface FileUpload {}

    /**
     * 用户注册验证分组
     */
    interface UserRegistration {}

    /**
     * 用户登录验证分组
     */
    interface UserLogin {}

    /**
     * 一言查询验证分组
     */
    interface HitokotoQuery {}

    /**
     * 邮件发送验证分组
     */
    interface MailSend {}

    /**
     * 局域网通信验证分组
     */
    interface LanCommunication {}
}