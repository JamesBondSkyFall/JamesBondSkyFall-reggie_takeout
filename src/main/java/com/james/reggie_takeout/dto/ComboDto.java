package com.james.reggie_takeout.dto;


import com.james.reggie_takeout.entity.Combo;
import com.james.reggie_takeout.entity.ComboDish;
import lombok.Data;
import java.util.List;

@Data
public class ComboDto extends Combo {

    private List<ComboDish> setmealDishes;

    private String categoryName;
}
