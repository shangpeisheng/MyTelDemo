package com.example.administrator.myteldemo;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/7/11.
 */

public class ContentInfoBean implements Serializable {
    private String title;
    private String tel;

    public ContentInfoBean(String title, String tel) {
        this.title = title;
        this.tel = tel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
