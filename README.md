> #### Another ways
- AsyncHttpClient，它把HTTP所有的通信细节全部封装在了内部，我们只需要简单调用几行代码就可以完成通信操作了。
- Universal-Image-Loader，它使得在界面上显示网络图片的操作变得极度简单，开发者不用关心如何从网络上获取图片，也不用关心开启线程、回收图片资源等细节

Volley可是说是把AsyncHttpClient和Universal-Image-Loader的优点集于了一身，Volley在性能方面进行了大幅度的调整，它的设计目标就是非常适合去进行数据量不大，但通信频繁的网络操作，而对于大数据量的网络操作，比如说下载文件等，Volley的表现就会非常糟糕。


注意：

1.mQueue只需要调用一次就行了Volley.newRequestQueue(this);这个方法内部会调用start方法，start方法默认开启1个缓存线程，4个网络请求线程。如果new多次了，那么系统资源肯定最后要被耗尽啊。

2.mQueue如果能设置为单例的就最好不过了，就不用频繁的去创建销毁线程了，占用系统资源。Volley内部并没有使用线程池来管理缓存线程和网络请求线程。

如果mQueue没有设置为单例模式，那么onDestroy方法必须调用Requ3.estQueue的stop方法，停止缓存线程和网络请求线程。这两个线程内部实现都是while无线循环的，除非调用了stop方法才退出while。想象一下，如果没有stop，那么启动一个activity，如果要做网络请求，那么就new一个RequestQueue，就是创建5个线程出来，但是没有去停止这几个线程，所以一直累积累积，最后后果不堪设想。

> ##### 添加访问权限

```java
<uses-permission android:name="android.permission.INTERNET" />  
```


1.  ##### Get方法获取String 发起一条HTTP请求，然后接收HTTP响应

```java
RequestQueue mQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest("http://www.baidu.com",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(stringRequest);
```
结果：返回给我们一长串的HTML代码

步骤
1. 创建一个RequestQueue对象。RequestQueue是一个请求队列对象，它可以缓存所有的HTTP请求，然后按照一定的算法并发地发出这些请求。我们不必为每一次HTTP请求都创建一个RequestQueue对象，这是非常浪费资源的，基本上在每一个需要和网络交互的Activity中创建一个RequestQueue对象就足够了。
2. 创建一个StringRequest对象。StringRequest的构造函数需要传入三个参数，第一个参数就是目标服务器的URL地址，第二个参数是服务器响应成功的回调，第三个参数是服务器响应失败的回调。其中，目标服务器地址我们填写的是百度的首页，然后在响应成功的回调里打印出服务器返回的内容，在响应失败的回调里打印出失败的详细信息。
3. 将StringRequest对象添加到RequestQueue里面。

---

2. #### Post方法

StringRequest中还提供了另外一种四个参数的构造函数，其中第一个参数就是指定请求类型的，我们可以使用如下方式进行指定：

```java
StringRequest stringRequest = new StringRequest(Method.POST, url,  listener, errorListener);
```
可是这只是指定了HTTP请求方式是POST，StringRequest中并没有提供设置POST参数的方法，但是当发出POST请求的时候，Volley会尝试调用StringRequest的父类——Request中的getParams()方法来获取POST参数，所以，我们只需要在StringRequest的匿名类中重写getParams()方法，在这里设置POST参数就可以了，代码如下所示：

```java
StringRequest stringRequest = new StringRequest(Method.POST, url,  listener, errorListener) {  
    @Override  
    protected Map<String, String> getParams() throws AuthFailureError {  
        Map<String, String> map = new HashMap<String, String>();  
        map.put("params1", "value1");  
        map.put("params2", "value2");  
        return map;  
    }  
};  
```

---
3. #### JsonRequest与JsonArrayRequest

```java
RequestQueue mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://m.weather.com.cn/data/101010100.html", null,  
        new Response.Listener<JSONObject>() {  
            @Override  
            public void onResponse(JSONObject response) {  
                Log.d("TAG", response.toString());  
            }  
        }, new Response.ErrorListener() {  
            @Override  
            public void onErrorResponse(VolleyError error) {  
                Log.e("TAG", error.getMessage(), error);  
            }  
        });  
        mQueue.add(jsonObjectRequest);  
```
这样当HTTP通信完成之后，服务器响应的天气信息就会回调到onResponse()方法中，并打印出来。

> #### ImageRequest的用法

        

```java
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        mQueue = Volley.newRequestQueue(this);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageRequest imageRequest = new ImageRequest(
                        "http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                imageView.setImageBitmap(response);
                            }
                        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imageView.setImageResource(R.drawable.facebook);
                    }
                });
                mQueue.add(imageRequest);
            }
        });
    } 
```
效果：

![](https://github.com/sunlianglong/Img/raw/master/Img/Photos/volleyphoto.png) 



ImageRequest的构造方法包含6个参数
1. 图片的URL地址
2. 图片请求成功的回调，这里我们把返回的Bitmap参数设置到ImageView中
3. 4.分别用于指定允许图片最大的宽度和高度，如果指定的网络图片的宽度或高度大于这里的最大值，则会对图片进行压缩，指定成0的话就表示不管图片有多大，都不会进行压缩。

5.用于指定图片的颜色属性，Bitmap.Config下的几个常量都可以在这里使用，其中ARGB_8888可以展示最好的颜色属性，每个图片像素占据4个字节的大小，而RGB_565则表示每个图片像素占据2个字节大小.

6.图片请求失败的回调，这里我们当请求失败时在ImageView中显示一张默认图片。

> #### ImageLoader的用法

1. 创建一个RequestQueue对象。
2. 创建一个ImageLoader对象。
3. 获取一个ImageListener对象。
4. 调用ImageLoader的get()方法加载网络上的图片。


```java
//创建一个RequestQueue对象。
RequestQueue mQueue = Volley.newRequestQueue(this);  


//new出一个ImageRequest对象
//ImageLoader的构造函数接收两个参数，第一个参数就是RequestQueue对象，第二个参数是一个ImageCache对象
//为了体现第二个参数的功能 新建BitmapCache类，传入参数
ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache() {  
    @Override  
    public void putBitmap(String url, Bitmap bitmap) {  
    }  
  
    @Override  
    public Bitmap getBitmap(String url) {  
        return null;  
    }  
});  

//获取一个ImageListener对象
ImageListener listener = ImageLoader.getImageListener(imageView,  
        R.drawable.default_image, R.drawable.failed_image); 
        //通过调用ImageLoader的getImageListener()方法能够获取到一个ImageListener对象，
        //getImageListener()方法接收三个参数，
       // 第一个参数指定用于显示图片的ImageView控件，
        //第二个参数指定加载图片的过程中显示的图片，
        //第三个参数指定加载图片失败的情况下显示的图片。
        
        
//调用ImageLoader的get()方法来加载图片   
imageLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg", listener);  
```
get()方法接收两个参数：第一个参数就是图片的URL地址，第二个参数则是刚刚获取到的ImageListener对象。如果你想对图片的大小进行限制，也可以使用get()方法的重载，指定图片允许的最大宽度和高度：

```java
imageLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",  
                listener, 200, 200);  
```


新建BitmapCache类，接口：ImageCache

这里只是简单实现一下，如果想要写一个性能非常好的ImageCache，最好借助Android提供的[LruCache](http://blog.csdn.net/guolin_blog/article/details/9316683)功能。


```java
public class BitmapCache implements ImageLoader.ImageCache {
    private LruCache<String,Bitmap>mCache;

    public BitmapCache(){
    //将缓存图片的大小设置为10M
        int maxSize = 10*1024*1024;
        mCache = new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key,Bitmap bitmap){
                return bitmap.getRowBytes()*bitmap.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String s) {
        return null;
    }
    @Override
    public void putBitmap(String s, Bitmap bitmap) {

    }
}
```


效果：

![](https://github.com/sunlianglong/Img/raw/master/Img/Photos/volley.gif) 
