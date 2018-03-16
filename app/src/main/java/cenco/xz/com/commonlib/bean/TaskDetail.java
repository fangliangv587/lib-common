package cenco.xz.com.commonlib.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/3/12.
 */

public class TaskDetail {

    /**
     * task_id : xxxx
     * building_id : yyyyy
     * type : 上刊
     * distribute_date : 2018/01/06
     * execute_date : 2018/02/06
     * media_type : 屏
     * users : xxxx,yyyyy,zzzz
     * state : 1
     * comment : 带报头/点位照片
     * point_count : 5
     * finish_count : 0
     * user : [{"user_id":"xxxx","realname":"张三","phone":"15588591966"},{"user_id":"xxxx","realname":"张三","phone":"15588591966"},{"user_id":"xxxx","realname":"张三","phone":"15588591966"}]
     * point_task : [{"point_task_item_id":"xxx","point_screen_id":"yyyy","point_screen_name":"zzz","user_id":"aaaa","state":1},{"point_task_item_id":"xxx","point_screen_id":"yyyy","point_screen_name":"zzz","user_id":"aaaa","state":1}]
     */

    private String task_id;
    private String building_id;
    private String type;
    private String distribute_date;
    private String execute_date;
    private String media_type;
    private String users;
    private int state;
    private String comment;
    private int point_count;
    private int finish_count;
    private List<UserBean> user;
    private List<PointTaskBean> point_task;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(String building_id) {
        this.building_id = building_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDistribute_date() {
        return distribute_date;
    }

    public void setDistribute_date(String distribute_date) {
        this.distribute_date = distribute_date;
    }

    public String getExecute_date() {
        return execute_date;
    }

    public void setExecute_date(String execute_date) {
        this.execute_date = execute_date;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPoint_count() {
        return point_count;
    }

    public void setPoint_count(int point_count) {
        this.point_count = point_count;
    }

    public int getFinish_count() {
        return finish_count;
    }

    public void setFinish_count(int finish_count) {
        this.finish_count = finish_count;
    }

    public List<UserBean> getUser() {
        return user;
    }

    public void setUser(List<UserBean> user) {
        this.user = user;
    }

    public List<PointTaskBean> getPoint_task() {
        return point_task;
    }

    public void setPoint_task(List<PointTaskBean> point_task) {
        this.point_task = point_task;
    }

    public static class UserBean {
        /**
         * user_id : xxxx
         * realname : 张三
         * phone : 15588591966
         */

        private String user_id;
        private String realname;
        private String phone;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class PointTaskBean {
        /**
         * point_task_item_id : xxx
         * point_screen_id : yyyy
         * point_screen_name : zzz
         * user_id : aaaa
         * state : 1
         */

        private String point_task_item_id;
        private String point_screen_id;
        private String point_screen_name;
        private String user_id;
        private int state;

        public String getPoint_task_item_id() {
            return point_task_item_id;
        }

        public void setPoint_task_item_id(String point_task_item_id) {
            this.point_task_item_id = point_task_item_id;
        }

        public String getPoint_screen_id() {
            return point_screen_id;
        }

        public void setPoint_screen_id(String point_screen_id) {
            this.point_screen_id = point_screen_id;
        }

        public String getPoint_screen_name() {
            return point_screen_name;
        }

        public void setPoint_screen_name(String point_screen_name) {
            this.point_screen_name = point_screen_name;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }
    }
}
