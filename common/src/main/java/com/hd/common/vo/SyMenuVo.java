package com.hd.common.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.hd.common.utils.LongToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: liwei
 * @Description:
 */
@Getter
@Setter
@ApiModel("菜单")
public class SyMenuVo implements Serializable {

    @JSONField(serializeUsing = LongToStringSerializer.class)
    private Long id;

    @JSONField(serializeUsing = LongToStringSerializer.class)
    @ApiModelProperty(value = "父菜单id")
    private Long parentId;

    private String enterpriseId;

    @ApiModelProperty(value = "菜单类型->0:目录,1:菜单")
    private Short type;

    private String levelCode;

    private String pathCode;

    private Integer enabled;

    private String name;

    private String url;

    private String note;

    @ApiModelProperty(value = "图标class")
    private String iconClass;

    private Boolean isVisible;

    @ApiModelProperty(value = "子菜单")
    private List<SyMenuVo> childs;

    @ApiModelProperty(value = "按钮")
    private List<SyMenuBtnVo> btns;

}