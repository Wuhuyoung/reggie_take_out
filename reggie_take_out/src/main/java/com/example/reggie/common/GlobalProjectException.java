package com.example.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobalProjectException {
    /**
     * 新增用户异常（用户名重复）
     * @param exception
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> Exception(SQLIntegrityConstraintViolationException exception) {
        log.info("出现异常 : {}", exception.getMessage());
        //出现用户名重复异常
        if(exception.getMessage().contains("Duplicate entry")) {
            String[] split = exception.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return Result.error(msg);
        }
        return Result.error("操作失败，出现未知异常");
    }

    /**
     * 处理业务异常
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Result<String> CustomException(CustomException exception) {
        log.info("出现业务异常 : {}", exception.getMessage());

        return Result.error(exception.getMessage());
    }
}
