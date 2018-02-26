# QsBase
## 基于AOP面向切面编程的Android高效开发框架

这不是一个框架，而是一套完整的Android APP开发生态圈~~

MVP架构+AOP面向切面编程，摒弃反射、代理等操作，稳定性和执行效率远优于一般的框架

强大的框架封装思想，能够轻松驾驭任何典型的APP项目开发

## 如何使用？
详细使用请参考完整项目[GrapeUniversity](https://github.com/qsmaxmin/GrapeUniversity)

#### step 1：项目的build.gradle添加依赖

        buildscript {
            repositories {
                    jcenter()
                    mavenCentral()
                    maven {
                        url 'https://jitpack.io'
                    }
                }
            dependencies {
                ...
                classpath 'org.aspectj:aspectjtools:1.8.10'
                classpath 'com.github.qsmaxmin:gradle_plugin_android_aspectjx:1.1.1'
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

#### step 2：app的build.gradle添加依赖

        apply plugin: 'android-aspectjx'
        ...


        dependencies {
            ...
            compile 'com.github.qsmaxmin:QsBase:2.1.8'
        }

#### step 3：自定义Application继承QsApplication

        application里有很多方法，开发者可以自行重写：

        public class GrapeApplication extends QsApplication {

            /**
             * 必须重写
             * 全局日志开关控制，返回true开启日志，上线前置为false
             */
            @Override public boolean isLogOpen() {
                return true;
            }

            /**
             * 必须重写
             * 网络请求所需的参数
             * 每个新的http请求都会调用一下这里
             * 给http设置终端地址
             * 给http设置请求头
             */
            @Override public void initHttpAdapter(HttpBuilder httpBuilder) {
                httpBuilder.setTerminal("https://www.baidu.com");
                httpBuilder.addHeader("Content-Type", "application/json");
                httpBuilder.addHeader("token", "123456");
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
                return new ProgressDialog();
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

            /**
             * http请求公共回调，可用来处理自定义异常等逻辑
             */
            @Override public void onCommonHttpResponse(Response response) {
                if (response != null) {
                    if (response.code() == 500) {
                        ....
                    }
                }
            }

            ...
        }

#### step 4：继承框架里面的Activity和Fragment

        1，Activity的使用：

        所有的Activity必须继承框架里的Activity
            为快速开发，框架有多个Activity供开发者继承如：
            QsActivity: 没有actionbar的activity
            QsABActivity: 有actionbar的activity
            QsViewpagerActivity: 没有actionbar的viewpager activity，集成Viewpager快速开发
            QsViewpagerABActivity: 有actionbar的viewpager activity，集成Viewpager快速开发

        例如：
        /**
         * V层
         */
        public class MainActivity extends QsViewPagerActivity<MainPresenter> {
            @Bind(R.id.tv_name)TextView tv_Name;

            initData(){
               HomePresenter presenter =  getPresenter();
               presenter.requestData();
            }

            @ThreadPoint(ThreadType.MAIN) public void updateUI(ModelUser modelUser) {
                tv_Name.setText(modelUser.userName);
            }
        }

        /**
         * P层
         */
        public class MainPresenter extends QsPresenter<MainActivity> {
             @ThreadPoint(ThreadType.HTTP) public void requestData() {
                UserHttp userHttp = createHttpRequest(UserHttp.class);
                ModelUser modelUser = userHttp.requestUserData(new BaseModelReq());
                if(isSuccess(modelUser)){
                    getView().updateUI(modelUser);
                }
             }
        }

        /**
         * M层
         * 可以定义一个http响应体基类，将公参放到里面，实现未实现的方法
         * 注：公参由服务端决定，这三个方法必须实现
         */
        public class BaseModel extends QsModel{
            public int     code;
            public String  msg;
            public boolean isEnd;
            /**
             * http请求是否成功，由子类实现
             */
            public boolean isResponseOk() {
                return code==0;
            }

            /**
             * 列表分页是否是最后一页，由子类实现
             */
            public boolean isLastPage() {
                return isEnd;
            }

            /**
             * 获取网络请求信息，由子类实现
             */
            public String getMessage() {
                return msg;
            }
        }

        /**
         * M层
         * 继承自己写的http响应体基类
         */
        public class ModelUser extends BaseModel{
            public String userId;
            public String userName;
            ...
        }

        /**
         * Http请求时的接口
         */
        public interface UserHttp {
            @POST("/api/v1/users") ModelUser requestUserData(@Body BaseModelReq req);
        }

        View层和Presenter层通过getView和getPresenter方法相互调用
        QsHelper里封装了很多常用的方法如：
        网络请求：QsHelper.getInstance().getHttpHelper()
        图片加载：QsHelper.getInstance().getImageHelper()
        线程池：QsHelper.getInstance().getThreadHelper()
        activity栈管理：QsHelper.getInstance().getScreenHelper()
        eventBus：QsHelper.getInstance().eventPost()
        activity跳转：QsHelper.getInstance().intent2Activity()
        打开对话框：QsHelper.getInstance().commitDialogFragment()
        资源获取：QsHelper.getInstance().getString()，QsHelper.getInstance().getDrawable()....

        tips：框架使用了沉浸式actionbar，所有没有使用系统的actionbar，在设置系统主题时需要添加如下样式：

            <!-- 系统主题样式 -->
            <style name="MyTheme" parent="Theme.AppCompat.Light.DarkActionBar">
                <!--不使用系统的toolbar-->
                <item name="android:windowActionBar">false</item>
                <item name="windowNoTitle">true</item>
            </style>



        2，Fragment的使用
        所有的Fragment必须继承框架的Fragment
            为快速开发，框架有多个Fragment供开发者继承如：
            QsFragment: 普通fragment
            QsListFragment: 带listView的Fragment
            QsPullListFragment: 带分页listView的Fragment
            ...
        Fragment写法和Activity一样...

#### 代码混淆
        -keep class com.qsmaxmin.qsbase** { *; }
        -dontwarn com.qsmaxmin.qsbase.*

        #View注入
        -keep class * extends java.lang.annotation.Annotation { *; }

        #okio
        -dontwarn okio.
        -dontwarn in.srain.cube.
        -keep class in.srain.cube.**

        #EventBus
        -keep class de.greenrobot.event.** {*;}
        -keepclassmembers class ** {
             public void onEvent*(**);
                void onEvent*(**);
        }

        #GSON
        -keep class sun.misc.Unsafe { *; }
        -keep class com.google.gson.examples.android.model. { *; }

        #AVAX
        -dontwarn javax.annotation.
        -keep class javax.annotation.
        -dontwarn javax.inject.
        -keep class javax.inject.

        #model防止混淆
        -keepclassmembers class * extends com.qsmaxmin.qsbase.common.model.QsModel {*;}
        -keepclassmembers class $ extends com.qsmaxmin.qsbase.common.model.QsModel {*;}

        #Glide防止混淆
        -keep public class extends com.bumptech.glide.module.AppGlideModule
        -keep public class extends com.bumptech.glide.module.LibraryGlideModule
        -keep class com.bumptech.glide.GeneratedAppGlideModuleImpl