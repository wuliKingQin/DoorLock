package cn.saiyi.doorlock.other;

/**
 * 描述：所有的请求地址，都在该类里面
 * 创建作者：黎丝军
 * 创建时间：2016/10/10 16:44
 */

public interface URL {

    //基础URL
    String ROOT_RUL = "http://120.76.236.153:8080";
    //与服务器建立长连接
    String SERVER_URL = ROOT_RUL + "/bblock/echo/";
    //登录
    String LOGIN = ROOT_RUL + "/bblock/user/login";
    //注册
    String REGISTER = ROOT_RUL + "/bblock/user/regist";
    //获取验证码
    String VERIFY_CODE = ROOT_RUL + "/bblock/authcode/sendcode/";
    //检查用户是否存在
    String IS_USER_EXIST = ROOT_RUL + "/bblock/user/check/";
    //修改密码
    String MODIFY_PASS = ROOT_RUL + "/bblock/user/updatepwd";
    //修改用户信息
    String MODIFY_USER_INFO = ROOT_RUL + "/bblock/user/updateuser";
    //查询用户信息
    String QUERY_USER_INFO = ROOT_RUL + "/bblock/user/queryuserinfo/";
    //新增设备
    String ADD_DEVICE = ROOT_RUL + "/bblock/device/add";
    //修改设备
    String MODIFY_DEVICE_NAME = ROOT_RUL + "/bblock/device/upddevicename";
    //删除设备
    String DELETE_DEVICE = ROOT_RUL + "/bblock/device/del";
    //移交设备
    String SHIFT_DEVICE = ROOT_RUL + "/bblock/device/update";
    //新增分享用户
    String ADD_SHARE_USER = ROOT_RUL + "/bblock/device/addinfo";
    //删除分享用户
    String DELETE_SHARE_USER = ROOT_RUL + "/bblock/device/delinfo";
    //根据mac查分享用户
    String MAC_QUERY_SHARE_USER = ROOT_RUL + "/bblock/device/query/";
    //根据手机查绑定的设备
    String PHONE_QUERY_BIND_DEVICE = ROOT_RUL + "/bblock/device/queryobjs/";
    //根据手机号判断是否是设备的主人
    String CHECKOUT_PHONE_MASTER = ROOT_RUL + "/bblock/device/checkphonemaster";
    //更换手势密码
    String CHANGE_GESTURE_PASS = ROOT_RUL + "/bblock/device/updatesignpwd";
    //手势密码开锁
    String GESTURE_OPEN_LOCK = ROOT_RUL + "/bblock/device/querysignpwd";
    //创建一次性密码
    String CREATE_ONCE_PASS = ROOT_RUL + "/bblock/device/updateycxpwd/";
    //使用一次性密码
    String USE_ONCE_PASS = ROOT_RUL + "/bblock/device/queryycxpwd";
    //指纹开锁
    String FINGERPRINT_OPEN_LOCK = ROOT_RUL + "/bblock/device/queryfppwd";
    //生成时效密码
    String CREATE_AGINE_PASS = ROOT_RUL + "/bblock/device/updatetpwd";
    //时效密码开锁
    String AGING_PASS_OPEN_LOCK = ROOT_RUL + "/bblock/device/updatetpwd";
    //上传文件
    String UPLOAD_FILE = ROOT_RUL + "/bblock/file/fileUpload/";
    //下载文件
    String DOWNLOAD_FILE = ROOT_RUL + "/bblock/file/fileUpload";
    //相关设置
    String CORRELATION_SETTING = ROOT_RUL + "/bblock/associate/queryass/";
    //开锁记录
    String OPEN_LOCK_RECORD = ROOT_RUL + "/bblock/history/queryhistory/";
    //清除开门记录
    String CLEAR_OPEN_LOCK_RECORD = ROOT_RUL + "/bblock/history/delRecord";
    //发送数据到设备
    String SEND_TO_DEVICE = ROOT_RUL + "/bblock/device/sendToDevice";
    //意见反馈
    String OPINION_ADD = ROOT_RUL + "/bblock/opinion/add";
    //版本检测
    String VERSION_CHECK = ROOT_RUL + "/bblock/version/query";
}
