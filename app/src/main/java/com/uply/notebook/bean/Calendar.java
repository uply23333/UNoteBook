package com.uply.notebook.bean;

import cn.bmob.v3.BmobObject;

public class Calendar extends BmobObject {

    // 存放在数据库中的属性
    private String title;
    private String content;
    private String notifyTime;

    // 辅助属性
    private String userName;
    private boolean isSync = false; // 标志该Note是否已经同步

    public Calendar(String title, String content, String notifyTime) {
        this.title = title;
        this.content = content;
        this.notifyTime = notifyTime;
    }

    public Calendar() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Calendar note = (Calendar) o;

        if (!title.equals(note.title)) return false;
        if (!content.equals(note.content)) return false;
        return notifyTime.equals(note.notifyTime);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + notifyTime.hashCode();
        return result;
    }
}
