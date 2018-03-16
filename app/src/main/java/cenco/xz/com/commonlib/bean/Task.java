package cenco.xz.com.commonlib.bean;

/**
 * Created by Administrator on 2018/3/12.
 * 任务
 */

public class Task {

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

    @Override
    public String toString() {
        return "Task{" +
                "task_id='" + task_id + '\'' +
                ", building_id='" + building_id + '\'' +
                ", type='" + type + '\'' +
                ", distribute_date='" + distribute_date + '\'' +
                ", execute_date='" + execute_date + '\'' +
                ", media_type='" + media_type + '\'' +
                ", users='" + users + '\'' +
                ", state=" + state +
                ", comment='" + comment + '\'' +
                ", point_count=" + point_count +
                ", finish_count=" + finish_count +
                '}';
    }
}
