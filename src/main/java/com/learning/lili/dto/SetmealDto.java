package com.learning.lili.dto;

import com.learning.lili.entity.Setmeal;
import com.learning.lili.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
