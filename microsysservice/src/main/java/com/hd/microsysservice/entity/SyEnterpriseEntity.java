package com.hd.microsysservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wli
 * @since 2021-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sy_enterprise")
public class SyEnterpriseEntity extends Model<SyEnterpriseEntity> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 企业编号(root：特殊企业，系统配置员使用关联菜)
     */
    @TableField("enterprise_id")
    private String enterpriseId;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 描述
     */
    @TableField("note")
    private String note;

    @TableLogic("delete_flag")
    //@TableField("delete_flag")
    private boolean deleteFlag;

    @TableField("user_count")
    private Integer userCount;

    @TableField("expire_date")
    private String expireDate;

}
