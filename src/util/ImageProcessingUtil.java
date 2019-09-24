package util;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static main.OverHeadCount.*;
import static util.ResultAnalysisUtil.*;

/**
 * 图像处理工具类
 */
public class ImageProcessingUtil {

    private static int x = 0;      //上传图片检测区域的左上角x坐标
    private static int y = 0;      //上传图片检测区域的左上角y坐标
    private static int width = 0;  //上传图片检测区域的长
    private static int height = 0; //上传图片检测区域的宽

    /*
     * 重新修改Mat图像的尺寸
     * @param source    原始Mat
     * @param scale     缩放比例
     * @return target   返回重新修改尺寸的Mat
     */
    public static Mat resizeMat(Mat source, double scale) {
        double width = WIDTH * scale;
        double height = HEIGHT * scale;
        Size resize = new Size(width, height);
        Mat target = new Mat();
        Imgproc.resize(source, target, resize);
        return target;
    }

    /*
     *重新修改bufferedimage图像的尺寸
     * @param source    原始BufferedImage
     * @param scale     缩放比例
     * @return target   返回重新修改尺寸的BufferedImage
     */
    public static BufferedImage resizeBufferedImage(BufferedImage source, float scale) {
        int type = source.getType();
        BufferedImage target = new BufferedImage(WIDTH, HEIGHT, type);
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(scale, scale));
        g.dispose();
        return target;
    }


    /*
     *Mat类转BufferedImage类
     */
    public static BufferedImage mat2BufferedImage(Mat mat) {

        //比特数组大小
        int dataSize = mat.cols() * mat.rows() * (int) mat.elemSize();
        //新建比特数组存储Mat图像
        byte[] data = new byte[dataSize];
        mat.get(0, 0, data);
        int type = mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        if (type == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < dataSize; i += 3) {
                byte blue = data[i + 0];
                data[i + 0] = data[i + 2];
                data[i + 2] = blue;
            }
        }
        BufferedImage bufferedImage = new BufferedImage(mat.cols(), mat.rows(), type);
        bufferedImage.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return bufferedImage;
    }

    /*
     * 检测区域矩形框坐标
     *
     * @param WIDTH    图片长度
     * @param HEIGHT   图片高度
     * @param scale    缩放比例
     * @return sbArea.toString()   返回检测区域的字符串类型
     * 左上顶点（x1,y1）  右上顶点（x2,y2）
     * 左下顶点（x3,y3）  右下顶点（x4,y4）
     * 格式：x1,y1,x2,y2,x3,y3,x4,y4
     */
    public static String detectionArea(double scale) {
        StringBuilder sbArea = new StringBuilder();
        switch (AREA) {
            case "上":
                x = 1;
                y = 1;
                width = (int) (WIDTH * scale - 2);
                height = (int) (HEIGHT * scale / 2 - 2);
                break;
            case "下":
                x = 1;
                y = (int) (HEIGHT * scale / 2);
                width = (int) (WIDTH * scale - 2);
                height = (int) (HEIGHT * scale / 2 - 2);
                break;
            case "左":
                x = 1;
                y = 1;
                width = (int) (WIDTH * scale / 2 - 2);
                height = (int) (HEIGHT * scale - 2);
                break;
            case "右":
                x = (int) (WIDTH * scale / 2);
                y = 1;
                width = (int) (WIDTH * scale / 2 - 2);
                height = (int) (HEIGHT * scale - 2);
                break;
        }

        sbArea.append(x).append(",").append(y).append(",");                 //左上顶点
        sbArea.append(x + width).append(",").append(y).append(",");         //右上顶点
        sbArea.append(x + width).append(",").append(y + height).append(",");//右下顶点
        sbArea.append(x).append(",").append(y + height);                    //左下顶点
        return sbArea.toString();
    }

    /**
     * 图片合成
     *
     * @param bufferedImage 原图
     */
    public static BufferedImage bufferedImageWithInfo(BufferedImage bufferedImage, String result) {
        Graphics g = bufferedImage.getGraphics();
        Graphics2D g2d = (Graphics2D) g.create();
        //检测区域
        g2d.setColor(Color.green);
        g2d.drawRect(x * (int) (1 / SCALE), y * (int) (1 / SCALE), width * (int) (1 / SCALE), height * (int) (1 / SCALE));
        g2d.setFont(new Font(null, Font.PLAIN, 20));

        //视频文字信息
        g2d.setColor(Color.white);
        int person_num = getPersonNum(result);
        int[] person_count = getPersonCount(result);
        g2d.drawString("当前人数:" + person_num, 10, 30);
        g2d.drawString("in:" + person_count[0], 10, 60);
        g2d.drawString("out:" + person_count[1], 10, 90);

        //人物框
        ArrayList<int[]> person_infos;
        if ((person_infos = getPersonInfos(result)) != null) {
            g2d.setColor(Color.red);
            for (int i = 0; i < person_infos.size(); i++) {
                g2d.drawString("ID:" + person_infos.get(i)[0], person_infos.get(i)[1], person_infos.get(i)[2] - 30);
                g2d.drawRect(person_infos.get(i)[1], person_infos.get(i)[2], person_infos.get(i)[3], person_infos.get(i)[4]);
            }
        }

        g2d.dispose();
        return bufferedImage;
    }
}

