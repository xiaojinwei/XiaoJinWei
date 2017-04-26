package com.cj.xjw.core.mvp.ui.main.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cj.chenj.recyclerview_lib.adapter.LoadMoreWrapper;
import com.cj.chenj.recyclerview_lib.adapter.MultiItemCommonAdapter;
import com.cj.chenj.recyclerview_lib.adapter.MultiItemTypeSupport;
import com.cj.chenj.recyclerview_lib.adapter.ViewHolder;
import com.cj.xjw.R;
import com.cj.xjw.base.App;
import com.cj.xjw.core.mvp.model.bean.NewsSummary;
import com.cj.xjw.core.utils.DimenUtil;

import java.util.List;

/**
 * Created by chenj on 2017/4/22.
 */

public class NewsListAdapter extends MultiItemCommonAdapter<NewsSummary> {

    //public static final int TYPE_FOOTER = 0;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_PHOTO_ITEM = 1;


    public NewsListAdapter(Context context, List<NewsSummary> datas, MultiItemTypeSupport multiItemTypeSupport) {
        super(context, datas, multiItemTypeSupport);
    }


    @Override
    protected void convert(ViewHolder holder, NewsSummary item, int position, int itemType) {
        switch (itemType) {
            case TYPE_ITEM:
                setTypeItem(holder,item,position);
                break;

            case TYPE_PHOTO_ITEM:
                setTypePhotoItem(holder,item,position);
                break;
        }
        setItemAppearAnimation(holder,position,R.anim.anim_bottom_in);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(isShowingHasAnimation(holder)){
            holder.itemView.clearAnimation();
        }
    }

    private boolean isShowingHasAnimation(ViewHolder holder) {
        return holder.itemView.getAnimation() != null && holder.itemView.getAnimation().hasStarted();
    }

    private void setTypePhotoItem(ViewHolder holder, NewsSummary item, int position) {
        setTextView(holder, item);
        setImageView(holder, item);
    }

    private void setImageView(ViewHolder holder, NewsSummary item) {
        String title = item.getTitle();
        String ptime = item.getPtime();
        holder.setText(R.id.news_summary_title_tv,title)
                .setText(R.id.news_summary_ptime_tv,ptime);
    }

    private void setTextView(ViewHolder holder, NewsSummary item) {
        int PhotoThreeHeight = (int) DimenUtil.dp2px(90);
        int PhotoTwoHeight = (int) DimenUtil.dp2px(120);
        int PhotoOneHeight = (int) DimenUtil.dp2px(150);

        String imgSrcLeft = null;
        String imgSrcMiddle = null;
        String imgSrcRight = null;

        ViewGroup.LayoutParams layoutParams = holder.getView(R.id.news_summary_photo_iv_group).getLayoutParams();

        if (item.getAds() != null) {
            List<NewsSummary.AdsBean> adsBeanList = item.getAds();
            int size = adsBeanList.size();
            if (size >= 3) {
                imgSrcLeft = adsBeanList.get(0).getImgsrc();
                imgSrcMiddle = adsBeanList.get(1).getImgsrc();
                imgSrcRight = adsBeanList.get(2).getImgsrc();

                layoutParams.height = PhotoThreeHeight;

                holder.setText(R.id.news_summary_title_tv,App.getApplication()
                        .getString(R.string.photo_collections, adsBeanList.get(0).getTitle()));
            } else if (size >= 2) {
                imgSrcLeft = adsBeanList.get(0).getImgsrc();
                imgSrcMiddle = adsBeanList.get(1).getImgsrc();

                layoutParams.height = PhotoTwoHeight;
            } else if (size >= 1) {
                imgSrcLeft = adsBeanList.get(0).getImgsrc();

                layoutParams.height = PhotoOneHeight;
            }
        } else if (item.getImgextra() != null) {
            int size = item.getImgextra().size();
            if (size >= 3) {
                imgSrcLeft = item.getImgextra().get(0).getImgsrc();
                imgSrcMiddle = item.getImgextra().get(1).getImgsrc();
                imgSrcRight = item.getImgextra().get(2).getImgsrc();

                layoutParams.height = PhotoThreeHeight;
            } else if (size >= 2) {
                imgSrcLeft = item.getImgextra().get(0).getImgsrc();
                imgSrcMiddle = item.getImgextra().get(1).getImgsrc();

                layoutParams.height = PhotoTwoHeight;
            } else if (size >= 1) {
                imgSrcLeft = item.getImgextra().get(0).getImgsrc();

                layoutParams.height = PhotoOneHeight;
            }
        } else {
            imgSrcLeft = item.getImgsrc();

            layoutParams.height = PhotoOneHeight;
        }

        setPhotoImageView(holder, imgSrcLeft, imgSrcMiddle, imgSrcRight);
        holder.getView(R.id.news_summary_photo_iv_group).setLayoutParams(layoutParams);
    }

    private void setPhotoImageView(ViewHolder holder, String imgSrcLeft, String imgSrcMiddle, String imgSrcRight) {
        if (imgSrcLeft != null) {
            showAndSetPhoto((ImageView) holder.getView(R.id.news_summary_photo_iv_left), imgSrcLeft);
        } else {
            hidePhoto((ImageView) holder.getView(R.id.news_summary_photo_iv_left));
        }

        if (imgSrcMiddle != null) {
            showAndSetPhoto((ImageView) holder.getView(R.id.news_summary_photo_iv_middle), imgSrcMiddle);
        } else {
            hidePhoto((ImageView) holder.getView(R.id.news_summary_photo_iv_middle));
        }

        if (imgSrcRight != null) {
            showAndSetPhoto((ImageView) holder.getView(R.id.news_summary_photo_iv_right), imgSrcRight);
        } else {
            hidePhoto((ImageView) holder.getView(R.id.news_summary_photo_iv_right));
        }
    }

    private void showAndSetPhoto(ImageView imageView, String imgSrc) {
        imageView.setVisibility(View.VISIBLE);
        Log.d("NEWS-IMG",imgSrc);
        Glide.with(App.getApplication()).load(imgSrc).asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.image_place_holder)
                .error(R.drawable.ic_load_fail)
                .into(imageView);
    }

    private void hidePhoto(ImageView imageView) {
        imageView.setVisibility(View.GONE);
    }

    private void setTypeItem(ViewHolder holder, NewsSummary item, int position) {
        String title = item.getLtitle();
        if (title == null) {
            title = item.getTitle();
        }
        String ptime = item.getPtime();
        String digest = item.getDigest();
        String imgSrc = item.getImgsrc();

        holder.setText(R.id.news_summary_title_tv,title)
                .setText(R.id.news_summary_ptime_tv,ptime)
                .setText(R.id.news_summary_digest_tv,digest);

        Glide.with(mContext).load(imgSrc).asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.image_place_holder)
                .error(R.drawable.ic_load_fail)
                .into((ImageView) holder.getView(R.id.news_summary_photo_iv));
    }


//    public void setLoadMoreView(View loadMoreView)
//    {
//        mLoadMoreView = loadMoreView;
//    }
//
//    public void setLoadMoreView(int layoutId)
//    {
//        mLoadMoreLayoutId = layoutId;
//    }
//
//    public void hideMoreView() {
//        mInShowMoreView = false;
//        notifyItemRemoved(getItemCount());
//    }
//
//    public void showMoreView() {
//        mInShowMoreView = true;
//        notifyItemInserted(getItemCount());
//    }

}
