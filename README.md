# loar

1.异步任务队列  com.loar.control 包下
//使用默认的实体创建执行任务
		QueueExecutor.getInstance().addTask(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                       
                        return null;
                    }
                });
								
也可以new QueueExecutor()创建一个新的任务队列

 也可以实现一个提供的任务对象Call，而不使用系统的Callable
 QueueExecutor.getInstance().addTask( new Call() {
 
 				//失败后需要执行的次数，当返回max并且抛异常可以无限次执行
        @Override
        public int executeCount() {
            return 0;
        }

				//首次执行的延迟时间				
				@Override
        public long delayExecuteTime() {
            return 0;
        }

				//两次执行的间隔时间
        @Override
        public long executeGap() {
            return 0;
        }
				
				//具体执行事件
        @Override
        public Object call() throws Exception {
            
            return null;
        }
    });
    
   addTask()会返回一个Future的结果对象，执行完成后的结果会保存在这个对象中。
   Future.getStatus()//获取执行的状态
   getResult()//获取执行结果，当状态为FINISH时才会有正确的结果
   getResultWait()//阻塞当前线程，直到执行结束(成功或者失败)，获取到结果

2.网络请求  com.loar.net 包下

3.数据操作  com.loar.storage 包下
1):自定义数据库，对包下的数据库进行扩展
public class MyProvider extends CommonProvider {

    @Override
    public SQLiteOpenHelper initHelper(Context context) {
        return new MyDatabaseHelper(context);
    }

    class MyDatabaseHelper extends DatabaseHelper {

        private final static String SQL_IMAGE = "CREATE TABLE " + "abc" + " ("
                + "_id INTEGER PRIMARY KEY ASC AUTOINCREMENT,"
                + CommonProvider.OptionsColumns.TAG + " TEXT,"
                + CommonProvider.OptionsColumns.KEY + " TEXT UNIQUE,"
                + CommonProvider.OptionsColumns.VALUE + " TEXT" + ");";

        protected MyDatabaseHelper(Context context) {
            //DATABASE_NAME,需要新建数据库，请使用自己的名称
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            addSql(SQL_IMAGE);//增加初始化sql语句
        }
    }
}

4.拖拽容器 com.loar.views.DragContainer

<com.justsy.test.DragContainer xmlns:android="http://schemas.android.com/apk/res/android" <br/>
    android:id="@+id/drag_container"<br/>
    android:layout_width="match_parent"<br/>
    android:layout_height="match_parent"><br/>
    
    <ScrollView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="#000000"
                android:scrollbars="vertical">
    
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">
    
                    <TextView
                        android:layout_width="50dp"
                        android:layout_height="50dp"
                        android:background="#fff222"
                        android:contentDescription="$canDrag$$canTarget$"
                        android:drawingCacheQuality="auto"
                        android:onClick="onClick"
                        android:text="3" />
    
                    <TextView
                        android:layout_width="50dp"
                        android:layout_height="50dp"
                        android:background="#ff00ff"
                        android:contentDescription="$canDrag$$canTarget$"
                        android:onClick="onClick"
                        android:text="4" />
                </LinearLayout>
    </ScrollView>
</com.loar.views.DragContainer>
在想要控件成为拖动源添加android:contentDescription="$canDrag$"即可
在想要成为拖动目标添加android:contentDescription="$canTarget$"即可
然后再DragContainer.setOnMDragListener(new DragContainer.OnMDragListener())设置监听进行相应的操作即可