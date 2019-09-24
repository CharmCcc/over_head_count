package util;

import entity.Message;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static main.OverHeadCount.*;
import static org.opencv.videoio.Videoio.CAP_PROP_FPS;
import static util.ImageProcessingUtil.*;
import static util.ImageProcessingUtil.bufferedImageWithInfo;



public class VideoCapture2ImageUtil{
    private String url = null;
    private List<String> accessToken = new ArrayList<>();
    // 阻塞队列，存储param信息
    private ConcurrentLinkedQueue<Message> params;

    public VideoCapture2ImageUtil(ConcurrentLinkedQueue<Message> params) {
        this.params = params;
        // 请求url
        url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_tracking";
        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
        for (int i = 0 ; i < AuthService.getNum(); i++ )
            accessToken.add(AuthService.getAuth(i));
        //载入opencv库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    /*
     *判断字符串是否为正整数
     */
    public static boolean isPositiveInt(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /*
     *捕获视频流，并按照每秒FRAME_PER_SECOND张图片将其上传处理，并将返回结果叠加到图片上展示
     * @param videoPath 视频流路径
     */
    public void RunByDisplay(String videoPath) throws Exception {

        //新建videoCapture对象
        VideoCapture vc = new VideoCapture();
        //判断videoCapture对象要打开的是本地摄像头还是本地视频或者网络摄像头
        if (isPositiveInt(videoPath)) {
            vc.open(Integer.parseInt(videoPath));
        } else {
            vc.open(videoPath);
        }
        if (!vc.isOpened()) {
            System.out.println("打开视频文件错误，自动退出系统");
            System.exit(0);
        }

        //每帧索引
        int frame_index = 0;
        //帧率
        double FPS = vc.get(CAP_PROP_FPS);

        JFrame frame = new JFrame("演示视频");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ImagePanel panel = new ImagePanel();
        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setSize(WIDTH + frame.getInsets().left + frame.getInsets().right, HEIGHT + frame.getInsets().top + frame.getInsets().bottom);

        //获取视频流帧图片
        Mat mat = new Mat();
        BufferedImage bufferedImage = null;

        //备份视频流帧图片
        BufferedImage backupBufferedImage = null;

        //检测区域
        String detectionArea = detectionArea(SCALE);

        //读取视频
        while (true) {
            //读取一帧是否成功的标志
            boolean flag = vc.read(mat);
            if (!flag) {
                break;
            }
            //每秒取FRAME_PER_SECOND帧图片进行处理并显示
            if (frame_index % (Math.ceil(FPS / (double) FRAME_PER_SECOND)) == 0) {

                // 帧信息
                Message message = new Message();

                //备份图像
                mat = resizeMat(mat, 1);
                backupBufferedImage = (mat2BufferedImage(mat));

                //缩小图像
                mat = resizeMat(mat, SCALE);
                bufferedImage = (mat2BufferedImage(mat));

                byte[] imgData = bufferedImageToBytes(bufferedImage);
                String imgStr = Base64Util.encode(imgData);
                String imgParam = URLEncoder.encode(imgStr, "UTF-8");
                String param = "image=" + imgParam + "&dynamic=" + "true" + "&case_id=" + 1 + "&case_init=" + "false" + "&area=" + detectionArea;
                String result = HttpUtil.post(url, accessToken.get(0), param);
                System.out.println(result);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                message.setParam(param);
                message.setTimestamp(timestamp);
                params.offer(message);
                panel.setBufferedImage(bufferedImageWithInfo(backupBufferedImage, result));
                panel.repaint();

            }
            frame_index++;

        }
        vc.release();
    }



    /*
     *捕获视频流，并按照每秒FRAME_PER_SECOND张图片将其上传处理，仅返回数据
     * @param videoPath 视频流路径
     */
    public void OnlyRun(String videoPath) throws Exception {

        //新建videoCapture对象
        VideoCapture vc = new VideoCapture();
        //判断videoCapture对象要打开的是本地摄像头还是本地视频或者网络摄像头
        if (isPositiveInt(videoPath)) {
            vc.open(Integer.parseInt(videoPath));
        } else {
            vc.open(videoPath);
        }
        if (!vc.isOpened()) {
            System.out.println("打开视频文件错误，自动退出系统");
            System.exit(0);
        }

        //每帧索引
        int frame_index = 0;
        //帧率
        double FPS = vc.get(CAP_PROP_FPS);

        //获取视频流帧图片
        Mat mat = new Mat();
        BufferedImage bufferedImage = null;

        //备份视频流帧图片
        BufferedImage backupBufferedImage = null;

        //检测区域
        String detectionArea = detectionArea(SCALE);
        //读取视频
        while (true) {
            //读取一帧是否成功的标志
            boolean flag = vc.read(mat);
            if (!flag) {
                break;
            }

            //每秒取FRAME_PER_SECOND帧图片进行处理并显示
            if (frame_index % (Math.ceil(FPS / (double) FRAME_PER_SECOND)) == 0) {
                // 帧信息
                Message message = new Message();
                //备份图像
                mat = resizeMat(mat, 1);
                backupBufferedImage = (mat2BufferedImage(mat));

                //缩小图像
                mat = resizeMat(mat, SCALE);
                bufferedImage = (mat2BufferedImage(mat));
                byte[] imgData = bufferedImageToBytes(bufferedImage);
                String imgStr = Base64Util.encode(imgData);
                String imgParam = URLEncoder.encode(imgStr, "UTF-8");
                String param = "image=" + imgParam + "&dynamic=" + "true" + "&case_id=" + 1 + "&case_init=" + "false" + "&area=" + detectionArea;
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                message.setParam(param);
                message.setTimestamp(timestamp);
                params.offer(message);
                //String result = HttpUtil.post(url, accessToken, param);
                //System.out.println(result);
            }
            frame_index++;

        }
        vc.release();
    }


    public static byte[] bufferedImageToBytes(BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpg", baos);
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(List<String> accessToken) {
        this.accessToken = accessToken;
    }
}