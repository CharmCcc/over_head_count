package main;


import java.util.Calendar;

public class Image {
    private Calendar currentTime;

    private String imagePath = "";

    //当前照片人数
    private int person_num = 0;

    //总人数
    private int total_person_num = 0;

    private class person_info {

        private int ID = 0;
        private int left = 0;
        private int top = 0;
        private int width = 0;
        private int height = 0;

        public person_info() {
        }

        public person_info(int ID, int left, int top, int width, int height) {
            this.ID = ID;
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    private class person_count {

        private int in = 0;
        private int out = 0;

        public person_count() {
        }

        public person_count(int in, int out) {
            this.in = in;
            this.out = out;
        }

        public int getIn() {
            return in;
        }

        public void setIn(int in) {
            this.in = in;
        }

        public int getOut() {
            return out;
        }

        public void setOut(int out) {
            this.out = out;
        }
    }

    public Image() {
    }

    public Image(Calendar currentTime, String imagePath, int person_num, int total_person_num) {
        this.currentTime = currentTime;
        this.imagePath = imagePath;
        this.person_num = person_num;
        this.total_person_num = total_person_num;
    }

    public Calendar getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Calendar currentTime) {
        this.currentTime = currentTime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getPerson_num() {
        return person_num;
    }

    public void setPerson_num(int person_num) {
        this.person_num = person_num;
    }

    public int getTotal_person_num() {
        return total_person_num;
    }

    public void setTotal_person_num(int total_person_num) {
        this.total_person_num = total_person_num;
    }
}
