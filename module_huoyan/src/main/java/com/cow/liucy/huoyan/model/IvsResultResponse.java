package com.cow.liucy.huoyan.model;

import java.io.Serializable;

/**
 * Created by anjubao on 2017/12/25.
 */

public class IvsResultResponse implements Serializable {

    /**
     * PlateResult : {"bright":0,"carBright":0,"carColor":0,"colorType":1,"colorValue":0,"confidence":99,"direction":0,"license":"青 PTW3Z3","location":{"RECT":{"bottom":392,"left":690,"right":834,"top":350}},"timeStamp":{"Timeval":{"sec":1458882234,"usec":921325}},"timeUsed":0,"triggerType":1,"type":1}
     * active_id : 0
     * clipImgSize : 1103
     * cmd : ivs_result
     * fullImgSize : 51566
     * id : 0
     * imageformat : jpg
     * timeString : 2016-03-25 13:03:54
     */

    private PlateResultBean PlateResult;
    private int active_id;
    private int clipImgSize;
    private String cmd;
    private int fullImgSize;
    private int id;
    private String imageformat;
    private String timeString;

    public PlateResultBean getPlateResult() {
        return PlateResult;
    }

    public void setPlateResult(PlateResultBean PlateResult) {
        this.PlateResult = PlateResult;
    }

    public int getActive_id() {
        return active_id;
    }

    public void setActive_id(int active_id) {
        this.active_id = active_id;
    }

    public int getClipImgSize() {
        return clipImgSize;
    }

    public void setClipImgSize(int clipImgSize) {
        this.clipImgSize = clipImgSize;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getFullImgSize() {
        return fullImgSize;
    }

    public void setFullImgSize(int fullImgSize) {
        this.fullImgSize = fullImgSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageformat() {
        return imageformat;
    }

    public void setImageformat(String imageformat) {
        this.imageformat = imageformat;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public static class PlateResultBean {
        /**
         * bright : 0
         * carBright : 0
         * carColor : 0
         * colorType : 1
         * colorValue : 0
         * confidence : 99
         * direction : 0
         * license : 青 PTW3Z3
         * location : {"RECT":{"bottom":392,"left":690,"right":834,"top":350}}
         * timeStamp : {"Timeval":{"sec":1458882234,"usec":921325}}
         * timeUsed : 0
         * triggerType : 1
         * type : 1
         */

        private int bright;
        private int carBright;
        private int carColor;
        private int colorType;
        private int colorValue;
        private int confidence;
        private int direction;
        private String license;
        private LocationBean location;
        private TimeStampBean timeStamp;
        private int timeUsed;
        private int triggerType;
        private int type;

        public int getBright() {
            return bright;
        }

        public void setBright(int bright) {
            this.bright = bright;
        }

        public int getCarBright() {
            return carBright;
        }

        public void setCarBright(int carBright) {
            this.carBright = carBright;
        }

        public int getCarColor() {
            return carColor;
        }

        public void setCarColor(int carColor) {
            this.carColor = carColor;
        }

        public int getColorType() {
            return colorType;
        }

        public void setColorType(int colorType) {
            this.colorType = colorType;
        }

        public int getColorValue() {
            return colorValue;
        }

        public void setColorValue(int colorValue) {
            this.colorValue = colorValue;
        }

        public int getConfidence() {
            return confidence;
        }

        public void setConfidence(int confidence) {
            this.confidence = confidence;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public TimeStampBean getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(TimeStampBean timeStamp) {
            this.timeStamp = timeStamp;
        }

        public int getTimeUsed() {
            return timeUsed;
        }

        public void setTimeUsed(int timeUsed) {
            this.timeUsed = timeUsed;
        }

        public int getTriggerType() {
            return triggerType;
        }

        public void setTriggerType(int triggerType) {
            this.triggerType = triggerType;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public static class LocationBean {
            /**
             * RECT : {"bottom":392,"left":690,"right":834,"top":350}
             */

            private RECTBean RECT;

            public RECTBean getRECT() {
                return RECT;
            }

            public void setRECT(RECTBean RECT) {
                this.RECT = RECT;
            }

            public static class RECTBean {
                /**
                 * bottom : 392
                 * left : 690
                 * right : 834
                 * top : 350
                 */

                private int bottom;
                private int left;
                private int right;
                private int top;

                public int getBottom() {
                    return bottom;
                }

                public void setBottom(int bottom) {
                    this.bottom = bottom;
                }

                public int getLeft() {
                    return left;
                }

                public void setLeft(int left) {
                    this.left = left;
                }

                public int getRight() {
                    return right;
                }

                public void setRight(int right) {
                    this.right = right;
                }

                public int getTop() {
                    return top;
                }

                public void setTop(int top) {
                    this.top = top;
                }
            }
        }

        public static class TimeStampBean {
            /**
             * Timeval : {"sec":1458882234,"usec":921325}
             */

            private TimevalBean Timeval;

            public TimevalBean getTimeval() {
                return Timeval;
            }

            public void setTimeval(TimevalBean Timeval) {
                this.Timeval = Timeval;
            }

            public static class TimevalBean {
                /**
                 * sec : 1458882234
                 * usec : 921325
                 */

                private int sec;
                private int usec;

                public int getSec() {
                    return sec;
                }

                public void setSec(int sec) {
                    this.sec = sec;
                }

                public int getUsec() {
                    return usec;
                }

                public void setUsec(int usec) {
                    this.usec = usec;
                }
            }
        }
    }
}
