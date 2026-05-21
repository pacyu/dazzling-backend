package com.dazzling.blog.exception;

import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ErrorResponse handlerNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage())
        .title("资源未找到")
        .build();
    }

    @ExceptionHandler(DataAccessException.class)
    public ErrorResponse handleDataAccess(DataAccessException ex, HttpServletRequest request) {
        return ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage())
        .title("数据查询失败，请稍后重试")
        .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, message)
        .title("请求参数错误")
        .property("requestUrl", request.getRequestURI())
        .build();
    }

    // 处理其他未捕获异常
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGeneric(Exception ex, HttpServletRequest request) {
        return ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, "系统内部错误")
        .build();
    }

}