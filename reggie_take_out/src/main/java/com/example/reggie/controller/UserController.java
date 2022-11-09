package com.example.reggie.controller;

import com.example.reggie.common.Result;
import com.example.reggie.entity.User;
import com.example.reggie.service.UserService;
import com.example.reggie.utils.SMSUtils;
import com.example.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        //获取手机号
        String phone = user.getPhone();

        //后端再次校验手机号（前端已校验过）
        if(StringUtils.isNotEmpty(phone)) {
            //生成随机的4位短信验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            //发送短信验证码
            //SMSUtils.sendMessage("瑞吉外卖", "", phone, code);

            //将验证码存入session中，方便后期登录时使用
            HttpSession session = request.getSession();
            session.setAttribute(phone, code);
            log.info("手机验证码:" + code);

            return Result.success("手机验证码发送成功");
        }
        return Result.error("手机验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpServletRequest request) {
        log.info(map.toString());

        //获取手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //获取session中的验证码
        Object codeCorrect = request.getSession().getAttribute(phone);

        //进行验证码的比对
        if(codeCorrect == null) {
            return Result.error("登陆失败");
        }
        if(!codeCorrect.equals(code)) {
            return Result.error("验证码错误，登陆失败");
        }

        //验证成功，则可以登录
        //判断是否是新用户，如果是则注册账号
        User user = userService.selectByPhone(phone);
        if(user == null) {
            user = new User();
            user.setPhone(phone);
            userService.save(user);
        }
        //存入session中，后续登录校验器会用到
        request.getSession().setAttribute("user", user.getId());
        return Result.success(user);
    }
}
