package cn.saiyi.doorlock.other;

/**
 * 描述：业务类型
 * 创建作者：黎丝军
 * 创建时间：2016/10/11 15:51
 */

public enum BusinessType {
    /**
     * 登录
     */
    LOGIN,
    /**
     * 注册
     */
    REGISTER,
    /**
     * 验证码
     */
    VERIFY,
    /**
     * 找回密码
     */
    FIND_PASS,
    /**
     * 修改信息
     */
    MODIFY_INFO,
    /**
     * 下载
     */
    DOWNLOAD,
    /**
     * 上传
     */
    UPLOAD,
    /**
     * app更新
     */
    APP_UPDATE,
    /**
     * 其他
     */
    OTHER
}
