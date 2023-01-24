package com.td.common.utils;

import com.td.common.vo.R;

public class RUtil {
    /**
     * 数据处理成功时的返回处理
     *
     * @param object 返回对象
     * @return
     */
    public static R success(Object object) {
        R r = new R();
        r.setData(object);
        //0表示成功
        r.setCode(200);
        r.setMsg("ok");
        return r;
    }

    /**
     * 数据处理成功时的返回处理（结合条数模式）
     *
     * @param object
     * @param count
     * @return
     */
    public static R success(Object object, Long count) {
        R r = success(object);
        //条数
        r.setCount(count);
        return r;
    }

    /**
     * 数据处理成功时的返回处理（无参数模式）
     *
     * @return
     */
    public static R success() {
        return success(null);
    }

    /**
     * 数据处理异常时的返回处理
     *
     * @param code
     * @param msg
     * @return
     */
    public static R error(Integer code, String msg) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    /**
     * 数据处理成功时的返回处理（自定义状态码模式）
     *
     * @param object
     * @param code
     * @return
     */
    public static R success(Object object, Integer code) {
        R r = new R();
        r.setData(object);
        //0表示成功
        r.setCode(code);
        r.setMsg("ok");
        return r;
    }

    /**
     * 数据处理成功时的返回处理（失败的另外一个编码，加上传输数据）
     *
     * @param object
     * @param code
     * @return
     */
    public static R errorTransData(Object object, Integer code) {
        R r = new R();
        r.setData(object);
        //0表示成功
        r.setCode(code);
        r.setMsg("false");
        return r;
    }

    /**
     * 数据处理异常时的返回处理
     *
     * @param msg
     * @return
     */
    public static R error(String msg) {
        R r = new R();
        r.setCode(201);
        r.setMsg(msg);
        return r;
    }

    /**
     * 数据处理异常时的返回处理
     *
     * @param code
     * @param msg
     * @return
     */
    public static R error(Integer code, String msg,Object object) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(object);
        return r;
    }
}
