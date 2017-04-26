package com.cj.chenj.recyclerview_lib;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.cj.chenj.recyclerview_lib.adapter.CommonAdapter;
import com.cj.chenj.recyclerview_lib.adapter.ViewHolder;
import com.cj.chenj.recyclerview_lib.layoutmanager.CardConfig;
import com.cj.chenj.recyclerview_lib.layoutmanager.CardDragUtil;
import com.cj.chenj.recyclerview_lib.layoutmanager.OverLayCardLayoutManeger;

import java.util.ArrayList;
import java.util.List;

/**
 * OverLayCardLayoutManeger布局管理器的使用例子
 */
public class OverLayCardActivity extends AppCompatActivity {

//    public int[] images = {
////            R.mipmap.c1,
////            R.mipmap.c2,
////            R.mipmap.c3,
////            R.mipmap.c4,
////            R.mipmap.c5,
////            R.mipmap.c6,
////            R.mipmap.c7,
////            R.mipmap.c8,
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_over_lay_card);
//
//        CardConfig.config(getApplication());//相关配置，如显示几张层叠卡片，旋转角度等
//
//        final RecyclerView recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
//        recycler_view.setLayoutManager(new OverLayCardLayoutManeger());//设置自定的卡片的布局管理器
//        final List<DataBean> data = getData();
//        final MyAdapter myAdapter = new MyAdapter(this, R.layout.card_layout_item, data);
//        recycler_view.setAdapter(myAdapter);
//
//        CardDragUtil.cardDrag(recycler_view,data);
//    }
//
//
//    private List<DataBean> getData() {
//        List<DataBean> data = new ArrayList<>();
//        for(int i=0;i<images.length;i++){
//            data.add(new DataBean(images[i],"C"+i));
//        }
//        return data;
//    }
//
//    class DataBean {
//        public int resId;
//        public String text;
//        public DataBean(int resId,String text){
//            this.resId = resId;
//            this.text = text;
//        }
//    }
//
//    class MyAdapter extends CommonAdapter<DataBean>{
//
//        public MyAdapter(Context context, int layoutId, List<DataBean> datas) {
//            super(context, layoutId, datas);
//        }
//
//        @Override
//        public void convert(ViewHolder viewHolder, DataBean item, int position) {
//            viewHolder.setImage(R.id.card_image,item.resId).setText(R.id.card_text,item.text);
//        }
//
//    }


}
