package com.hello.model.db.table;

import com.hello.model.db.HelloDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

//语音笔记的表
@Table(database = HelloDB.class)
public class Note extends BaseModel {
    //自增id，主键
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String title;
    @Column
    public String content;
    @Column
    public String time;
    @Column
    public String recordFile;

    public Note() {
    }

    public Note(String title, String content, String time, String recordFile) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.recordFile = recordFile;
    }
}
