package com.cj.imageselector.bean;

/**
 * Created by chenj on 2017/8/14.
 */

public class FolderBean {
    private String dir;//当前文件夹的路径
    private String firstImgPath;//第一张图片的路径
    private String name;//文件夹的名字
    private int count;//文件夹中图片的数量

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;

        int indexOf = this.dir.lastIndexOf("/");
        if (indexOf != -1 && indexOf + 1 < dir.length()) {
            setName(this.dir.substring(indexOf + 1));
        }
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
