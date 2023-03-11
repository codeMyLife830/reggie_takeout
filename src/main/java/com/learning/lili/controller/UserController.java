package com.learning.lili.controller;

import com.learning.lili.common.R;
import com.learning.lili.entity.User;
import com.learning.lili.service.UserService;
import com.learning.lili.utils.SMSUtils;
import com.learning.lili.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R<String> login(@RequestBody Map user, HttpSession session) {
        log.info("用户信息：{}", user.toString());
        // 1. 获取用户输入的手机号和验证码以及生成的原始验证码
        String phone = (String) user.get("phone");
        String inputCode = (String) user.get("code");
        String rawCode = (String) session.getAttribute(phone);
        // 2. 比对用户输入的验证码是否正确
        // 默认登录
        if (phone.equals("13112345678")) {
            rawCode = "082799";
        }
        if (inputCode.equals(rawCode)) {
            // 3. 帮用户做一个自动注册
            User user1 = new User();
            user1.setPhone(phone);
            userService.save(user1);
            session.setAttribute("user", user1.getId());
            // 4. 将User存起来
            return R.success("登录成功");
        }
        return R.error("验证码有误");
    }

    /**
     * 获取验证码
     * @return
     */
    @PostMapping("/getCode")
    public R<String> getCode(@RequestBody Map user, HttpSession session) {
        // 1. 获取用户手机号
        String phone = (String) user.get("phone");
        // 2. 生成验证码
        // 默认登录方便演示
        String code1 = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
        String code2 = ValidateCodeUtils.generateValidateCode4String(4);
        log.info("数字验证码：{}", code1);
        log.info("字符串验证码：{}", code2);
        // 3. 利用阿里云短信服务给当前用户发送短信
//        SMSUtils.sendMessage("瑞吉外卖", "", phone, code1);
        // 4. 将验证码存入Session
        session.setAttribute(phone, code1);
        return R.success("验证码发送成功");
    }

    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出登录成功");
    }
}
