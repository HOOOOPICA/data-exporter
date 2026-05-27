package com.hpk.dataexpoter.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("export_task")
public class ExportTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer status; // 0待处理 1进行中 2成功 3失败

    private String filePath;

    private String errorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime finishedAt;
}