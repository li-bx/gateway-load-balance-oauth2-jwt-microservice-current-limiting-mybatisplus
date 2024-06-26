package com.hd.microsysservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

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
@TableName("sy_user")
public class SyUserEntity extends Model<SyUserEntity> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属部门
     */
    @TableField("org_id")
    private Long orgId;

    /**
     * 租户id
     */
    @TableField("enterprise_id")
    private String enterpriseId;

    /**
     * 1:启用,0:停用
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 登录账户
     */
    @TableField("account")
    private String account;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 密码hash值
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 手机
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 职务
     */
    @TableField("job_name")
    private String jobName;

    /**
     * 修改时间
     */
    @TableField("modified_time")
    private Date modifiedTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


    @TableField("id_center")
    private Long idCenter;

    @TableLogic("delete_flag")
    boolean deleteFlag;

    @TableField("head_pic")
    Long headPic;

    @TableField("type_Flag")
    Long typeFlag;
}
