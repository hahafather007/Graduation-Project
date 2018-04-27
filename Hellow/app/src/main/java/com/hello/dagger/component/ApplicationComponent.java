package com.hello.dagger.component;

import com.hello.application.HelloApplication;
import com.hello.dagger.module.ApplicationModule;
import com.hello.dagger.module.BackupServiceModule;
import com.hello.dagger.module.BookActivityModule;
import com.hello.dagger.module.MainActivityModule;
import com.hello.dagger.module.NoteCreateActivityModule;
import com.hello.dagger.module.NoteFragmentMoudle;
import com.hello.dagger.module.RemindActivityModule;
import com.hello.dagger.module.SecondaryHelloFragmentModule;
import com.hello.dagger.module.SecondaryNewsFragmentModule;
import com.hello.dagger.module.SettingActivityModule;
import com.hello.dagger.module.SportActivityModule;
import com.hello.dagger.module.SystemServiceModule;
import com.hello.dagger.module.TodayTodoFragmentMoudle;
import com.hello.dagger.module.VoiceServiceModule;
import com.hello.dagger.module.WakeUpServiceModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        SystemServiceModule.class,
        MainActivityModule.class,
        SettingActivityModule.class,
        RemindActivityModule.class,
        BookActivityModule.class,
        NoteFragmentMoudle.class,
        TodayTodoFragmentMoudle.class,
        SecondaryHelloFragmentModule.class,
        SecondaryNewsFragmentModule.class,
        NoteCreateActivityModule.class,
        SportActivityModule.class,
        WakeUpServiceModule.class,
        VoiceServiceModule.class,
        BackupServiceModule.class
})
public interface ApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(HelloApplication application);

        ApplicationComponent build();
    }

    void inject(HelloApplication application);
}
