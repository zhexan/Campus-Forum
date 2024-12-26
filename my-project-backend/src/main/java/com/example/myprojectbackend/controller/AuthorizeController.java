package com.example.myprojectbackend.controller;

import com.example.myprojectbackend.entity.RestBean;
import com.example.myprojectbackend.entity.vo.request.ConfirmResetVO;
import com.example.myprojectbackend.entity.vo.request.EmailRegisterVO;
import com.example.myprojectbackend.entity.vo.request.EmailResetVO;
import com.example.myprojectbackend.service.AccountService;
import com.example.myprojectbackend.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    AccountService accountService;
    @Resource
    ControllerUtils utils;
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(
            @RequestParam @Pattern(regexp = "register|reset|modify")  String type,
            @RequestParam @Email String email,
            HttpServletRequest request) {
        return utils.messageHandle(() ->
                accountService.registerEmailVerifyCode(type, email, request.getRemoteAddr()));
    }

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody EmailRegisterVO vo){
        return utils.messageHandle(() ->
                accountService.registerEmailAccount(vo));
    }
    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){
        return utils.messageHandle(() ->
                accountService.resetConfirm(vo));

    }
    @PostMapping("/reset-password")
    public RestBean<Void> resetPassword(@RequestBody @Valid EmailResetVO vo) {
        return utils.messageHandle(() ->
                accountService.resetEmailAccountPassword(vo));

    }
}
