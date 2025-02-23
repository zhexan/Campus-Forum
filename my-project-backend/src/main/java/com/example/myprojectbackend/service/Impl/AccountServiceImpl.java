package com.example.myprojectbackend.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myprojectbackend.entity.dto.Account;
import com.example.myprojectbackend.entity.vo.request.*;
import com.example.myprojectbackend.mapper.AccountMapper;
import com.example.myprojectbackend.service.AccountService;
import com.example.myprojectbackend.utils.Const;
import com.example.myprojectbackend.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Resource
    FlowUtils flowUtils;
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByUsernameOrEmail(username);
        if (account == null)
            throw new UsernameNotFoundException("用户不存在");
        return User.withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();

    }

    public Account findAccountByUsernameOrEmail(String usernameOrEmail) {
        return this.query()
                .eq("username", usernameOrEmail)
                .or()
                .eq("email", usernameOrEmail)
                .one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) {
            if(!this.verifyLimit(ip)){
                return "请求频繁，稍后再试";
            }
            Random random = new Random();
            int code = random.nextInt(899999) +100000;
            Map<String, Object> data = Map.of("type",type,"email",email,"code",code);
            amqpTemplate.convertAndSend("mail", data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email,String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO info) {
            String email = info.getEmail();
            String code = this.getEmailVerifyCode(email);
            if(code == null) {
                return "请先获取验证码";
            }
            if(!code.equals(info.getCode())) return "验证码输入错误";
            if(this.existsAccountByEmail(email)) return "该邮箱已被注册";
            String username = info.getUsername();
            if(this.existsAccountByUserName(username)) return "该用户名已被注册";
            String password = encoder.encode(info.getPassword());
            Account account = new Account(null,username, password, email, "user", new Date());
           if(this.save(account)) {
               this.deleteEmailVerifyCode(email);
               return null;
           } else {
               return "内部错误";
           }
    }
    @Override
    public String resetConfirm(ConfirmResetVO vo){
        String email = vo.getEmail();
        String code = this.getEmailVerifyCode(email);
        if(code == null) {
            return "请先获取验证码";
        }
        if(!code.equals(vo.getCode())) {
            return "验证码输入错误";
        }
        return null;
    }
    @Override
    public String resetEmailAccountPassword(EmailResetVO vo) {
        String email = vo.getEmail();
        String verify = this.resetConfirm(new ConfirmResetVO(email,vo.getCode()));
        if(verify != null) return verify;
        String password = encoder.encode(vo.getPassword());
        boolean update =this.update().eq("email", email).set("password", password).update();
        if(update) {
            this.deleteEmailVerifyCode(email);
        }
        return update ? null : "内部错误";

    }

    public Account findAccountById(int id) {
        return this.query().eq("id", id).one();
    }

    /**
     * 修改邮箱
     * @param id
     * @param vo
     * @return
     */
    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String email = vo.getEmail();
        String code = this.getEmailVerifyCode(email);
        if(code == null) {
            return "请先获取验证码";
        }
        if(!code.equals(vo.getCode())) {
            return "验证码输入错误";
        }
        Account account = this.findAccountByUsernameOrEmail(email);
        if(account != null && account.getId() != id) {
            return "该邮箱已被注册";
        }
        this.update().eq("id", id).set("email", email).update();
        return null;
    }

    public String changePassword(int id, ChangePasswordVO vo) {
        String password = this.query().eq("id", id).one().getPassword();
        if(!encoder.matches(vo.getPassword(), password)) {
            return "密码错误";
        } else {
            return this.update().eq("id", id)
                    .set("password", encoder.encode(vo.getNew_password()))
                    .update() ? null : "内部错误";
        }
    }

    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key, 60);
    }

    private void deleteEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    private String getEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }
    private boolean existsAccountByEmail(String email){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }
    private boolean existsAccountByUserName(String username){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", username));
    }
}
