package com.cj.xjw.core.mvp.ui.zhihu.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cj.chenj.recyclerview_lib.adapter.CommonAdapter;
import com.cj.chenj.recyclerview_lib.adapter.ViewHolder;
import com.cj.xjw.R;
import com.cj.xjw.core.component.ImageLoader;
import com.cj.xjw.core.mvp.model.bean.SectionListBean;
import com.cj.xjw.core.utils.DimenUtil;
import com.cj.xjw.core.utils.Util;

import java.util.List;

/**
 * Created by cj_28 on 2017/4/27.
 */

public class SectionAdapter extends CommonAdapter<SectionListBean.DataBean> {
    public SectionAdapter(Context context, int layoutId, List<SectionListBean.DataBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder viewHolder, SectionListBean.DataBean item, int position) {
        //Glide在加载GridView等时,由于ImageView和Bitmap实际大小不符合,第一次时加载可能会变形(我这里出现了放大),必须在加载前再次固定ImageView大小
        ViewGroup.LayoutParams lp = viewHolder.getView(R.id.section_bg).getLayoutParams();
        lp.width = (int)(Util.getScreenWidth() - DimenUtil.dp2px(12)) / 2;
        lp.height = (int)DimenUtil.dp2px(120);

        ImageLoader.load(mContext,item.getThumbnail(), (ImageView) viewHolder.getView(R.id.section_bg));
        viewHolder.setText(R.id.section_kind,item.getName())
                .setText(R.id.section_des,item.getDescription());
    }
}
