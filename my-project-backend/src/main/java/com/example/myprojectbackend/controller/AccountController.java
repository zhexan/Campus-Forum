package com.example.myprojectbackend.controller;

import com.example.myprojectbackend.entity.RestBean;
import com.example.myprojectbackend.entity.dto.Account;
import com.example.myprojectbackend.entity.dto.AccountDetails;
import com.example.myprojectbackend.entity.vo.request.AccountPrivacyVO;
import com.example.myprojectbackend.entity.vo.request.ChangePasswordVO;
import com.example.myprojectbackend.entity.vo.request.DetailsSaveVo;
import com.example.myprojectbackend.entity.vo.request.ModifyEmailVO;
import com.example.myprojectbackend.entity.vo.response.AccountDetailsVo;
import com.example.myprojectbackend.entity.vo.response.AccountVo;
import com.example.myprojectbackend.entity.vo.response.FindPrivacyVO;
import com.example.myprojectbackend.service.AccountDetailsService;
import com.example.myprojectbackend.service.AccountPrivacyService;
import com.example.myprojectbackend.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/user/")
public class AccountController {
    @Resource
    AccountService accountService;
    @Resource
    AccountDetailsService accountDetailsService;
    @Resource
    AccountPrivacyService accountPrivacyService;
    @GetMapping("/info")
    public RestBean<AccountVo> getInfo(@RequestAttribute("id") int id) {
        Account account = accountService.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVo.class));

    }

    @GetMapping("/details")
    public RestBean<AccountDetailsVo> detail(@RequestAttribute("id") int id) {
        AccountDetails details = Optional
                .ofNullable(accountDetailsService.findAccountById(id))
                .orElseGet(AccountDetails::new);
        return RestBean.success(details.asViewObject(AccountDetailsVo.class));
    }
    @PostMapping("/save-details")
    public RestBean<Void> saveDetail(@RequestAttribute("id") int id,
                                        @RequestBody @Valid DetailsSaveVo vo) {
        boolean success = accountDetailsService.saveAccountDetails(id, vo);
        return success ? RestBean.success() : RestBean.failure(400, "此用户名已被使用");
    }
    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute("id") int id,
                                      @RequestBody @Valid ModifyEmailVO vo) {
        String result = accountService.modifyEmail(id, vo);
        return result == null ? RestBean.success() : RestBean.failure(400, result);
    }
    @PostMapping("/change_password")
    public RestBean<Void> changePassword(@RequestAttribute("id") int id,
                                         @RequestBody @Valid ChangePasswordVO vo){
        String result = accountService.changePassword(id, vo);
        return result == null ? RestBean.success() : RestBean.failure(400, result);
    }
    @PostMapping("/save_privacy")
    public RestBean<Void> savePrivacy(@RequestAttribute("id") int id,
                                      @RequestBody @Valid AccountPrivacyVO vo) {
        accountPrivacyService.savePrivacy(id, vo);
        return RestBean.success();
    }
    @GetMapping("/privacy")
    public RestBean<FindPrivacyVO> Privacy(@RequestAttribute("id") int id) {
        return RestBean.success(accountPrivacyService.accountPrivacy(id).asViewObject(FindPrivacyVO.class));
    }

}
