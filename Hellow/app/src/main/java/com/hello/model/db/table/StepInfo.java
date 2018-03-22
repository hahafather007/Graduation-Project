package com.hello.model.db.table;

import com.hello.model.db.HelloDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = HelloDB.class)
public class StepInfo extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String time;
    @Column
    public int stepCount;

    public StepInfo() {
    }

    public StepInfo(String time, int stepCount) {
        this.time = time;
        this.stepCount = stepCount;
    }
}
