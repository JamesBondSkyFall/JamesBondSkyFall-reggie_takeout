package com.james.reggie_takeout.dto;

//import com.itheima.reggie.entity.Dish;
//import com.itheima.reggie.entity.DishFlavor;
import com.james.reggie_takeout.entity.Dish;
import com.james.reggie_takeout.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
