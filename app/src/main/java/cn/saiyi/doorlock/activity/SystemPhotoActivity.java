package cn.saiyi.doorlock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.saiyi.framework.activity.AbsBaseActivity;
import com.saiyi.framework.util.ResourcesUtils;

import java.util.ArrayList;
import java.util.List;

import cn.saiyi.doorlock.R;
import cn.saiyi.doorlock.adapter.SystemPhotoAdapter;
import cn.saiyi.doorlock.bean.PhotoBean;
import cn.saiyi.doorlock.other.Constant;

/**
 * 描述：系统头像选择界面
 * 创建作者：黎丝军
 * 创建时间：2016/10/20 11:17
 */

public class SystemPhotoActivity extends AbsBaseActivity {

    private PhotoBean mSelectHeadIcon;
    //图片列表适配器
    private SystemPhotoAdapter mPhotoAdapter;
    //用于装载系统头像
    private GridView mSystemPhotoView;

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_system_photo);
    }

    @Override
    public void findViews() {
        mSystemPhotoView = getViewById(R.id.rv_system_photo);
    }

    @Override
    public void initObjects() {
        mPhotoAdapter = new SystemPhotoAdapter(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTitle(R.string.system_title);
        setTitleSize(Constant.TEXT_SIZE);
        setActionBarBackgroundColor(Color.WHITE);
        setTitleColor(R.color.color7);
        actionBar.setRightButtonText("完成");
        actionBar.setRightButtonTextColor(R.color.color7);
        actionBar.setLeftButtonBackground(R.mipmap.ic_blue_back,25,25);

        mSystemPhotoView.setAdapter(mPhotoAdapter);

        initPhotoData();
    }

    /**
     * 初始化头像数据
     */
    private void initPhotoData() {
        int index = 0;
        PhotoBean photoBean;
        final List<PhotoBean> data = new ArrayList<>();
        final String[] photoResId = getResources().getStringArray(R.array.system_photo);
        for(;index < photoResId.length;index ++) {
            photoBean = new PhotoBean();
            photoBean.setPhotoName(photoResId[index]);
            photoBean.setPhotoResId(ResourcesUtils.getMipmapResId(this,photoBean.getPhotoName()));
            data.add(photoBean);
        }
        mPhotoAdapter.setListData(data);
    }

    @Override
    public void setListeners() {
        actionBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getIntent().putExtra("selectedPhoto",mSelectHeadIcon);
                setResult(61,getIntent());
                finish();
            }
        });
        mSystemPhotoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectHeadIcon = (PhotoBean)parent.getItemAtPosition(position);
                mPhotoAdapter.setSelectedState(position);
            }
        });
    }

    @Override
    protected boolean isActionBar() {
        return true;
    }
}
