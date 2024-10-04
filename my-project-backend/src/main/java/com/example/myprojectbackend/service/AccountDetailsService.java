package com.example.myprojectbackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myprojectbackend.entity.dto.AccountDetails;
import com.example.myprojectbackend.entity.vo.request.DetailsSaveVo;

public interface AccountDetailsService extends IService<AccountDetails> {
    AccountDetails findAccountById(int id);
    boolean saveAccountDetails(int id, DetailsSaveVo Vo);
}
