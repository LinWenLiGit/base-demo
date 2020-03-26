package com.lwl.base.code.generator.codegenerate.model;

import lombok.Data;

import java.util.Date;

/**
 * 代码生成人信息
 * @author Administrator
 */
@Data
public class CodeCopyRight {

    /**作者*/
    private String author;
    /**版本*/
    private String version;
    /**生成时间*/
    private Date since;

    public CodeCopyRight(String author, String version) {
        this.author = author;
        this.version = version;
        this.since = new Date();
    }
}
