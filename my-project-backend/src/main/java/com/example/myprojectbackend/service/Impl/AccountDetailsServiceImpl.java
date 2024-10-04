package com.example.myprojectbackend.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myprojectbackend.entity.dto.Account;
import com.example.myprojectbackend.entity.dto.AccountDetails;
import com.example.myprojectbackend.entity.vo.request.DetailsSaveVo;
import com.example.myprojectbackend.mapper.AccountDetailsMapper;
import com.example.myprojectbackend.service.AccountDetailsService;
import com.example.myprojectbackend.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {

    @Resource
    AccountService service;
    @Override
    public AccountDetails findAccountById(int id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public synchronized boolean saveAccountDetails(int id, DetailsSaveVo vo) {
        Account account = service.findAccountByUsernameOrEmail(vo.getUsername());
        if(account == null || account.getId() == id) {
            service.update()
                    .eq("id", id)
                    .set("username", vo.getUsername())
                    .update();
            this.saveOrUpdate(new AccountDetails(
                    id, vo.getGender(), vo.getPhone(),
                    vo.getQq(), vo.getWechat(), vo.getDesc()
            ));
            return true;
        }
        return false;
    }

}
