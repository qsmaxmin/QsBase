# QsBase
## 基于AOP面向切面编程的Android高效开发框架(transform + javassist)

MVP(或MVVM)架构+AOP面向切面编程，摒弃反射、代理等操作，稳定性和执行效率高

积累很多框架封装思想，能够轻松驾驭任何典型的APP项目开发

轻量级框架，轻松实现类似EventBus，ButterKnife(MVVM架构不再支持)，Retrofit等核心注解功能，使代码简洁优雅。

## 如何使用？
详细使用请参考完整项目[QsBaseDemo](https://github.com/qsmaxmin/QsBaseDemo)

#### step 1：Project下的 'build.gradle' 添加仓库地址

        buildscript {
            repositories {
                ...
                maven {
                    url 'https://jitpack.io'
                }
            }

            dependencies {
                ...
                classpath 'com.github.qsmaxmin:QsTransform:10.10.7'
            }
        }
        
        allprojects {
            repositories {
                ...
                maven {
                    url 'https://jitpack.io'
                }
            }
        }

#### step 2：app Module下的 'build.gradle' 添加插件

        apply plugin: 'com.qsmaxmin.plugin'
        ...

        完成以上两步，SDK依赖环境就算搭建完毕了。


#### step 3：自定义Application

        该步有两种方案(任选其一即可)：
        1，继承QsApplication，开发者可以自行重写回调方法
        2，实现QsIApplication接口并在onCreate回调里调用QsHelper.getInstance().init(this); 参考QsApplication源码。

        public class GrapeApplication extends QsApplication {

            /**
             * 必须重写
             * 全局日志开关控制，返回true开启日志，上线前置为false
             */
            @Override public boolean isLogOpen() {
                return true;
            }

            /**
             * 注册http请求公共回调
             */
            @Override  public QsHttpCallback registerGlobalHttpListener() {
                return new CustomHttpCallback();
            }

            /**
             * 全局页面为空的布局
             */
            @Override public int emptyLayoutId() {
                return R.layout.fragment_common_empty;
            }

            /**
             * 全局页面出现错误的布局
             */
            @Override public int errorLayoutId() {
                return R.layout.fragment_common_httperror;
            }

            /**
             * 全局页面正在加载时的布局
             */
            @Override public int loadingLayoutId() {
                return R.layout.fragment_common_loading;
            }

            /**
             * 自定义进度条
             */
            @Override public QsProgressDialog getCommonProgressDialog() {
                return new DefaultProgressDialog();
            }

            /**
             * 全局Activity状态回调
             */
            @Override public void onActivityResume(Activity activity) {
                ...
            }

            /**
             * 全局Activity状态回调
             */
            @Override public void onActivityPause(Activity activity) {
                ...
            }

            ...
        }

#### step 4：继承框架里面的Activity和Fragment

        1，请使用QsTheme：
            <style name="YourTheme" parent="QsTheme">
                <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
                <item name="colorPrimary">@color/colorPrimary</item>
                <item name="colorAccent">@color/colorAccent</item>
            </style>

        2，Activity的使用：
        所有的Activity必须继承框架里的Activity
            为快速开发，框架有多个Activity供开发者继承
            MvActivity: 基类activity，可重写actionbarLayoutId()返回actionbar布局
            MvListActivity: 集成Viewpager的activity，快速开发
            MvListActivity: 集成List的activity，实现抽象方法即可
            MvViewpagerActivity: 集成Viewpager的activity，实现抽象方法即可
            ...

        例如：
        /**
         * MVVM架构的V层
         */
        public class MainActivity extends MvActivity{
            @BindBundle("bundle_key_user_id")String useId;      //绑定bundle传递数值
            private ActivityMainBinding binding;

            @Override public View onCreateContentView(LayoutInflater inflater, ViewGroup parent) {
                binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, viewGroup, true);
                return binding.getRoot();
            }

            @Override public void initData(Bundle bundle){
                requestDataFromNetwork(userId);
            }

            /**
             * 用ThreadPoint使得该方法在HTTP线程中执行
             */
            @ThreadPoint(ThreadType.HTTP)
            public void requestDataFromNetwork(String userId) {
                UserHttp http = createHttpRequest(UserHttp.class);
                ModelUser modelUser = http.requestUserData(userId);
                updateUI(modelUser);
            }

            /**
             * 用ThreadPoint使得该方法在MAIN线程中执行
             */
            @ThreadPoint(ThreadType.MAIN)
            public void updateUI(ModelUser modelUser) {
                binding.setUserData(modelUser);
            }
        }

        /**
         * Http请求时的接口定义
         */
        public interface UserHttp {
            @POST("/api/v1/users") ModelUser requestUserData(@FormParam("user_id") String userId);
        }


        /**
         * MVVM架构的M层
         * 继承自己写的http响应体基类
         */
        public class ModelUser{
            public String userName;
            public String userId;
            ...
        }

        QsHelper里封装了很多常用的方法如：
        网络请求：QsHelper.getHttpHelper()
        图片加载：QsHelper.getImageHelper()
        线程池：QsHelper.getThreadHelper()
        Activity栈管理：QsHelper.getScreenHelper()
        EventBus：QsHelper.eventPost()
        Activity跳转：QsHelper.intent2Activity()
        打开对话框：QsHelper.commitDialogFragment()
        资源获取：QsHelper.getString()，QsHelper.getDrawable()....


        3，Fragment的使用
        所有的Fragment必须继承框架的Fragment，用法和MvActivity基本一致
            为快速开发，框架有多个Fragment供开发者继承如：
            MvFragment: 普通fragment
            MvListFragment: 带listView的Fragment
            MvPullListFragment: 带分页listView的Fragment
            ...
        Fragment写法和Activity一样...

        4，注解的使用
        @BindBundle可以直接绑定Bundle值到field
        @Subscribe注解可以绑定事件接收到method，可通过QsHelper.eventPost(xxx)发送事件
        @Property注解可以持久化参数到本地（注意Class必须有@AutoProperty注解才能生效）
        @ThreadPoint注解的方法，可以让该方法在指定线程中执行
        @Permission注解的方法，会在方法执行前申请指定的权限，权限申请成功后再接着执行该方法

#### 代码混淆
        -keep class com.qsmaxmin.qsbase** { *; }
        -dontwarn com.qsmaxmin.qsbase.*
        -keep class * extends java.lang.annotation.Annotation { *; }

        #okhttp
        -dontwarn okio.**
        -dontwarn okhttp3.**
        -dontwarn in.srain.cube.**
        -keep class in.srain.cube.**{*;}
        -keep class okhttp3.**{*;}

        -dontwarn javax.annotation.**
        -dontwarn org.conscrypt.**
        -keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

        #GSON
        -keep class sun.misc.Unsafe { *; }
        -keep class com.google.gson.examples.android.model. { *; }

        #框架部分实现类防止混淆
        -keep class * extends com.qsmaxmin.annotation.QsNotProguard{*;}
        -keep class $ extends com.qsmaxmin.annotation.QsNotProguard{*;}
        
        #Glide防止混淆
        -keep class * extends com.bumptech.glide.module.AppGlideModule{*;}
        -keep class * extends com.bumptech.glide.module.LibraryGlideModule{*;}
        -keep class com.bumptech.glide.GeneratedAppGlideModuleImpl{*;}
        -keep class * implements com.bumptech.glide.module.GlideModule
        -keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
          **[] $VALUES;
          public *;
        }
        -dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder