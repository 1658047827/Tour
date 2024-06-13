package com.fudan.exception;

public enum RetCode implements BaseCode {
    /**
     * 主键id生成错误
     */
    ID_GENERATE_ERROR(1001, "请求错误，请重试"),
    /**
     * 用户名不存在
     */
    USERNAME_ERROR(10001, "用户名不存在"),
    /**
     * 密码错误
     */
    PASSWORD_ERROR(10002, "密码错误"),
    /**
     * User为NULL
     */
    NULL_USER(10003, "空对象"),
    /**
     * 用户名格式非法
     */
    USERNAME_FORMAT_ERROR(10004, "用户名格式非法"),
    /**
     * 手机号格式非法
     */
    PHONE_NUM_FORMAT_ERROR(10005, "手机号格式非法"),
    /**
     * 身份证号码格式非法
     */
    ID_CARD_NUM_FORMAT_ERROR(10006, "身份证号码格式非法"),
    /**
     * 邮箱格式非法
     */
    EMAIL_FORMAT_ERROR(10007, "邮箱格式非法"),
    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(10008, "用户名已存在"),
    /**
     * 手机号已经注册
     */
    PHONE_NUM_EXISTS(10009, "手机号已经注册"),
    /**
     * 身份证号重复
     */
    ID_CARD_NUM_EXISTS(10010, "身份证号重复"),
    /**
     * 邮箱重复
     */
    EMAIL_EXISTS(10011, "邮箱重复"),
    /**
     * 用户id不存在
     */
    USER_ID_NOT_FOUND(10012, "用户id不存在"),
    /**
     * 密码更改失败
     */
    PASSWORD_CHANGE_FAILURE(10013, "密码更改失败"),
    /**
     * 用户信息更新失败
     */
    USER_INFO_CHANGE_FAILURE(10014, "用户信息更新失败"),
    /**
     * 请求头无效
     */
    INVALID_TOKEN(20001, "登录过期或失效"),
    /**
     * 权限不足
     */
    UNAUTHORIZED_REQUEST(20002, "无权访问"),
    /**
     * 未登录
     */
    NOT_LOGGED_IN(20003, "未登录"),

    /**
     * Redis数据库为查询
     */
    REDIS_NOT_FOUND(20004, "登录过期或失效，redis"),
    /**
     * 无效请求，不符合请求数据要求
     */
    INVALID_REQUEST(20005, "无效请求"),
    /**
     * 未找到对应商店
     */
    STORE_NOT_FOUND(30001,"未找到对应商店"),
    /**
     * 商店插入失败
     */
    STORE_INSERT_FAILURE(30002,"商店插入失败"),
    /**
     * 商店名重复
     */
    STORE_NAME_EXISTS(30003,"商店名重复"),
    /**
     * 商店更新失败
     */
    STORE_UPDATE_FAILURE(30004,"商店更新失败"),
    /**
     * 已经有未批准的开店申请了
     */
    STORE_REQUEST_EXISTS(30005,"已经有未批准的开店申请了"),
    /**
     * 相应开店请求未找到
     */
    STORE_OPEN_REQUEST_NOT_FOUND(30011,"相应开店请求未找到"),
    /**
     * 开店申请更新失败
     */
    STORE_OPEN_REQUEST_UPDATE_FAILURE(30012,"开店申请更新失败"),
    /**
     * 开店申请插入失败
     */
    STORE_OPEN_REQUEST_INSERT_FAILURE(30013,"开店申请插入失败"),
    /**
     * 无法再次批准已经批准通过的开店申请
     */
    STORE_OPEN_REQUEST_HAS_BEEN_APPROVED(30014,"无法再次批准已经批准通过的开店申请"),
    /**
     * 无法再次批准已经拒绝通过的开店申请
     */
    STORE_OPEN_REQUEST_HAS_BEEN_DENIED(30015,"无法再次批准已经拒绝通过的开店申请"),
    /**
     * 相应修改信息请求未找到
     */
    STORE_MODIFY_REQUEST_NOT_FOUND(30021,"相应修改信息请求未找到"),
    /**
     * 修改信息申请更新失败
     */
    STORE_MODIFY_REQUEST_UPDATE_FAILURE(30022,"修改信息申请更新失败"),
    /**
     * 修改信息申请插入失败
     */
    STORE_MODIFY_REQUEST_INSERT_FAILURE(30023,"修改信息申请插入失败"),
    /**
     * 无法再次批准已经批准通过的修改信息申请
     */
    STORE_MODIFY_REQUEST_HAS_BEEN_APPROVED(30024,"无法再次批准已经批准通过的修改信息申请"),
    /**
     * 无法再次批准已经拒绝通过的修改信息申请
     */
    STORE_MODIFY_REQUEST_HAS_BEEN_DENIED(30025,"无法再次批准已经拒绝通过的修改信息申请"),
    /**
     * 修改信息相关操作失败
     */
    STORE_MODIFY_REQUEST_FAILURE(30020,"修改信息申请相关操作失败"),
    /**
     * 关店申请插入失败
     */
    STORE_DELETE_REQUEST_INSERT_FAILURE(30031,"关店申请插入失败"),
    /**
     * 关店申请更新失败
     */
    STORE_DELETE_REQUEST_UPDATE_FAILURE(30032,"关店申请更新失败"),
    /**
     * 关店申请已经存在
     */
    STORE_DELETE_REQUEST_HAS_EXISETED(30034,"关店申请已经存在"),
    /**
     * 关店申请未找到
     */
    STORE_DELETE_REQUEST_NOT_FOUND(30033,"关店申请未找到"),
    /**
     * 账户充值失败
     */
    ACCOUNT_RECHARGE_FAILURE(40001, "充值失败"),
    /**
     * 账户不存在
     */
    ACCOUNT_NOT_FOUND(40002, "账户不存在"),
    /**
     * 余额不足
     */
    ACCOUNT_INSUFFICIENT_BALANCE(40003, "余额不足"),
    /**
     * 购物车项插入失败
     */
    CART_INSERT_FAILURE(50001, "购物车项插入失败"),
    /**
     * 购物车项删除失败
     */
    CART_DELETE_FAILURE(50002, "购物车项删除失败"),
    /**
     * 购物车项修改失败
     */
    CART_MODIFY_FAILURE(50003, "购物车项修改失败"),
    /**
     * 购物车项不存在
     */
    CART_ITEM_NOT_FOUND(50004, "购物车项不存在"),
    /**
     * 购物车商品数量必须为正整数
     */
    CART_COMMODITY_NUM_NONPOS(50005, "商品数量必须为正整数"),
    /**
     * 未找到商品
     */
    COMMODITY_NOT_FOUND(60001, "商品未找到"),
    /**
     * 库存不足
     */
    COMMODITY_STOCKS_INSUFFICIENT(60002, "库存不足"),
    /**
     * "商品上架申请未找到"
     */
    COMMODITY_LAUNCH_REQUEST_NOT_FOUND(60003, "商品上架申请未找到"),
    /**
     * 商品上架失败
     */
    COMMODITY_LAUNCH_FAILURE(60004, "商品上架失败"),
    /**
     * 商品信息修改申请失败
     */
    COMMODITY_MODIFY_REQUEST_FAILURE(60011, "商品信息修改申请失败"),
    /**
     * 图片文件名缺失
     */
    IMG_NAME_NULL(70001, "图片名称缺失"),
    /**
     * 图片传输失败
     */
    IMG_UPLOAD_FAIL(70002, "图片传输失败"),
    /**
     * 空文件
     */
    EMPTY_FILE(70003, "空文件"),
    /**
     * 文件类型不符
     */
    FILE_TYPE_ERROR(70004, "请上传正确图片格式的文件"),
    /**
     * 商品类别不存在
     */
    CATEGORY_NOT_FOUND(80001, "商品类别不存在"),
    /**
     * 商品类别名称已经存在
     */
    CATEGORY_NAME_EXISTS(80002, "商品类别名称已经存在"),
    /**
     * 活动添加失败
     */
    ACTIVITY_INSERT_FAILURE(90001, "活动添加失败"),
    /**
     * 活动没找到
     */
    ACTIVITY_NOT_FOUND(90002, "活动没找到"),
    /**
     * 该商店已经参加活动
     */
    STORE_IN_ACTIVITY(90003, "该商店已经参加活动"),
    /**
     * 该商店已经有未处理的参加活动申请
     */
    ACTIVITY_REQUEST_NOT_PROCESSED(90004, "该商店已经有未处理的参加活动申请"),
    /**
     * 活动余额不足
     */
    ACTIVITY_BALANCE_INSUFFICIENT(90005, "活动已结束"),
    /**
     * 注册资金不足
     */
    REGISTERED_CAPITAL_NOT_ENOUGH(90006, "注册资金不足"),
    /**
     * 上月销售额不足
     */
    MONTHLY_SALES_VALUE_NOT_ENOUGH(90007, "上月销售额不足"),
    /**
     * 上月销售量不足
     */
    MONTHLY_SALES_VOLUME_NOT_ENOUGH(90008, "上月销售量不足"),
    /**
     * 商城利润账户资金不足
     */
    WEB_SHOP_ACCOUNT_BALANCE_INSUFFICIENT(90009, "商城利润账户资金不足"),
    /**
     * 地址未找到
     */
    ADDRESS_NOT_FOUND(11001, "用户未添加地址"),
    /**
     * 地址信息不可为空
     */
    ADDRESS_INFO_NULL(11002, "信息为空"),
    /**
     * 将地址改为非默认地址
     */
    ADDRESS_CHANGE_DEFAULT_DENY(11003, "地址不可变为非默认，请选择其他地址作为默认地址"),
    /**
     * 发货信息未找到
     */
    DELIVERY_INFO_NOT_FOUND(12001, "发货信息不存在"),
    /**
     * 该订单之前的退货申请未被处理
     */
    RETURN_GOODS_REQUEST_UNPROCESSED(13001, "该订单之前的退货申请未被处理"),
    /**
     * 该订单状态不是代发货或待收货
     */
    ORDER_STATUS_ERROR(13002, "该订单状态不是代发货或待收货"),
    /**
     * 该订单中有商品参加活动，不能退货
     */
    ORDER_IS_IN_ACTIVITY(13003, "该订单中有商品参加活动，不能退货"),
    /**
     * 订单过期
     */
    ORDER_OVERDUE(13004, "订单过期"),
    /**
     * 订单未找到
     */
    ORDER_NOT_FOUND(13005, "订单未找到"),
    /**
     * 订单不可删除
     */
    ORDER_CANT_DELETE(13006, "仅可删除已完成订单"),
    /**
     * 订单不可撤销
     */
    ORDER_CANT_QUASH(13006, "订单不可撤销"),
    /**
     * 用户相关操作成功
     */
    USER_SUCCESS(200, "用户相关操作成功"),
    /**
     * 商店相关操作成功
     */
    STORE_SUCCESS(200,"商店相关操作成功"),
    /**
     * 开店请求相关操作成功
     */
    STORE_OPEN_REQUEST_SUCCESS(200,"开店请求相关操作成功"),
    /**
     * 修改信息请求相关操作成功
     */
    STORE_MODIFY_REQUEST_SUCCESS(200,"修改信息请求相关操作成功"),
    /**
     * 关店申请相关操作成功
     */
    STORE_DELETE_REQUEST_SUCCESS(200,"关店申请相关操作成功"),
    /**
     * 账户相关操作成功
     */
    ACCOUNT_SUCCESS(200, "账户相关操作成功"),
    /**
     * 购物车相关操作成功
     */
    CART_SUCCESS(200, "购物车相关操作成功"),

    /**
     * 商品相关操作成功
     */
    COMMODITY_SUCCESS(200, "商品相关操作成功"),
    /**
     * 商品类别相关操作成功
     */
    CATEGORY_SUCCESS(200, "商品类别相关操作成功"),
    /**
     * 活动相关操作成功
     */
    ACTIVITY_SUCCESS(200, "活动相关操作成功"),
    /**
     * 图片传输成功
     */
    IMG_SUCCESS(200, "图片传输成功"),
    /**
     * 流水操作成功
     */
    BILL_SUCCESS(200, "成功"),
    /**
     * 地址操作成功
     */
    ADDRESS_SUCCESS(200, "成功"),
    /**
     * 发货信息操作成功
     */
    DELIVERY_INFO_SUCCESS(200, "成功"),
    /**
     * 退货申请相关操作成功
     */
    RETURN_GOODS_REQUEST_SUCCESS(200, "退货申请相关操作成功"),
    /**
     * 订单相关成功
     */
    ORDER_SUCCESS(200, "成功")
    ;

    private Integer code;
    private String message;

    RetCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
