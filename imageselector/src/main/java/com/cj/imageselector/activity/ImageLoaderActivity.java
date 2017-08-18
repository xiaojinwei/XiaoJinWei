package com.cj.imageselector.activity;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.imageselector.R;
import com.cj.imageselector.adpter.ImageAdapter;
import com.cj.imageselector.bean.FolderBean;
import com.cj.imageselector.common.KeyConstants;
import com.cj.imageselector.widget.ListImageDirPopupWindow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chenj on 2017/8/14.
 */

public class ImageLoaderActivity extends AppCompatActivity  {
    //选中的图片
    static Set<String> mImgSelected = new HashSet<>();
    private TextView mChoiceSure;

    static Set<String> getImgSelected() {
        return mImgSelected;
    }

    public static boolean containsImg(String picPath) {
        return mImgSelected.contains(picPath);
    }

    public static boolean addImg(Context context,String picPath) {
        if (sMode == MODE_MULTI) {
            if (getImgSelected().size() >= sMaxSelectCount) {
                String msg = context.getString(R.string.you_can_choose_at_most)
                        + String.valueOf(getImgSelected().size())
                        + context.getString(R.string.quantity);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                return false;
            }
            getImgSelected().add(picPath);
        } else if (sMode == MODE_SINGLE) {
            if (getImgSelected().size() >= 1) {
                Toast.makeText(context, R.string.you_can_choose_at_most_1, Toast.LENGTH_SHORT).show();
                return false;
            }
            getImgSelected().add(picPath);
        }

        if (context instanceof ImageLoaderActivity) {
            ((ImageLoaderActivity)context).selectImgSizeChange(getImgSelected().size());
        }
        return true;
    }

    public static void removeImg(Context context,String picPath) {
        mImgSelected.remove(picPath);

        if (context instanceof ImageLoaderActivity) {
            ((ImageLoaderActivity)context).selectImgSizeChange(getImgSelected().size());
        }
    }

    //总共可以选择多少张图片
    public static final String EXTRA_SELECT_COUNT = KeyConstants.SELECT_COUNT;
    //选择模式 （单选还是多选）
    public static final String EXTRA_SELECT_MODE = KeyConstants.SELECT_MODE;
    //是否保持上次选中的状态
    public static final String EXTRA_SAVE_STATUS = KeyConstants.SAVE_STATUS;
    //选择图片返回的结果
    public static final String EXTRA_CHOICE_RESULT = KeyConstants.CHOICE_RESULT;
    //进入时就清除状态（如果之前是保存状态，下次进入的时候不要之前选中的状态（图片））
    public static final String EXTRA_ENTER_CLEAR_STATUS = KeyConstants.ENTER_CLEAR_STATUS;
    //默认多选的个数
    public static final int EXTRA_DEFAULT_SELECT_MAX_COUNT = 9;

    //选择图片的模式  多选
    public static final int MODE_MULTI = 0x0001;
    //选择图片的模式  单选
    public static final int MODE_SINGLE = 0x0002;
    //单选还是多选
    private static int sMode = MODE_MULTI;
    //最大图片选择的个数
    private static int sMaxSelectCount = EXTRA_DEFAULT_SELECT_MAX_COUNT;
    //是否保存上次的选择的状态(不保存在退出时会清除)
    private static boolean sIsSavaStatus = false;
    //进入时就清除状态（如果之前是保存状态，下次进入的时候不要之前选中的状态（图片））
    private static boolean sIsEnterClearStatus = false;

    private static final int REQUEST_CODE = 0x10;

    private GridView mGridView;
    private List<String> mImgs;
    private ImageAdapter mImageAdapter;

    private RelativeLayout mBottomRl;
    private TextView mDirName;
    private TextView mPreview;

    private File mCurrentDir;
    private int mMaxCount;

    private List<FolderBean> mFolderBeens = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    private static final int DATA_LOAD_SUCCESS = 0x110;

    private ListImageDirPopupWindow mDirPopupWindow;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DATA_LOAD_SUCCESS) {

                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                //绑定数据到view中
                data2View();
                //初始化popup
                initDirPopupWindow();
            }
        }
    };
    private Toolbar mToolbar;

    private void initDirPopupWindow() {
        mDirPopupWindow = new ListImageDirPopupWindow(this,mFolderBeens);
        mDirPopupWindow.setAnimationStyle(R.style.DirPopupWindowAnim);

        mDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });

        mDirPopupWindow.setOnDirSelectedListener(new ListImageDirPopupWindow.OnDirSelectedListener() {
            @Override
            public void onSelected(FolderBean folderBean) {
                //切换文件夹时
                //更新文件夹
                mCurrentDir = new File(folderBean.getDir());
                //更新数量
                mMaxCount = folderBean.getCount();
                //更新图片集
                mImgs.clear();
                mImgs.addAll(Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if(name.endsWith(".jpeg")
                                || name.endsWith(".jpg")
                                || name.endsWith(".png"))
                            return true;
                        return false;
                    }
                })));

                //mImageAdapter = new ImageAdapter(ImageLoaderActivity.this,mCurrentDir.getAbsolutePath(),mImgs);
                //mGridView.setAdapter(mImageAdapter);
                mImageAdapter.setDirPath(mCurrentDir.getAbsolutePath());
                mImageAdapter.notifyDataSetChanged();
                //setText(mMaxCount + getString(R.string.quantity));
                mDirName.setText(mCurrentDir.getName());

                mDirPopupWindow.dismiss();
            }
        });
    }

    /**
     * 使内容区域变亮
     */
    private void lightOn() {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 1.0f;
        getWindow().setAttributes(attributes);
    }

    /**
     * 使内容区域变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.3f;
        getWindow().setAttributes(attributes);
    }

    private void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(ImageLoaderActivity.this,R.string.no_pictures_were_scanned,Toast.LENGTH_SHORT).show();
            return;
        }
        if (mImgs == null) {
            mImgs = new ArrayList<>();
        }
        mImgs.clear();
        mImgs.addAll(Arrays.asList(mCurrentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".jpeg")
                        || name.endsWith(".jpg")
                        || name.endsWith(".png"))
                    return true;
                return false;
            }
        })));
        mImageAdapter = new ImageAdapter(this,mCurrentDir.getAbsolutePath(),mImgs);
        mGridView.setAdapter(mImageAdapter);

        //mDirCount.setText(mMaxCount + getString(R.string.quantity));
        mDirName.setText(mCurrentDir.getName());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader);
        //StatusBarUtil.statusBarTintColor(this,R.color.status_bg);

        initView();
        initIntent();
        initData();
        initEvent();
    }

    private void initIntent() {
        Intent intent = getIntent();
        sIsEnterClearStatus = intent.getBooleanExtra(EXTRA_ENTER_CLEAR_STATUS,false);
        if (sIsEnterClearStatus) {
            resetStatus();
        }
        sMode = intent.getIntExtra(EXTRA_SELECT_MODE,MODE_MULTI);
        sMaxSelectCount = intent.getIntExtra(EXTRA_SELECT_COUNT, EXTRA_DEFAULT_SELECT_MAX_COUNT);
        sIsSavaStatus = intent.getBooleanExtra(EXTRA_SAVE_STATUS,false);

    }

    protected void setToolBar(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initEvent() {

        mDirName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDirPopupWindow.showAsDropDown(mBottomRl,0,0);
                lightOff();
            }
        });

        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ImageLoaderActivity.this,ImageDetailActivity.class);
                intent.putExtra(KeyConstants.DIR_PATH,"");//选中的图片都是完整的地址路径存放在集合中的，所以这里的父文件夹传空串
                intent.putStringArrayListExtra(KeyConstants.IMG_PATHS, new ArrayList<String>(getImgSelected()) );
                intent.putExtra(KeyConstants.IMG_INDEX,0);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        mChoiceSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backChoicePic();
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(ImageLoaderActivity.this,ImageDetailActivity.class);
                intent.putExtra(KeyConstants.DIR_PATH,mCurrentDir.getAbsolutePath());
                intent.putStringArrayListExtra(KeyConstants.IMG_PATHS, new ArrayList<String>(mImgs) );
                intent.putExtra(KeyConstants.IMG_INDEX,position);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

    }

    /**
     * 利用ContentProvider扫描手机中的所有图片
     */
    private void initData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.the_current_memory_card_is_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.loading));


        //initForThread();//方式一
        initForLoader();//方式二

        //有可能不清楚上次的状态，所以初始化时就要回调
        selectImgSizeChange(getImgSelected().size());
    }

    private void initForLoader() {
        getLoaderManager().initLoader(0,null,mLoaderCallback);
    }

    private void initForThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(imgUri, null, MediaStore.Images.Media.MIME_TYPE
                                + " = ? or " + MediaStore.Images.Media.MIME_TYPE
                                + " = ? ", new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                Set<String> mDirPaths = new HashSet<String>();

                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();

                    FolderBean folderBean = null;

                    //如果是同一个文件夹中的图片，父文件夹只加一次
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    }else{
                        mDirPaths.add(dirPath);
                        folderBean = new FolderBean();
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImgPath(path);
                    }

                    if(parentFile.list() == null ) continue;

                    //获取文件夹下的图片的个数
                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            if(name.endsWith(".jpeg")
                                    || name.endsWith(".jpg")
                                    || name.endsWith(".png"))
                                return true;
                            return false;
                        }
                    }).length;

                    folderBean.setCount(picSize);

                    mFolderBeens.add(folderBean);

                    if (picSize > mMaxCount) {
                        mMaxCount = picSize;
                        mCurrentDir = parentFile;
                    }
                }
                cursor.close();
                //不需至null，因为在方法里面，所以会自动释放
                mDirPaths = null;
                //扫描完成
                mHandler.sendEmptyMessage(DATA_LOAD_SUCCESS);
            }
        }).start();
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.grid_view);
        mBottomRl = (RelativeLayout) findViewById(R.id.bottom_rl);
        mDirName = (TextView) findViewById(R.id.dir_name);
        mPreview = (TextView) findViewById(R.id.preview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mChoiceSure = (TextView) findViewById(R.id.choice_sure);

        setToolBar(mToolbar,getString(R.string.picture));
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>(){

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(ImageLoaderActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,IMAGE_PROJECTION,
                    IMAGE_PROJECTION[4] +" > 0 AND " + IMAGE_PROJECTION[3] + " = ? OR " +
                    IMAGE_PROJECTION[3] + " = ? ",
                    new String[]{"image/jpeg","image/png"},
                    IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            Set<String> mDirPaths = new HashSet<String>();

            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();

                FolderBean folderBean = null;

                //如果是同一个文件夹中的图片，父文件夹只加一次
                if (mDirPaths.contains(dirPath)) {
                    continue;
                }else{
                    mDirPaths.add(dirPath);
                    folderBean = new FolderBean();
                    folderBean.setDir(dirPath);
                    folderBean.setFirstImgPath(path);
                }

                if(parentFile.list() == null ) continue;

                //获取文件夹下的图片的个数
                int picSize = parentFile.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if(name.endsWith(".jpeg")
                                || name.endsWith(".jpg")
                                || name.endsWith(".png"))
                            return true;
                        return false;
                    }
                }).length;

                folderBean.setCount(picSize);

                mFolderBeens.add(folderBean);

                if (picSize > mMaxCount) {
                    mMaxCount = picSize;
                    mCurrentDir = parentFile;
                }
            }
            cursor.close();
            //不需至null，因为在方法里面，所以会自动释放
            mDirPaths = null;
            //扫描完成
            mHandler.sendEmptyMessage(DATA_LOAD_SUCCESS);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    /**
     * 选中的图片数量发生变法时被调用
     * @param size
     */
    private void selectImgSizeChange(int size) {
        if (size <= 0) {
            mPreview.setEnabled(false);
            mChoiceSure.setEnabled(false);
            mChoiceSure.setText(getString(R.string.choice_sure));
        }else{
            mPreview.setEnabled(true);
            mChoiceSure.setEnabled(true);
            mChoiceSure.setText(getString(R.string.choice_sure) + "(" + size + "/" + sMaxSelectCount +")");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (mImageAdapter != null) {
                    selectImgSizeChange(getImgSelected().size());
                    mImageAdapter.notifyDataSetChanged();
                }
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.loader_menu,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.choice_sure) {
//            backChoicePic();
//        }
//        return true;
//    }

    /**
     * 返回选中的图片地址
     */
    private void backChoicePic() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_CHOICE_RESULT,new ArrayList<String>(getImgSelected()));
        setResult(RESULT_OK,intent);

//        if (!sIsSavaStatus) {
//            resetStatus();
//        }

        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!sIsSavaStatus) {
            resetStatus();
        }
        super.onBackPressed();
    }

    /**
     * 重置参数
     */
    private void resetStatus() {
        //单选还是多选
        sMode = MODE_MULTI;
        //最大图片选择的个数
        sMaxSelectCount = EXTRA_DEFAULT_SELECT_MAX_COUNT;
        //是否保存上次的选择的状态
        sIsSavaStatus = false;
        //清除选中的图片
        getImgSelected().clear();
    }
}
