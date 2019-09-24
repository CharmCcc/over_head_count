package main;

import Service.threadPoolService.paramPostToApiRunnable;
import Service.threadPoolService.paramSentToQueueRunnable;
import entity.Message;
import util.*;

import java.util.concurrent.*;


public class OverHeadCount {

    //本地视频路径或网络摄像头路径或者本地摄像头，用以读取视频
    public final static String videoPath = "video\\video2.mp4";

    //演示标志，值为true时，开视频演示，值为false则不演示
    public final static boolean DISPLAY_FLAG = false;

    //每秒取FRAME_PER_SECOND帧图片进行分析
    public final static int FRAME_PER_SECOND = 10;

    //检测区域,填写"上"、"下"、"左"、"右"
    //用以表示图片的上下左右的一半部分
    //行人从检测区域外进入检测区域内算是"进入"，反之则是"走出"
    public final static String AREA = "下";

    //演示视频大小
    public final static int HEIGHT = 540;
    public final static int WIDTH = 960;

    //上传的视频截图比例，越小处理速度越快，但有可能损失精度
    public final static double SCALE = 1;

    //线程池参数
    public final static int COMSUMER_CORE_POOL_SIZE = 2;
    public final static int PRODUCER_CORE_POOL_SIZE = 1;
    public static void main(String[] args) {

        // 阻塞队列，存储param信息
        ConcurrentLinkedQueue<Message> params = new ConcurrentLinkedQueue<Message>();

        //计数器
        CountDownLatch count = new CountDownLatch(COMSUMER_CORE_POOL_SIZE*2);

        //线程池资源
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(COMSUMER_CORE_POOL_SIZE*2 + PRODUCER_CORE_POOL_SIZE);

        VideoCapture2ImageUtil vc2iu = new VideoCapture2ImageUtil(params);

        try {
            if (DISPLAY_FLAG) {
                vc2iu.RunByDisplay(videoPath);
            } else {
                for (int i = 0; i < PRODUCER_CORE_POOL_SIZE ; i++){
                    fixedThreadPool.submit(new paramSentToQueueRunnable(vc2iu,videoPath));
                }
                Thread.sleep(5000);
                String url = vc2iu.getUrl();


                for (int i = 0; i < COMSUMER_CORE_POOL_SIZE; i++) {
                    fixedThreadPool.submit(new paramPostToApiRunnable(params, url, vc2iu.getAccessToken().get(0), "第 "+0+" 个账号-->猪用恒" + i + "号", count));
                    fixedThreadPool.submit(new paramPostToApiRunnable(params, url, vc2iu.getAccessToken().get(1), "第 "+1+" 个账号-->猪用恒" + i + "号", count));
                }

//                for (int j = 0 ; j < vc2iu.getAccessToken().size() ; j++) {
//                    String accessToken = vc2iu.getAccessToken().get(j);
//                    for (int i = 0; i < COMSUMER_CORE_POOL_SIZE; i++) {
//                        fixedThreadPool.submit(new paramPostToApiRunnable(params, url, accessToken, "第 "+j+" 个账号-->猪用恒" + i + "号", count));
//                    }
//                }
                //计数器等待，直到队列为空(图像分析完成)
                count.await();
                System.out.println("结束啦！！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fixedThreadPool.shutdown();
        }
    }
}
