package cn.saiyi.doorlock.listenerimpl;

import android.content.Intent;
import android.view.View;

import com.saiyi.framework.listenerimpl.BaseListenerImpl;
import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.activity.ModifyActivity;
import cn.saiyi.doorlock.activity.ResetPassActivity;
import cn.saiyi.doorlock.activity.SystemPhotoActivity;
import cn.saiyi.doorlock.blls.UserBusiness;
import cn.saiyi.doorlock.fragment.UserFragment;

/**
 * 描述：用户监听实例
 * 创建作者：黎丝军
 * 创建时间：2016/9/30 9:59
 */

public class UserListenerImpl extends BaseListenerImpl<UserFragment,UserBusiness> {

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_user_exit:
                getBusiness().existLogin();
                break;
            case R.id.tv_user_change_head:
                getFragment().startActivityForResult(new Intent(getContext(),SystemPhotoActivity.class),16);
                break;
            case R.id.tv_user_name:
                startModifyActivity(R.string.user_name,0);
                break;
            case R.id.tv_user_address:
                startModifyActivity(R.string.user_address,1);
                break;
            case R.id.tv_user_contact:
                startModifyActivity(R.string.user_contact,2);
                break;
            case R.id.tv_user_modify_pass:
                final Intent intent = new Intent(getContext(),ResetPassActivity.class);
                intent.putExtra(UserFragment.USER_MODIFY_PASS,true);
                getFragment().startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 启动修改界面
     * @param title 修改标题
     * @param what 修改类型
     */
    private void startModifyActivity(int title,int what) {
        final Intent intent = new Intent(getContext(), ModifyActivity.class);
        intent.putExtra(ModifyActivity.MODIFY_TITLE,getContext().getResources().getString(title));
        intent.putExtra(ModifyActivity.MODIFY_WHAT,what);
        getFragment().startActivityForResult(intent,UserFragment.REQUEST_CODE);
    }
}
