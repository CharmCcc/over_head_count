package Service.threadPoolService;

import entity.Message;
import util.HttpUtil;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author chm
 * @date 2019/9/20 13:19
 */
public class paramPostToApiRunnable implements Runnable{
    // 阻塞队列，存储param信息
    private ConcurrentLinkedQueue<Message> params;
    // 连接API相关信息
    private String url = null;
    private String accessToken = null;
    // 临时信息，线程名字,测试用
    private String name;
    //计数器
    private CountDownLatch count;

    public paramPostToApiRunnable(ConcurrentLinkedQueue<Message> params, String url, String accessToken,String name,CountDownLatch countDownLatch) {
        this.params = params;
        this.url = url;
        this.accessToken = accessToken;
        this.name = name;
        this.count = countDownLatch;
    }

    @Override
    public void run(){
        try {
            while(!params.isEmpty()) {
                Message message = params.poll();
                String param = message.getParam();
                Timestamp timestamp = message.getTimestamp();
                Date date = new Date();
                date = timestamp;
                String result = HttpUtil.post(url, accessToken, param);
                System.out.println(name + " analysis :" + result + "-->时间:"+date);
                Thread.sleep(300);
            }
            count.countDown();//计数器-1
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
