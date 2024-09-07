package com.james.reggie_takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.james.reggie_takeout.common.R;
import com.james.reggie_takeout.entity.Combo;
import com.james.reggie_takeout.entity.Employee;
import com.james.reggie_takeout.entity.User;
import com.james.reggie_takeout.service.UserService;
import com.james.reggie_takeout.utils.SMSUtils;
import com.james.reggie_takeout.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            // 通过这种方式，而不是实际发送验证码，来获取网页上传来的 code
            log.info("code={}", code);

            // 调用阿里云提供的短信服务 API 完成发送短信
            // SMSUtils.sendMessage("reggie", "", phone, code);

            // 需要将生成的验证码保存到 Session
            session.setAttribute(phone, code);

            // 最后 return
            return R.success("SMS sent successfully");
        }

        return R.error("SMS sent failed");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        // 此处也可以新建一个 UserDto 的类来接受 phone 和 code 参数

        log.info(map.toString());

        // 获取手机号
        String phone = map.get("phone").toString();


        // 获取验证码
        String code = map.get("code").toString();

        // 从 session 中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        // 进行验证码比对

        if(codeInSession != null && codeInSession.equals(code)){

            // 如果能够比对(codeInSession.equals(code))成功，说明登录成功
            // 判断当前手机号对应的用户是否为新用户，（这个过程，用户本身是无感知的）

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);

            if(user == null){
                // 如果用户是新用户，则自动完成注册 （这个过程，用户本身是无感知的）
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            // 登录成功后，需要将用户放入 session ， 以通过校验
            session.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("login failed");



    }
}
