package com.hello.model.db.table;

import com.hello.model.db.HelloDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.LocalDateTime;

//语音笔记的表
@Table(database = HelloDB.class)
public class Note extends BaseModel {
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String title;
    @Column
    public String content;
    @Column
    public LocalDateTime time;

    public Note(String title, String content, LocalDateTime time) {
        this.title = title;
        this.content = content;
        this.time = time;
    }
}
