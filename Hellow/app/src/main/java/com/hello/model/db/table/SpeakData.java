package com.hello.model.db.table;

import com.hello.model.db.HelloDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = HelloDB.class)
public class SpeakData extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String openId;
    @Column
    public String userTalk;
    @Column
    public String helloTalk;
    @Column
    public String time;

    public SpeakData() {
    }

    public SpeakData(String openId, String userTalk, String helloTalk, String time) {
        this.openId = openId;
        this.userTalk = userTalk;
        this.helloTalk = helloTalk;
        this.time = time;
    }
}
