package cenco.xz.com.commonlib.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/3/12.
 */

public class TaskPointPlan {

    /**
     * ad_plan_id : xxxx
     * name : yyyyy
     * material : zzz
     * images : ["a","b","c"]
     */

    private String ad_plan_id;
    private String name;
    private String material;
    private List<String> images;

    public String getAd_plan_id() {
        return ad_plan_id;
    }

    public void setAd_plan_id(String ad_plan_id) {
        this.ad_plan_id = ad_plan_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
