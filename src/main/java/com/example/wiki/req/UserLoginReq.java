package com.example.wiki.req;

import javax.validation.constraints.NotEmpty;

/**
 * 查询用户-请求参数
 */
public class UserLoginReq extends PageReq {
    /**
     * null和空字符串都不符合
     */
    @NotEmpty(message = "用户名不能为空")
    private String loginName;

    @NotEmpty(message = "密码不能为空")
    private String password;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", loginName=").append(loginName);
        sb.append(", password=").append(password);
        sb.append("]");
        return sb.toString();
    }
}