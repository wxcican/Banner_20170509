package com.fuicuiedu.xc.banner_20170509;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;//适配器
    private TextView title;//图片标题
    private List<ImageView> imageViews;//图片控件的集合
    private List<View> dots;//圆点的集合
    private int oldPosttion;//旧点的位置
    private int currentItem;//当前的图片空间

    private ScheduledExecutorService scheduledExecutorService;//用来定时轮播


    //存放图片的id
    private int[] imageIds = new int[]{
            R.drawable.gxs,
            R.drawable.ljq,
            R.drawable.tgq,
            R.drawable.wcb,
            R.drawable.zzy
    };

    //存放图片的标题
    private String[] titles = new String[]{
            "我是gxs",
            "我是ljq",
            "我是tgq",
            "我是wcb",
            "我是zzy"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.main_vp);

        imageViews = new ArrayList<>();
        for (int i = 0; i < imageIds.length; i++) {
            ImageView image = new ImageView(this);
            image.setBackgroundResource(imageIds[i]);
            imageViews.add(image);
        }

        dots = new ArrayList<>();
        dots.add(findViewById(R.id.dot_0));
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));
        dots.add(findViewById(R.id.dot_3));
        dots.add(findViewById(R.id.dot_4));

        title = (TextView) findViewById(R.id.main_title);
        title.setText(titles[0]);

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);

        //完成标题和圆点的改变
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //标题的改变
                title.setText(titles[position]);

                //圆点的改变
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPosttion).setBackgroundResource(R.drawable.dot_normal);

                oldPosttion = position;

                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开一个后台线程
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //给线程池添加一个“定时调度任务”
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPagerTask(),
                2,
                2,
                TimeUnit.SECONDS
        );
    }

    //轮播任务
    private class ViewPagerTask implements Runnable{
        @Override
        public void run() {
            //取余来实现轮播
            currentItem = (currentItem + 1) % imageIds.length;
            //通知viewpager更新
            handler.sendEmptyMessage(0);
        }
    }

    //控制Viewpager改变
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //第二个参数设置为false，不要滑动效果
            viewPager.setCurrentItem(currentItem,false);
        }
    };

    //关闭线程池
    @Override
    protected void onStop() {
        super.onStop();
        if (scheduledExecutorService != null){
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter{
        //获取当前窗体的数量
        @Override
        public int getCount() {
            return imageIds.length;
        }

        //判断是否由对象生成的界面
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //返回一个对象，这个对象表明适配器选择哪个对象放在当前的窗体中
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageViews.get(position);
            container.addView(imageView);
            return imageView;
        }

        //是从ViewGroup移除当前的view
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }
    }
}
