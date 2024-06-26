package com.hd.microsysservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wli
 * @since 2021-08-30
 */
@Data
@Accessors(chain = true)
@TableName("sy_attach")
public class SyAttachEntity extends Model<SyAttachEntity> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    public void  setId(Long id){
        this.id=id;
    }

    @TableField("enterprise_id")
    private String enterpriseId;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 上传时间
     */
    @TableField("upload_time")
    private Date uploadTime;

    /**
     * 上传人id
     */
    @TableField("upload_by")
    private Long uploadBy;

    /**
     * 上传文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件mimetype
     */
    @TableField("content_type")
    private String contentType;

    @TableField("file_new_name")
    private String fileNewName;
}
