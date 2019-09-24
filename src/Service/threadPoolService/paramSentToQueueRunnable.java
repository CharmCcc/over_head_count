package Service.threadPoolService;

import util.VideoCapture2ImageUtil;

/**
 * @author chm
 * @date 2019/9/20 14:55
 */
public class paramSentToQueueRunnable implements Runnable{
    private volatile VideoCapture2ImageUtil vc2iu;
    private String videoPath;

    public paramSentToQueueRunnable(VideoCapture2ImageUtil vc2iu,String videoPath) {
        this.vc2iu = vc2iu;
        this.videoPath = videoPath;
    }

    @Override
    public void run() {
        try {
            vc2iu.OnlyRun(videoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
