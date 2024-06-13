package com.fudan.util;

import com.fudan.exception.RetCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class R {
    private int status;
    private String info;
    private Object data;
    public R(RetCode retcode, Object data) {
        this.data = data;
        this.status = retcode.getCode();
        this.info = retcode.getMessage();
    }
    public R(RetCode retcode) {
        this.status = retcode.getCode();
        this.info = retcode.getMessage();
    }
}
