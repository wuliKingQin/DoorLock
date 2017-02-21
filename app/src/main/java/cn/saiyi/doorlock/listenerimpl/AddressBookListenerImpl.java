package cn.saiyi.doorlock.listenerimpl;
import android.view.View;
import com.saiyi.framework.listenerimpl.BaseListenerImpl;
import cn.saiyi.doorlock.activity.AddressBookActivity;
import cn.saiyi.doorlock.blls.AddressBookBusiness;

/**
 * 描述：通讯录监听管理器
 * 创建作者：黎丝军
 * 创建时间：2016/10/12 15:39
 */

public class AddressBookListenerImpl extends BaseListenerImpl<AddressBookActivity,AddressBookBusiness>{

    @Override
    protected void actionBarLeftClick(View leftView) {
        getActivity().finish();
    }

    @Override
    protected void actionBarRightClick(View rightView) {
        getBusiness().addShareAccount(getActivity(),getActivity().getContactAdapter().getSelectedData());
    }
}
