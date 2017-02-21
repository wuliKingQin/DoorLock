package cn.saiyi.doorlock.device;

/**
 * 描述：硬件回应结果
 * 创建作者：黎丝军
 * 创建时间：2016/10/27 9:32
 */

public abstract class Result implements IResult {

    public Result() {
    }

    public Result(byte result) {
        resultHandle(result);
    }

    @Override
    public void resultHandle(byte result) {
        boolean isSuccess = false;
        String resultInfo = null;
        switch (result) {
            case SUCCESS_OPERATE:
                resultInfo = "锁已经打开";
                isSuccess = true;
                break;
            case SUCCESS_RECEIVE:
                resultInfo = "临时密匙接收成功";
                isSuccess = true;
                break;
            case SUCCESS_NULL:
                resultInfo = "临时密匙为空";
                isSuccess = true;
                break;
            case SUCCESS_LOSE:
                resultInfo = "临时密码失效";
                isSuccess = true;
                break;
            case FAIL_UNREGISTERED:
                resultInfo = "未注册";
                isSuccess = false;
                break;
            case FAIL_PASS_ERROR:
                resultInfo = "密码错误";
                isSuccess = false;
                break;
            case FAIL_LIMIT:
                resultInfo = "无权开锁";
                isSuccess = false;
                break;
            case FAIL_ADMIN_UNREGISTERED:
                resultInfo = "管理员未注册";
                isSuccess = false;
                break;
            case FAIL_APP_SPILL:
                resultInfo = "app编号溢出";
                isSuccess = false;
                break;
            case FAIL_TIME_FORMAT_ERROR:
                resultInfo = "时间格式错误";
                isSuccess = false;
                break;
            case FAIL_LOW_VOLTAGE:
                resultInfo = "低电压无法开锁";
                isSuccess = false;
                break;
            case FAIL_OPERATE:
                resultInfo = "操作失败";
                isSuccess = false;
                break;
            default:
                break;
        }
        if(isSuccess) {
            onSuccess(resultInfo);
        } else {
            onFail(resultInfo);
        }
    }

    /**
     * 操作成功提示
     * @param hintInfo 提示信息
     */
    public abstract void onSuccess(String hintInfo);

    /**
     * 操作失败提示
     * @param failInfo 失败信息
     */
    protected abstract void onFail(String failInfo);
}
