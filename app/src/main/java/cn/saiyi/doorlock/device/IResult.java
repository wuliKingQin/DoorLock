package cn.saiyi.doorlock.device;

/**
 * 描述：硬件执行结果
 * 创建作者：黎丝军
 * 创建时间：2016/10/27 9:17
 */

public interface IResult {
    //操作成功
    byte SUCCESS_OPERATE = 0x00;
    //临时密匙接收成功
    byte SUCCESS_RECEIVE = 0x01;
    //临时密匙为空
    byte SUCCESS_NULL = 0x02;
    //临时密码失效
    byte SUCCESS_LOSE = 0x03;
    //未注册
    byte FAIL_UNREGISTERED = 0x04;
    //密码错误
    byte FAIL_PASS_ERROR = 0x05;
    //开锁无权限
    byte FAIL_LIMIT = 0x06;
    //管理员未注册
    byte FAIL_ADMIN_UNREGISTERED = 0x07;
    //app编号溢出
    byte FAIL_APP_SPILL = 0x08;
    //时间格式错误
    byte FAIL_TIME_FORMAT_ERROR = 0x09;
    //低电压无法开锁
    byte FAIL_LOW_VOLTAGE = 0x0a;
    //操作失败
    byte FAIL_OPERATE = 0x0b;
    //一次性密码响应1
    byte FAIL_ONE_PASS_1 = 0x0e;
    //一次性密码响应2
    byte FAIL_ONE_PASS_2 = 0x0f;

    /**
     * 结果处理
     * @param result 消息
     */
    void resultHandle(byte result);
}
